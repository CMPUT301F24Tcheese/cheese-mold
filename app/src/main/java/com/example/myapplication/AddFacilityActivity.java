package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.List;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class AddFacilityActivity extends AppCompatActivity {

    private EditText editTextStreet, editTextCity, editTextProvince, editTextPostalCode;
    private Button buttonCreateFacility;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_facility_activity);

        editTextStreet = findViewById(R.id.editTextStreet);
        editTextCity = findViewById(R.id.editTextCity);
        editTextProvince = findViewById(R.id.editTextProvince);
        editTextPostalCode = findViewById(R.id.editTextPostalCode);
        buttonCreateFacility = findViewById(R.id.buttonCreateFacility);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        buttonCreateFacility.setOnClickListener(v -> createFacility());
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
                        // Return to FacilityActivity
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

    // Optional: Method for loading facilities (if needed for future extensions)
    private void loadFacilitiesFromFirestore() {
        CollectionReference facilityCollection = db.collection("Facilities");

        facilityCollection.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@NonNull QuerySnapshot value, @NonNull FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w("AddFacilityActivity", "Listen failed.", error);
                    return;
                }

                if (value != null) {
                    // You can use this method to handle changes or updates to facilities, if needed.
                }
            }
        });
    }
}
