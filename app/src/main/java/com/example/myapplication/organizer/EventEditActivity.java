package com.example.myapplication.organizer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.objects.Event;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.UUID;

public class EventEditActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1; // Constant for identifying image picker intent request
    private FirebaseFirestore db; // Firestore database instance
    private StorageReference storageRef; // Storage reference for Firebase Storage
    private String eventId; // Variable to store the ID of the event being edited
    private EditText editTextTitle, editTextDate, editTextLimitEntrants; // EditTexts for user input
    private Button buttonUpdateEvent, buttonUploadPoster; // Buttons for updating event and uploading poster
    private Uri posterUri; // URI to hold the selected poster image

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Call to parent class method to set up the activity
        setContentView(R.layout.activity_event_edit); // Set the layout for this activity

        db = FirebaseFirestore.getInstance(); // Initialize Firestore database instance
        storageRef = FirebaseStorage.getInstance().getReference("event_posters"); // Initialize Firebase Storage reference

        eventId = getIntent().getStringExtra("event_id"); // Retrieve event ID passed from previous activity

        editTextTitle = findViewById(R.id.editTextTitle); // Connect EditText for event title with layout
        editTextDate = findViewById(R.id.editTextDateTime); // Connect EditText for event date with layout
        editTextLimitEntrants = findViewById(R.id.editTextLimitEntrants); // Connect EditText for limit entrants with layout
        buttonUpdateEvent = findViewById(R.id.buttonUpdateEvent); // Connect Button for updating event with layout
        buttonUploadPoster = findViewById(R.id.buttonUploadPoster); // Connect Button for uploading poster with layout

        loadEventData(eventId); // Load existing event data from Firestore using the event ID

        buttonUpdateEvent.setOnClickListener(view -> updateEvent()); // Set click listener to update event information
        buttonUploadPoster.setOnClickListener(view -> openFileChooser()); // Set click listener to open file chooser for poster
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI); // Create an intent to pick an image from gallery
        startActivityForResult(intent, PICK_IMAGE_REQUEST); // Start the activity to choose an image
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data); // Call the parent method for handling result
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            posterUri = data.getData(); // Get the URI of the selected image
            uploadPosterToFirebase(); // Call method to upload the poster image to Firebase Storage
        }
    }

    private void uploadPosterToFirebase() {
        if (posterUri != null) { // Check if a poster image has been selected
            String fileName = UUID.randomUUID().toString(); // Generate a unique file name for the poster
            StorageReference fileReference = storageRef.child(fileName); // Create a reference in Firebase Storage for the poster

            fileReference.putFile(posterUri) // Upload the selected poster image to Firebase Storage
                    .addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        String posterUrl = uri.toString(); // Get the URL of the uploaded poster
                        savePosterUrlToFirestore(posterUrl); // Save the poster URL to Firestore
                    }))
                    .addOnFailureListener(e -> Toast.makeText(EventEditActivity.this, "Failed to upload poster", Toast.LENGTH_SHORT).show()); // Show failure message if upload fails
        }
    }

    private void savePosterUrlToFirestore(String posterUrl) {
        DocumentReference eventRef = db.collection("events").document(eventId); // Create a reference to the specific event document in Firestore
        eventRef.update("posterUrl", posterUrl) // Update the event document with the new poster URL
                .addOnSuccessListener(aVoid -> Toast.makeText(EventEditActivity.this, "Poster updated successfully", Toast.LENGTH_SHORT).show()) // Show success message
                .addOnFailureListener(e -> Toast.makeText(EventEditActivity.this, "Failed to update poster", Toast.LENGTH_SHORT).show()); // Show failure message if update fails
    }

    private void loadEventData(String eventId) {
        DocumentReference eventRef = db.collection("events").document(eventId); // Create a reference to the event document in Firestore
        eventRef.get().addOnCompleteListener(task -> { // Fetch the event data from Firestore
            if (task.isSuccessful()) { // Check if the data retrieval was successful
                Event event = task.getResult().toObject(Event.class); // Convert Firestore document data to Event object
                if (event != null) { // Check if the event object is not null
                    editTextTitle.setText(event.getTitle()); // Populate EditText with the event title
                    editTextDate.setText(event.getDate()); // Populate EditText with the event date
                    editTextLimitEntrants.setText(event.getLimitEntrants() != null ? event.getLimitEntrants().toString() : ""); // Populate EditText with limit entrants, if available
                } else {
                    Toast.makeText(EventEditActivity.this, "Event not found", Toast.LENGTH_SHORT).show(); // Show message if event is not found
                }
            } else {
                Toast.makeText(EventEditActivity.this, "Failed to load event", Toast.LENGTH_SHORT).show(); // Show failure message if data retrieval fails
            }
        });
    }

    private void updateEvent() {
        String updatedTitle = editTextTitle.getText().toString(); // Get the updated title from EditText
        String updatedDateTime = editTextDate.getText().toString(); // Get the updated date and time from EditText
        Long updatedLimitEntrants = editTextLimitEntrants.getText().toString().isEmpty() ? null : Long.valueOf(editTextLimitEntrants.getText().toString()); // Get the updated limit entrants as Long or null if not provided

        DocumentReference eventRef = db.collection("events").document(eventId); // Create a reference to the event document in Firestore
        eventRef.update("title", updatedTitle, "dateTime", updatedDateTime, "limitEntrants", updatedLimitEntrants) // Update the event document with the new values
                .addOnSuccessListener(aVoid -> Toast.makeText(EventEditActivity.this, "Event updated successfully", Toast.LENGTH_SHORT).show()) // Show success message
                .addOnFailureListener(e -> Toast.makeText(EventEditActivity.this, "Failed to update event", Toast.LENGTH_SHORT).show()); // Show failure message if update fails
    }
}
