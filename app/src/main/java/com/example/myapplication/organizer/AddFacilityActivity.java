package com.example.myapplication.organizer;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.FacilityActivity;
import com.example.myapplication.objects.Facility;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddFacilityActivity extends AppCompatActivity {

    private EditText editTextStreet, editTextCity, editTextProvince, editTextPostalCode; // Input fields for facility address
    private Button buttonCreateFacility, buttonBackToFacility; // Buttons for creating facility and going back
    private FirebaseFirestore db; // Firebase Firestore instance
    private String organizerId; // Device ID used as organizer ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Call the parent class's onCreate method
        setContentView(R.layout.add_facility_activity); // Set the layout for the activity

        db = FirebaseFirestore.getInstance(); // Get Firebase Firestore instance

        // Get device ID to use as organizer ID
        organizerId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.d("AddFacilityActivity", "Organizer Device ID: " + organizerId);

        // Initialize UI elements
        editTextStreet = findViewById(R.id.editTextStreet);
        editTextCity = findViewById(R.id.editTextCity);
        editTextProvince = findViewById(R.id.editTextProvince);
        editTextPostalCode = findViewById(R.id.editTextPostalCode);
        buttonCreateFacility = findViewById(R.id.buttonCreateFacility);
        buttonBackToFacility = findViewById(R.id.buttonBackToFacility);

        // Check if a facility already exists for this device ID
        checkIfFacilityExists();

        buttonCreateFacility.setOnClickListener(v -> createFacility()); // Set click listener to create facility
        buttonBackToFacility.setOnClickListener(view -> finish()); // Set click listener to close the current activity
    }

    // Method to check if a facility already exists to prevent duplicate creation
    private void checkIfFacilityExists() {
        db.collection("Facilities").document(organizerId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) { // Check if the task completed successfully
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) { // If a facility already exists, prevent creation and navigate back
                            Toast.makeText(this, "You have already added a facility.", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(AddFacilityActivity.this, FacilityActivity.class));
                            finish(); // Close the current activity
                        }
                    } else {
                        Log.e("AddFacilityActivity", "Error checking facility: ", task.getException());
                    }
                });
    }

    // Method to create a new facility
    private void createFacility() {
        String street = editTextStreet.getText().toString().trim(); // Get street input
        String city = editTextCity.getText().toString().trim(); // Get city input
        String province = editTextProvince.getText().toString().trim(); // Get province input
        String postalCode = editTextPostalCode.getText().toString().trim(); // Get postal code input

        // Check if all input fields are filled
        if (!street.isEmpty() && !city.isEmpty() && !province.isEmpty() && !postalCode.isEmpty()) {
            // Use organizerId as facility ID
            Facility facility = new Facility(organizerId, street, city, province, postalCode);

            db.collection("Facilities").document(organizerId).set(facility) // Use organizerId as document ID
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(AddFacilityActivity.this, "Facility built", Toast.LENGTH_SHORT).show(); // Show success message
                        startActivity(new Intent(AddFacilityActivity.this, FacilityActivity.class)); // Navigate to FacilityActivity
                        finish(); // Close the current activity after successful creation
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(AddFacilityActivity.this, "Failed to save facility", Toast.LENGTH_SHORT).show(); // Show failure message
                        Log.w("AddFacilityActivity", "Error adding facility", e);
                    });
        } else {
            Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show(); // Show a warning if fields are not filled
        }
    }
}
