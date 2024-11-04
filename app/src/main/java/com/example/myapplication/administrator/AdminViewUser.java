package com.example.myapplication.administrator;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.objects.Event;
import com.example.myapplication.objects.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class AdminViewUser extends AppCompatActivity {
        private Users user;
        private ImageView profilePic;
        private TextView userName;
        private TextView role;
        private TextView facility;
        private ArrayAdapter<String> orgEventsAdapter;
        private ListView orgEvents;
        private ArrayAdapter<String> eventsAdapter;
        private ListView events;
        private Button back;
        private Button delete;
        private TextView facilityH;
        private TextView eventOrgH;
        private TextView eventsH;
        private FirebaseFirestore db;

        ArrayList<String> orgEventsList;
        ArrayList<String> eventsList;

    /**
     * on create for viewing an individual event from the Administrator view
     * @param savedInstanceState
     */
    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_admin_user);
            db = FirebaseFirestore.getInstance();

            // fetch the event from the other activity
            user = getIntent().getParcelableExtra("user");

            back = findViewById(R.id.adminUserBack);
            delete = findViewById(R.id.adminUserDelete);

            profilePic = findViewById(R.id.ProfilePicture);
            userName = findViewById(R.id.UserName);
            role = findViewById(R.id.UserRole);
            facility = findViewById(R.id.UserFacility);
            orgEvents = findViewById(R.id.UserOrgEvents);
            orgEventsAdapter = new ArrayAdapter<>(this, R.layout.textview);
            orgEvents.setAdapter(orgEventsAdapter);
            events = findViewById(R.id.UserInEvents);
            eventsAdapter = new ArrayAdapter<>(this, R.layout.textview);
            events.setAdapter(eventsAdapter);

            facilityH = findViewById(R.id.faciliyHeader);
            eventOrgH = findViewById(R.id.userOrgEventsHeader);
            eventsH = findViewById(R.id.userInEventsHeader);

            Glide.with(com.example.myapplication.administrator.AdminViewUser.this).load(user.getProfilePicture()).into(profilePic);
            userName.setText(user.getName());
            role.setText(user.getRole());

            if (Objects.equals(user.getRole(), "Organizer") || Objects.equals(user.getRole(), "Administrator")) {
                getOrgFacility(user.getUserId(), facility);
                getOrgEvents(user.getUserId());
            }

            getJoinedEvents(user.getUserId());

            back.setOnClickListener(view -> {
                finish();
            });

            delete.setOnClickListener(view -> {
                // TODO add delete functionality for Project part 4
            });

        }

    /**
     * Method to find organizer facility if it exists
     * @param id user id
     * @param facility Textview for the facility
     */
    private void getOrgFacility(String id, TextView facility) {
        db.collection("Facilities").document(id).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            String facilityName = " > " + document.getString("name");
                            facility.setText(facilityName);
                            facilityH.setText("Facility");
                        }
                    }
                });
    }

    /**
     * method to find organizer events if they exist
     * @param id user id
     */
    private void getOrgEvents(String id) {
        db.collection("events").whereEqualTo("creatorID", id).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            eventOrgH.setText("Organized Events");
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String event = document.getString("name");
                                orgEventsAdapter.add(" > " + event);
                                orgEventsAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                });
    }

    /**
     * method to find joined events if any exist
     * @param id user id
     */
    private void getJoinedEvents(String id) {
        db.collection("users").document(id).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        ArrayList<String> eventListFromDb = (ArrayList<String>) documentSnapshot.get("Event List");
                        if (eventListFromDb != null && !eventListFromDb.isEmpty()) {
                            eventsH.setText("Joined Events");
                            for (String event : eventListFromDb) {
                                insertJoinedEvents(event);
                            }
                        }
                    }
                });
    }

    /**
     * method to add an event to the list
     * @param eventId id of event added to list
     */
    private void insertJoinedEvents(String eventId) {
        db.collection("events").document(eventId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            String name = document.getString("name");
                            eventsAdapter.add(" > " + name);
                            eventsAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }
}
