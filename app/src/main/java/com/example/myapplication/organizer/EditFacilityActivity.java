package com.example.myapplication.organizer;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.objects.Facility;
import com.example.myapplication.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;


/**
 * Activity for editing facility details. This activity allows the user to update
 * the information of a specific facility, including name, description, and address.
 */
public class EditFacilityActivity extends AppCompatActivity {

    private EditText editTextName, editTextDescription, editTextStreet, editTextCity, editTextProvince, editTextPostalCode; // Input fields for facility details
    private Button buttonUpdateFacility, buttonCancel; // Buttons for updating facility and canceling the operation
    private FirebaseFirestore db; // Firestore database instance
    private String facilityId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Call the parent class's onCreate method
        setContentView(R.layout.activity_edit_facility); // Set the layout file for this activity

        db = FirebaseFirestore.getInstance(); // Initialize the Firestore database instance

        editTextName = findViewById(R.id.editTextFacilityName);
        editTextDescription = findViewById(R.id.editTextFacilityDescription);
        editTextStreet = findViewById(R.id.editTextStreet); // Link the street EditText to its layout element
        editTextCity = findViewById(R.id.editTextCity); // Link the city EditText to its layout element
        editTextProvince = findViewById(R.id.editTextProvince); // Link the province EditText to its layout element
        buttonUpdateFacility = findViewById(R.id.buttonUpdateFacility); // Link the update button to its layout element
        buttonCancel = findViewById(R.id.buttonCancel); // Link the cancel button to its layout element

        facilityId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        loadFacilityData();


        buttonUpdateFacility.setOnClickListener(view -> updateFacility()); // Set click listener for updating facility

        buttonCancel.setOnClickListener(view -> finish()); // Set click listener for canceling and closing the activity
    }


    /**
     * Updates the facility details in Firestore with the data entered by the user.
     */
    private void updateFacility() {
        String name = editTextName.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        String street = editTextStreet.getText().toString().trim(); // Get the street input and remove leading/trailing spaces
        String city = editTextCity.getText().toString().trim(); // Get the city input and remove leading/trailing spaces
        String province = editTextProvince.getText().toString().trim(); // Get the province input and remove leading/trailing spaces

        if (!street.isEmpty() && !city.isEmpty() && !province.isEmpty()) {
            Facility updatedFacility = new Facility(facilityId, name, description, street, city, province); // Create a Facility object with updated data

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


    /**
     * Loads the existing facility data from Firestore and populates the input fields.
     */
    private void loadFacilityData() {
        db.collection("Facilities").document(facilityId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Facility facility = documentSnapshot.toObject(Facility.class);
                        if (facility != null) {
                            editTextName.setText(facility.getName());
                            editTextDescription.setText(facility.getDescription());
                            editTextStreet.setText(facility.getStreet());
                            editTextCity.setText(facility.getCity());
                            editTextProvince.setText(facility.getProvince());
                        }
                    }
                });
    }
}
