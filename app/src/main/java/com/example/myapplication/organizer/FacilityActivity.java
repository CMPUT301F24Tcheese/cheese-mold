package com.example.myapplication.organizer;

import android.os.Bundle;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import com.example.myapplication.objects.Facility;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;


public class FacilityActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FacilityAdapter facilityAdapter;
    private ArrayList<Facility> facilityList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facility);

        db = FirebaseFirestore.getInstance();

        // Initialize RecyclerView and ArrayList
        recyclerView = findViewById(R.id.recyclerViewFacilities);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        facilityList = new ArrayList<>();
        facilityAdapter = new FacilityAdapter(facilityList);
        recyclerView.setAdapter(facilityAdapter);

        // Load facilities from Firebase when activity is created
        loadFacilitiesFromFirebase();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload facilities every time the activity resumes to ensure the latest data is displayed
        loadFacilitiesFromFirebase();
    }

    /**
     * Loads facilities from Firebase Firestore and updates the RecyclerView.
     */
    private void loadFacilitiesFromFirebase() {
        db.collection("Facilities").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        facilityList.clear(); // Clear the list to avoid duplicates
                        for (DocumentSnapshot document : task.getResult()) {
                            Facility facility = document.toObject(Facility.class);
                            facilityList.add(facility);
                        }
                        facilityAdapter.notifyDataSetChanged(); // Refresh adapter to show new data
                    } else {
                        Log.e("FacilityActivity", "Error loading facilities", task.getException());
                    }
                });
    }
}
