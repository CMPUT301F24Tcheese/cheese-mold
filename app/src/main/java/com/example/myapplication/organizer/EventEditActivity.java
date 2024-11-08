/**
 * Activity for editing existing event, involving database information update,
 * poster update and event deletion
 */
package com.example.myapplication.organizer;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.objects.Event;
import com.example.myapplication.organizer.OrganizerMainActivity;
import com.example.myapplication.organizer.OrganizerNotificationActivity;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;
import java.util.UUID;

import javax.microedition.khronos.opengles.GL;

public class EventEditActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private FirebaseFirestore db;
    private StorageReference storageRef;
    private String eventId;
    private EditText editTextTitle, editTextDate, editTextLimitEntrants, editTextDescription;
    private Button buttonUpdateEvent, buttonUploadPoster, buttonCancel, buttonDeleteEvent,buttonNotification, buttonQrCode;
    private Uri posterUri;
    private Calendar selectedDateTime;
    private String qrCodeUrl;

    /**
     * onCreate function for the edit event activity
     * @param savedInstanceState saved instances
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_edit);

        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference("event_posters");

        eventId = getIntent().getStringExtra("event_id");
        if (eventId == null || eventId.isEmpty()) {
            Toast.makeText(this, "Event ID not provided", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        editTextTitle = findViewById(R.id.editTextTitle);
        editTextDate = findViewById(R.id.editTextDateTime);
        editTextLimitEntrants = findViewById(R.id.editTextLimitEntrants);
        editTextDescription = findViewById(R.id.editTextDescription);
        buttonUpdateEvent = findViewById(R.id.buttonUpdateEvent);
        buttonUploadPoster = findViewById(R.id.buttonUploadPoster);
        buttonCancel = findViewById(R.id.buttonCancel);
        buttonDeleteEvent = findViewById(R.id.buttonDeleteEvent);
        buttonNotification = findViewById(R.id.buttonNotification);
        buttonQrCode = findViewById(R.id.buttonQRCode);
        selectedDateTime = Calendar.getInstance();

        loadEventData(eventId);

        editTextDate.setOnClickListener(view -> showDatePickerDialog());
        buttonUpdateEvent.setOnClickListener(view -> updateEvent());
        buttonUploadPoster.setOnClickListener(view -> openFileChooser());
        buttonCancel.setOnClickListener(view -> finish());
        buttonDeleteEvent.setOnClickListener(view -> deleteEvent());
        buttonNotification.setOnClickListener(view -> {
            Intent intent = new Intent(EventEditActivity.this, OrganizerNotificationActivity.class);
            intent.putExtra("event_id", eventId); // Pass event ID to OrganizerNotificationActivity
            startActivity(intent);
        });
        buttonQrCode.setOnClickListener(view -> {
            Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.image_dialog);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            ImageView imageView = dialog.findViewById(R.id.dialogImageView);

            Glide.with(this).load(qrCodeUrl).into(imageView);
            dialog.show();
        });
    }

    /**
     * This method activates when the user wants to edit date for the event and get the calendar
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
                    editTextDate.setText(formattedDateTime);
                },
                hour, minute, true // Use 24-hour format, change to false for AM/PM format
        );
        timePickerDialog.show();
    }

    /**
     * This method allows the user to select an image from their device
     * and asks for device permission
     */
    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            posterUri = data.getData();
            uploadPosterToFirebase();
        }
    }

    /**
     * This method update the event poster
     */
    private void uploadPosterToFirebase() {
        if (posterUri != null) {
            String fileName = UUID.randomUUID().toString();
            StorageReference fileReference = storageRef.child(fileName);

            fileReference.putFile(posterUri)
                    .addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        String posterUrl = uri.toString();
                        savePosterUrlToFirestore(posterUrl);
                    }))
                    .addOnFailureListener(e -> Toast.makeText(EventEditActivity.this, "Failed to upload poster", Toast.LENGTH_SHORT).show());
        }
    }

    /**
     * This method upload the new poster to database
     * @param posterUrl the poster info
     */
    private void savePosterUrlToFirestore(String posterUrl) {
        DocumentReference eventRef = db.collection("events").document(eventId);
        eventRef.update("posterUrl", posterUrl)
                .addOnSuccessListener(aVoid -> Toast.makeText(EventEditActivity.this, "Poster updated successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(EventEditActivity.this, "Failed to update poster", Toast.LENGTH_SHORT).show());
    }

    /**
     * This method get the event data from database
     * @param eventId the current eventID
     */
    private void loadEventData(String eventId) {
        DocumentReference eventRef = db.collection("events").document(eventId);
        eventRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Event event = task.getResult().toObject(Event.class);
                if (event != null) {
                    editTextTitle.setText(event.getTitle());
                    editTextDate.setText(event.getDateTime());
                    editTextLimitEntrants.setText(event.getLimitEntrants() != null ? event.getLimitEntrants().toString() : "");
                    editTextDescription.setText(event.getDescription());
                    qrCodeUrl = event.getQRcode();
                } else {
                    Toast.makeText(EventEditActivity.this, "Event not found", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(EventEditActivity.this, "Failed to load event", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * This method updates the event for all the editable fields and make instant update
     * on the firebase for the event
     */
    private void updateEvent() {
        String updatedTitle = editTextTitle.getText().toString();
        String updatedDateTime = editTextDate.getText().toString();
        String updatedLimitEntrantsStr = editTextLimitEntrants.getText().toString();
        String updatedDescription = editTextDescription.getText().toString();

        // Check if Limit Entrants is a valid number
        Long updatedLimitEntrants = null;
        if (!updatedLimitEntrantsStr.isEmpty()) {
            try {
                updatedLimitEntrants = Long.valueOf(updatedLimitEntrantsStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Limit Entrants should be a valid number", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        DocumentReference eventRef = db.collection("events").document(eventId);
        eventRef.update("name", updatedTitle, "dateTime", updatedDateTime, "limitEntrants", updatedLimitEntrants, "description", updatedDescription)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(EventEditActivity.this, "Event updated successfully", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(EventEditActivity.this, OrganizerMainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(EventEditActivity.this, "Failed to update event", Toast.LENGTH_SHORT).show());
    }

    /**
     * This method deletes the event from the database
     */
    private void deleteEvent() {
        DocumentReference eventRef = db.collection("events").document(eventId);
        eventRef.delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(EventEditActivity.this, "Event deleted successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(EventEditActivity.this, OrganizerMainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(EventEditActivity.this, "Failed to delete event", Toast.LENGTH_SHORT).show());
    }

}
