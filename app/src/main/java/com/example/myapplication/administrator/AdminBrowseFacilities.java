/**
 * Activity for browsing facilities
 * Used by the Administrator Only
 * @author Noah Vincent
 * @Issue Facility browse button crashes the app
 */

package com.example.myapplication.administrator;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.administrator.fragments.DeleteFacilityFragment;
import com.example.myapplication.controllers.NotificationController;
import com.example.myapplication.controllers.RoleActivityController;
import com.example.myapplication.entrant.CaptureAct;
import com.example.myapplication.entrant.NotificationActivity;
import com.example.myapplication.objects.Facility;
import com.example.myapplication.objects.FacilityArrayAdapter;
import com.example.myapplication.users.UpdateProfileActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.ArrayList;

public class AdminBrowseFacilities extends AppCompatActivity implements DeleteFacilityFragment.DeleteFacilityDialogueListener {
    private FirebaseFirestore db;
    private Button back;
    private TextView header;
    private ListView facilityList;
    private FacilityArrayAdapter facilityAdapter;
    private ArrayList<Facility> dataList;
    private Button notificationBtn;
    private Button qrCodeBtn;
    private ImageView updateProfileImg;
    private NotificationController notificationController;
    private RoleActivityController roleActivityController;
    private String device;



    /**
     * Method to delete facilities that violate app policy
     * @param facility the facility being deleted
     */
    @Override
    public void DeleteFacility(Facility facility) {
        db.collection("Facilities").document(facility.getId()).delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        facilityAdapter.remove(facility);
                                        facilityAdapter.notifyDataSetChanged();
                                    }
                                });
    }

    /**
     * method to delete events that are within the event that is deleted
     * @param id facility id
     */
    @Override
    public void DeleteEvents(String id) {
        db.collection("events").whereEqualTo("creatorID", id).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                String eventId = document.getId();
                                RemoveEvents(eventId);
                                DocumentReference documentReference = document.getReference();
                                documentReference.delete();
                            }
                        }
                    }
                });
    }

    /**
     * method to remove the events from user lists
     * @param id event id
     */
    public void RemoveEvents(String id) {
        db.collection("users").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                ArrayList<String> list = (ArrayList<String>) document.get("Event List");
                                if (list != null) {
                                    if (list.contains(id)) {
                                        list.remove(id);
                                        DocumentReference documentReference = document.getReference();
                                        documentReference.update("Event List", list);
                                    }
                                }
                            }
                        }
                    }
                });
    }

    /**
     * onCreate function for displaying Facility information
     * @param savedInstanceState saved instance
     */
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
        header.setText("FACILITIES");

        dataList = new ArrayList<Facility>();

        facilityList = findViewById(R.id.contentListView);
        facilityAdapter = new FacilityArrayAdapter(this, dataList);
        facilityList.setAdapter(facilityAdapter);

        device = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        notificationController.startListening(device);
        roleActivityController.getData(device, updateProfileImg);

        notificationBtn.setOnClickListener(view -> {
            startActivity(new Intent(AdminBrowseFacilities.this, NotificationActivity.class));
        });

        // Set a click listener on the update profile button
        updateProfileImg.setOnClickListener(view -> {
            startActivity(new Intent(AdminBrowseFacilities.this, UpdateProfileActivity.class)); // Navigate to the update profile screen
        });

        qrCodeBtn.setOnClickListener(view -> {
            ScanOptions options = new ScanOptions();
            options.setPrompt("Scan QR code");
            options.setOrientationLocked(true);
            options.setCaptureActivity(CaptureAct.class);
            options.setBeepEnabled(false);

            barcodeLauncher.launch(options);
        });

        // searching firebase to get all existing facility information
        db.collection("Facilities").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String id = document.getString("id");
                                String name = document.getString("name");
                                String description = document.getString("description");
                                String street = document.getString("street");
                                String city = document.getString("city");
                                String province = document.getString("province");
                                Facility thing = new Facility(id, name, description, street, city, province);
                                dataList.add(thing);
                                facilityAdapter.notifyDataSetChanged();
                            }
                        } else {
                            Log.d("AdminBrowseFacilities", "Error getting documents: ", task.getException());
                        }
                    }
                });

        back.setOnClickListener(view -> {
            finish();
        });

        facilityList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Facility facility = dataList.get(i);
                new DeleteFacilityFragment(facility).show(getSupportFragmentManager(), "Delete Facility");
                return false;
            }
        });
    }

    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() == null) {
            Toast.makeText(AdminBrowseFacilities.this, "Cancelled", Toast.LENGTH_SHORT).show();
        } else {
            String scannedUrl = result.getContents();
            if (scannedUrl.startsWith("myapp://event")) {
                Toast.makeText(AdminBrowseFacilities.this, "Scan Successful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(scannedUrl));
                startActivity(intent);
            }
        }
    });


}
