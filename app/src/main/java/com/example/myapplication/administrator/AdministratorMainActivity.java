/**
 * Main Activity for the administrators
 * Used by the Administrator Only
 * Functionality to act as the home page for administrators
 * @author Noah Vincent
 * @Issue Facility browse button crashes the app
 */

package com.example.myapplication.administrator;

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
import com.example.myapplication.EntrantEventDetailActivity;
import com.example.myapplication.EventAdapter;
import com.example.myapplication.R;
import com.example.myapplication.controllers.RoleActivityController;
import com.example.myapplication.entrant.CaptureAct;
import com.example.myapplication.entrant.NotificationActivity;
import com.example.myapplication.objects.Event;
import com.example.myapplication.organizer.AddEventActivity;
import com.example.myapplication.organizer.AddFacilityActivity;
import com.example.myapplication.organizer.EventEditActivity;
import com.example.myapplication.organizer.OrganizerMainActivity;
import com.example.myapplication.users.MainActivity;
import com.example.myapplication.users.UpdateProfileActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.ArrayList;

public class AdministratorMainActivity extends AppCompatActivity implements EventAdapter.OnEventClickListener {
    private FirebaseFirestore db; // Firebase Firestore database object for retrieving user data
    private TextView browseFacilitiesBtn;
    private TextView browseEventsBtn;
    private TextView browseProfilesBtn;
    private TextView browseImagesBtn;
    private TextView browseQRCodeBtn;
    private Button notificationBtn;
    private Button qrCodeBtn;
    private Button createFacilityBtn;
    private TextView browseTextView, facilityTextView, eventsTextView;
    private LinearLayout viewBrowseButtonsLayout, viewFacilityLayout, viewEventsLayout;
    private RecyclerView facilityEventsView, joinedEventsView;
    private EventAdapter facilityEventAdapter, joinedEventAdapter;
    private String device;
    private boolean facilityExist;
    private boolean isFacilityView;
    private ArrayList<Event> facilityEventList, joinedEventList;
    private LinearLayout facilityExistLayout, facilityNotExistLayout;
    private FloatingActionButton fab;
    private RoleActivityController roleActivityController;
    private TextView organizerMainFacilityName, organizerMainFacilityAddress;

    /**
     * onCreate function for displaying home page information
     * @param savedInstanceState saved instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Call the parent class's onCreate method to initialize the activity

        // Initialize Firebase services
        db = FirebaseFirestore.getInstance(); // Get the instance of Firebase Firestore database

        setContentView(R.layout.acitvity_administrator_main); // Set the layout for the main activity screen
        roleActivityController = new RoleActivityController(this);

        // initialize the browse button UI elements
        browseFacilitiesBtn = findViewById(R.id.browseFacilitiesBtn);
        browseEventsBtn = findViewById(R.id.browseEventsBtn);
        browseProfilesBtn = findViewById(R.id.browseProfilesBtn);
        browseImagesBtn = findViewById(R.id.browseImagesBtn);
        browseQRCodeBtn = findViewById(R.id.browseQRcodesBtn);
        notificationBtn = findViewById(R.id.notificationBtn);
        qrCodeBtn = findViewById(R.id.qrCodeBtn);
        browseTextView = findViewById(R.id.browseTextView);
        facilityTextView = findViewById(R.id.facilityTextView);
        eventsTextView = findViewById(R.id.eventsTextView);
        viewBrowseButtonsLayout = findViewById(R.id.viewBrowseButtonsLayout);
        viewEventsLayout = findViewById(R.id.viewEventsLayout);
        viewFacilityLayout = findViewById(R.id.viewFacilityLayout);
        createFacilityBtn = findViewById(R.id.createFacilityBtn);
        facilityEventsView = findViewById(R.id.organizerMainFacilityEventView);
        joinedEventsView = findViewById(R.id.organizerMainEventView);
        facilityExistLayout = findViewById(R.id.facilityExistView);
        facilityNotExistLayout = findViewById(R.id.facilityNotExistView);
        fab = findViewById(R.id.organizerMainAddEventFab);
        organizerMainFacilityName = findViewById(R.id.organizerMainFacilityName);
        organizerMainFacilityAddress = findViewById(R.id.organizerMainFacilityAddress);


        facilityEventsView.setLayoutManager(new LinearLayoutManager(this));
        joinedEventsView.setLayoutManager(new LinearLayoutManager(this));

        facilityEventList = new ArrayList<>();
        joinedEventList = new ArrayList<>();

        facilityEventAdapter = new EventAdapter(facilityEventList, this);
        joinedEventAdapter = new EventAdapter(joinedEventList, this);

        facilityEventsView.setAdapter(facilityEventAdapter);
        joinedEventsView.setAdapter(joinedEventAdapter);

        isFacilityView = false;

        device = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        notificationBtn.setOnClickListener(view -> {
            startActivity(new Intent(AdministratorMainActivity.this, NotificationActivity.class));
        });

        qrCodeBtn.setOnClickListener(view -> {
            ScanOptions options = new ScanOptions();
            options.setPrompt("Scan QR code");
            options.setOrientationLocked(true);
            options.setCaptureActivity(CaptureAct.class);

            barcodeLauncher.launch(options);
        });

        browseTextView.setOnClickListener(view -> {
            isFacilityView = false;
            showBrowseView();
        });

        facilityTextView.setOnClickListener(view -> {
            isFacilityView = true;
            showFacilityView();
        });

        eventsTextView.setOnClickListener(view -> {
            isFacilityView = false;
            showEventsView();
        });

        // click listener to browse facilites
        browseFacilitiesBtn.setOnClickListener(view -> {
            startActivity(new Intent(AdministratorMainActivity.this, AdminBrowseFacilities.class));
        });

        // click listener to browse events
        browseEventsBtn.setOnClickListener(view -> {
            startActivity(new Intent(AdministratorMainActivity.this, AdminBrowseEvents.class));
        });

        // click listener to browse profiles
        browseProfilesBtn.setOnClickListener(view -> {
            startActivity(new Intent(AdministratorMainActivity.this, AdminBrowseUsers.class));
        });

        // click listener to browse images
        browseImagesBtn.setOnClickListener(view -> {
            startActivity(new Intent(AdministratorMainActivity.this, AdminBrowseImages.class));
        });

        // click listner to browse QRCodes
        browseQRCodeBtn.setOnClickListener(view -> {
            startActivity(new Intent(AdministratorMainActivity.this, AdminBrowseQRCodes.class));
        });

    }

    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() == null) {
            Toast.makeText(AdministratorMainActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
        } else {
            String scannedUrl = result.getContents();
            if (scannedUrl.startsWith("myapp://event")) {
                Toast.makeText(AdministratorMainActivity.this, "Scan Successful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(scannedUrl));
                startActivity(intent);
            }
        }
    });

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
    private void checkFacilityExist(AdministratorMainActivity.OnFacilityExistCheckCompleteListener callback) {
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
     * Displays the facility view based on whether the facility exists or not.
     */
    private void showFacilityView() {
        viewEventsLayout.setVisibility(View.GONE);
        viewBrowseButtonsLayout.setVisibility(View.GONE);
        viewFacilityLayout.setVisibility(View.VISIBLE);

        checkFacilityExist(() -> {
            // Update the UI based on the facility existence check result
            if (!facilityExist) {
                facilityExistLayout.setVisibility(View.GONE);
                facilityNotExistLayout.setVisibility(View.VISIBLE);
                fab.setVisibility(View.GONE);

                createFacilityBtn.setOnClickListener(view -> {
                    startActivity(new Intent(AdministratorMainActivity.this, AddFacilityActivity.class));
                });

            } else {
                facilityNotExistLayout.setVisibility(View.GONE);
                facilityExistLayout.setVisibility(View.VISIBLE);
                fab.setVisibility(View.VISIBLE);

                fab.setOnClickListener(view -> {
                    startActivity(new Intent(AdministratorMainActivity.this, AddEventActivity.class));
                });
                roleActivityController.getFacilityData(device, organizerMainFacilityName, organizerMainFacilityAddress, facilityEventAdapter, facilityEventList); // Load the facility data
            }
        });
    }

    /**
     * Displays the events view for the organizer.
     */
    private void showEventsView() {
        viewFacilityLayout.setVisibility(View.GONE);
        viewBrowseButtonsLayout.setVisibility(View.GONE);
        viewEventsLayout.setVisibility(View.VISIBLE);
        roleActivityController.loadJoinedEvents(device, joinedEventAdapter, joinedEventList); // Load the events the organizer has joined
    }

    /**
     * Displays the browsing functionality for the Admin over base functionality
     */
    private void showBrowseView() {
        viewFacilityLayout.setVisibility(View.GONE);
        viewEventsLayout.setVisibility(View.GONE);
        viewBrowseButtonsLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onEventClick(Event event) {
        Intent intent = isFacilityView ?
                new Intent(AdministratorMainActivity.this, EventEditActivity.class) :
                new Intent(AdministratorMainActivity.this, EntrantEventDetailActivity.class);

        intent.putExtra("event_id", event.getId());
        intent.putExtra("event", (Parcelable) event);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isFacilityView) {
            showFacilityView();
        } else {
            roleActivityController.loadJoinedEvents(device, joinedEventAdapter, joinedEventList);
        }

    }

}
