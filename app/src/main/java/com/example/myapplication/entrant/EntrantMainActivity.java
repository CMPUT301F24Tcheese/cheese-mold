package com.example.myapplication.entrant;

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
import com.example.myapplication.EventDetailActivity;
import com.example.myapplication.R;
import com.example.myapplication.users.UpdateProfileActivity;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.atomic.AtomicReference;

public class EntrantMainActivity extends AppCompatActivity {

    private FirebaseFirestore db; // Firebase Firestore database object for retrieving user data
    private TextView welcomeText; // TextView to display the welcome message with the user's name
    private ImageView profilePic; // ImageView to display the user's profile picture
    private Button updateProfileBtn; // Button for users to navigate to the update profile screen
    private String device;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Call the parent class's onCreate method to initialize the activity

        // Initialize Firebase services
        db = FirebaseFirestore.getInstance(); // Get the instance of Firebase Firestore database

        setContentView(R.layout.activity_entrant_main); // Set the layout for the main activity screen

        // Initialize the UI elements
        welcomeText = findViewById(R.id.welcomeTextView); // Find the TextView by its ID to display the welcome message
        profilePic = findViewById(R.id.welcomeProfilePictureMain); // Find the ImageView by its ID for profile picture display
        updateProfileBtn = findViewById(R.id.updateProfileBtn); // Find the Button by its ID for navigating to update profile screen

        device = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        getData(device);



        // Set a click listener on the update profile button
        updateProfileBtn.setOnClickListener(view -> {
            startActivity(new Intent(EntrantMainActivity.this, UpdateProfileActivity.class)); // Navigate to the update profile screen
            finish(); // Close the MainActivity
        });

        // Initialize the button to navigate to MyEventActivity
        Button btnOpenEventPage = findViewById(R.id.btnOpenEventPage); // Find the button by its ID
        btnOpenEventPage.setOnClickListener(view -> {
            Intent intent = new Intent(EntrantMainActivity.this, MyEventActivity.class); // Create an intent to open EventActivity
            intent.putExtra("device",device);
            startActivity(intent); // Start EventActivity
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
                            Glide.with(EntrantMainActivity.this)
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
