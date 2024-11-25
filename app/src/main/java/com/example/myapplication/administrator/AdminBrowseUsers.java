/**
 * Activity for browsing QRCodes
 * Used by the Administrator Only
 * @author Noah vincent
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.administrator.fragments.DeleteUserFragment;
import com.example.myapplication.controllers.NotificationController;
import com.example.myapplication.controllers.RoleActivityController;
import com.example.myapplication.entrant.CaptureAct;
import com.example.myapplication.entrant.NotificationActivity;
import com.example.myapplication.objects.Event;
import com.example.myapplication.objects.Facility;
import com.example.myapplication.objects.UserArrayAdapter;
import com.example.myapplication.objects.Users;
import com.example.myapplication.users.MainActivity;
import com.example.myapplication.users.UpdateProfileActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
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

public class AdminBrowseUsers extends AppCompatActivity {
    private FirebaseFirestore db;
    private Button back;
    private TextView header;
    private ListView usersList;
    private UserArrayAdapter usersAdapter;
    private ArrayList<Users> dataList;
    private Button notificationBtn;
    private Button qrCodeBtn;
    private ImageView updateProfileImg;
    private NotificationController notificationController;
    private RoleActivityController roleActivityController;
    private String device;



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == 1) {
            dataList.clear();
            usersAdapter.clear();
            // searching firebase for all registered users
            db.collection("users").get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    String userId = document.getId();
                                    String firstname = document.getString("Firstname");
                                    String lastname = document.getString("Lastname");
                                    String email = document.getString("Email");
                                    String profilePicture = document.getString("Profile Picture");
                                    String role = document.getString("role");
                                    Users thing = new Users(userId, firstname, lastname, email, profilePicture, role);
                                    dataList.add(thing);
                                    usersAdapter.notifyDataSetChanged();
                                }
                            } else {
                                Log.d("AdminBrowseFacilities", "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
    }

    /**
     * onCreate function for displaying User information
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
        header.setText("USER PROFILES");

        dataList = new ArrayList<Users>();

        usersList = findViewById(R.id.contentListView);
        usersAdapter = new UserArrayAdapter(this, dataList);
        usersList.setAdapter(usersAdapter);

        device = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        notificationController.startListening(device);
        roleActivityController.getData(device, updateProfileImg);

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
                        usersAdapter.clear();
                        // searching firebase for all registered users
                        db.collection("users").get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                String userId = document.getId();
                                                String firstname = document.getString("Firstname");
                                                String lastname = document.getString("Lastname");
                                                String email = document.getString("Email");
                                                String profilePicture = document.getString("Profile Picture");
                                                String role = document.getString("role");
                                                Users thing = new Users(userId, firstname, lastname, email, profilePicture, role);
                                                dataList.add(thing);
                                                usersAdapter.notifyDataSetChanged();
                                            }
                                        } else {
                                            Log.d("AdminBrowseFacilities", "Error getting documents: ", task.getException());
                                        }
                                    }
                                });
                        }
                    });

        // searching firebase for all registered users
        db.collection("users").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String userId = document.getId();
                                String firstname = document.getString("Firstname");
                                String lastname = document.getString("Lastname");
                                String email = document.getString("Email");
                                String profilePicture = document.getString("Profile Picture");
                                String role = document.getString("role");
                                Users thing = new Users(userId, firstname, lastname, email, profilePicture, role);
                                dataList.add(thing);
                                usersAdapter.notifyDataSetChanged();
                            }
                        } else {
                            Log.d("AdminBrowseFacilities", "Error getting documents: ", task.getException());
                        }
                    }
                });

        back.setOnClickListener(view -> {
            finish();
        });

        usersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Users user = dataList.get(i);
                Intent intent = new Intent(AdminBrowseUsers.this, AdminViewUser.class);
                intent.putExtra("user", (Parcelable) user);
                someActivityResultLauncher.launch(intent);
            }
        });

        notificationBtn.setOnClickListener(view -> {
            startActivity(new Intent(AdminBrowseUsers.this, NotificationActivity.class));
        });

        // Set a click listener on the update profile button
        updateProfileImg.setOnClickListener(view -> {
            startActivity(new Intent(AdminBrowseUsers.this, UpdateProfileActivity.class)); // Navigate to the update profile screen
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
            Toast.makeText(AdminBrowseUsers.this, "Cancelled", Toast.LENGTH_SHORT).show();
        } else {
            String scannedUrl = result.getContents();
            if (scannedUrl.startsWith("myapp://event")) {
                Toast.makeText(AdminBrowseUsers.this, "Scan Successful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(scannedUrl));
                startActivity(intent);
            }
        }
    });
}
