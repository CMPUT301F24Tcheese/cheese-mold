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

/**
 * This Activity represents the detail of an event after the user scanned the QR code.
 * The users are allowed to join or unjoin the waiting list of an event on this page.
 */
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

        db = FirebaseFirestore.getInstance();
        joinEvent = findViewById(R.id.eventDetailJoin);
        cancel = findViewById(R.id.eventDetailCancel);
        eventName = findViewById(R.id.eventDetailName);
        eventDescription = findViewById(R.id.eventDetailDescription);
        imageView = findViewById(R.id.imageView);

        Intent intent = getIntent();
        user = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        Uri data = intent.getData();

        if (data != null && "event".equals(data.getHost())) {
            // Extract the event ID from the deep link URL
            String eventId = data.getQueryParameter("id");

            if (eventId != null) {
                loadEventDetailsFromFirestore(eventId); // Method to load event data based on ID
            } else {
                Toast.makeText(this, "Event ID not found", Toast.LENGTH_SHORT).show();
                finish();
            }
        }

        cancel.setOnClickListener(v -> finish());
    }

    /**
     * Loads event details from Firestore using the provided event ID.
     *
     * @param eventId The ID of the event to be loaded.
     */
    private void loadEventDetailsFromFirestore(String eventId) {
        db.collection("events").document(eventId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();
                            if (doc.exists()) {
                                eventToLoad = doc.toObject(Event.class);
                                if (eventToLoad != null) {
                                    eventToLoad.setId(doc.getId());
                                    eventName.setText(eventToLoad.getTitle());
                                    eventDescription.setText(eventToLoad.getDescription());
                                    Picasso.get().load(eventToLoad.getPosterUrl()).into(imageView);

                                    setupJoinButton();
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
     * Configures the join button based on the user's current status regarding the event.
     * Sets up the button to either join or unjoin the waiting list.
     */
    private void setupJoinButton() {
        if (eventToLoad.getWaitingList() != null && eventToLoad.getWaitingList().contains(user)) {
            joinEvent.setText("Unjoin Event");
            joinEvent.setOnClickListener(v -> {
                eventToLoad.removeWaitingList(user);
                FireStoreRemoveWaitingList(eventToLoad.getId(), user);
                FireStoreRemoveEventId(eventToLoad.getId(), user);
                Toast.makeText(EventDetailActivity.this, "Unjoined " + eventToLoad.getTitle(), Toast.LENGTH_SHORT).show();
                finish();
            });
        } else {
            joinEvent.setOnClickListener(v -> {
                if (eventToLoad.getGeo()) {
                    showGeolocationDialog(eventToLoad, user);
                } else {
                    attemptToJoinWaitingList(eventToLoad.getId(), user);
                }
            });
        }
    }

    /**
     * Attempts to add the user to the waiting list for the specified event.
     *
     * @param eventId The ID of the event to join.
     * @param device The user's device ID.
     */
    private void attemptToJoinWaitingList(String eventId, String device) {
        db.collection("events").document(eventId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Long limitEntrants = documentSnapshot.getLong("limitEntrants");
                if (limitEntrants == null) {
                    limitEntrants = Long.MAX_VALUE;
                }
                ArrayList<String> waitingList = (ArrayList<String>) documentSnapshot.get("waitlist");

                if (waitingList != null && waitingList.size() >= limitEntrants) {
                    Toast.makeText(this, "The waiting list is full. You cannot join.", Toast.LENGTH_SHORT).show();
                } else {
                    db.collection("events").document(eventId)
                            .update("waitlist", FieldValue.arrayUnion(device))
                            .addOnSuccessListener(aVoid -> {
                                Log.d("Firestore", "User added to waiting list.");
                                Toast.makeText(this, "You have been added to the waiting list.", Toast.LENGTH_SHORT).show();
                                FireStoreAddEventId(eventId, device);
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                Log.w("Firestore", "Error adding user to waiting list", e);
                                Toast.makeText(this, "Failed to join waiting list. Please try again.", Toast.LENGTH_SHORT).show();
                            });
                }
            } else {
                Toast.makeText(this, "Event not found.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Error accessing event data. Please try again.", Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * Adds the event ID to the user's list of events in Firestore.
     *
     * @param eventId The ID of the event to add.
     * @param device The user's device ID.
     */
    private void FireStoreAddEventId(String eventId, String device) {
        db.collection("users").document(device)
                .update("Event List", FieldValue.arrayUnion(eventId))
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Element added to array"))
                .addOnFailureListener(e -> Log.w("Firestore", "Error adding element to array", e));
    }

    /**
     * Removes the user's device ID from the event's waiting list in Firestore.
     *
     * @param eventId The ID of the event to update.
     * @param device The user's device ID.
     */
    private void FireStoreRemoveWaitingList(String eventId, String device) {
        db.collection("events").document(eventId)
                .update("waitlist", FieldValue.arrayRemove(device))
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Element removed from array"))
                .addOnFailureListener(e -> Log.w("Firestore", "Error removing element from array", e));
    }

    /**
     * Removes the event ID from the user's list of events in Firestore.
     *
     * @param eventId The ID of the event to remove.
     * @param device The user's device ID.
     */
    private void FireStoreRemoveEventId(String eventId, String device) {
        db.collection("users").document(device)
                .update("Event List", FieldValue.arrayRemove(eventId))
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Element removed from array"))
                .addOnFailureListener(e -> Log.w("Firestore", "Error removing element from array", e));
    }

    /**
     * Shows a geolocation dialog to the user for the specified event.
     *
     * @param event The event for which geolocation is needed.
     * @param userId The ID of the user.
     */
    public void showGeolocationDialog(Event event, String userId) {
        GeoAlertDialogFragment dialog = GeoAlertDialogFragment.newInstance(event, userId);
        dialog.show(getSupportFragmentManager(), "GeoAlertDialog");
    }

    /**
     * Callback method when the user clicks to join the event from the geolocation dialog.
     *
     * @param event The event to join.
     * @param userId The ID of the user.
     */
    @Override
    public void onJoinClicked(Event event, String userId) {
        if (event != null) {
            attemptToJoinWaitingList(event.getId(), userId);
        } else {
            Log.e("EventDetailActivity", "Event is null in onJoinClicked");
        }
    }
}
