package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.objects.Event;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class EntrantEventDetailActivity extends AppCompatActivity {

    private ImageView eventPoster;
    private TextView eventName, eventDescription;
    private Button cancel, unjoinEvent;
    private FirebaseFirestore db;
    private String user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_entrant_event_detail);

        db = FirebaseFirestore.getInstance();
        eventPoster = findViewById(R.id.entrantEventPoster);
        eventName = findViewById(R.id.entrantEventDetailName);
        eventDescription = findViewById(R.id.entrantEventDetailDescription);
        cancel = findViewById(R.id.entrantEventDetailCancel);
        unjoinEvent = findViewById(R.id.entrantEventDetailUnjoin);
        user = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);


        Intent intent = getIntent();
        Event event = intent.getParcelableExtra("event");

        if (event != null) {
            Picasso.get()
                    .load(event.getPosterUrl())
                    .into(eventPoster);

            eventName.setText(event.getTitle());
            eventDescription.setText(event.getDescription());
        }

        cancel.setOnClickListener(view -> {
            finish();
        });

        unjoinEvent.setOnClickListener(view -> {
            FireStoreRemoveWaitingList(event.getId(), user);
            FireStoreRemoveeventId(event.getId(), user);
            Toast.makeText(EntrantEventDetailActivity.this, "Unjoined " + event.getTitle(), Toast.LENGTH_SHORT).show();
            finish();
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
}