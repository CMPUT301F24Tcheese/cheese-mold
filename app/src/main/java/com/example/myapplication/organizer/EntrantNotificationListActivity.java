package com.example.myapplication.organizer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * NOTE: !!! This file does not have any usage for current version, therefore NO Test Case for this java class !!!
 * This activity displays a list of entrants for an event and allows the user to select entrants for notifications.
 * The list can display either users with a specific role or entrants on the event waitlist.
 */

public class EntrantNotificationListActivity extends AppCompatActivity {

    private RecyclerView recyclerViewEntrants;
    private EntrantListAdapter entrantListAdapter;
    private List<String> entrantList;
    private FirebaseFirestore db;
    private String eventId;
    private boolean isChosenEntrantsMode;
    private ArrayList<String> selectedEntrants, entrantListDisplay;

    /**
     * Called when the activity is first created. Sets up the UI and initializes the data loading.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this contains the most recent data supplied in onSaveInstanceState(Bundle).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entrant_notification_list);

        db = FirebaseFirestore.getInstance();

        recyclerViewEntrants = findViewById(R.id.recyclerViewEntrants);
        recyclerViewEntrants.setLayoutManager(new LinearLayoutManager(this));
        entrantList = new ArrayList<>();
        entrantListDisplay = new ArrayList<>();

        // Retrieve previously selected entrants from Intent
        selectedEntrants = getIntent().getStringArrayListExtra("selectedEntrants");
        if (selectedEntrants == null) {
            selectedEntrants = new ArrayList<>();
        }

        // Pass selectedEntrants to adapter
        entrantListAdapter = new EntrantListAdapter(entrantList, selectedEntrants, entrantListDisplay);
        recyclerViewEntrants.setAdapter(entrantListAdapter);

        eventId = getIntent().getStringExtra("event_id");
        isChosenEntrantsMode = getIntent().getBooleanExtra("isChosenEntrantsMode", false);

        if (eventId == null) {
            Toast.makeText(this, "Event ID not provided", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadEntrants();

        // Save button to return selected entrants
        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(view -> {
            ArrayList<String> updatedSelectedEntrants = new ArrayList<>(entrantListAdapter.getSelectedEntrants());
            Intent resultIntent = new Intent();
            resultIntent.putStringArrayListExtra("selectedEntrants", updatedSelectedEntrants);
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }

    /**
     * Loads the list of entrants based on the mode (chosen entrants or event waitlist).
     */
    private void loadEntrants() {
        if (isChosenEntrantsMode) {
            db.collection("users")
                    .whereEqualTo("role", "Entrant")
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        entrantList.clear();
                        if (!querySnapshot.isEmpty()) {
                            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                String userId = document.getId();
                                String firstName = document.getString("Firstname");
                                String lastName = document.getString("Lastname");
                                String fullName = firstName + " " + lastName;
                                entrantListDisplay.add(fullName);
                                entrantList.add(userId);
                            }
                            entrantListAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(this, "No entrants found with role 'entrant'", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.w("EntrantNotificationList", "Failed to load entrants", e);
                        Toast.makeText(this, "Failed to load entrants", Toast.LENGTH_SHORT).show();
                    });
        } else {
            db.collection("events").document(eventId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null && document.exists()) {
                                List<String> waitlist = (List<String>) document.get("waitlist");
                                if (waitlist != null) {
                                    entrantList.clear();
                                    for (String deviceId : waitlist) {
                                        db.collection("users").document(deviceId)
                                                .get()
                                                .addOnSuccessListener(userDoc -> {
                                                    if (userDoc.exists()) {
                                                        String firstName = userDoc.getString("Firstname");
                                                        String lastName = userDoc.getString("Lastname");
                                                        String fullName = firstName + " " + lastName;
                                                        entrantList.add(fullName);
                                                        entrantListAdapter.notifyDataSetChanged();
                                                    }
                                                })
                                                .addOnFailureListener(e -> Log.w("EntrantNotificationList", "Failed to load user data", e));
                                    }
                                } else {
                                    Toast.makeText(this, "No entrants found in the waiting list", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(this, "Event not found", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(this, "Failed to load entrants", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
