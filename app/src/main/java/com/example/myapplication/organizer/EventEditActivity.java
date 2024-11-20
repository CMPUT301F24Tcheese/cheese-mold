/**
 * Activity for editing existing event, involving database information update,
 * poster update and event deletion
 */
package com.example.myapplication.organizer;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.objects.Event;
import com.example.myapplication.organizer.OrganizerMainActivity;
import com.example.myapplication.organizer.OrganizerNotificationActivity;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;
import java.util.UUID;

import javax.microedition.khronos.opengles.GL;

public class EventEditActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private String eventId;
    private Button buttonEditEventDetail, buttonBack, buttonDeleteEvent,buttonNotification, buttonQrCode;
    private String qrCodeUrl;
    private Button buttonViewLists;

    /**
     * onCreate function for the edit event activity
     * @param savedInstanceState saved instances
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_edit);

        db = FirebaseFirestore.getInstance();

        eventId = getIntent().getStringExtra("event_id");
        if (eventId == null || eventId.isEmpty()) {
            Toast.makeText(this, "Event ID not provided", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        buttonEditEventDetail = findViewById((R.id.buttonEditEventDetail));
        buttonBack = findViewById(R.id.buttonBack);
        buttonDeleteEvent = findViewById(R.id.buttonDeleteEvent);
        buttonNotification = findViewById(R.id.buttonNotification);
        buttonQrCode = findViewById(R.id.buttonQRCode);
        buttonViewLists = findViewById(R.id.buttonViewLists);

        loadEventData(eventId);

        buttonBack.setOnClickListener(view -> finish());
        buttonDeleteEvent.setOnClickListener(view -> deleteEvent());
        buttonNotification.setOnClickListener(view -> {
            Intent intent = new Intent(EventEditActivity.this, OrganizerNotificationActivity.class);
            intent.putExtra("event_id", eventId); // Pass event ID to OrganizerNotificationActivity
            startActivity(intent);
        });
        buttonQrCode.setOnClickListener(view -> {
            Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.image_dialog);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            ImageView imageView = dialog.findViewById(R.id.dialogImageView);

            Glide.with(this).load(qrCodeUrl).into(imageView);
            dialog.show();
        });

        buttonViewLists.setOnClickListener(view -> {
            Intent intent = new Intent(EventEditActivity.this, ListOptionsActivity.class);
            intent.putExtra("event_id", eventId);
            startActivity(intent);
        });

        buttonEditEventDetail.setOnClickListener(view -> {
            Intent intent = new Intent(EventEditActivity.this, EditEventDetailActivity.class);
            intent.putExtra("event_id", eventId);
            startActivity(intent);
        });
    }

    /**
     * This method get the event data from database
     * @param eventId the current eventID
     */
    private void loadEventData(String eventId) {
        DocumentReference eventRef = db.collection("events").document(eventId);
        eventRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Event event = task.getResult().toObject(Event.class);
                if (event != null) {
                    qrCodeUrl = event.getQRcode();
                } else {
                    Toast.makeText(EventEditActivity.this, "Event not found", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(EventEditActivity.this, "Failed to load event", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * This method deletes the event from the database
     */
    private void deleteEvent() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Event")
                .setMessage("Are you sure you want to delete this event?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    DocumentReference eventRef = db.collection("events").document(eventId);
                    eventRef.delete()
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(EventEditActivity.this, "Event deleted successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(EventEditActivity.this, OrganizerMainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            })
                            .addOnFailureListener(e -> Toast.makeText(EventEditActivity.this, "Failed to delete event", Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton("Cancel", null)
                .show();
    }


}
