package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.myapplication.administrator.AdministratorMainActivity;
import com.example.myapplication.entrant.EntrantMainActivity;
import com.example.myapplication.organizer.OrganizerMainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

// MainActivity handles the main screen of the app where user details are displayed after login
public class MainActivity extends AppCompatActivity {

    private FirebaseFirestore db; // Firebase Firestore database object for retrieving user data
    private String device;

    void openAppForRole(String role) {
        if (role.equals("Entrant")) {
            startActivity(new Intent(this, EntrantMainActivity.class)); // Navigate back to the login screen
            finish(); // Close the MainActivity
        } else if (role.equals("Organizer")) {
            startActivity(new Intent(this, OrganizerMainActivity.class)); // Navigate back to the login screen
            finish(); // Close the MainActivity
        } else if (role.equals("Administrator")) {
            startActivity(new Intent(this, AdministratorMainActivity.class)); // Navigate back to the login screen
            finish(); // Close the MainActivity
        }
    }

    void getUser(String device) {
        db.collection("users").document(device).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String role = document.getString("role");
                            if (role != null) {
                                openAppForRole(role);
                            } else {
                                HashMap<String, String> data = new HashMap<>(); // Create a HashMap to store user data
                                data.put("role", "Entrant"); // Add user's email to the map
                                db.collection("users").document(device).set(data);
                            }
                        } else {
                            Log.d("MainActivity", "No such document"); // Log a message if the document does not exist
                        }
                    } else {
                        Log.w("MainActivity", "Error getting documents.", task.getException()); // Log a warning if there was an error retrieving the document
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        startActivity(new Intent(MainActivity.this, RegisterActivity.class)); // Navigate back to the login screen
                    }
                });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Call the parent class's onCreate method to initialize the activity
        setContentView(R.layout.activity_main); // Set the layout for the main activity screen

        // Initialize Firebase services
        FirebaseApp.initializeApp(this); // Ensure Firebase is initialized before using any Firebase services
        db = FirebaseFirestore.getInstance(); // Get the instance of Firebase Firestore database

        /**
         * checking the role of the user joining and redirecting to the respective
         * main activity and view
         */
        device = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        getUser(device);
    }
}
