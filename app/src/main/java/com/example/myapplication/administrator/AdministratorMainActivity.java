package com.example.myapplication.administrator;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.myapplication.EventActivity;
import com.example.myapplication.R;
import com.example.myapplication.users.UpdateProfileActivity;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdministratorMainActivity extends AppCompatActivity {
    private FirebaseFirestore db; // Firebase Firestore database object for retrieving user data
    private TextView welcomeText; // TextView to display the welcome message with the user's name
    private ImageView profilePic; // ImageView to display the user's profile picture
    private Button updateProfileBtn; // Button for users to navigate to the update profile screen
    private Button browseFacilitiesBtn;
    private Button browseEventsBtn;
    private Button browseProfilesBtn;
    private Button browseImagesBtn;
    private Button browseQRCodeBtn;
    private String device;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Call the parent class's onCreate method to initialize the activity

        // Initialize Firebase services
        db = FirebaseFirestore.getInstance(); // Get the instance of Firebase Firestore database

        setContentView(R.layout.acitvity_administrator_main); // Set the layout for the main activity screen

        // Initialize the UI elements
        welcomeText = findViewById(R.id.welcomeTextView); // Find the TextView by its ID to display the welcome message
        profilePic = findViewById(R.id.welcomeProfilePictureMain); // Find the ImageView by its ID for profile picture display
        updateProfileBtn = findViewById(R.id.updateProfileBtn); // Find the Button by its ID for navigating to update profile screen

        // initialize the browse button UI elements
        browseFacilitiesBtn = findViewById(R.id.browseFacilitiesBtn);
        browseEventsBtn = findViewById(R.id.browseEventsBtn);
        browseProfilesBtn = findViewById(R.id.browseProfilesBtn);
        browseImagesBtn = findViewById(R.id.browseImagesBtn);
        browseQRCodeBtn = findViewById(R.id.browseQRcodesBtn);

        device = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        getData(device);

        // Set a click listener on the update profile button
        updateProfileBtn.setOnClickListener(view -> {
            startActivity(new Intent(AdministratorMainActivity.this, UpdateProfileActivity.class)); // Navigate to the update profile screen
            finish(); // Close the MainActivity
        });

        browseFacilitiesBtn.setOnClickListener(view -> {
            startActivity(new Intent(AdministratorMainActivity.this, AdminBrowseFacilities.class));
        });

        browseEventsBtn.setOnClickListener(view -> {
            // TODO fill later
        });

        browseProfilesBtn.setOnClickListener(view -> {
            startActivity(new Intent(AdministratorMainActivity.this, AdminBrowseUsers.class));
        });

        browseImagesBtn.setOnClickListener(view -> {
            // TODO fill later
        });

        browseQRCodeBtn.setOnClickListener(view -> {
            // TODO fill later
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

                            // Load the user's profile picture using Glide, a third-party image loading library
                            Glide.with(AdministratorMainActivity.this)
                                    .load(profilePicUrl) // Load the image from the URL obtained from Firestore
                                    .placeholder(R.drawable.baseline_person_outline_24) // Display a default placeholder while the image loads
                                    .error(R.drawable.baseline_person_outline_24) // Show a default image if loading the picture fails
                                    .into(profilePic); // Set the loaded image into the ImageView
                        } else {
                            Log.d("MainActivity", "No such document"); // Log a message if the document does not exist
                        }
                    } else {
                        Log.w("MainActivity", "Error getting documents.", task.getException()); // Log a warning if there was an error retrieving the document
                    }
                });
    }
}
