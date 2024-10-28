package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EmailActivity extends AppCompatActivity {

    private ImageView userAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email); // Ensure this layout is correctly set

        // Initialize the user avatar ImageView
        userAvatar = findViewById(R.id.userAvatar); // Ensure this ID matches your XML

        // Set a click listener on the user avatar to navigate to ProfileActivity
        userAvatar.setOnClickListener(view -> {
            Intent intent = new Intent(EmailActivity.this, ProfileActivity.class);
            startActivity(intent); // Start ProfileActivity
        });

        // Implement other email-related features here
    }
}
