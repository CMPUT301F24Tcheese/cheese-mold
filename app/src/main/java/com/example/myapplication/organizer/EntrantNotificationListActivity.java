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

public class EntrantNotificationListActivity extends AppCompatActivity {

    private RecyclerView recyclerViewEntrants;
    private EntrantListAdapter entrantListAdapter;
    private List<String> entrantList;
    private FirebaseFirestore db;
    private String eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entrant_notification_list);

        db = FirebaseFirestore.getInstance();

        recyclerViewEntrants = findViewById(R.id.recyclerViewEntrants);
        recyclerViewEntrants.setLayoutManager(new LinearLayoutManager(this));
        entrantList = new ArrayList<>();

        // Retrieve previously selected entrants from the Intent
        ArrayList<String> preSelectedEntrants = getIntent().getStringArrayListExtra("selectedEntrants");
        entrantListAdapter = new EntrantListAdapter(entrantList, preSelectedEntrants);
        recyclerViewEntrants.setAdapter(entrantListAdapter);

        // Retrieve the event ID from the Intent
        eventId = getIntent().getStringExtra("event_id");
        if (eventId == null) {
            Toast.makeText(this, "Event ID not provided", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadEntrants();

        // Save button to return selected entrants
        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(view -> {
            ArrayList<String> selectedEntrants = new ArrayList<>(entrantListAdapter.getSelectedEntrants());
            Intent resultIntent = new Intent();
            resultIntent.putStringArrayListExtra("selectedEntrants", selectedEntrants);
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }

    private void loadEntrants() {
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
