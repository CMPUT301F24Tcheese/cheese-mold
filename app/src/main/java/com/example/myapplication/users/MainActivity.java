package com.example.myapplication.users;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.administrator.AdministratorMainActivity;
import com.example.myapplication.entrant.EntrantMainActivity;
import com.example.myapplication.organizer.OrganizerMainActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

/**
 * MainActivity handles the main screen of the app where user details are displayed after login
 */
public class MainActivity extends AppCompatActivity {

    private FirebaseFirestore db; // Firebase Firestore database object for retrieving user data
    private String device; // for storing device id

    /**
     * Function that passes tre user onto the correct activity depending on their role
     * Entrants to the entrant view
     * Organizers to the organizer view
     * Administrators to he administrator view
     * @param role User's role
     */
    void openAppForRole(String role) {
        if (role.equals("Entrant")) {
            startActivity(new Intent(this, EntrantMainActivity.class)); // Navigate to entrant home screen
            finish();
        } else if (role.equals("Organizer")) {
            startActivity(new Intent(this, OrganizerMainActivity.class)); // Navigate to organizer home screen
            finish();
        } else if (role.equals("Administrator")) {
            startActivity(new Intent(this, AdministratorMainActivity.class)); // Navigate to administrator home screen
            finish();
        }
    }

    /**
     * Function that gets the current users credentials frm Firebase and logs them in
     * with their device id. If they do not exist, they are redirected to the sign up screen
     * @param device User's device id
     */
    void getUser(String device) {
        db.collection("users").document(device).get() // fetch data using device id
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String role = documentSnapshot.getString("role"); // grab role from firebase
                            if (role != null) {
                                openAppForRole(role); // redirect to home screen depending on role
                            } else {
                                Log.d("MainActivity", "role does not exist or is incorrect"); // Log a message if the document does not exist
                            }
                        } else {
                            startActivity(new Intent(MainActivity.this, RegisterActivity.class)); // Navigate sign up screen if user does not exist
                        }
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

        // grabbing device id
        device = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        // get user and redirect them to home page
        getUser(device);
    }
}
