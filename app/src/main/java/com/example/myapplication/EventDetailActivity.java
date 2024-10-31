package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_event);

        // Initialization
        Button joinEvent = findViewById(R.id.eventDetailJoin);
        Button cancel = findViewById(R.id.eventDetailCancel);
        TextView textView = findViewById(R.id.eventDetail);
        ImageView imageView = findViewById(R.id.imageView);
        db = FirebaseFirestore.getInstance();

        // Retrieve the event and user that were clicked/passed from the previous activity
        Intent intent = getIntent();
        String users = intent.getStringExtra("device");
        Event event = (Event) intent.getSerializableExtra("event");
        String eventDescription = event.getDescription();

        Uri data = intent.getData(); // Get the intent data for qr code

        if (data != null && "event".equals(data.getHost())) {
            // Extract the event ID from the deep link URL
            String eventId = data.getQueryParameter("id");

            //if (eventId != null) {
                //loadEventDetails(eventId); // Method to load event data based on ID
            //} else {
                //Toast.makeText(this, "Event ID not found", Toast.LENGTH_SHORT).show();
            //}
        }

        textView.setText(eventDescription); // set the event description on the detail activity

        //Load image from poster URL
        Picasso.get()
                .load(event.getPosterUrl()) // Load the image URL
                .into(imageView); // Set the image into your ImageView



        if (event.getWaitingList() != null && event.getWaitingList().getList().contains(users)) {
                // Logic for when the user is in the waiting list
                joinEvent.setText("Unjoin Event"); // replace the text button with unjoin event
                joinEvent.setOnClickListener(v -> {
                    event.removeWaitingList(users);
                    FireStoreRemoveWaitingList(event.getId(), users);
                    FireStoreRemoveeventId(event.getId(), users);
                    finish();
                });
            } else {
                // Logic for when the user is not in the waiting list
                joinEvent.setOnClickListener(v -> {
                    if (event.getGeo()) {
                        showGeolocationDialog(event, users);

                    }
                    else {
                            event.addWaitingList(users);
                            FirestoreAddWaitingList(event.getId(), users);
                            FireStoreAddEventId(event.getId(), users);
                            finish();
                        }

                });
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
                .update("EventID", FieldValue.arrayUnion(eventId))
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
                .update("EventID",FieldValue.arrayRemove(device))
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





}