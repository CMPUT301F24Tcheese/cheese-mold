package com.example.myapplication.administrator;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.objects.QRCode;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AdminBrowseImages extends AppCompatActivity {
    private FirebaseFirestore db;
    private Button back;
    private TextView header;
    private ListView imageList;
    private ImageArrayAdapter imageAdapter;
    private ArrayList<Image> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_browse);
        db = FirebaseFirestore.getInstance();

        back = findViewById(R.id.back_button);

        header = findViewById(R.id.browseHeader);
        header.setText("Images");

        dataList = new ArrayList<Image>();

        imageList = findViewById(R.id.contentListView);
        imageAdapter = new ImageArrayAdapter(this, dataList);
        imageList.setAdapter(imageAdapter);

        db.collection("events").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String url = document.getString("posterUrl");
                                String type = "Event Poster";
                                String id = document.getId();
                                Image thing = new Image(url, type, id);
                                dataList.add(thing);
                                imageAdapter.notifyDataSetChanged();
                            }
                        } else {
                            Log.d("AdminBrowseFacilities", "Error getting documents: ", task.getException());
                        }
                    }
                });
        db.collection("users").get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        String url = document.getString("Profile Picture");
                                        String type = "Profile Picture";
                                        String id = document.getId();
                                        Image thing = new Image(url, type, id);
                                        dataList.add(thing);
                                        imageAdapter.notifyDataSetChanged();
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
