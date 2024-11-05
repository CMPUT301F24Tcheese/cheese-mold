package com.example.myapplication.organizer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.myapplication.EventAdapter;
import com.example.myapplication.EventDetailActivity;
import com.example.myapplication.R;
import com.example.myapplication.entrant.CaptureAct;
import com.example.myapplication.entrant.NotificationActivity;
import com.example.myapplication.objects.Event;
import com.example.myapplication.objects.Facility;
import com.example.myapplication.users.UpdateProfileActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.ArrayList;


/**
 * OrganizerMainActivity is the main activity for the organizer feature of the application.
 * It allows the organizer to view and manage facilities and events.
 */
public class OrganizerMainActivity extends AppCompatActivity implements EventAdapter.OnEventClickListener{

    private FirebaseFirestore db; // Firebase Firestore database object for retrieving user data
    private TextView  facilityTextView, eventsTextView, createFacilityText,organizerMainFacilityName, organizerMainFacilityAddress, facilityEventText; // TextView to display the welcome message with the user's name
    private ImageView updateProfileImg; // ImageView to display the user's profile picture
    private Button notificationBtn, qrCodeBtn, createFacilityBtn; // Button for users to navigate to the update profile screen
    private String device;
    private FloatingActionButton fab;
    private SwipeRefreshLayout facilitySwipeRefreshLayout, eventSwipeRefreshLayout;
    private RecyclerView facilityEventsView, joinedEventsView;
    private EventAdapter facilityEventAdapter, joinedEventAdapter;
    private ArrayList<Event> facilityEventList, joinedEventList;
    private boolean facilityExist;
    private LinearLayout viewFacilityLayout, viewEventLayout, facilityDetailLayout, facilityExistLayout, facilityNotExistLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Call the parent class's onCreate method to initialize the activity

        // Initialize Firebase services
        db = FirebaseFirestore.getInstance(); // Get the instance of Firebase Firestore database

        setContentView(R.layout.activity_organizer_main); // Set the layout for the main activity screen

        // Initialize the UI elements
        viewFacilityLayout = findViewById(R.id.viewFacilityLayout);
        viewEventLayout = findViewById(R.id.viewEventsLayout);
        facilityTextView = findViewById(R.id.facilityTextView);
        eventsTextView = findViewById(R.id.eventsTextView);
        createFacilityText = findViewById(R.id.createFacilityText);
        organizerMainFacilityName = findViewById(R.id.organizerMainFacilityName);
        organizerMainFacilityAddress = findViewById(R.id.organizerMainFacilityAddress);
        facilityEventText = findViewById(R.id.facilityEventText);
        updateProfileImg = findViewById(R.id.updateProfileImg);
        notificationBtn = findViewById(R.id.notificationBtn);
        qrCodeBtn = findViewById(R.id.qrCodeBtn);
        createFacilityBtn = findViewById(R.id.createFacilityBtn);
        fab = findViewById(R.id.organizerMainAddEventFab);
        facilitySwipeRefreshLayout = findViewById(R.id.organizerMainFacilitySwipeRefreshLayout);
        eventSwipeRefreshLayout = findViewById(R.id.organizerMainEventSwipeRefreshLayout);
        facilityEventsView = findViewById(R.id.organizerMainFacilityEventView);
        joinedEventsView = findViewById(R.id.organizerMainEventView);
        facilityDetailLayout = findViewById(R.id.organizerMainFacilityLayout);
        facilityExistLayout = findViewById(R.id.facilityExistView);
        facilityNotExistLayout = findViewById(R.id.facilityNotExistView);

        facilityEventsView.setLayoutManager(new LinearLayoutManager(this));
        joinedEventsView.setLayoutManager(new LinearLayoutManager(this));

        facilityEventList = new ArrayList<>();
        joinedEventList = new ArrayList<>();

        facilityEventAdapter = new EventAdapter(facilityEventList, this);
        joinedEventAdapter = new EventAdapter(joinedEventList, this);

        facilityEventsView.setAdapter(facilityEventAdapter);
        joinedEventsView.setAdapter(joinedEventAdapter);



        device = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        getData(device);
        showFacilityView();

        // Refreshes the facility view on swipe
        facilitySwipeRefreshLayout.setOnRefreshListener(() -> {
            refreshData(facilityEventList, facilityEventAdapter, true);
        });

        // Refreshes the event view on swipe
        eventSwipeRefreshLayout.setOnRefreshListener(() -> {
            refreshData(joinedEventList, joinedEventAdapter, false);
        });

        // Set a click listener on the update profile button
        updateProfileImg.setOnClickListener(view -> {
            startActivity(new Intent(OrganizerMainActivity.this, UpdateProfileActivity.class)); // Navigate to the update profile screen
        });

        notificationBtn.setOnClickListener(view -> {
            startActivity(new Intent(OrganizerMainActivity.this, NotificationActivity.class));
        });

        qrCodeBtn.setOnClickListener(view -> {
            ScanOptions options = new ScanOptions();
            options.setPrompt("Scan QR code");
            options.setOrientationLocked(true);
            options.setCaptureActivity(CaptureAct.class);

            barcodeLauncher.launch(options);
        });

        facilityDetailLayout.setOnClickListener(view -> {
            startActivity(new Intent(OrganizerMainActivity.this, EditFacilityActivity.class));
        });

        // Option to view facility
        facilityTextView.setOnClickListener(view -> {
            showFacilityView();
        });

        // Option to view joined events
        eventsTextView.setOnClickListener(view -> {
            showEventsView();
        });

    }

    /**
     * Retrieves user data from Firestore and loads it into the activity UI components
     * @param device The unique ID of the logged-in user
     */
    private void getData(String device) {
        // Access the "users" collection in Firestore and get the document corresponding to the userId
        db.collection("users").document(device).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) { // Check if the data retrieval task was successful
                        DocumentSnapshot document = task.getResult(); // Get the document snapshot from Firestore

                        if(document.exists()) { // Verify that the document exists
                            String profilePicUrl = document.getString("Profile Picture"); // Retrieve the URL of the user's profile picture

                            // Load the user's profile picture using Glide, a third-party image loading library
                            Glide.with(OrganizerMainActivity.this)
                                    .load(profilePicUrl) // Load the image from the URL obtained from Firestore
                                    .placeholder(R.drawable.baseline_person_outline_24) // Display a default placeholder while the image loads
                                    .error(R.drawable.baseline_person_outline_24) // Show a default image if loading the picture fails
                                    .into(updateProfileImg); // Set the loaded image into the ImageView
                        } else {
                            Log.d("MainActivity", "No such document"); // Log a message if the document does not exist
                        }
                    } else {
                        Log.w("MainActivity", "Error getting documents.", task.getException()); // Log a warning if there was an error retrieving the document
                    }
                });
    }


    /**
     * Displays the facility view based on whether the facility exists or not.
     */
    private void showFacilityView() {
        viewEventLayout.setVisibility(View.GONE);
        viewFacilityLayout.setVisibility(View.VISIBLE);

        checkFacilityExist(() -> {
            // Update the UI based on the facility existence check result
            if (!facilityExist) {
                facilityExistLayout.setVisibility(View.GONE);
                facilityNotExistLayout.setVisibility(View.VISIBLE);
                fab.setVisibility(View.GONE);

                createFacilityBtn.setOnClickListener(view -> {
                    startActivity(new Intent(OrganizerMainActivity.this, AddFacilityActivity.class));
                });

            } else {
                facilityNotExistLayout.setVisibility(View.GONE);
                facilityExistLayout.setVisibility(View.VISIBLE);
                fab.setVisibility(View.VISIBLE);

                fab.setOnClickListener(view -> {
                    startActivity(new Intent(OrganizerMainActivity.this, AddEventActivity.class));
                });
                getFacilityData(); // Load the facility data
            }
        });
    }

    /**
     * Displays the events view for the organizer.
     */
    private void showEventsView() {
        viewFacilityLayout.setVisibility(View.GONE);
        viewEventLayout.setVisibility(View.VISIBLE);
        loadJoinedEvents(); // Load the events the organizer has joined
    }


    /**
     * Interface for the callback after checking facility existence.
     */
    interface OnFacilityExistCheckCompleteListener {
        void onComplete();
    }


    /**
     * Checks if a facility exists for the current user.
     *
     * @param callback Callback to be executed after the check is complete.
     */
    private void checkFacilityExist(OnFacilityExistCheckCompleteListener callback) {
        db.collection("Facilities").document(device).get()
                .addOnSuccessListener(documentSnapshot -> {
                    facilityExist = documentSnapshot.exists();
                    callback.onComplete();
                })
                .addOnFailureListener(e -> {
                    Log.w("OrganizerMainActivityFacilityExist", "Error getting document: " + e.getMessage());
                    callback.onComplete();
                });
    }


    /**
     * Retrieves the facility data from Firestore and updates the UI.
     */
    private void getFacilityData() {
        db.collection("Facilities").document(device).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Facility facility = documentSnapshot.toObject(Facility.class);
                        if (facility != null) {
                            organizerMainFacilityName.setText(facility.getName());
                            organizerMainFacilityAddress.setText(facility.getAdress());
                            loadFacilityEvents();
                        }
                    }
                });
    }


    /**
     * Loads events associated with the facility from Firestore.
     */
    private void loadFacilityEvents() {
        facilityEventList.clear();
        db.collection("events").whereEqualTo("creatorID", device).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                            Event event = doc.toObject(Event.class);
                            if (event != null) {
                                event.setId(doc.getId());
                                facilityEventList.add(event);
                            }
                        }
                        facilityEventAdapter.notifyDataSetChanged();
                    } else {
                        Log.d("Load Facility Events", "No events found for this creator.");
                    }
                }).addOnFailureListener(e -> Log.e("Event Data", "Error fetching events", e));
    }


    /**
     * Loads events that the user has joined.
     */
    private void loadJoinedEvents() {
        db.collection("users").document(device).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        ArrayList<String> eventListFromDb = (ArrayList<String>) documentSnapshot.get("Event List");
                        if (eventListFromDb != null && !eventListFromDb.isEmpty()) {
                            for (String event : eventListFromDb) {
                                Log.d("getEventsFromFirestore", event);
                                loadEvents(event);
                            }
                        } else {
                            Log.d("getEventsFromFirestore", "No events found in the user's event list.");
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("getEventsFromFirestore", "Error fetching user event list: " + e.getMessage());
                });
    }


    /**
     * Loads a specific event from Firestore and updates the joined event list.
     *
     * @param eventId The ID of the event to load.
     */
    private void loadEvents(String eventId) {
        joinedEventList.clear();
        db.collection("events").document(eventId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    Event event = documentSnapshot.toObject(Event.class);
                    if (event != null) {
                        event.setId(documentSnapshot.getId());
                        joinedEventList.add(event);
                        joinedEventAdapter.notifyDataSetChanged();
                    }
                });
    }


    /**
     * Refreshes the data displayed in the UI.
     *
     * @param eventList The list of events to refresh.
     * @param adapter   The adapter associated with the RecyclerView.
     * @param forFacility Whether the refresh is for facility events or joined events.
     */
    private void refreshData(ArrayList<Event> eventList, EventAdapter adapter, boolean forFacility) {
        eventList.clear();
        adapter.notifyDataSetChanged();
        if (forFacility) {
            loadFacilityEvents();
            facilitySwipeRefreshLayout.setRefreshing(false);
        } else {
            loadJoinedEvents();
            eventSwipeRefreshLayout.setRefreshing(false);
        }

    }

    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() == null) {
            Toast.makeText(OrganizerMainActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
        } else {
            String scannedUrl = result.getContents();
            if (scannedUrl.startsWith("myapp://event")) {
                Toast.makeText(OrganizerMainActivity.this, "Scan Successful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(scannedUrl));
                startActivity(intent);
            }
        }
    });


    @Override
    public void onEventClick(Event event) {
        Intent intentFromEM = getIntent();
        String device = intentFromEM.getStringExtra("device");

        Intent intent =  new Intent(OrganizerMainActivity.this, EventDetailActivity.class); // Create an intent to open the EventEditActivity.
        intent.putExtra("device",device);
        intent.putExtra("event", (Parcelable) event);
        startActivity(intent);
    }
}
