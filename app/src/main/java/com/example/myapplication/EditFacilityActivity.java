package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

public class EditFacilityActivity extends AppCompatActivity {

    private EditText editTextStreet, editTextCity, editTextProvince, editTextPostalCode; // Input fields for facility details
    private Button buttonUpdateFacility, buttonCancel; // Buttons for updating facility and canceling the operation
    private FirebaseFirestore db; // Firestore database instance
    private String facilityId; // Unique identifier for the facility to be edited

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Call the parent class's onCreate method
        setContentView(R.layout.activity_edit_facility); // Set the layout file for this activity

        db = FirebaseFirestore.getInstance(); // Initialize the Firestore database instance

        editTextStreet = findViewById(R.id.editTextStreet); // Link the street EditText to its layout element
        editTextCity = findViewById(R.id.editTextCity); // Link the city EditText to its layout element
        editTextProvince = findViewById(R.id.editTextProvince); // Link the province EditText to its layout element
        editTextPostalCode = findViewById(R.id.editTextPostalCode); // Link the postal code EditText to its layout element
        buttonUpdateFacility = findViewById(R.id.buttonUpdateFacility); // Link the update button to its layout element
        buttonCancel = findViewById(R.id.buttonCancel); // Link the cancel button to its layout element

        Intent intent = getIntent(); // Retrieve the intent that started this activity
        facilityId = intent.getStringExtra("facilityId"); // Get the facility ID from the intent
        editTextStreet.setText(intent.getStringExtra("street")); // Set the street EditText with data from the intent
        editTextCity.setText(intent.getStringExtra("city")); // Set the city EditText with data from the intent
        editTextProvince.setText(intent.getStringExtra("province")); // Set the province EditText with data from the intent
        editTextPostalCode.setText(intent.getStringExtra("postalCode")); // Set the postal code EditText with data from the intent

        buttonUpdateFacility.setOnClickListener(view -> updateFacility()); // Set click listener for updating facility

        buttonCancel.setOnClickListener(view -> finish()); // Set click listener for canceling and closing the activity
    }

    private void updateFacility() {
        String street = editTextStreet.getText().toString().trim(); // Get the street input and remove leading/trailing spaces
        String city = editTextCity.getText().toString().trim(); // Get the city input and remove leading/trailing spaces
        String province = editTextProvince.getText().toString().trim(); // Get the province input and remove leading/trailing spaces
        String postalCode = editTextPostalCode.getText().toString().trim(); // Get the postal code input and remove leading/trailing spaces

        if (!street.isEmpty() && !city.isEmpty() && !province.isEmpty() && !postalCode.isEmpty()) {
            Facility updatedFacility = new Facility(facilityId, street, city, province, postalCode); // Create a Facility object with updated data

            db.collection("Facilities").document(facilityId) // Access the "Facilities" collection and target the specific document by ID
                    .set(updatedFacility, SetOptions.merge()) // Update the document with new data, merging fields if they already exist
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(EditFacilityActivity.this, "Facility updated", Toast.LENGTH_SHORT).show(); // Show success message on successful update
                        finish(); // Close the activity and return to the previous screen
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(EditFacilityActivity.this, "Update failed", Toast.LENGTH_SHORT).show(); // Show error message if the update fails
                    });
        } else {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show(); // Show warning if any field is left empty
        }
    }
}
