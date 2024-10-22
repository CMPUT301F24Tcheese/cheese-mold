package com.example.myapplication.organizer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.myapplication.EventActivity;
import com.example.myapplication.LoginActivity;
import com.example.myapplication.R;
import com.example.myapplication.UpdateProfileActivity;
import com.example.myapplication.administrator.AdministratorMainActivity;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class OrganizerMainActivity extends AppCompatActivity {

    private FirebaseAuth auth; // Firebase authentication object for managing user sessions
    private FirebaseFirestore db; // Firebase Firestore database object for retrieving user data
    private TextView welcomeText; // TextView to display the welcome message with the user's name
    private ImageView profilePic; // ImageView to display the user's profile picture
    private Button updateProfileBtn; // Button for users to navigate to the update profile screen

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Call the parent class's onCreate method to initialize the activity

        // Initialize Firebase services
        FirebaseApp.initializeApp(this); // Ensure Firebase is initialized before using any Firebase services
        auth = FirebaseAuth.getInstance(); // Get the instance of Firebase authentication
        db = FirebaseFirestore.getInstance(); // Get the instance of Firebase Firestore database

        setContentView(R.layout.activity_organizer_main); // Set the layout for the main activity screen

        // Initialize the UI elements
        welcomeText = findViewById(R.id.welcomeTextView); // Find the TextView by its ID to display the welcome message
        profilePic = findViewById(R.id.welcomeProfilePictureMain); // Find the ImageView by its ID for profile picture display
        updateProfileBtn = findViewById(R.id.updateProfileBtn); // Find the Button by its ID for navigating to update profile screen

        // Get the currently logged-in user, if any
        FirebaseUser currentUser = auth.getCurrentUser();

        // Set a click listener on the update profile button
        updateProfileBtn.setOnClickListener(view -> {
            startActivity(new Intent(OrganizerMainActivity.this, UpdateProfileActivity.class)); // Navigate to the update profile screen
            finish(); // Close the MainActivity
        });

        // Initialize the button to navigate to EventActivity
        Button btnOpenEventPage = findViewById(R.id.btnOpenEventPage); // Find the button by its ID
        btnOpenEventPage.setOnClickListener(view -> {
            Intent intent = new Intent(OrganizerMainActivity.this, EventActivity.class); // Create an intent to open EventActivity
            startActivity(intent); // Start EventActivity
        });

        // Check if a user is logged in
        if (currentUser != null) {
            String userId = currentUser.getUid(); // Get the unique ID of the currently logged-in user
            getData(userId); // Retrieve user data from Firestore
        } else {
            // Redirect to login screen if no user is logged in
            startActivity(new Intent(OrganizerMainActivity.this, LoginActivity.class)); // Navigate to LoginActivity
        }
    }

    /**
     * Retrieves user data from Firestore and loads it into the activity UI components
     * @param userId The unique ID of the logged-in user
     */
    private void getData(String userId) {
        // Access the "users" collection in Firestore and get the document corresponding to the userId
        db.collection("users").document(userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) { // Check if the data retrieval task was successful
                        DocumentSnapshot document = task.getResult(); // Get the document snapshot from Firestore

                        if(document.exists()) { // Verify that the document exists
                            String firstname = document.getString("Firstname"); // Retrieve the user's first name from the document
                            String lastname = document.getString("Lastname"); // Retrieve the user's last name from the document
                            String profilePicUrl = document.getString("Profile Picture"); // Retrieve the URL of the user's profile picture
                            welcomeText.setText("Welcome " + firstname + " " + lastname); // Set the welcome text with the user's full name

                            // Load the user's profile picture using Glide, a third-party image loading library
                            Glide.with(OrganizerMainActivity.this)
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
