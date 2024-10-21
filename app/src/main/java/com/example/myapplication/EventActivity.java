package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;
import com.example.myapplication.Event;

public class EventActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EventAdapter eventAdapter;
    private List<Event> eventList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize RecyclerView and Adapter
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventList = new ArrayList<>();
        eventAdapter = new EventAdapter(eventList);
        recyclerView.setAdapter(eventAdapter);

        // Load Events from Firestore
        loadEventsFromFirestore();

        // Floating Action Button to add Event or Facility
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> showAddDialog());

        Button myFacilityButton = findViewById(R.id.buttonMyFacility);
        myFacilityButton.setOnClickListener(view -> {
            Intent intent = new Intent(EventActivity.this, FacilityActivity.class);
            startActivity(intent);
        });

        // Profile and Email ImageView Listeners
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

    private void loadEventsFromFirestore() {
        CollectionReference eventCollection = db.collection("events");

        eventCollection.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@NonNull QuerySnapshot value, @NonNull FirebaseFirestoreException error) {
                if (error != null) {
                    return;
                }

                if (value != null) {
                    eventList.clear();
                    eventList.addAll(value.toObjects(Event.class));
                    eventAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void showAddDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Action");
        String[] options = {"Add Event", "Add Facility"};
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                // Navigate to Add Event Activity
                startActivity(new Intent(EventActivity.this, AddEventActivity.class));
            } else {
                // Navigate to Add Facility Activity
                startActivity(new Intent(EventActivity.this, AddFacilityActivity.class));
            }
        });
        builder.create().show();
    }
}
