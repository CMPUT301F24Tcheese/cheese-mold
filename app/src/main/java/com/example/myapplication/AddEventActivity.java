package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
                    Toast.makeText(AddEventActivity.this, "Event Created", Toast.LENGTH_SHORT).show();
                    // Return to Event Activity
                    startActivity(new Intent(AddEventActivity.this, EventActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(AddEventActivity.this, "Failed to create event", Toast.LENGTH_SHORT).show());
    }
}
