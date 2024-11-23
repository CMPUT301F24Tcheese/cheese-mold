package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.entrant.EntrantMainActivity;
import com.example.myapplication.objects.Event;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

/**
 * This activity is used to unjoin event for entrants
 * This will be integrated into one single activity on the next part
 */
public class EntrantEventDetailActivity extends AppCompatActivity {

    private ImageView eventPoster;
    private TextView eventName, eventDescription;
    private Button cancel, unjoinEvent, buttonDecline, buttonConfirm;
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
        buttonDecline = findViewById(R.id.button_decline);
        buttonConfirm = findViewById(R.id.button_confirm);

        user = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);


        Intent intent = getIntent();
        Event event = intent.getParcelableExtra("event");
        Log.d("Local","Lottery List After: " + event.getLottery());


        //Change the visibility of the button of decline and Confirm, These two buttons are only visible when selected to lottery list.
        if (event.getLottery().contains(user)){
            buttonConfirm.setVisibility(View.VISIBLE);
            buttonDecline.setVisibility(View.VISIBLE);
        }

        else {
            Log.d("Local","Confirm List: " + event.getConfirmedList());
            Log.d("Local","Max Capacity: " + event.getFinalEntrantsNum());
        }

        buttonConfirm.setOnClickListener(view -> {
            FireStoreRemoveList(event.getId(),user,"lotteryList");
            FireStoreAddList(event.getId(),user,"confirmedList");
            buttonConfirm.setVisibility(View.GONE);
            buttonDecline.setVisibility(View.GONE);
            Toast.makeText(EntrantEventDetailActivity.this,"You are now joined to the event: "+event.getTitle(),Toast.LENGTH_SHORT).show();
            startActivity(new Intent(EntrantEventDetailActivity.this,EntrantMainActivity.class));

        });

        buttonDecline.setOnClickListener(view -> {
            FireStoreRemoveList(event.getId(),user,"lotteryList");
            FireStoreAddList(event.getId(),user,"cancelledList");
            FireStoreRemoveeventId(event.getId(),user);
            Toast.makeText(EntrantEventDetailActivity.this,"You are removed from the event: "+event.getTitle(),Toast.LENGTH_SHORT).show();
            Intent intent1 = new Intent(EntrantEventDetailActivity.this,EntrantMainActivity.class);
            startActivity(intent1);

        });

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
            FireStoreRemoveList(event.getId(), user, "waitlist");
            FireStoreRemoveeventId(event.getId(), user);
            Toast.makeText(EntrantEventDetailActivity.this, "Unjoined " + event.getTitle(), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(EntrantEventDetailActivity.this,EntrantMainActivity.class));
        });


    }



    /**
     * Remove the device from the waiting list of an event on Firebase
     * @param eventId The id of the event
     * @param device The user's device ID who is removing themselves from the list
     */
    public void FireStoreRemoveList(String eventId, String device, String list) {

        db.collection("events").document(eventId)
                .update(list, FieldValue.arrayRemove(device))
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

    private void FireStoreAddList(String eventId, String device, String list) {
        db.collection("events").document(eventId)
                .update(list, FieldValue.arrayUnion(device))
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Element added to array"))
                .addOnFailureListener(e -> Log.w("Firestore", "Error adding element to array", e));
    }

}