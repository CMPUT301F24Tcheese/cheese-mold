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

    private EditText editTextStreet, editTextCity, editTextProvince, editTextPostalCode;
    private Button buttonUpdateFacility, buttonCancel;
    private FirebaseFirestore db;
    private String facilityId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_facility);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize Views
        editTextStreet = findViewById(R.id.editTextStreet);
        editTextCity = findViewById(R.id.editTextCity);
        editTextProvince = findViewById(R.id.editTextProvince);
        editTextPostalCode = findViewById(R.id.editTextPostalCode);
        buttonUpdateFacility = findViewById(R.id.buttonUpdateFacility);
        buttonCancel = findViewById(R.id.buttonCancel);

        // Get Facility data from intent
        Intent intent = getIntent();
        facilityId = intent.getStringExtra("facilityId");
        editTextStreet.setText(intent.getStringExtra("street"));
        editTextCity.setText(intent.getStringExtra("city"));
        editTextProvince.setText(intent.getStringExtra("province"));
        editTextPostalCode.setText(intent.getStringExtra("postalCode"));

        // Update Facility Button
        buttonUpdateFacility.setOnClickListener(view -> updateFacility());

        // Cancel Button
        buttonCancel.setOnClickListener(view -> finish());
    }

    private void updateFacility() {
        String street = editTextStreet.getText().toString().trim();
        String city = editTextCity.getText().toString().trim();
        String province = editTextProvince.getText().toString().trim();
        String postalCode = editTextPostalCode.getText().toString().trim();

        if (!street.isEmpty() && !city.isEmpty() && !province.isEmpty() && !postalCode.isEmpty()) {
            // Create updated Facility object
            Facility updatedFacility = new Facility(facilityId, street, city, province, postalCode);

            // Save updates to Firestore
            db.collection("Facilities").document(facilityId)
                    .set(updatedFacility, SetOptions.merge())
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(EditFacilityActivity.this, "Facility updated", Toast.LENGTH_SHORT).show();
                        finish(); // Return to previous screen
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(EditFacilityActivity.this, "Update failed", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
        }
    }
}
