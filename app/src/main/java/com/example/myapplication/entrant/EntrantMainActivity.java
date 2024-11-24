package com.example.myapplication.entrant;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.myapplication.EntrantEventDetailActivity;
import com.example.myapplication.EventAdapter;
import com.example.myapplication.EventDetailActivity;
import com.example.myapplication.R;
import com.example.myapplication.controllers.NotificationController;
import com.example.myapplication.controllers.RoleActivityController;
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

    private Button notificationBtn;
    private Button qrCodeBtn;
    private ImageView updateProfile;
    private RecyclerView eventsView;
    private EventAdapter eventAdapter;
    private ArrayList<Event> eventList;
    private String device;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RoleActivityController roleActivityController;
    private NotificationController notificationController;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Call the parent class's onCreate method to initialize the activity

        // Request POST_NOTIFICATIONS permission if needed (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }



        setContentView(R.layout.activity_entrant_main); // Set the layout for the main activity screen
        roleActivityController = new RoleActivityController(this);
        notificationController = new NotificationController(this);

        // Initialize the UI elements
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

        roleActivityController.getData(device, updateProfile);
        notificationController.startListening(device);


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
            options.setBeepEnabled(false);

            barcodeLauncher.launch(options);
        });

        notificationBtn.setOnClickListener(view -> {
            startActivity(new Intent(EntrantMainActivity.this, NotificationActivity.class));
        });

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

        Log.d("Local","Lottery List Before: " + event.getLottery());
        startActivity(intent);
    }

    /**
     * Refreshes the eventList when the user swipes down
     */
    private void refreshData() {
        eventList.clear();
        eventAdapter.notifyDataSetChanged();
        roleActivityController.loadJoinedEvents(device, eventAdapter, eventList);
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        roleActivityController.getData(device, updateProfile);
        roleActivityController.loadJoinedEvents(device, eventAdapter, eventList);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Stop listening to avoid memory leaks
        if (notificationController != null) {
            notificationController.stopListening();
        }
    }
}
