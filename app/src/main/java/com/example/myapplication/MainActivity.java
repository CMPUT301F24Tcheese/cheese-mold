package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Document;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private TextView welcomeText;
    private ImageView profilePic;
    private Button signoutBtn;
    private Button updateProfileBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        welcomeText = findViewById(R.id.welcomeTextView);
        profilePic = findViewById(R.id.welcomeProfilePictureMain);
        signoutBtn = findViewById(R.id.signoutBtn);
        updateProfileBtn = findViewById(R.id.updateProfileBtn);

        FirebaseUser currentUser = auth.getCurrentUser(); // checks if any user is currently logged in

        signoutBtn.setOnClickListener(view -> {
            auth.signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        });

        updateProfileBtn.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, UpdateProfileActivity.class));
            finish();
        });

        if (currentUser != null) {
            //if a user is logged in, get information from the database and fill up main activity
            String userId = currentUser.getUid();
            getData(userId);

        } else {
            // if no users are logged in, redirect to login page
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }


    }


    /**
     * Gets the user data from the firestore and loads it into the activity
     * @param userId
     */
    private void getData(String userId) {
        db.collection("users").document(userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();

                        if(document.exists()) {
                            String firstname = document.getString("Firstname");
                            String lastname = document.getString("Lastname");
                            String profilePicUrl = document.getString("Profile Picture");
                            welcomeText.setText("Welcome " + firstname + " " + lastname);

                            // Load the current profile picture from the URL
                            // Glide is a third party framework. Not built into android studio
                            // It's in the dependencies
                            // Sets the user profile image in the imageview
                            Glide.with(MainActivity.this)
                                    .load(profilePicUrl)
                                    .placeholder(R.drawable.baseline_person_outline_24) // While loading there is a default picture template
                                    .error(R.drawable.baseline_person_outline_24) // In case picture fails to load from db, uses default template
                                    .into(profilePic);
                        } else {
                            Log.d("MainActivity", "No such document");
                        }

                    } else {
                        Log.w("MainActivity", "Error getting documents.", task.getException());
                    }
                });

    }
}