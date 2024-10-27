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

import com.example.myapplication.objects.Event;
import com.example.myapplication.organizer.AddEventActivity;
import com.example.myapplication.organizer.AddFacilityActivity;
import com.example.myapplication.organizer.EventEditActivity;
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

    private RecyclerView recyclerView; // RecyclerView for displaying the list of events.
    private EventAdapter eventAdapter; // Adapter for handling and binding event data to the RecyclerView.
    private List<Event> eventList; // List to store event objects.
    private FirebaseFirestore db; // Firebase Firestore instance for database operations.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Call the superclass method to handle activity creation.
        setContentView(R.layout.activity_event); // Set the XML layout for this activity.

        db = FirebaseFirestore.getInstance(); // Initialize the Firestore database instance.

        recyclerView = findViewById(R.id.recyclerViewEvent); // Link the RecyclerView to the layout.
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); // Set layout manager for linear arrangement of items.
        eventList = new ArrayList<>(); // Initialize the list that will hold events.
        eventAdapter = new EventAdapter(eventList, this); // Create an instance of the adapter and set the click listener.
        recyclerView.setAdapter(eventAdapter); // Attach the adapter to the RecyclerView.

        loadEventsFromFirestore(); // Call method to load events from Firestore.

        FloatingActionButton fab = findViewById(R.id.fab); // Find the FloatingActionButton from the layout.
        fab.setOnClickListener(view -> showAddDialog()); // Set an onClick listener to open a dialog for adding events or facilities.

        Button myFacilityButton = findViewById(R.id.buttonMyFacility); // Find the button for navigating to facilities.
        myFacilityButton.setOnClickListener(view -> {
            Intent intent = new Intent(EventActivity.this, FacilityActivity.class); // Create an intent to start FacilityActivity.
            startActivity(intent); // Start the new activity.
        });

        ImageView profileImage = findViewById(R.id.profileImage); // Find the ImageView for the profile icon.
        profileImage.setOnClickListener(v -> {
            Intent intent = new Intent(EventActivity.this, ProfileActivity.class); // Create an intent to start ProfileActivity.
            startActivity(intent); // Start the new activity.
        });

        ImageView emailImage = findViewById(R.id.emailImage); // Find the ImageView for the email icon.
        emailImage.setOnClickListener(v -> {
            Intent intent = new Intent(EventActivity.this, EmailActivity.class); // Create an intent to start EmailActivity.
            startActivity(intent); // Start the new activity.
        });
    }

    private void loadEventsFromFirestore() {
        CollectionReference eventCollection = db.collection("events"); // Get reference to the Firestore collection named "events".

        eventCollection.addSnapshotListener(new EventListener<QuerySnapshot>() { // Set up a listener to watch for changes in the "events" collection.
            @Override
            public void onEvent(@NonNull QuerySnapshot value, @NonNull FirebaseFirestoreException error) {
                if (error != null) { // Check if there is any error when fetching data.
                    return; // Exit if there is an error.
                }

                if (value != null) { // Proceed if the fetched data is not null.
                    eventList.clear(); // Clear the current list of events.
                    for (QueryDocumentSnapshot doc : value) { // Loop through each document in the Firestore query snapshot.
                        Event event = doc.toObject(Event.class); // Convert Firestore document to Event object.
                        event.setId(doc.getId()); // Assign the Firestore document ID to the event object.
                        eventList.add(event); // Add the event object to the list.
                    }
                    eventAdapter.notifyDataSetChanged(); // Notify the adapter to refresh the RecyclerView with new data.
                }
            }
        });
    }

    private void showAddDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this); // Create an AlertDialog builder.
        builder.setTitle("Choose Action"); // Set the title of the dialog.
        String[] options = {"Add Event", "Add Facility"}; // Define options for the dialog.
        builder.setItems(options, (dialog, which) -> { // Set what happens when a user selects an option.
            if (which == 0) { // If the user selects "Add Event"
                startActivity(new Intent(EventActivity.this, AddEventActivity.class)); // Start AddEventActivity.
            } else { // If the user selects "Add Facility"
                startActivity(new Intent(EventActivity.this, AddFacilityActivity.class)); // Start AddFacilityActivity.
            }
        });
        builder.create().show(); // Display the dialog.
    }

    @Override
    public void onEventClick(Event event) {
        Intent intent = new Intent(EventActivity.this, EventEditActivity.class); // Create an intent to open the EventEditActivity.
        intent.putExtra("event_id", event.getId()); // Attach the event ID as extra data to the intent.
        startActivity(intent); // Start the new activity to edit the event.
    }
}
