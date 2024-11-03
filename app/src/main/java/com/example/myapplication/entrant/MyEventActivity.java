package com.example.myapplication.entrant;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.EmailActivity;
import com.example.myapplication.EventActivity;
import com.example.myapplication.EventAdapter;
import com.example.myapplication.EventDetailActivity;
import com.example.myapplication.ProfileActivity;
import com.example.myapplication.R;
import com.example.myapplication.entrant.MyEventActivity;
import com.example.myapplication.objects.Event;
import com.example.myapplication.objects.Users;
import com.example.myapplication.objects.WaitingList;
import com.example.myapplication.organizer.AddEventActivity;
import com.example.myapplication.organizer.AddFacilityActivity;
import com.example.myapplication.organizer.EventEditActivity;
import com.example.myapplication.users.UpdateProfileActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MyEventActivity extends AppCompatActivity implements EventAdapter.OnEventClickListener {

    private RecyclerView recyclerView; // RecyclerView for displaying the list of events.
    private EventAdapter eventAdapter; // Adapter for handling and binding event data to the RecyclerView.
    private List<Event> eventList; // List to store event objects.
    private FirebaseFirestore db; // Firebase Firestore instance for database operations.
    private Button qrCodeBtn;
    private ImageView profileImage;

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
        qrCodeBtn = findViewById(R.id.qrCodeBtn2);

        // Load Events from Firestore
        Intent intentFromEM = getIntent();
        String device = intentFromEM.getStringExtra("device");
        loadEventsFromFirestore();
        getData(device);


        // Profile and Email ImageView Listeners
        profileImage = findViewById(R.id.updateProfileImg2);
        profileImage.setOnClickListener(v -> {
            Intent intent = new Intent(MyEventActivity.this, UpdateProfileActivity.class);
            startActivity(intent);
        });

        ImageView emailImage = findViewById(R.id.emailImage);
        emailImage.setOnClickListener(v -> {
            Intent intent = new Intent(MyEventActivity.this, NotificationActivity.class);
            startActivity(intent);
        });

        qrCodeBtn.setOnClickListener(view -> {
            ScanOptions options = new ScanOptions();
            options.setPrompt("Scan QR code");
            options.setBeepEnabled(true);
            options.setOrientationLocked(true);
            options.setCaptureActivity(CaptureAct.class);

            barcodeLauncher.launch(options);
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
                        event.setWaitingList(new WaitingList(waitlist)); // set the waitlist

                        if (event.getWaitingList().getList().contains(device)) { // only add the eventlist when the user is in the event.
                            eventList.add(event); // Add the event object to the list.
                        }
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
        intent.putExtra("event",event);
        startActivity(intent); // Start the new activity to edit the event.
    }

    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() == null) {
            Toast.makeText(MyEventActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MyEventActivity.this, "Scanned: " + result.getContents(), Toast.LENGTH_SHORT).show();
        }
    });

    private void getData(String device) {
        // Access the "users" collection in Firestore and get the document corresponding to the userId
        db.collection("users").document(device).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) { // Check if the data retrieval task was successful
                        DocumentSnapshot document = task.getResult(); // Get the document snapshot from Firestore

                        if(document.exists()) { // Verify that the document exists
                            String profilePicUrl = document.getString("Profile Picture"); // Retrieve the URL of the user's profile picture


                            setImageInView(profileImage, profilePicUrl);


                        } else {
                            Log.d("MainActivity", "No such document"); // Log a message if the document does not exist
                        }
                    } else {
                        Log.w("MainActivity", "Error getting documents.", task.getException()); // Log a warning if there was an error retrieving the document
                    }
                });
    }

    private void setImageInView(ImageView view, String picUrl) {
        // Load the user's profile picture using Glide, a third-party image loading library
        Glide.with(MyEventActivity.this)
                .load(picUrl) // Load the image from the URL obtained from Firestore
                .placeholder(R.drawable.baseline_person_outline_24) // Display a default placeholder while the image loads
                .error(R.drawable.baseline_person_outline_24) // Show a default image if loading the picture fails
                .into(view); // Set the loaded image into the ImageView
    }

}
