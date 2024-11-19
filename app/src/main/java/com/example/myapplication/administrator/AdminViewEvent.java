/**
 * Activity for the viewing user data for the administrators
 * Used by the Administrator Only
 * @author Noah Vincent
 */

package com.example.myapplication.administrator;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.myapplication.administrator.fragments.DeleteEventFragment;
import com.example.myapplication.administrator.fragments.DeleteUserFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.objects.Event;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;

public class AdminViewEvent extends AppCompatActivity implements DeleteEventFragment.DeleteEventDialogListener {
    private Event event;
    private TextView title;
    private TextView description;
    private ImageView poster;
    private Button back;
    private Button delete;
    private TextView organizer;
    private TextView facility;
    private TextView descriptionHeader;
    private FirebaseFirestore db;



    /**
     * onCreate for viewing an individual event for te Administrator
     * @param savedInstanceState saved instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_event);
        db = FirebaseFirestore.getInstance();

        // fetch the event from the other activity
        event = getIntent().getParcelableExtra("event");

        back = findViewById(R.id.adminEventBack);
        delete = findViewById(R.id.adminEventDelete);

        title = findViewById(R.id.adminEventName);
        description = findViewById(R.id.eventDetail);
        descriptionHeader = findViewById(R.id.descHeader);

        poster = findViewById(R.id.poster);

        title.setText(event.getTitle());
        description.setText(event.getDescription());

        if (event.getPosterUrl() != null) {
            Glide.with(AdminViewEvent.this).load(event.getPosterUrl()).into(poster);
        }

        organizer = findViewById(R.id.organizer);
        facility = findViewById(R.id.facility);

        setOrgAndFac(event.getCreatorID(), organizer, facility);

        back.setOnClickListener(view -> {
            setResult(0);
            finish();
        });

        delete.setOnClickListener(view -> {
            new DeleteEventFragment(event).show(getSupportFragmentManager(), "Delete Event");
        });

    }

    /**
     * Method for retrieving the organizer name and facility name for a given event
     * @param id creator id
     * @param organizer TextView for organizer name
     * @param facility TextView for facility name
     */
    private void setOrgAndFac(String id, TextView organizer, TextView facility) {
        db.collection("users").document(id).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            String name = document.getString("Firstname") + ' ' + document.getString("Lastname");
                            organizer.setText(name);
                        }
                    }
                });
        db.collection("Facilities").document(id).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            String name = document.getString("name");
                            facility.setText(name);
                        }
                    }
                });
    }

    /**
     * deletes the event from Firebase
     * @param event the event to be deleted
     */
    @Override
    public void deleteEvent(Event event) {
        db.collection("events").document(event.getId()).delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        RemoveEvents(event.getId());
                    }
                });
        setResult(Activity.RESULT_OK);
        finish();
    }

    /**
     * method to remove the events from user lists
     * @param id event id
     */
    public void RemoveEvents(String id) {
        db.collection("users").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                ArrayList<String> list = (ArrayList<String>) document.get("Event List");
                                if (list != null) {
                                    if (list.contains(id)) {
                                        list.remove(id);
                                        DocumentReference documentReference = document.getReference();
                                        documentReference.update("Event List", list);
                                    }
                                }
                            }
                        }
                    }
                });
    }
}
