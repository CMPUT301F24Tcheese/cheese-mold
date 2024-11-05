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

        // 初始化
        db = FirebaseFirestore.getInstance();
        joinEvent = findViewById(R.id.eventDetailJoin);
        cancel = findViewById(R.id.eventDetailCancel);
        eventName = findViewById(R.id.eventDetailName);
        eventDescription = findViewById(R.id.eventDetailDescription);
        imageView = findViewById(R.id.imageView);

        // 获取设备ID
        user = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        // 从Intent获取 eventId
        String eventId = getIntent().getStringExtra("event_id");

        if (eventId != null && !eventId.isEmpty()) {
            loadEventDetailsFromFirestore(eventId);
        } else {
            Toast.makeText(this, "Event ID not found", Toast.LENGTH_SHORT).show();
            finish();
        }

        cancel.setOnClickListener(v -> finish());
    }

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

    private void FireStoreAddEventId(String eventId, String device) {
        db.collection("users").document(device)
                .update("Event List", FieldValue.arrayUnion(eventId))
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Element added to array"))
                .addOnFailureListener(e -> Log.w("Firestore", "Error adding element to array", e));
    }

    private void FireStoreRemoveWaitingList(String eventId, String device) {
        db.collection("events").document(eventId)
                .update("waitlist", FieldValue.arrayRemove(device))
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Element removed from array"))
                .addOnFailureListener(e -> Log.w("Firestore", "Error removing element from array", e));
    }

    private void FireStoreRemoveEventId(String eventId, String device) {
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
            attemptToJoinWaitingList(event.getId(), userId);
        } else {
            Log.e("EventDetailActivity", "Event is null in onJoinClicked");
        }
    }
}
