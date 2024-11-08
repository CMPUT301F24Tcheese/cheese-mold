/**
 * Activity for displaying a list of facilities.
 * This activity retrieves facility data from Firestore and displays it in a RecyclerView.
 */
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

/**
 * FacilityActivity is responsible for displaying facilities using a RecyclerView.
 * It loads facility data from Firestore and updates the UI accordingly.
 */
public class FacilityActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FacilityAdapter facilityAdapter;
    private ArrayList<Facility> facilityList;
    private FirebaseFirestore db;

    /**
     * Initializes the activity, sets up the RecyclerView, and loads data from Firestore.
     *
     * @param savedInstanceState The saved instance state for restoring the activity.
     */
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

    /**
     * Reloads the facilities when the activity resumes to display the latest data.
     */
    @Override
    protected void onResume() {
        super.onResume();
        // Reload facilities every time the activity resumes to ensure the latest data is displayed
        loadFacilitiesFromFirebase();
    }

    /**
     * Loads facilities from Firebase Firestore and updates the RecyclerView.
     * If the task is successful, the facility list is populated and the adapter is notified.
     * In case of an error, a log message is displayed.
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
