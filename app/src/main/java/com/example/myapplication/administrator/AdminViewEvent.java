package com.example.myapplication.administrator;

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

public class AdminViewEvent extends AppCompatActivity {
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

        Glide.with(AdminViewEvent.this).load(event.getPosterUrl()).into(poster);

        organizer = findViewById(R.id.organizer);
        facility = findViewById(R.id.facility);

        setOrgAndFac(event.getCreatorID(), organizer, facility);

        back.setOnClickListener(view -> {
            finish();
        });

        delete.setOnClickListener(view -> {
            // TODO add delete functionality for Project part 4
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
}
