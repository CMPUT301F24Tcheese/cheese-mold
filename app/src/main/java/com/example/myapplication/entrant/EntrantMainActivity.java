package com.example.myapplication.entrant;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.myapplication.EntrantEventDetailActivity;
import com.example.myapplication.EventAdapter;
import com.example.myapplication.EventDetailActivity;
import com.example.myapplication.R;
import com.example.myapplication.objects.Event;
import com.example.myapplication.users.UpdateProfileActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.ArrayList;


/**
 * This is the main activity of entrants. The entrant will
 * navigate the entire app starting from this point
 */
public class EntrantMainActivity extends AppCompatActivity implements EventAdapter.OnEventClickListener{

    private FirebaseFirestore db; // Firebase Firestore database object for retrieving user data
    private TextView welcomeText; // TextView to display the welcome message with the user's name
    private Button notificationBtn;
    private Button qrCodeBtn;
    private ImageView updateProfile;
    private RecyclerView eventsView;
    private EventAdapter eventAdapter;
    private ArrayList<Event> eventList;
    private String device;
    private SwipeRefreshLayout swipeRefreshLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Call the parent class's onCreate method to initialize the activity

        // Initialize Firebase services
        db = FirebaseFirestore.getInstance(); // Get the instance of Firebase Firestore database

        setContentView(R.layout.activity_entrant_main); // Set the layout for the main activity screen

        // Initialize the UI elements
        welcomeText = findViewById(R.id.welcomeTextView); // Find the TextView by its ID to display the welcome message
        notificationBtn = findViewById(R.id.notificationBtn);
        qrCodeBtn = findViewById(R.id.qrCodeBtn);
        updateProfile = findViewById(R.id.updateProfileImg);
        eventsView = findViewById(R.id.entrantMainEventView);
        swipeRefreshLayout = findViewById(R.id.entrantMainswipeRefreshLayout);
        eventsView.setLayoutManager(new LinearLayoutManager(this));
        eventList = new ArrayList<>();
        eventAdapter = new EventAdapter(eventList, this);
        eventsView.setAdapter(eventAdapter);


        device = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        getData(device);


        swipeRefreshLayout.setOnRefreshListener(() -> {
            refreshData();
        });

        // Set a click listener on the update profile button
        updateProfile.setOnClickListener(view -> {
            startActivity(new Intent(EntrantMainActivity.this, UpdateProfileActivity.class)); // Navigate to the update profile screen
        });

        qrCodeBtn.setOnClickListener(view -> {
            ScanOptions options = new ScanOptions();
            options.setPrompt("Scan QR code");
            options.setOrientationLocked(true);
            options.setCaptureActivity(CaptureAct.class);

            barcodeLauncher.launch(options);
        });

        notificationBtn.setOnClickListener(view -> {
            startActivity(new Intent(EntrantMainActivity.this, NotificationActivity.class));
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
                            String firstname = document.getString("Firstname"); // Retrieve the user's first name from the document
                            String lastname = document.getString("Lastname"); // Retrieve the user's last name from the document
                            String profilePicUrl = document.getString("Profile Picture"); // Retrieve the URL of the user's profile picture
                            welcomeText.setText("Welcome " + firstname + " " + lastname); // Set the welcome text with the user's full name

                            setImageInView(updateProfile, profilePicUrl);

                        } else {
                            Log.d("MainActivity", "No such document"); // Log a message if the document does not exist
                        }
                    } else {
                        Log.w("MainActivity", "Error getting documents.", task.getException()); // Log a warning if there was an error retrieving the document
                    }
                });
    }

    /**
     * This method uses Glide to load image into ImageView
     * directly using a link
     * @param view the ImageView to load into
     * @param picUrl the link of the image
     */
    private void setImageInView(ImageView view, String picUrl) {
        // Load the user's profile picture using Glide, a third-party image loading library
        Glide.with(EntrantMainActivity.this)
                .load(picUrl) // Load the image from the URL obtained from Firestore
                .placeholder(R.drawable.baseline_person_outline_24) // Display a default placeholder while the image loads
                .error(R.drawable.baseline_person_outline_24) // Show a default image if loading the picture fails
                .into(view); // Set the loaded image into the ImageView
    }


    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() == null) {
            Toast.makeText(EntrantMainActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
        } else {
            String scannedUrl = result.getContents();
            if (scannedUrl.startsWith("myapp://event")) {
                Toast.makeText(EntrantMainActivity.this, "Scan Successful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(scannedUrl));
                startActivity(intent);
            }
        }
    });


    /**
     * Retrieves the list of events associated with the user from Firestore.
     */
    private void getEventsFromFirestore() {
        eventList.clear();
        eventAdapter.notifyDataSetChanged();
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
     * Loads the event details for a specific event ID from Firestore and adds it to the event list.
     * @param eventId The ID of the event to be loaded.
     */
    private void loadEvents(String eventId) {
        db.collection("events").document(eventId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    Event event = documentSnapshot.toObject(Event.class);
                    if (event != null) {
                        event.setId(documentSnapshot.getId());
                        eventList.add(event);
                        eventAdapter.notifyDataSetChanged();
                    }
                });
    }


    /**
     * Handles click events on individual event items in the RecyclerView.
     * @param event The event that was clicked.
     */
    @Override
    public void onEventClick(Event event) {
        Intent intentFromEM = getIntent();
        String device = intentFromEM.getStringExtra("device");

        Intent intent =  new Intent(EntrantMainActivity.this, EntrantEventDetailActivity.class); // Create an intent to open the EventEditActivity.
        intent.putExtra("device",device);
        intent.putExtra("event", (Parcelable) event);
        startActivity(intent);
    }

    /**
     * Refreshes the eventList when the user swipes down
     */
    private void refreshData() {
        eventList.clear();
        eventAdapter.notifyDataSetChanged();
        getEventsFromFirestore();
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData(device);
        getEventsFromFirestore();
    }
}
