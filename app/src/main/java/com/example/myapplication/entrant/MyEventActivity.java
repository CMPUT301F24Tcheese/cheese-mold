package com.example.myapplication.entrant;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.EmailActivity;
import com.example.myapplication.EventAdapter;
import com.example.myapplication.EventDetailActivity;
import com.example.myapplication.ProfileActivity;
import com.example.myapplication.R;
import com.example.myapplication.objects.Event;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MyEventActivity extends AppCompatActivity implements EventAdapter.OnEventClickListener {

    private RecyclerView recyclerView; // RecyclerView for displaying the list of events.
    private EventAdapter eventAdapter; // Adapter for handling and binding event data to the RecyclerView.
    private List<Event> eventList; // List to store event objects.
    private FirebaseFirestore db; // Firebase Firestore instance for database operations.



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Call the superclass method to handle activity creation.
        setContentView(R.layout.activity_my_event); // Set the XML layout for this activity.

        db = FirebaseFirestore.getInstance(); // Initialize the Firestore database instance.

        recyclerView = findViewById(R.id.myOwnEvents); // Link the RecyclerView to the layout.
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); // Set layout manager for linear arrangement of items.
        eventList = new ArrayList<>(); // Initialize the list that will hold events.
        eventAdapter = new EventAdapter(eventList, this); // Create an instance of the adapter and set the click listener.
        recyclerView.setAdapter(eventAdapter); // Attach the adapter to the RecyclerView.


        // Load Events from Firestore
        loadEventsFromFirestore();


        // Profile and Email ImageView Listeners
        ImageView profileImage = findViewById(R.id.profileImage);
        profileImage.setOnClickListener(v -> {
            Intent intent = new Intent(MyEventActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        ImageView emailImage = findViewById(R.id.emailImage);
        emailImage.setOnClickListener(v -> {
            Intent intent = new Intent(MyEventActivity.this, EmailActivity.class);
            startActivity(intent);
        });

    }

    /**
     * It initializes the local event object and loads the the UI with data from Firebase
     */
    private void loadEventsFromFirestore() {
        Intent intentFromEM = getIntent();
        String device = intentFromEM.getStringExtra("device");

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
                        Boolean geolocationEnabled = doc.getBoolean("geolocationEnabled");
                        event.setGeo(geolocationEnabled);
                        ArrayList<String> waitlist = (ArrayList<String>) doc.get("waitlist");
                        event.setWaitingList(waitlist); // set the waitlist

                        //if (event.getWaitingList().getList().contains(device)) { // only add the eventlist when the user is in the event.
                            eventList.add(event); // Add the event object to the list.
//                        }
                    }
                    eventAdapter.notifyDataSetChanged(); // Notify the adapter to refresh the RecyclerView with new data.
                }
            }
        });
    }


    /**
     * This navigates to a new activity when an activity is clicked
     * @param event
     *          The event that is being clicked
     */
    @Override
    public void onEventClick(Event event) {
        Intent intentFromEM = getIntent();
        String device = intentFromEM.getStringExtra("device");

        Intent intent =  new Intent(MyEventActivity.this, EventDetailActivity.class); // Create an intent to open the EventEditActivity.
        intent.putExtra("device",device);
        intent.putExtra("event", (Parcelable) event);
        startActivity(intent); // Start the new activity to edit the event.
    }


}
