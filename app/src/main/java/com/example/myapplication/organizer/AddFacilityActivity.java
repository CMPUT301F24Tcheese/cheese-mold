package com.example.myapplication.organizer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.objects.Facility;
import com.example.myapplication.FacilityActivity;
import com.example.myapplication.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddFacilityActivity extends AppCompatActivity {

    private EditText editTextStreet, editTextCity, editTextProvince, editTextPostalCode; // EditText fields for facility address information.
    private Button buttonCreateFacility, buttonBackToFacility; // Buttons to create facility and navigate back.
    private FirebaseFirestore db; // Firestore database instance for storing facility data.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Call the superclass method to handle the activity creation.
        setContentView(R.layout.add_facility_activity); // Set the layout resource for this activity.

        editTextStreet = findViewById(R.id.editTextStreet); // Initialize EditText for street input.
        editTextCity = findViewById(R.id.editTextCity); // Initialize EditText for city input.
        editTextProvince = findViewById(R.id.editTextProvince); // Initialize EditText for province input.
        editTextPostalCode = findViewById(R.id.editTextPostalCode); // Initialize EditText for postal code input.
        buttonCreateFacility = findViewById(R.id.buttonCreateFacility); // Initialize button for creating facility.
        buttonBackToFacility = findViewById(R.id.buttonBackToFacility); // Initialize button to return to the previous screen.

        db = FirebaseFirestore.getInstance(); // Get an instance of Firestore database.

        buttonCreateFacility.setOnClickListener(v -> createFacility()); // Set a click listener on the create facility button to invoke the method.
        buttonBackToFacility.setOnClickListener(view -> finish()); // Set a click listener on the back button to close the current activity and return.
    }

    private void createFacility() {
        String street = editTextStreet.getText().toString().trim(); // Retrieve and trim the input for the street.
        String city = editTextCity.getText().toString().trim(); // Retrieve and trim the input for the city.
        String province = editTextProvince.getText().toString().trim(); // Retrieve and trim the input for the province.
        String postalCode = editTextPostalCode.getText().toString().trim(); // Retrieve and trim the input for the postal code.

        if (!street.isEmpty() && !city.isEmpty() && !province.isEmpty() && !postalCode.isEmpty()) {
            String id = db.collection("Facilities").document().getId(); // Generate a unique ID for the facility document.

            Facility facility = new Facility(id, street, city, province, postalCode); // Create a new Facility object with the provided information.

            db.collection("Facilities").document(id).set(facility) // Save the Facility object to the Firestore database using the generated ID.
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(AddFacilityActivity.this, "Facility built", Toast.LENGTH_SHORT).show(); // Display a success message if the facility is created successfully.
                        startActivity(new Intent(AddFacilityActivity.this, FacilityActivity.class)); // Start the FacilityActivity to show the updated list.
                        finish(); // Close the current AddFacilityActivity after saving.
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(AddFacilityActivity.this, "Failed to save facility", Toast.LENGTH_SHORT).show(); // Display an error message if saving fails.
                        Log.w("AddFacilityActivity", "Error adding facility", e); // Log the error for debugging purposes.
                    });
        } else {
            Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show(); // Show a warning if any of the input fields are empty.
        }
    }
}
