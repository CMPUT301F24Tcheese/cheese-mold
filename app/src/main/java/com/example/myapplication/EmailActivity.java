package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

public class EmailActivity extends AppCompatActivity {

    private ImageView userAvatar;
    private FirebaseFirestore db; // Firestore instance for Firebase operations

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
