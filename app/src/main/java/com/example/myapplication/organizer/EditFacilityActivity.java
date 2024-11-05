package com.example.myapplication.organizer;

import android.content.Intent;
import android.os.Bundle;
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

    private EditText editTextName, editTextDescription, editTextStreet, editTextCity, editTextProvince, editTextPostalCode;
    private Button buttonUpdateFacility, buttonCancel;
    private FirebaseFirestore db;
    private String facilityId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_facility);

        db = FirebaseFirestore.getInstance();

        editTextName = findViewById(R.id.editTextFacilityName);
        editTextDescription = findViewById(R.id.editTextFacilityDescription);
        editTextStreet = findViewById(R.id.editTextStreet);
        editTextCity = findViewById(R.id.editTextCity);
        editTextProvince = findViewById(R.id.editTextProvince);
        buttonUpdateFacility = findViewById(R.id.buttonUpdateFacility);
        buttonCancel = findViewById(R.id.buttonCancel);

        // Retrieve the facility ID passed through the intent
        facilityId = getIntent().getStringExtra("FACILITY_ID");

        // Load facility data if a valid facility ID is provided
        if (facilityId != null) {
            loadFacilityData();
        } else {
            Toast.makeText(this, "No Facility ID provided", Toast.LENGTH_SHORT).show();
            finish();
        }

        buttonUpdateFacility.setOnClickListener(view -> updateFacility());
        buttonCancel.setOnClickListener(view -> finish());
    }

    /**
     * Updates the facility details in Firestore with the data entered by the user.
     */
    private void updateFacility() {
        String name = editTextName.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        String street = editTextStreet.getText().toString().trim();
        String city = editTextCity.getText().toString().trim();
        String province = editTextProvince.getText().toString().trim();

        if (!street.isEmpty() && !city.isEmpty() && !province.isEmpty()) {
            Facility updatedFacility = new Facility(facilityId, name, description, street, city, province);

            db.collection("Facilities").document(facilityId)
                    .set(updatedFacility, SetOptions.merge())
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(EditFacilityActivity.this, "Facility updated", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(EditFacilityActivity.this, "Update failed", Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
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
                    } else {
                        Toast.makeText(this, "Facility not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to load facility data", Toast.LENGTH_SHORT).show());
    }
}
