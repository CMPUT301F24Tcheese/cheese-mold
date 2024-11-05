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

    private EditText editTextName, editTextDescription, editTextStreet, editTextCity, editTextProvince; // Input fields for facility details
    private Button buttonUpdateFacility, buttonCancel; // Buttons for updating facility and canceling the operation
    private FirebaseFirestore db; // Firestore database instance
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

        loadFacilityData(); // Load facility data using the facilityId

        buttonUpdateFacility.setOnClickListener(view -> updateFacility());
        buttonCancel.setOnClickListener(view -> finish());
    }

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
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to load facility data.", Toast.LENGTH_SHORT).show());
    }

    private void updateFacility() {
        String name = editTextName.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        String street = editTextStreet.getText().toString().trim();
        String city = editTextCity.getText().toString().trim();
        String province = editTextProvince.getText().toString().trim();

        if (!name.isEmpty() && !description.isEmpty() && !street.isEmpty() && !city.isEmpty() && !province.isEmpty()) {
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
}
