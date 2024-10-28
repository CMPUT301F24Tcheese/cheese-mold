package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

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

public class EventDetailActivity extends AppCompatActivity {

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
                    FirestoreRemoveWaitingList(event.getId(), users);
                    FireStoreRemoveeventId(event.getId(), users);
                    finish();
                });
            } else {
                // Logic for when the user is not in the waiting list
                joinEvent.setOnClickListener(v -> {
                    event.addWaitingList(users);
                    FirestoreAddWaitingList(event.getId(),users);
                    FireStoreAddeventId(event.getId(), users);
                    finish();
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
    public void FireStoreAddeventId(String eventId,String device){
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
    public void FirestoreRemoveWaitingList(String eventId,String device) {
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



}