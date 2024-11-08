/**
 * NOTE: !!!This class is not yet being used!!!
 * Activity for managing email-related functions.
 * This activity includes navigation to the user's profile when the avatar is clicked.
 */
package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

/**
 * EmailActivity is responsible for handling email operations and interactions.
 * It includes an ImageView for the user avatar, which navigates to ProfileActivity when clicked.
 */
public class EmailActivity extends AppCompatActivity {

    private ImageView userAvatar;
    private FirebaseFirestore db; // Firestore instance for Firebase operations

    /**
     * Initializes the activity, sets up the UI components, and sets click listeners.
     *
     * @param savedInstanceState The saved instance state for restoring the activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email); // Set the layout

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize the user avatar ImageView
        userAvatar = findViewById(R.id.userAvatar); // Ensure this ID matches your XML

        // Set a click listener on the user avatar to navigate to ProfileActivity
        userAvatar.setOnClickListener(view -> {
            Intent intent = new Intent(EmailActivity.this, ProfileActivity.class);
            startActivity(intent); // Start ProfileActivity
        });

        // Additional email-related features can be added here
    }
}
