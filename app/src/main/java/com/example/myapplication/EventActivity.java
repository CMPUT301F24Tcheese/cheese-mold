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

import com.example.myapplication.entrant.MyEventActivity;
import com.example.myapplication.objects.Event;
import com.example.myapplication.objects.Users;
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
import java.util.Objects;

public class EventActivity extends AppCompatActivity implements EventAdapter.OnEventClickListener {

    private RecyclerView recyclerView; // RecyclerView for displaying the list of events.
    private EventAdapter eventAdapter; // Adapter for handling and binding event data to the RecyclerView.
    private List<Event> eventList; // List to store event objects.
    private FirebaseFirestore db; // Firebase Firestore instance for database operations.
    private Users user; // The user who is using the app



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


        // when click the event on the event list, it directs to EventDetailActivity that shows the details of the event.
        // The EventDetail activity allow the user to join the activity.
//        eventAdapter.setOnEventClickListener(event -> {
//            Intent intent = new Intent(EventActivity.this, EventDetailActivity.class);
//            intent.putExtra("event", event); // send the event to new activity
//            intent.putExtra("user",user); // send user to new activity
//            startActivity(intent);
//        });

        Button myEventButton = findViewById(R.id.myeventbutt);
        myEventButton.setOnClickListener(v->{
            Intent intent = new Intent(EventActivity.this, MyEventActivity.class);
            startActivity(intent);
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

        Intent intentFromEntrant = getIntent();
        String device = intentFromEntrant.getStringExtra("device");
        String role = intentFromEntrant.getStringExtra("role");

        Intent intent;

        if (Objects.equals(role, "Entrant")){
            intent =  new Intent(EventActivity.this, EventDetailActivity.class); // Create an intent to open the EventEditActivity.
        }
        else {
            intent =  new Intent(EventActivity.this, EventEditActivity.class);
        }
        intent.putExtra("event_id", event.getId()); // Attach the event ID as extra data to the intent.
        startActivity(intent); // Start the new activity to edit the event.
    }


}
