package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class AddFacilityActivity extends AppCompatActivity {

    private EditText editTextStreet, editTextCity, editTextProvince, editTextPostalCode;
    private Button buttonCreateFacility, buttonBackToFacility;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_facility_activity);

        // Initialize views
        editTextStreet = findViewById(R.id.editTextStreet);
        editTextCity = findViewById(R.id.editTextCity);
        editTextProvince = findViewById(R.id.editTextProvince);
        editTextPostalCode = findViewById(R.id.editTextPostalCode);
        buttonCreateFacility = findViewById(R.id.buttonCreateFacility);
        buttonBackToFacility = findViewById(R.id.buttonBackToFacility);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Set click listeners
        buttonCreateFacility.setOnClickListener(v -> createFacility());
        buttonBackToFacility.setOnClickListener(view -> finish());
    }

    private void createFacility() {
        String street = editTextStreet.getText().toString().trim();
        String city = editTextCity.getText().toString().trim();
        String province = editTextProvince.getText().toString().trim();
        String postalCode = editTextPostalCode.getText().toString().trim();

        if (!street.isEmpty() && !city.isEmpty() && !province.isEmpty() && !postalCode.isEmpty()) {
            // Generate unique ID
            String id = db.collection("Facilities").document().getId();

            // Create Facility object
            Facility facility = new Facility(id, street, city, province, postalCode);

            // Save to Firestore
            db.collection("Facilities").document(id).set(facility)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(AddFacilityActivity.this, "Facility built", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(AddFacilityActivity.this, FacilityActivity.class));
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(AddFacilityActivity.this, "Failed to save facility", Toast.LENGTH_SHORT).show();
                        Log.w("AddFacilityActivity", "Error adding facility", e);
                    });
        } else {
            Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
        }
    }
}
