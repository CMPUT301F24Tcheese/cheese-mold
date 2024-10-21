package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AddEventActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText editTextEventName, editTextEventDescription, editTextEventDateTime, editTextLimitEntrants;
    private Switch switchGeolocation;
    private Button buttonSaveEvent, buttonUploadPoster;
    private ImageView imageViewPosterPreview;
    private Uri posterUri;
    private FirebaseFirestore db;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        // Initialize Firestore and Storage
        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference("event_posters");

        // Initialize views
        editTextEventName = findViewById(R.id.editTextEventName);
        editTextEventDescription = findViewById(R.id.editTextEventDescription);
        editTextEventDateTime = findViewById(R.id.editTextEventDateTime);
        editTextLimitEntrants = findViewById(R.id.editTextLimitEntrants);
        switchGeolocation = findViewById(R.id.switchGeolocation);
        buttonSaveEvent = findViewById(R.id.buttonSaveEvent);
        buttonUploadPoster = findViewById(R.id.buttonUploadPoster);
        imageViewPosterPreview = findViewById(R.id.imageViewPosterPreview);

        // Set click listener for Save Event button
        buttonSaveEvent.setOnClickListener(view -> saveEvent());

        // Set click listener for Upload Poster button
        buttonUploadPoster.setOnClickListener(view -> openFileChooser());
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Poster Image"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            posterUri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), posterUri);
                imageViewPosterPreview.setImageBitmap(bitmap); // Show image preview
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveEvent() {
        String eventName = editTextEventName.getText().toString().trim();
        String eventDescription = editTextEventDescription.getText().toString().trim();
        String eventDateTime = editTextEventDateTime.getText().toString().trim();
        String limitEntrants = editTextLimitEntrants.getText().toString().trim();
        boolean geolocationEnabled = switchGeolocation.isChecked();

        if (!eventName.isEmpty() && !eventDescription.isEmpty() && !eventDateTime.isEmpty()) {
            // Create a new event object
            Map<String, Object> event = new HashMap<>();
            event.put("name", eventName);
            event.put("description", eventDescription);
            event.put("dateTime", eventDateTime);
            event.put("limitEntrants", limitEntrants.isEmpty() ? null : Integer.parseInt(limitEntrants));
            event.put("geolocationEnabled", geolocationEnabled);

            if (posterUri != null) {
                // Upload poster to Firebase Storage
                uploadPosterToStorage(event);
            } else {
                // Save event without poster
                saveEventToFirestore(event, null);
            }
        } else {
            Toast.makeText(this, "Please fill out all required fields", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadPosterToStorage(Map<String, Object> event) {
        StorageReference fileReference = storageReference.child(System.currentTimeMillis() + ".jpg");
        fileReference.putFile(posterUri)
                .addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl()
                        .addOnSuccessListener(uri -> saveEventToFirestore(event, uri.toString())))
                .addOnFailureListener(e -> Toast.makeText(AddEventActivity.this, "Failed to upload poster", Toast.LENGTH_SHORT).show());
    }

    private void saveEventToFirestore(Map<String, Object> event, String posterUrl) {
        if (posterUrl != null) {
            event.put("posterUrl", posterUrl);
        }

        db.collection("events")
                .add(event)
                .addOnSuccessListener(documentReference -> {
                    //Log.d("EventCreation", "Event created successfully!");
                    String eventId = documentReference.getId();

                    //Generate QR code to Firebase Storage, the method is defined right after this one
                    Bitmap qrCode = generateQRCode(posterUrl);
                    if (qrCode != null) {
                        //upload QR code to Firebase, the method is defined after
                        uploadQRCodeToStorage(eventId, qrCode);
                    }
                    Toast.makeText(AddEventActivity.this, "Event Created", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(AddEventActivity.this, EventActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AddEventActivity.this, "Failed to create event", Toast.LENGTH_SHORT).show();
                    Log.e("EventCreation", "Failed to create event: " + e.getMessage());
                });
    }

    /**
     * This will generate the QR code
     * @param text
     *  This will be the poster associated with the QR code
     * @return
     *  This will return a bitmap typed QR code
     */
    private Bitmap generateQRCode(String text) {
        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, 500, 500);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }

            return bmp;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * This method will upload the QR code to Firebase
     * @param eventId
     *  This is the event name
     * @param qrCodeBitmap
     *  This is the QR code
     */
    private void uploadQRCodeToStorage(String eventId, Bitmap qrCodeBitmap) {
        ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
        qrCodeBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOut);
        byte[] data = byteArrayOut.toByteArray();

        // Save QR code with the event ID as the filename
        StorageReference qrCodeRef = storageReference.child("qrcodes/" + eventId + "_qr.jpg");
        UploadTask uploadTask = qrCodeRef.putBytes(data);

        uploadTask.addOnSuccessListener(taskSnapshot -> {
            qrCodeRef.getDownloadUrl().addOnSuccessListener(uri -> {
                //Save QR Code URL in Firestore
                saveQRCodeUrlToFirestore(eventId, uri.toString());
            });
        }).addOnFailureListener(e -> {
            Toast.makeText(AddEventActivity.this, "Failed to upload QR code", Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * This method save the download URL of the QR code to Firebase
     * @param eventId
     * @param qrCodeUrl
     */
    private void saveQRCodeUrlToFirestore(String eventId, String qrCodeUrl) {
        db.collection("events").document(eventId)
                .update("qrCodeUrl", qrCodeUrl)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(AddEventActivity.this, "QR Code Saved", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AddEventActivity.this, "Failed to save QR Code URL", Toast.LENGTH_SHORT).show();
                });
    }
}
