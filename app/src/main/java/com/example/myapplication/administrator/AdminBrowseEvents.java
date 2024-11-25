/**
 * Activity for browsing Events
 * Used by the Administrator Only
 * @author Noah Vincent
 */

package com.example.myapplication.administrator;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.controllers.NotificationController;
import com.example.myapplication.controllers.RoleActivityController;
import com.example.myapplication.entrant.CaptureAct;
import com.example.myapplication.entrant.NotificationActivity;
import com.example.myapplication.objects.Event;
import com.example.myapplication.objects.EventArrayAdapter;
import com.example.myapplication.objects.Facility;
import com.example.myapplication.objects.FacilityArrayAdapter;
import com.example.myapplication.objects.Users;
import com.example.myapplication.users.UpdateProfileActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.ArrayList;

public class AdminBrowseEvents extends AppCompatActivity {

    private FirebaseFirestore db;
    private Button back;
    private TextView header;
    private ListView eventList;
    private EventArrayAdapter eventAdapter;
    private ArrayList<Event> dataList;
    private Button notificationBtn;
    private Button qrCodeBtn;
    private ImageView updateProfileImg;
    private NotificationController notificationController;
    private RoleActivityController roleActivityController;
    private String device;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_browse);
        db = FirebaseFirestore.getInstance();

        roleActivityController = new RoleActivityController(this);
        notificationController = new NotificationController(this);

        back = findViewById(R.id.back_button);
        notificationBtn = findViewById(R.id.notificationBtn);
        qrCodeBtn = findViewById(R.id.qrCodeBtn);
        updateProfileImg = findViewById(R.id.updateProfileImg);

        header = findViewById(R.id.browseHeader);
        header.setText("EVENTS");

        dataList = new ArrayList<Event>();

        eventList = findViewById(R.id.contentListView);
        eventAdapter = new EventArrayAdapter(this, dataList);
        eventList.setAdapter(eventAdapter);

        device = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        notificationController.startListening(device);
        roleActivityController.getData(device, updateProfileImg);

        notificationBtn.setOnClickListener(view -> {
            startActivity(new Intent(AdminBrowseEvents.this, NotificationActivity.class));
        });

        // Set a click listener on the update profile button
        updateProfileImg.setOnClickListener(view -> {
            startActivity(new Intent(AdminBrowseEvents.this, UpdateProfileActivity.class)); // Navigate to the update profile screen
        });

        qrCodeBtn.setOnClickListener(view -> {
            ScanOptions options = new ScanOptions();
            options.setPrompt("Scan QR code");
            options.setOrientationLocked(true);
            options.setCaptureActivity(CaptureAct.class);
            options.setBeepEnabled(false);

            barcodeLauncher.launch(options);
        });

        /**
         * must be set inside the onCreate, setting up this type of launcher makes it possible
         * for the delete method to be called properly from a few activities away.
         */
        ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        dataList.clear();
                        eventAdapter.clear();
                        // searching firebase for all registered users
                        db.collection("events").get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                String id = document.getId();
                                                String title = document.getString("name");
                                                String description = document.getString("description");
                                                String posterURL = document.getString("posterUrl");
                                                String QRcode = document.getString("qrCodeUrl");
                                                String user = document.getString("creatorID");
                                                Event thing = new Event(id, title , description, posterURL, QRcode, user);
                                                dataList.add(thing);
                                                eventAdapter.notifyDataSetChanged();
                                            }
                                        } else {
                                            Log.d("AdminBrowseEvents", "Error getting documents: ", task.getException());
                                        }
                                    }
                                });
                    }
                });

        db.collection("events").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String id = document.getId();
                                String title = document.getString("name");
                                String description = document.getString("description");
                                String posterURL = document.getString("posterUrl");
                                String QRcode = document.getString("qrCodeUrl");
                                String user = document.getString("creatorID");
                                Event thing = new Event(id, title , description, posterURL, QRcode, user);
                                dataList.add(thing);
                                eventAdapter.notifyDataSetChanged();
                            }
                        } else {
                            Log.d("AdminBrowseEvents", "Error getting documents: ", task.getException());
                        }
                    }
                });

        back.setOnClickListener(view -> {
            finish();
        });

        eventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Event event = dataList.get(i);
                Intent intent = new Intent(AdminBrowseEvents.this, AdminViewEvent.class);
                intent.putExtra("event", (Parcelable) event);
                someActivityResultLauncher.launch(intent);
            }
        });

    }

    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() == null) {
            Toast.makeText(AdminBrowseEvents.this, "Cancelled", Toast.LENGTH_SHORT).show();
        } else {
            String scannedUrl = result.getContents();
            if (scannedUrl.startsWith("myapp://event")) {
                Toast.makeText(AdminBrowseEvents.this, "Scan Successful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(scannedUrl));
                startActivity(intent);
            }
        }
    });

}
