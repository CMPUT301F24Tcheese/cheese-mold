package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.administrator.Image;
import com.example.myapplication.entrant.GeoAlertDialogFragment;
import com.example.myapplication.objects.Event;
import com.example.myapplication.objects.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
        Uri data = intent.getData(); // Get the intent data from qr code

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
        cancel.setOnClickListener(v -> {
            finish();
        });

    }

    /**
     * Add the device to the waiting list of an event on Firebase
     * @param eventId
     *      The id of the event
     * @param device
     *      The users who is going to jonin
     */
    public void FirestoreAddWaitingList(String eventId,String device){
        db.collection("events").document(eventId)
                .update("waitlist", FieldValue.arrayUnion(device))
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Element added to array"))
                .addOnFailureListener(e -> Log.w("Firestore", "Error adding element to array", e));
    }

    /**
     *Add the eventId to the user's array of EventID in firebase, to indicate they have joined this event
     * @param eventId
     * @param device
     */
    public void FireStoreAddEventId(String eventId,String device){
        db.collection("users").document(device)
                .update("Event List", FieldValue.arrayUnion(eventId))
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Element added to array"))
                .addOnFailureListener(e -> Log.w("Firestore", "Error adding element to array", e));
    }

    /**
     * Remove the device to the waiting list of an event on Firebase
     * @param eventId
     *      The id of the event
     * @param device
     *      The users who is going to jonin
     */
    public void FireStoreRemoveWaitingList(String eventId,String device) {
        db.collection("events").document(eventId)
                .update("waitlist", FieldValue.arrayRemove(device))
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Element removed from array"))
                .addOnFailureListener(e -> Log.w("Firestore", "Error removing element from array", e));

    }

    /**
     *Remove the eventID from user on Firebase, to indicate they unjoin the event.
     * @param eventId
     * @param device
     */
    public void FireStoreRemoveeventId(String eventId,String device){
        db.collection("users").document(device)
                .update("Event List",FieldValue.arrayRemove(eventId))
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
            event.addWaitingList(userId);
            FirestoreAddWaitingList(event.getId(), userId);
            FireStoreAddEventId(event.getId(), userId);
            finish();
        } else {
            Log.e("EventDetailActivity", "Event is null in onJoinClicked");
        }
    }

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
//                                    eventToLoad.setTitle((String) doc.get("name"));
                                    eventName.setText(eventToLoad.getTitle());
                                    eventDescription.setText(eventToLoad.getDescription());
                                    Picasso.get()
                                            .load(eventToLoad.getPosterUrl())
                                            .into(imageView);


                                    Log.d("QR Code Firestore", eventToLoad.getWaitingList().toString());
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
                                                eventToLoad.addWaitingList(user);
                                                FirestoreAddWaitingList(eventToLoad.getId(), user);
                                                FireStoreAddEventId(eventToLoad.getId(), user);
                                                finish();
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



}