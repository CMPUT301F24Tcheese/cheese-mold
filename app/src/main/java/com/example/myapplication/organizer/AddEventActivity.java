package com.example.myapplication.organizer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.EventActivity;
import com.example.myapplication.R;
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
import java.util.UUID;

public class AddEventActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1; // Constant for identifying the image picker request

    private EditText editTextEventName, editTextEventDescription, editTextEventDateTime, editTextLimitEntrants; // Fields for event details input
    private Switch switchGeolocation; // Switch for enabling geolocation feature
    private Button buttonSaveEvent, buttonUploadPoster, buttonCancel; // Buttons for saving event, uploading poster, and canceling
    private ImageView imageViewPosterPreview; // ImageView for displaying the poster preview
    private Uri posterUri; // URI to hold the selected image for the poster
    private FirebaseFirestore db; // Firestore database instance
    private StorageReference storageReference; // Firebase Storage reference for uploading images

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Call the parent class onCreate method to initialize the activity
        setContentView(R.layout.activity_add_event); // Set the layout for this activity

        db = FirebaseFirestore.getInstance(); // Initialize Firestore database instance
        storageReference = FirebaseStorage.getInstance().getReference("event_posters"); // Initialize Firebase Storage reference

        editTextEventName = findViewById(R.id.editTextEventName); // Connect EditText for event name to the layout
        editTextEventDescription = findViewById(R.id.editTextEventDescription); // Connect EditText for event description to the layout
        editTextEventDateTime = findViewById(R.id.editTextEventDateTime); // Connect EditText for event date and time to the layout
        editTextLimitEntrants = findViewById(R.id.editTextLimitEntrants); // Connect EditText for limit entrants to the layout
        switchGeolocation = findViewById(R.id.switchGeolocation); // Connect Switch for geolocation to the layout
        buttonSaveEvent = findViewById(R.id.buttonSaveEvent); // Connect Button for saving the event to the layout
        buttonUploadPoster = findViewById(R.id.buttonUploadPoster); // Connect Button for uploading poster to the layout
        imageViewPosterPreview = findViewById(R.id.imageViewPosterPreview); // Connect ImageView for poster preview to the layout
        buttonCancel = findViewById(R.id.buttonCancel); // Connect Button for cancel action to the layout

        buttonSaveEvent.setOnClickListener(view -> saveEvent()); // Set a click listener on the save event button to trigger the saveEvent method
        buttonUploadPoster.setOnClickListener(view -> openFileChooser()); // Set a click listener on the upload poster button to open the file chooser
        buttonCancel.setOnClickListener(view -> {
            startActivity(new Intent(AddEventActivity.this, EventActivity.class)); // Navigate back to EventActivity (list of events)
            finish(); // Close the AddEventActivity
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent(); // Create an intent for file selection
        intent.setType("image/*"); // Set the intent to select images
        intent.setAction(Intent.ACTION_GET_CONTENT); // Specify action to get content
        startActivityForResult(Intent.createChooser(intent, "Select Poster Image"), PICK_IMAGE_REQUEST); // Start activity to pick an image
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data); // Call the parent class method to handle the result
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            posterUri = data.getData(); // Get the URI of the selected image

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), posterUri); // Convert URI to Bitmap
                imageViewPosterPreview.setImageBitmap(bitmap); // Display the selected image as a preview
            } catch (IOException e) {
                e.printStackTrace(); // Print the error stack trace if an exception occurs
            }
        }
    }

    private void saveEvent() {
        String eventName = editTextEventName.getText().toString().trim(); // Retrieve event name from the input field
        String eventDescription = editTextEventDescription.getText().toString().trim(); // Retrieve event description from the input field
        String eventDateTime = editTextEventDateTime.getText().toString().trim(); // Retrieve event date and time from the input field
        String limitEntrants = editTextLimitEntrants.getText().toString().trim(); // Retrieve limit entrants value from the input field
        boolean geolocationEnabled = switchGeolocation.isChecked(); // Get the state of the geolocation switch

        if (!eventName.isEmpty() && !eventDescription.isEmpty() && !eventDateTime.isEmpty()) {
            Map<String, Object> event = new HashMap<>(); // Create a map to hold the event details
            event.put("name", eventName); // Add event name to the map
            event.put("description", eventDescription); // Add event description to the map
            event.put("dateTime", eventDateTime); // Add event date and time to the map
            event.put("limitEntrants", limitEntrants.isEmpty() ? null : Integer.parseInt(limitEntrants)); // Add limit entrants or null if not set
            event.put("geolocationEnabled", geolocationEnabled); // Add geolocation status to the map

            if (posterUri != null) {
                uploadPosterAndSaveEvent(event); // Upload poster image and save the event
            } else {
                saveEventToFirestore(event, null); // Save the event without a poster image
            }
        } else {
            Toast.makeText(this, "Please fill out all required fields", Toast.LENGTH_SHORT).show(); // Show a toast message if required fields are missing
        }
    }

    private void uploadPosterAndSaveEvent(Map<String, Object> event) {
        StorageReference reference = storageReference.child("poster_images/" + UUID.randomUUID() + ".jpg"); // Create a reference for the poster image in Firebase Storage
        reference.putFile(posterUri) // Upload the poster image file to Firebase Storage
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        reference.getDownloadUrl().addOnSuccessListener(uri -> {
                            String downloadUrl = uri.toString(); // Retrieve the download URL of the uploaded image
                            saveEventToFirestore(event, downloadUrl); // Save the event details with the poster URL
                        });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AddEventActivity.this, "Failed to upload poster", Toast.LENGTH_SHORT).show(); // Show a toast if poster upload fails
                });
    }

    private void saveEventToFirestore(Map<String, Object> event, String posterUrl) {
        if (posterUrl != null) {
            event.put("posterUrl", posterUrl); // Add the poster URL to the event data
        }

        db.collection("events") // Access the "events" collection in Firestore
                .add(event) // Add the event data to the collection
                .addOnSuccessListener(documentReference -> {
                    String eventId = documentReference.getId(); // Get the unique ID of the created event
                    Bitmap qrCode = generateQRCode(posterUrl); // Generate a QR code for the poster URL
                    if (qrCode != null) {
                        uploadQRCodeToStorage(eventId, qrCode); // Upload the generated QR code to Firebase Storage
                    }
                    Toast.makeText(AddEventActivity.this, "Event Created", Toast.LENGTH_SHORT).show(); // Show a success message
                    startActivity(new Intent(AddEventActivity.this, EventActivity.class)); // Navigate back to EventActivity
                    finish(); // Close the AddEventActivity
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AddEventActivity.this, "Failed to create event", Toast.LENGTH_SHORT).show(); // Show a failure message if event creation fails
                });
    }

    private Bitmap generateQRCode(String text) {
        QRCodeWriter writer = new QRCodeWriter(); // Initialize QR code writer
        try {
            BitMatrix bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, 500, 500); // Generate QR code as a BitMatrix
            int width = bitMatrix.getWidth(); // Get width of the QR code
            int height = bitMatrix.getHeight(); // Get height of the QR code
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565); // Create a bitmap for the QR code
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE); // Set pixels to black or white based on BitMatrix
                }
            }
            return bmp; // Return the generated QR code bitmap
        } catch (WriterException e) {
            e.printStackTrace(); // Print error if QR code generation fails
            return null; // Return null if an error occurs
        }
    }

    private void uploadQRCodeToStorage(String eventId, Bitmap qrCodeBitmap) {
        ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream(); // Initialize a byte array output stream
        qrCodeBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOut); // Compress the QR code bitmap to JPEG format
        byte[] data = byteArrayOut.toByteArray(); // Convert the JPEG to a byte array
        StorageReference qrCodeRef = storageReference.child("qrcodes/" + eventId + "_qr.jpg"); // Create a reference for the QR code in Firebase Storage
        UploadTask uploadTask = qrCodeRef.putBytes(data); // Upload the QR code data to Firebase Storage

        uploadTask.addOnSuccessListener(taskSnapshot -> {
            qrCodeRef.getDownloadUrl().addOnSuccessListener(uri -> {
                saveQRCodeUrlToFirestore(eventId, uri.toString()); // Save the download URL of the QR code to Firestore
            });
        }).addOnFailureListener(e -> {
            Toast.makeText(AddEventActivity.this, "Failed to upload QR code", Toast.LENGTH_SHORT).show(); // Show a failure message if QR code upload fails
        });
    }

    private void saveQRCodeUrlToFirestore(String eventId, String qrCodeUrl) {
        db.collection("events").document(eventId) // Access the specific event document by ID
                .update("qrCodeUrl", qrCodeUrl) // Update the document with the QR code URL
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(AddEventActivity.this, "QR Code Saved", Toast.LENGTH_SHORT).show(); // Show a success message
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AddEventActivity.this, "Failed to save QR Code URL", Toast.LENGTH_SHORT).show(); // Show a failure message if saving fails
                });
    }
}
