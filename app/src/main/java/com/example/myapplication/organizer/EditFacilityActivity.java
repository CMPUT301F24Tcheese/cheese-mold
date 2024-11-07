package com.example.myapplication.organizer;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.objects.Facility;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

public class EditFacilityActivity extends AppCompatActivity {

    // UI elements for facility details input
    private EditText editTextName, editTextDescription, editTextStreet, editTextCity, editTextProvince;
    private Button buttonUpdateFacility, buttonCancel;

    // Firestore database instance
    private FirebaseFirestore db;
    private String facilityId; // Facility ID based on device ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_facility);

        db = FirebaseFirestore.getInstance();

        // Initialize UI elements
        editTextName = findViewById(R.id.editTextFacilityName);
        editTextDescription = findViewById(R.id.editTextFacilityDescription);
        editTextStreet = findViewById(R.id.editTextStreet);
        editTextCity = findViewById(R.id.editTextCity);
        editTextProvince = findViewById(R.id.editTextProvince);
        buttonUpdateFacility = findViewById(R.id.buttonUpdateFacility);
        buttonCancel = findViewById(R.id.buttonCancel);

        // Use device ID as the facility ID
        facilityId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        // Load existing facility data
        loadFacilityData();

        // Set up button listeners
        buttonUpdateFacility.setOnClickListener(view -> updateFacility());
        buttonCancel.setOnClickListener(view -> finish());
    }

    /**
     * Loads existing facility data from Firestore and populates the input fields.
     * Displays a Toast message if no facility is found or if loading fails.
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
                    } else {
                        Toast.makeText(this, "No facility found for this device.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to load facility data.", Toast.LENGTH_SHORT).show()
                );
    }

    /**
     * Updates the facility details in Firestore with the data from the input fields.
     * Validates input before attempting to update and displays Toast messages for success or failure.
     */
    private void updateFacility() {
        String name = editTextName.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        String street = editTextStreet.getText().toString().trim();
        String city = editTextCity.getText().toString().trim();
        String province = editTextProvince.getText().toString().trim();

        // Regex to check if input is alphabetic only
        String alphabeticPattern = "^[a-zA-Z]+$";

        if (validateInput(name, description, street, city, province, alphabeticPattern)) {
            Facility updatedFacility = new Facility(facilityId, name, description, street, city, province);

            db.collection("Facilities").document(facilityId)
                    .set(updatedFacility, SetOptions.merge())
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(EditFacilityActivity.this, "Facility updated", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(EditFacilityActivity.this, "Update failed", Toast.LENGTH_SHORT).show()
                    );
        } else {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Validates the input fields to ensure they are not empty and that city and province
     * contain only alphabetic characters. Displays appropriate error messages via Toast.
     *
     * @param name The facility name
     * @param description The facility description
     * @param street The facility street address
     * @param city The facility city
     * @param province The facility province
     * @param alphabeticPattern The regex pattern to check alphabetic input
     * @return True if all inputs are valid, otherwise false
     */
    private boolean validateInput(String name, String description, String street, String city, String province, String alphabeticPattern) {
        if (name.isEmpty() || description.isEmpty() || street.isEmpty() || city.isEmpty() || province.isEmpty()) {
            return false;
        }

        if (!city.matches(alphabeticPattern)) {
            Toast.makeText(this, "City should contain only letters", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!province.matches(alphabeticPattern)) {
            Toast.makeText(this, "Province should contain only letters", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}
