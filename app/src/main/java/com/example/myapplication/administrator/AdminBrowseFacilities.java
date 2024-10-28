package com.example.myapplication.administrator;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.objects.Facility;
import com.example.myapplication.objects.FacilityArrayAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AdminBrowseFacilities extends AppCompatActivity {
    private FirebaseFirestore db;
    private Button back;
    private TextView header;
    private ListView facilityList;
    private FacilityArrayAdapter facilityAdapter;
    private ArrayList<Facility> dataList;

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

        db.collection("Facilities").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String id = document.getString("id");
                                String street = document.getString("street");
                                String city = document.getString("city");
                                String province = document.getString("province");
                                String postalCode = document.getString("postalCode");
                                Facility thing = new Facility(id, street, city, province, postalCode);
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
    }


}
