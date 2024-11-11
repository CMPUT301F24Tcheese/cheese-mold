/**
 * Activity for browsing facilities
 * Used by the Administrator Only
 * @author Noah Vincent
 * @Issue Facility browse button crashes the app
 */

package com.example.myapplication.administrator;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.administrator.fragments.DeleteFacilityFragment;
import com.example.myapplication.objects.Facility;
import com.example.myapplication.objects.FacilityArrayAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AdminBrowseFacilities extends AppCompatActivity implements DeleteFacilityFragment.DeleteFacilityDialogueListener {
    private FirebaseFirestore db;
    private Button back;
    private TextView header;
    private ListView facilityList;
    private FacilityArrayAdapter facilityAdapter;
    private ArrayList<Facility> dataList;

    /**
     * Method to delete facilities that violate app policy
     * @param facility the facility being deleted
     */
    @Override
    public void DeleteFacility(Facility facility) {
        db.collection("Facilities").document(facility.getId()).delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        facilityAdapter.remove(facility);
                                        facilityAdapter.notifyDataSetChanged();
                                    }
                                });
    }

    /**
     * onCreate function for displaying Facility information
     * @param savedInstanceState saved instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_browse);
        db = FirebaseFirestore.getInstance();

        back = findViewById(R.id.back_button);

        header = findViewById(R.id.browseHeader);
        header.setText("FACILITIES");

        dataList = new ArrayList<Facility>();

        facilityList = findViewById(R.id.contentListView);
        facilityAdapter = new FacilityArrayAdapter(this, dataList);
        facilityList.setAdapter(facilityAdapter);

        // searching firebase to get all existing facility information
        db.collection("Facilities").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String id = document.getString("id");
                                String name = document.getString("name");
                                String description = document.getString("description");
                                String street = document.getString("street");
                                String city = document.getString("city");
                                String province = document.getString("province");
                                Facility thing = new Facility(id, name, description, street, city, province);
                                dataList.add(thing);
                                facilityAdapter.notifyDataSetChanged();
                            }
                        } else {
                            Log.d("AdminBrowseFacilities", "Error getting documents: ", task.getException());
                        }
                    }
                });

        back.setOnClickListener(view -> {
            finish();
        });

        facilityList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Facility facility = dataList.get(i);
                new DeleteFacilityFragment(facility).show(getSupportFragmentManager(), "Delete Facility");
                return false;
            }
        });
    }


}
