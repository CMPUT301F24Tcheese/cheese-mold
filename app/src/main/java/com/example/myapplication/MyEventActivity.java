package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;

public class MyEventActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EventAdapter eventAdapter;
    private List<Event> eventList;
    private FirebaseFirestore db;
    Users user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_event);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize RecyclerView and Adapter
        recyclerView = findViewById(R.id.myOwnEvents);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventList = new ArrayList<>();
        eventAdapter = new EventAdapter(eventList);
        recyclerView.setAdapter(eventAdapter);

        Intent intentFromMain = getIntent();
        String email = intentFromMain.getStringExtra("email");
        user = new Users(email); // Initialize the User with its email



        // Load Events from Firestore
        loadEventsFromFirestore();


        // Profile and Email ImageView Listeners
        ImageView profileImage = findViewById(R.id.profileImage);
        profileImage.setOnClickListener(v -> {
            Intent intent = new Intent(MyEventActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        ImageView emailImage = findViewById(R.id.emailImage);
        emailImage.setOnClickListener(v -> {
            Intent intent = new Intent(MyEventActivity.this, EmailActivity.class);
            startActivity(intent);
        });


        // when click the event on the event list, it directs to EventDetailActivity that shows the details of the event.
        // The EventDetail activity allow the user to join the activity.
        eventAdapter.setOnItemClickListener(event -> {
            Intent intent = new Intent(MyEventActivity.this, EventDetailActivity.class);
            intent.putExtra("event", event); // send the event to new activity
            intent.putExtra("user",user); // send user to new activity
            startActivity(intent);
        });

    }

    private void loadEventsFromFirestore() {
        CollectionReference eventCollection = db.collection("MyEvents");

        eventCollection.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@NonNull QuerySnapshot value, @NonNull FirebaseFirestoreException error) {
                if (error != null) {
                    return;
                }

                if (value != null) {
                    eventList.clear();
                    eventList.addAll(value.toObjects(Event.class));
                    eventAdapter.notifyDataSetChanged();
                }
            }
        });
    }





}
