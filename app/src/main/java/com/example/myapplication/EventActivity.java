package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.objects.Event;
import com.example.myapplication.organizer.AddEventActivity;
import com.example.myapplication.organizer.AddFacilityActivity;
import com.example.myapplication.organizer.EventEditActivity;
import com.example.myapplication.organizer.FacilityActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;

public class EventActivity extends AppCompatActivity implements EventAdapter.OnEventClickListener {

    private RecyclerView recyclerView;
    private EventAdapter eventAdapter;
    private List<Event> eventList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        db = FirebaseFirestore.getInstance();

        recyclerView = findViewById(R.id.recyclerViewEvent);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventList = new ArrayList<>();
        eventAdapter = new EventAdapter(eventList, this);
        recyclerView.setAdapter(eventAdapter);

        loadEventsFromFirestore();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> showAddDialog());

        Button myFacilityButton = findViewById(R.id.buttonMyFacility);
        myFacilityButton.setOnClickListener(view -> {
            Intent intent = new Intent(EventActivity.this, FacilityActivity.class);
            startActivity(intent);
        });

        ImageView profileImage = findViewById(R.id.profileImage);
        profileImage.setOnClickListener(v -> {
            Intent intent = new Intent(EventActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        ImageView emailImage = findViewById(R.id.emailImage);
        emailImage.setOnClickListener(v -> {
            Intent intent = new Intent(EventActivity.this, EmailActivity.class);
            startActivity(intent);
        });
    }

    /**
     * Loads events from Firestore and populates the RecyclerView with the data.
     * Listens for changes in the Firestore collection and updates the UI accordingly.
     */
    private void loadEventsFromFirestore() {
        CollectionReference eventCollection = db.collection("events");

        eventCollection.addSnapshotListener((value, error) -> {
            if (error != null) {
                return;
            }

            if (value != null) {
                eventList.clear();
                for (QueryDocumentSnapshot doc : value) {
                    Event event = doc.toObject(Event.class);
                    event.setId(doc.getId());
                    event.setWaitingList((ArrayList<String>) doc.get("waitlist"));
                    event.setFinalEntrantsNum(doc.getLong("maxCapacity"));
                    event.setFirstDraw(doc.getBoolean("firstDraw"));
                    event.setLottery((ArrayList<String>) doc.get("lotteryList"));

                    //Log.d("Local", "Lottery list:" + event.getLottery());

                    eventList.add(event);


                }
                eventAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * Displays a dialog for the user to choose whether to add an event or a facility.
     * Launches the appropriate activity based on the user's selection.
     */
    private void showAddDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Action");
        String[] options = {"Add Event", "Add Facility"};
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                startActivity(new Intent(EventActivity.this, AddEventActivity.class));
            } else {
                startActivity(new Intent(EventActivity.this, AddFacilityActivity.class));
            }
        });
        builder.create().show();
    }

    /**
     * Handles click events on individual event items in the RecyclerView.
     * Launches the EventEditActivity with the selected event's details passed as extras.
     *
     * @param event The Event object that was clicked.
     */
    @Override
    public void onEventClick(Event event) {
        Intent intent = new Intent(EventActivity.this, EventEditActivity.class);
        intent.putExtra("event_id", event.getId());
        intent.putExtra("title", event.getTitle());
        intent.putExtra("description", event.getDescription());
        intent.putExtra("posterUrl", event.getPosterUrl());
        intent.putExtra("dateTime", event.getDateTime());
        intent.putExtra("limitEntrants", event.getLimitEntrants());
        startActivity(intent);
    }
}
