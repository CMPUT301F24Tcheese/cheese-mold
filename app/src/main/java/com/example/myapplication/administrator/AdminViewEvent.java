package com.example.myapplication.administrator;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.objects.Event;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdminViewEvent extends AppCompatActivity {
    Event event;
    TextView title;
    TextView description;
    ImageView poster;
    Button back;
    Button delete;
    FirebaseFirestore db;

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

        poster = findViewById(R.id.poster);

        title.setText(event.getTitle());
        description.setText(event.getDescription());

        Glide.with(AdminViewEvent.this).load(event.getPosterUrl()).into(poster);

        back.setOnClickListener(view -> {
            finish();
        });

        delete.setOnClickListener(view -> {
            // TODO add delete functionality for Project part 4
        });

    }
}
