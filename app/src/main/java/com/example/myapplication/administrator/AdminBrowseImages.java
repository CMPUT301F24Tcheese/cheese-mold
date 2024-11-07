/**
 * The activity for the Browsing images
 * Used by Administrators only
 * @author Noah Vincent
 */
package com.example.myapplication.administrator;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
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
    private TextView posterTextView;
    private TextView profileTextView;
    private LinearLayout posters;
    private LinearLayout profiles;
    private ListView profileList;
    private ListView posterList;
    private ImageArrayAdapter profileAdapter;
    private PosterArrayAdapter posterAdapter;
    private ArrayList<Image> dataListProfiles;
    private ArrayList<Image> dataListPosters;

    /**
     * onCreate function for displaying Image information
     * @param savedInstanceState saved instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_browse_images);
        db = FirebaseFirestore.getInstance();

        back = findViewById(R.id.back_button);

        dataListProfiles = new ArrayList<Image>();
        dataListPosters = new ArrayList<Image>();

        profileList = findViewById(R.id.profileListView);
        profileAdapter = new ImageArrayAdapter(this, dataListProfiles);
        profileList.setAdapter(profileAdapter);

        posterList = findViewById(R.id.posterListView);
        posterAdapter = new PosterArrayAdapter(this, dataListPosters);
        posterList.setAdapter(posterAdapter);

        posterTextView = findViewById(R.id.postersTextView);
        profileTextView = findViewById(R.id.profilesTextView);

        posters = findViewById(R.id.viewPostersLayout);
        profiles = findViewById(R.id.viewProfilesLayout);

        showPosters();

        posterTextView.setOnClickListener(view -> {
            showPosters();
        });

        profileTextView.setOnClickListener(view -> {
            showProfiles();
        });

        back.setOnClickListener(view -> {
            finish();
        });
    }

    /**
     * set posters to visible and profiles to invisible
     */
    private void showPosters() {
        posters.setVisibility(View.VISIBLE);
        profiles.setVisibility(View.GONE);

        dataListPosters.clear();
        // searching firebase for events to retrieve poster images
        db.collection("events").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String url = document.getString("posterUrl");
                                String type = document.getString("name");
                                String id = document.getId();
                                if (url != null) {
                                    Image thing = new Image(url, type, id);
                                    dataListPosters.add(thing);
                                    posterAdapter.notifyDataSetChanged();
                                }
                            }
                        } else {
                            Log.d("AdminBrowseFacilities", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    /**
     * set profiles to visible and posters to invisible
     */
    private void showProfiles() {
        posters.setVisibility(View.GONE);
        profiles.setVisibility(View.VISIBLE);

        dataListProfiles.clear();
        // searching firebase for users to receive profile pictures
        db.collection("users").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String url = document.getString("Profile Picture");
                                String type = document.getString("Firstname") + " " + document.getString("Lastname");
                                String id = document.getId();
                                Image thing = new Image(url, type, id);
                                dataListProfiles.add(thing);
                                profileAdapter.notifyDataSetChanged();
                            }
                        } else {
                            Log.d("AdminBrowseFacilities", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }


}
