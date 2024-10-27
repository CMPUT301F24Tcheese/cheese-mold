package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
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
        db = FirebaseFirestore.getInstance();

        // Retrieve the event and user that were clicked/passed from the previous activity
        Intent intent = getIntent();
        String users = intent.getStringExtra("device");
        Event event = (Event) intent.getSerializableExtra("event");
        String eventDescription = event.getDescription();

        textView.setText(eventDescription); // set the event description on the detail activity


        checkIfUserInWaitingList(event.getId(), users, isInWaitingList -> {
            if (isInWaitingList) {
                // Logic for when the user is in the waiting list
                joinEvent.setText("Unjoin Event"); // replace the text button with unjoin event
                joinEvent.setOnClickListener(v -> {
                    event.removeWaitingList(users);
                    FirestoreRemoveWaitingList(event.getId(), users);
                    finish();
                });
            } else {
                // Logic for when the user is not in the waiting list
                joinEvent.setOnClickListener(v -> {
                    event.addWaitingList(users);
                    FirestoreAddWaitingList(event.getId(),users);
                    finish();
                });
            }
        });

        // When join event button is clicked, the user is added to the waitingList
//        if (event.getWaitingList() != null && event.getWaitingList().getList().contains(users)) {
//            joinEvent.setText("Unjoin Event"); // replace the text button with unjoin event
//            event.removeWaitingList(users);
//            FirestoreRemoveWaitingList(event.getId(),users);
//
//
//        } else {
//            joinEvent.setOnClickListener(v -> {
//                event.addWaitingList(users);
//                FirestoreAddWaitingList(event.getId(),users);
//            });
//        }



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

    public void checkIfUserInWaitingList(String eventId, String userId, Callback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("events").document(eventId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // Retrieve the waitingList array as an ArrayList
                    ArrayList<String> waitingList = (ArrayList<String>) document.get("waitlist");

                    if (waitingList != null && waitingList.contains(userId)) {
                        // User is in the waiting list
                        callback.onResult(true);
                    } else {
                        // User is not in the waiting list
                        callback.onResult(false);
                    }
                } else {
                    Log.d("Firestore", "No such document");
                    callback.onResult(false);
                }
            } else {
                Log.d("Firestore", "get failed with ", task.getException());
                callback.onResult(false);
            }
        });
    }

    // Define a callback interface
    public interface Callback {
        void onResult(boolean isInWaitingList);
    }

}
