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
import com.example.myapplication.objects.Facility;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import android.text.Editable;
import android.text.TextWatcher;

/**
 * Activity for adding a new facility. This activity allows the user to input details
 * for a facility and save it to Firebase Firestore, ensuring no duplicate facilities
 * are created for the same organizer.
 */
public class AddFacilityActivity extends AppCompatActivity {

    private EditText editTextStreet, editTextCity, editTextProvince, editTextName, editTextDescription; // Input fields for facility address
    private Button buttonCreateFacility, buttonBackToFacility; // Buttons for creating facility and going back
    private FirebaseFirestore db; // Firebase Firestore instance
    private String organizerId; // Device ID used as organizer ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_facility_activity);

        db = FirebaseFirestore.getInstance();

        // Get device ID to use as organizer ID
        organizerId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.d("AddFacilityActivity", "Organizer Device ID: " + organizerId);

        // Initialize UI elements
        editTextStreet = findViewById(R.id.editTextStreet);
        editTextCity = findViewById(R.id.editTextCity);
        editTextProvince = findViewById(R.id.editTextProvince);
        editTextName = findViewById(R.id.editTextFacilityName);
        editTextDescription = findViewById(R.id.editTextFacilityDescription);
        buttonCreateFacility = findViewById(R.id.buttonCreateFacility);
        buttonBackToFacility = findViewById(R.id.buttonBackToFacility);

        // Add text watchers to city and province fields
        addTextWatcher(editTextCity, "City");
        addTextWatcher(editTextProvince, "Province");

        // Check if a facility already exists for this device ID
        checkIfFacilityExists();

        buttonCreateFacility.setOnClickListener(v -> createFacility());
        buttonBackToFacility.setOnClickListener(view -> finish());
    }

    /**
     * Adds a TextWatcher to the given EditText to check for numeric input
     * and show a Toast if the input contains numbers.
     *
     * @param editText The EditText to monitor
     * @param fieldName The name of the field for error message clarity
     */
    private void addTextWatcher(EditText editText, String fieldName) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().matches(".*\\d.*")) { // Check if input contains any digits
                    Toast.makeText(AddFacilityActivity.this, fieldName + " must contain only letters", Toast.LENGTH_SHORT).show();
                    editText.setText(s.toString().replaceAll("\\d", "")); // Remove any digits
                    editText.setSelection(editText.getText().length()); // Move cursor to end
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    /**
     * Checks if a facility already exists for the current organizer. If a facility exists,
     * it navigates back to the main activity and displays a message.
     */
    private void checkIfFacilityExists() {
        db.collection("Facilities").document(organizerId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Toast.makeText(this, "You have already added a facility.", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(AddFacilityActivity.this, OrganizerMainActivity.class));
                            finish();
                        }
                    } else {
                        Log.e("AddFacilityActivity", "Error checking facility: ", task.getException());
                    }
                });
    }

    /**
     * Creates a new facility using the information provided in the input fields.
     * It saves the facility to Firestore and navigates to the main activity upon success.
     */
    private void createFacility() {
        String name = editTextName.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        String street = editTextStreet.getText().toString().trim();
        String city = editTextCity.getText().toString().trim();
        String province = editTextProvince.getText().toString().trim();

        // Check if all input fields are filled
        if (!name.isEmpty() && !description.isEmpty() && !street.isEmpty() && !city.isEmpty() && !province.isEmpty()) {
            Facility facility = new Facility(organizerId, name, description, street, city, province);

            db.collection("Facilities").document(organizerId).set(facility)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(AddFacilityActivity.this, "Facility built", Toast.LENGTH_SHORT).show();
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
