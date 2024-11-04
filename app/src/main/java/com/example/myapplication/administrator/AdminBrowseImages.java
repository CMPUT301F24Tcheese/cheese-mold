/**
 * The activity for the Browsing images
 * Used by Administrators only
 */
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
import com.example.myapplication.administrator.ImageArrayAdapter;
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
    private ListView profileList;
    private ListView posterList;
    private ImageArrayAdapter profileAdapter;
    private PosterArrayAdapter posterAdapter;
    private ArrayList<Image> dataList;
    private ArrayList<Image> dataListTwo;

    /**
     * onCreate function for displaying Image information
     * @param savedInstanceState saved instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_browse);
        db = FirebaseFirestore.getInstance();

        back = findViewById(R.id.back_button);

        header = findViewById(R.id.browseHeader);
        header.setText("Images");

        dataList = new ArrayList<Image>();
        dataListTwo = new ArrayList<Image>();

        profileList = findViewById(R.id.contentListView);
        profileAdapter = new ImageArrayAdapter(this, dataList);
        profileList.setAdapter(profileAdapter);

        posterList = findViewById(R.id.contentListViewTwo);
        posterAdapter = new PosterArrayAdapter(this, dataListTwo);
        posterList.setAdapter(posterAdapter);

        // searching firebase for events to retrieve poster images
        db.collection("events").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String url = document.getString("posterUrl");
                                String type = "Event Poster";
                                String id = document.getId();
                                if (url != null) {
                                    Image thing = new Image(url, type, id);
                                    dataListTwo.add(thing);
                                    posterAdapter.notifyDataSetChanged();
                                }
                            }
                        } else {
                            Log.d("AdminBrowseFacilities", "Error getting documents: ", task.getException());
                        }
                    }
                });
        // searching firebase for users to receive profile pictures
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
                                        profileAdapter.notifyDataSetChanged();
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
