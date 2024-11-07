/**
 * Activity for creating new events
 * Only organizer with a facility can create new events
 */
package com.example.myapplication.organizer;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.objects.Event;
import com.example.myapplication.organizer.OrganizerMainActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AddEventActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText editTextEventName, editTextEventDescription, editTextEventDateTime, editTextLimitEntrants;
    private Switch switchGeolocation;
    private Button buttonSaveEvent, buttonUploadPoster, buttonCancel;
    private ImageView imageViewPosterPreview;
    private Uri posterUri;
    private FirebaseFirestore db;
    private StorageReference storageReference;
    private String device;
    private Calendar selectedDateTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference("event_posters");
        editTextEventName = findViewById(R.id.editTextEventName);
        editTextEventDescription = findViewById(R.id.editTextEventDescription);
        editTextEventDateTime = findViewById(R.id.editTextEventDateTime);
        editTextLimitEntrants = findViewById(R.id.editTextLimitEntrants);
        switchGeolocation = findViewById(R.id.switchGeolocation);
        buttonSaveEvent = findViewById(R.id.buttonSaveEvent);
        buttonUploadPoster = findViewById(R.id.buttonUploadPoster);
        imageViewPosterPreview = findViewById(R.id.imageViewPosterPreview);
        buttonCancel = findViewById(R.id.buttonCancel);
        selectedDateTime = Calendar.getInstance();

        editTextEventDateTime.setOnClickListener(view -> showDatePickerDialog());
        buttonSaveEvent.setOnClickListener(view -> checkEventNameUnique());
        buttonUploadPoster.setOnClickListener(view -> openFileChooser());
        buttonCancel.setOnClickListener(view -> finish());

        device = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    /**
     * This method activates when the user wants to select date for the event and get the calendar
     */
    private void showDatePickerDialog() {
        int year = selectedDateTime.get(Calendar.YEAR);
        int month = selectedDateTime.get(Calendar.MONTH);
        int day = selectedDateTime.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, yearSelected, monthSelected, dayOfMonthSelected) -> {
                    selectedDateTime.set(yearSelected, monthSelected, dayOfMonthSelected);
                    showTimePickerDialog();
                },
                year, month, day
        );
        datePickerDialog.show();
    }

    /**
     * This method set the time for the event
     */
    private void showTimePickerDialog() {
        int hour = selectedDateTime.get(Calendar.HOUR_OF_DAY);
        int minute = selectedDateTime.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDaySelected, minuteSelected) -> {
                    selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDaySelected);
                    selectedDateTime.set(Calendar.MINUTE, minuteSelected);

                    String formattedDateTime = android.text.format.DateFormat.format("yyyy-MM-dd HH:mm", selectedDateTime).toString();
                    editTextEventDateTime.setText(formattedDateTime);
                },
                hour, minute, true // Use 24-hour format, change to false for AM/PM format
        );
        timePickerDialog.show();
    }

    /**
     * This method allows the user to select an image from their device
     */
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Poster Image"), PICK_IMAGE_REQUEST);
    }

    /**
     * Handles the result of the image selection activity.
     * It is activated when the user select an image from their device
     * it retrieves the selected image URI, converts it to a bitmap, and displays the image
     * @param requestCode request the code of the activity
     * @param resultCode see if the code matches
     * @param data the result data from image selection
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            posterUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), posterUri);
                imageViewPosterPreview.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * This method check if the event name created is unique, preventing same event name
     */
    private void checkEventNameUnique() {
        String eventName = editTextEventName.getText().toString().trim();

        if (TextUtils.isEmpty(eventName)) {
            Toast.makeText(this, "Event name is required", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("events").whereEqualTo("name", eventName)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        boolean isDuplicate = false;
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (document.exists()) {
                                isDuplicate = true;
                                break;
                            }
                        }
                        if (isDuplicate) {
                            Toast.makeText(AddEventActivity.this, "Event name already exists. Please choose a different name.", Toast.LENGTH_SHORT).show();
                        } else {
                            saveEvent();
                        }
                    } else {
                        Toast.makeText(AddEventActivity.this, "Error checking event name uniqueness", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * This method saves the event and upload it to database
     */
    private void saveEvent() {
        String eventName = editTextEventName.getText().toString().trim();
        String eventDescription = editTextEventDescription.getText().toString().trim();
        String eventDateTime = editTextEventDateTime.getText().toString().trim();
        String limitEntrants = editTextLimitEntrants.getText().toString().trim();

        if (TextUtils.isEmpty(eventName) || TextUtils.isEmpty(eventDescription) || TextUtils.isEmpty(eventDateTime)) {
            Toast.makeText(this, "Please fill out all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isPositiveInteger(limitEntrants)) {
            Toast.makeText(this, "Entrant limit must be a positive number.", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean geolocationEnabled = switchGeolocation.isChecked();

        Map<String, Object> event = new HashMap<>();
        event.put("name", eventName);
        event.put("description", eventDescription);
        event.put("dateTime", eventDateTime);
        event.put("limitEntrants", Integer.parseInt(limitEntrants));
        event.put("geolocationEnabled", geolocationEnabled);
        event.put("creatorID", device);

        ArrayList<String> waitlist = new ArrayList<>();
        ArrayList<String> cancelledList = new ArrayList<>();
        ArrayList<String> confirmedList = new ArrayList<>();
        event.put("waitlist", waitlist);
        event.put("cancelledList", cancelledList);
        event.put("confirmedList", confirmedList);

        if (posterUri != null) {
            uploadPosterAndSaveEvent(event);
        } else {
            saveEventToFirestore(event, null);
        }
    }

    /**
     * Check if the input is a positive int
     * @param str the user input for entrants limit of a event
     * @return check the result
     */
    private boolean isPositiveInteger(String str) {
        try {
            int num = Integer.parseInt(str);
            return num > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * This method save and upload the event poster
     * @param event the current event to be created
     */
    private void uploadPosterAndSaveEvent(Map<String, Object> event) {
        StorageReference reference = storageReference.child("poster_images/" + UUID.randomUUID() + ".jpg");
        reference.putFile(posterUri)
                .addOnSuccessListener(taskSnapshot -> reference.getDownloadUrl().addOnSuccessListener(uri -> {
                    String downloadUrl = uri.toString();
                    saveEventToFirestore(event, downloadUrl);
                }))
                .addOnFailureListener(e -> Toast.makeText(AddEventActivity.this, "Failed to upload poster", Toast.LENGTH_SHORT).show());
    }

    /**
     * This method upload event to database
     * @param event the event to be saved
     * @param posterUrl the poster info
     */
    private void saveEventToFirestore(Map<String, Object> event, String posterUrl) {
        if (posterUrl != null) {
            event.put("posterUrl", posterUrl);
        }

        db.collection("events")
                .add(event)
                .addOnSuccessListener(documentReference -> {
                    String eventId = documentReference.getId();
                    Bitmap qrCode = generateQRCode(eventId);
                    if (qrCode != null) {
                        uploadQRCodeToStorage(eventId, qrCode);
                    }
                    Toast.makeText(AddEventActivity.this, "Event Created", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(AddEventActivity.this, OrganizerMainActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(AddEventActivity.this, "Failed to create event", Toast.LENGTH_SHORT).show());
    }

    /**
     * This method generate the QR code for the event and link it to the event detail page
     * @param eventId the eventID relating to the QR code
     * @return QR code
     */
    private Bitmap generateQRCode(String eventId) {
        QRCodeWriter writer = new QRCodeWriter();
        String deepLinkUrl = "myapp://event?id=" + eventId;
        try {
            BitMatrix bitMatrix = writer.encode(deepLinkUrl, BarcodeFormat.QR_CODE, 500, 500);
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
     * This method upload the generated qr code to database
     * @param eventId the eventID relating to the QR code
     * @param qrCodeBitmap the QR code
     */
    private void uploadQRCodeToStorage(String eventId, Bitmap qrCodeBitmap) {
        ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
        qrCodeBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOut);
        byte[] data = byteArrayOut.toByteArray();
        StorageReference qrCodeRef = storageReference.child("qrcodes/" + eventId + "_qr.jpg");
        UploadTask uploadTask = qrCodeRef.putBytes(data);

        uploadTask.addOnSuccessListener(taskSnapshot -> qrCodeRef.getDownloadUrl().addOnSuccessListener(uri -> {
            saveQRCodeUrlToFirestore(eventId, uri.toString());
        })).addOnFailureListener(e -> Toast.makeText(AddEventActivity.this, "Failed to upload QR code", Toast.LENGTH_SHORT).show());
    }

    private void saveQRCodeUrlToFirestore(String eventId, String qrCodeUrl) {
        db.collection("events").document(eventId)
                .update("qrCodeUrl", qrCodeUrl)
                .addOnSuccessListener(aVoid -> Toast.makeText(AddEventActivity.this, "QR Code Saved", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(AddEventActivity.this, "Failed to save QR Code URL", Toast.LENGTH_SHORT).show());
    }
}
