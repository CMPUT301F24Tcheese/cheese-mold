/**
 * The activity for the Browsing images
 * Used by Administrators only
 * @author Noah Vincent
 */
package com.example.myapplication.administrator;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.administrator.ImageArrayAdapter;
import com.example.myapplication.administrator.fragments.DeletePosterFragment;
import com.example.myapplication.administrator.fragments.DeleteProfilePicFragment;
import com.example.myapplication.administrator.fragments.DeleteQRCodeFragment;
import com.example.myapplication.controllers.NotificationController;
import com.example.myapplication.controllers.RoleActivityController;
import com.example.myapplication.entrant.CaptureAct;
import com.example.myapplication.entrant.NotificationActivity;
import com.example.myapplication.objects.QRCode;
import com.example.myapplication.users.UpdateProfileActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class AdminBrowseImages extends AppCompatActivity implements DeletePosterFragment.DeletePosterDialogListener, DeleteProfilePicFragment.DeleteProfilePicDialogListener {
    private FirebaseFirestore db;
    private Button back;
    private TextView posterTextView;
    private TextView profileTextView;
    private LinearLayout posters;
    private LinearLayout profiles;
    private ListView profileList;
    private ListView posterList;
    private ImageArrayAdapter profileAdapter;
    private PosterArrayAdapter posterAdapter;
    private ArrayList<Image> dataListProfiles;
    private ArrayList<Image> dataListPosters;
    private Image del;
    private Button notificationBtn;
    private Button qrCodeBtn;
    private ImageView updateProfileImg;
    private NotificationController notificationController;
    private RoleActivityController roleActivityController;
    private String device;



    @Override
    public void DeletePoster(Image image) {
        Map<String,Object> updates = new HashMap<>();
        updates.put("posterUrl", null);
        db.collection("events").document(image.getId())
                .update(updates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        posterAdapter.remove(image);
                        posterAdapter.notifyDataSetChanged();
                    }
                });
    }

    @Override
    public void DeleteProfilePic(Image image) throws InterruptedException {
        del = image;
        db.collection("users").document(image.getId()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            String name = document.getString("Firstname") + '+' + document.getString("Lastname");
                            String id = document.getId();
                            String defaultProfilePicUrl = "https://avatar.iran.liara.run/username?username=" + name;
                            Map<String,Object> updates = new HashMap<>();
                            updates.put("Profile Picture", defaultProfilePicUrl);
                            replaceProfilePic(updates, id, defaultProfilePicUrl);
                        }
                    }
                });
    }

    private void replaceProfilePic(Map<String, Object> updates, String id, String url) {
        db.collection("users").document(id)
                .update(updates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        profileAdapter.remove(del);
                        del.setUrl(url);
                        profileAdapter.add(del);
                        profileAdapter.notifyDataSetChanged();
                    }
                });
    }

    /**
     * onCreate function for displaying Image information
     * @param savedInstanceState saved instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_browse_images);
        db = FirebaseFirestore.getInstance();

        roleActivityController = new RoleActivityController(this);
        notificationController = new NotificationController(this);

        back = findViewById(R.id.back_button);
        notificationBtn = findViewById(R.id.notificationBtn);
        qrCodeBtn = findViewById(R.id.qrCodeBtn);
        updateProfileImg = findViewById(R.id.updateProfileImg);


        dataListProfiles = new ArrayList<Image>();
        dataListPosters = new ArrayList<Image>();

        profileList = findViewById(R.id.profileListView);
        profileAdapter = new ImageArrayAdapter(this, dataListProfiles);
        profileList.setAdapter(profileAdapter);

        posterList = findViewById(R.id.posterListView);
        posterAdapter = new PosterArrayAdapter(this, dataListPosters);
        posterList.setAdapter(posterAdapter);

        posterTextView = findViewById(R.id.postersTextView);
        profileTextView = findViewById(R.id.profilesTextView);

        posters = findViewById(R.id.viewPostersLayout);
        profiles = findViewById(R.id.viewProfilesLayout);

        device = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        notificationController.startListening(device);
        roleActivityController.getData(device, updateProfileImg);

        showPosters();

        posterTextView.setOnClickListener(view -> {
            showPosters();
        });

        profileTextView.setOnClickListener(view -> {
            showProfiles();
        });

        back.setOnClickListener(view -> {
            finish();
        });

        profileList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Image image = dataListProfiles.get(i);
                new DeleteProfilePicFragment(image).show(getSupportFragmentManager(), "Delete profile pic");
                return false;
            }
        });

        posterList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Image image = dataListPosters.get(i);
                new DeletePosterFragment(image).show(getSupportFragmentManager(), "Delete poster image");
                return false;
            }
        });

        notificationBtn.setOnClickListener(view -> {
            startActivity(new Intent(AdminBrowseImages.this, NotificationActivity.class));
        });

        // Set a click listener on the update profile button
        updateProfileImg.setOnClickListener(view -> {
            startActivity(new Intent(AdminBrowseImages.this, UpdateProfileActivity.class)); // Navigate to the update profile screen
        });

        qrCodeBtn.setOnClickListener(view -> {
            ScanOptions options = new ScanOptions();
            options.setPrompt("Scan QR code");
            options.setOrientationLocked(true);
            options.setCaptureActivity(CaptureAct.class);
            options.setBeepEnabled(false);

            barcodeLauncher.launch(options);
        });
    }

    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() == null) {
            Toast.makeText(AdminBrowseImages.this, "Cancelled", Toast.LENGTH_SHORT).show();
        } else {
            String scannedUrl = result.getContents();
            if (scannedUrl.startsWith("myapp://event")) {
                Toast.makeText(AdminBrowseImages.this, "Scan Successful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(scannedUrl));
                startActivity(intent);
            }
        }
    });

    /**
     * set posters to visible and profiles to invisible
     */
    private void showPosters() {
        posters.setVisibility(View.VISIBLE);
        profiles.setVisibility(View.GONE);

        dataListPosters.clear();
        // searching firebase for events to retrieve poster images
        db.collection("events").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String url = document.getString("posterUrl");
                                String type = document.getString("name");
                                String id = document.getId();
                                if (url != null) {
                                    Image thing = new Image(url, type, id);
                                    dataListPosters.add(thing);
                                    posterAdapter.notifyDataSetChanged();
                                }
                            }
                        } else {
                            Log.d("AdminBrowseFacilities", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    /**
     * set profiles to visible and posters to invisible
     */
    private void showProfiles() {
        posters.setVisibility(View.GONE);
        profiles.setVisibility(View.VISIBLE);

        dataListProfiles.clear();
        // searching firebase for users to receive profile pictures
        db.collection("users").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String url = document.getString("Profile Picture");
                                String type = document.getString("Firstname") + " " + document.getString("Lastname");
                                String id = document.getId();
                                Image thing = new Image(url, type, id);
                                dataListProfiles.add(thing);
                                profileAdapter.notifyDataSetChanged();
                            }
                        } else {
                            Log.d("AdminBrowseFacilities", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }


}
