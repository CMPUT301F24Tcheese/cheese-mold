package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;

public class FacilityActivity extends AppCompatActivity {
    private RecyclerView recyclerViewFacilities;
    private FacilityAdapter facilityAdapter;
    private List<Facility> facilityList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facility);

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize RecyclerView and Adapter
        recyclerViewFacilities = findViewById(R.id.recyclerViewFacilities);
        recyclerViewFacilities.setLayoutManager(new LinearLayoutManager(this));
        facilityList = new ArrayList<>();
        facilityAdapter = new FacilityAdapter(facilityList);
        recyclerViewFacilities.setAdapter(facilityAdapter);

        // Load facilities from Firestore
        loadFacilitiesFromFirestore();

        // Button to navigate back to EventActivity
        Button buttonBackToEvents = findViewById(R.id.buttonBackToEvents);
        buttonBackToEvents.setOnClickListener(view -> {
            finish(); // Close FacilityActivity and return to EventActivity
        });

        // Floating Action Button to add a new Facility
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            // Navigate to Add Facility Activity
            startActivity(new Intent(FacilityActivity.this, AddFacilityActivity.class));
        });
    }

    private void loadFacilitiesFromFirestore() {
        CollectionReference facilityCollection = db.collection("Facilities");

        facilityCollection.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@NonNull QuerySnapshot value, @NonNull FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w("FacilityActivity", "Listen failed.", error);
                    return;
                }

                if (value != null) {
                    // Update the facility list and notify the adapter
                    facilityList.clear();
                    facilityList.addAll(value.toObjects(Facility.class));
                    facilityAdapter.notifyDataSetChanged(); // Notify adapter to refresh the list
                }
            }
        });
    }
}
