package com.example.myapplication.organizer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.R;
import com.example.myapplication.objects.Event;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

public class EventEditActivity extends AppCompatActivity {

    private EditText editTextTitle, editTextDescription, editTextDateTime, editTextLimitEntrants;
    private Button buttonUpdateEvent, buttonCancel;
    private FirebaseFirestore db;
    private String eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_edit);

        db = FirebaseFirestore.getInstance();

        editTextTitle = findViewById(R.id.editTextTitle);
        editTextDescription = findViewById(R.id.editTextDescription);
        editTextDateTime = findViewById(R.id.editTextDateTime);
        editTextLimitEntrants = findViewById(R.id.editTextLimitEntrants);

        Intent intent = getIntent();
        if (intent != null) {
            eventId = intent.getStringExtra("event_id");
            String title = intent.getStringExtra("title");
            String description = intent.getStringExtra("description");
            String dateTime = intent.getStringExtra("dateTime");
            Long limitEntrants = intent.getLongExtra("limitEntrants", 0);

            editTextTitle.setText(title);
            editTextDescription.setText(description);
            editTextDateTime.setText(dateTime);
            editTextLimitEntrants.setText(String.valueOf(limitEntrants));
        }

        buttonUpdateEvent = findViewById(R.id.buttonUpdateEvent);
        buttonCancel = findViewById(R.id.buttonCancel);

        buttonUpdateEvent.setOnClickListener(view -> updateEvent());
        buttonCancel.setOnClickListener(view -> finish());
    }

    private void updateEvent() {
        String title = editTextTitle.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        String dateTime = editTextDateTime.getText().toString().trim();
        Long limitEntrants = Long.parseLong(editTextLimitEntrants.getText().toString().trim());

        if (!title.isEmpty() && !description.isEmpty() && !dateTime.isEmpty()) {
            Event updatedEvent = new Event(eventId, title, description, null, limitEntrants);
            updatedEvent.setDate(dateTime);

            db.collection("events").document(eventId)
                    .set(updatedEvent, SetOptions.merge())
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(EventEditActivity.this, "Event updated", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(EventEditActivity.this, "Update failed", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
        }
    }
}
