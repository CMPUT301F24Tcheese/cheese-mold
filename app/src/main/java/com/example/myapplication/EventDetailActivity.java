package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.entrant.GeoAlertDialogFragment;
import com.example.myapplication.objects.Event;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class EventDetailActivity extends AppCompatActivity implements GeoAlertDialogFragment.GeolocationDialogListener {

    private FirebaseFirestore db;
    private Button joinEvent, cancel;
    private TextView eventName, eventDescription;
    private ImageView imageView;
    private String user;
    private Event eventToLoad;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_event);

        // Initialization
        joinEvent = findViewById(R.id.eventDetailJoin);
        cancel = findViewById(R.id.eventDetailCancel);
        eventName = findViewById(R.id.eventDetailName);
        eventDescription = findViewById(R.id.eventDetailDescription);
        imageView = findViewById(R.id.imageView);
        db = FirebaseFirestore.getInstance();

        // Retrieve the event and user that were clicked/passed from the previous activity
        Intent intent = getIntent();
        user = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        Uri data = intent.getData(); // Get the intent data from QR code

        if (data != null && "event".equals(data.getHost())) {
            // Extract the event ID from the deep link URL
            String eventId = data.getQueryParameter("id");

            if (eventId != null) {
                loadEventDetailsFromFirestore(eventId); // Method to load event data based on ID
            } else {
                Toast.makeText(this, "Event ID not found", Toast.LENGTH_SHORT).show();
            }
        }

        // Exit from this activity when cancel button is clicked
        cancel.setOnClickListener(v -> finish());
    }

    /**
     * Attempts to add the device to the waiting list of an event on Firebase,
     * but only if the waiting list has not reached the limit.
     *
     * @param eventId The id of the event
     * @param device The user's device ID who is attempting to join
     */
    public void attemptToJoinWaitingList(String eventId, String device) {
        db.collection("events").document(eventId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Long limitEntrants = documentSnapshot.getLong("limitEntrants");
                ArrayList<String> waitingList = (ArrayList<String>) documentSnapshot.get("waitlist");

                if (waitingList != null && limitEntrants != null) {
                    if (waitingList.size() >= limitEntrants) {
                        // Show a message if the waiting list is full
                        Toast.makeText(this, "The waiting list is full. You cannot join.", Toast.LENGTH_SHORT).show();
                    } else {
                        // Add the user to the waiting list
                        db.collection("events").document(eventId)
                                .update("waitlist", FieldValue.arrayUnion(device))
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("Firestore", "User added to waiting list.");
                                    Toast.makeText(this, "You have been added to the waiting list.", Toast.LENGTH_SHORT).show();
                                    FireStoreAddEventId(eventId, device); // Add the event ID to the user's event list
                                })
                                .addOnFailureListener(e -> {
                                    Log.w("Firestore", "Error adding user to waiting list", e);
                                    Toast.makeText(this, "Failed to join waiting list. Please try again.", Toast.LENGTH_SHORT).show();
                                });
                    }
                } else {
                    Toast.makeText(this, "Error loading event data.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Event not found.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Error accessing event data. Please try again.", Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * Add the eventId to the user's array of EventID in Firebase, to indicate they have joined this event
     * @param eventId
     * @param device
     */
    public void FireStoreAddEventId(String eventId, String device) {
        db.collection("users").document(device)
                .update("Event List", FieldValue.arrayUnion(eventId))
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Element added to array"))
                .addOnFailureListener(e -> Log.w("Firestore", "Error adding element to array", e));
    }

    /**
     * Load event details from Firestore based on the event ID.
     * @param eventId The ID of the event to load
     */
    public void loadEventDetailsFromFirestore(String eventId) {
        db.collection("events").document(eventId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();
                            if (doc.exists()) {
                                eventToLoad = doc.toObject(Event.class);
                                if (eventToLoad != null) {
                                    // Set UI elements only after the event data is loaded
                                    eventToLoad.setId(doc.getId());
                                    eventName.setText(eventToLoad.getTitle());
                                    eventDescription.setText(eventToLoad.getDescription());
                                    Picasso.get()
                                            .load(eventToLoad.getPosterUrl())
                                            .into(imageView);

                                    // Setup the join/unjoin logic
                                    if (eventToLoad.getWaitingList() != null && eventToLoad.getWaitingList().contains(user)) {
                                        joinEvent.setText("Unjoin Event");
                                        joinEvent.setOnClickListener(v -> {
                                            eventToLoad.removeWaitingList(user);
                                            FireStoreRemoveWaitingList(eventToLoad.getId(), user);
                                            FireStoreRemoveeventId(eventToLoad.getId(), user);
                                            finish();
                                        });
                                    } else {
                                        joinEvent.setOnClickListener(v -> {
                                            if (eventToLoad.getGeo()) {
                                                showGeolocationDialog(eventToLoad, user);
                                            } else {
                                                attemptToJoinWaitingList(eventToLoad.getId(), user); // Use the new method with limit check
                                            }
                                        });
                                    }
                                }
                            } else {
                                Toast.makeText(EventDetailActivity.this, "Event not found", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.w("Firestore", "Error getting document", task.getException());
                        }
                    }
                });
    }

    /**
     * Remove the device from the waiting list of an event on Firebase
     * @param eventId The id of the event
     * @param device The user's device ID who is removing themselves from the list
     */
    public void FireStoreRemoveWaitingList(String eventId, String device) {
        db.collection("events").document(eventId)
                .update("waitlist", FieldValue.arrayRemove(device))
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Element removed from array"))
                .addOnFailureListener(e -> Log.w("Firestore", "Error removing element from array", e));
    }

    /**
     * Remove the eventID from user on Firebase, to indicate they unjoined the event.
     * @param eventId
     * @param device
     */
    public void FireStoreRemoveeventId(String eventId, String device) {
        db.collection("users").document(device)
                .update("Event List", FieldValue.arrayRemove(eventId))
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Element removed from array"))
                .addOnFailureListener(e -> Log.w("Firestore", "Error removing element from array", e));
    }

    public void showGeolocationDialog(Event event, String userId) {
        GeoAlertDialogFragment dialog = GeoAlertDialogFragment.newInstance(event, userId);
        dialog.show(getSupportFragmentManager(), "GeoAlertDialog");
    }

    @Override
    public void onJoinClicked(Event event, String userId) {
        if (event != null) {
            attemptToJoinWaitingList(event.getId(), userId); // Use the new method with limit check
        } else {
            Log.e("EventDetailActivity", "Event is null in onJoinClicked");
        }
    }
}
