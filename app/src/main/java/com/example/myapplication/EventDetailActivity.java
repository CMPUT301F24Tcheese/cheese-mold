package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

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
        Event event = (Event) intent.getSerializableExtra("event");
        Users users = (Users) intent.getSerializableExtra("user");

        assert event != null;
        textView.setText(event.getDescription()); // set the event description on the detail activity


        // When join event button is clicked, the user is added to the waitingList
        if (!event.getWaitingList().contains( users))

            joinEvent.setOnClickListener(v -> {
                event.addWaitingList(users);
                assert users != null;

                // add the user to waitinglist in firestore
                FaddWaitingList(event.getEventId(),users.getEmail());

            });
        else {
            joinEvent.setText("Unjoin Event");
            joinEvent.setOnClickListener(v -> {
                event.removeWaitingList(users);
                assert users != null;

                // remove the user from waitinglist in firestore
                FremoveWaitingList(event.getEventId(),users.getEmail());

            });

        }

        // Exit from this activity when cancel button is clicked
        cancel.setOnClickListener(v -> {
            finish();
        });

    }

    /**
     * This method add the user to the event.
     * @param eventId
     *         The unique iD for an event(The document ID)
     * @param email
     *         verification of the user
     */
    private void FaddWaitingList(String eventId, String email) {
        db.collection("event").document(eventId)
                .update("waitingList", FieldValue.arrayUnion(email));

    }

    private void FremoveWaitingList(String eventId, String email) {
        db.collection("event").document(eventId)
                .update("waitingList", FieldValue.arrayRemove(email));

    }
}
