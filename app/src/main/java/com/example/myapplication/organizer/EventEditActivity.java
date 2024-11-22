/**
 * Activity for editing existing event, involving database information update,
 * poster update and event deletion
 */
package com.example.myapplication.organizer;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.myapplication.EventDetailActivity;
import com.example.myapplication.R;
import com.example.myapplication.objects.Event;
import com.example.myapplication.objects.Lottery;
import com.example.myapplication.objects.WaitingList;
import com.example.myapplication.organizer.OrganizerMainActivity;
import com.example.myapplication.organizer.OrganizerNotificationActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import javax.microedition.khronos.opengles.GL;

public class EventEditActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private String eventId;
    private Button buttonEditEventDetail, buttonBack, buttonDeleteEvent,buttonNotification, buttonQrCode, buttonLottery;
    private String qrCodeUrl;
    private Event eventToLoad;


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
        buttonLottery = findViewById(R.id.button_lottery);

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

        buttonEditEventDetail.setOnClickListener(view -> {
            Intent intent = new Intent(EventEditActivity.this, EditEventDetailActivity.class);
            intent.putExtra("event_id", eventId);
            startActivity(intent);
        });

        buttonLottery.setOnClickListener(view -> {
           db.collection("events").document(eventId)
                   .get()
                   .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot doc = task.getResult();
                                if (doc.exists()) {

                                    eventToLoad = doc.toObject(Event.class);
                                    if (eventToLoad != null) {
                                        eventToLoad.setId(doc.getId());

                                        // Retrieve cloud object to local
                                        ArrayList<String> fireWaitlist = (ArrayList<String>) doc.get("waitlist");
                                        ArrayList<String> fireLotteryList = (ArrayList<String>) doc.get("lotteryList");



                                        eventToLoad.setWaitingList(fireWaitlist);
                                        ArrayList<String> waitlist = eventToLoad.getWaitingList();

                                        Log.d("Local", "Retrieved waitlist: " + waitlist.toString());

                                        //set the data from firebase to Long
                                        Long maxCapacity = doc.getLong("maxCapacity");
                                        if (maxCapacity != null) {
                                            eventToLoad.setFinalEntrantsNum(maxCapacity);
                                        }


                                        if (waitlist != null && !waitlist.isEmpty()) {

                                            // The Organizer only need to set the max capacity of the event once.
                                            if (eventToLoad.getDrawAmount() == 0){
                                                AlertDialog.Builder builder = new AlertDialog.Builder(EventEditActivity.this);

                                                final EditText input = new EditText(EventEditActivity.this);
                                                input.setHint("Type something...");
                                                input.setInputType(android.text.InputType.TYPE_CLASS_TEXT);
                                                builder.setView(input)
                                                        .setTitle("Draw Lottery")
                                                        .setMessage("Please enter the capacity for the event")
                                                        .setPositiveButton("Confirm", (dialog, which) -> {
                                                            // Perform the lottery draw
                                                            String inputText = input.getText().toString();

                                                            try {
                                                                    long inputLong = Long.parseLong(inputText);
                                                                    Toast.makeText(EventEditActivity.this, "Capacity of this event: " + inputLong, Toast.LENGTH_SHORT).show();

                                                                    eventToLoad.setFinalEntrantsNum(inputLong);
                                                                    eventToLoad.setDrawAmount(eventToLoad.getDrawAmount() + 1);




                                                            } catch (NumberFormatException e) {
                                                                    Toast.makeText(EventEditActivity.this, "Invalid Input", Toast.LENGTH_SHORT).show();
                                                            }

                                                            //
                                                            if (eventToLoad.getLottery().size() == eventToLoad.getFinalEntrantsNum() && eventToLoad.getFinalEntrantsNum() > 0){
                                                                Toast.makeText(EventEditActivity.this, "The Lottery is full, Please wait for Entrant response", Toast.LENGTH_SHORT).show();
                                                            }
                                                            else {
                                                                eventToLoad.drawLottery();
                                                                Log.d("Local", "After draw: " + waitlist.toString());
                                                                updateFirebaseLottery(eventId, eventToLoad);
                                                            }


                                                        })
                                                        .setNegativeButton("Cancel", null)
                                                        .show();
                                            }




                                        }

                                        else {
                                            Toast.makeText(EventEditActivity.this, "Wait list is empty, cannot make draws", Toast.LENGTH_SHORT).show();

                                        }

                                    }
                                } else {
                                    Toast.makeText(EventEditActivity.this, "Event not found", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Log.w("Firestore", "Error getting document", task.getException());
                            }
                        }
                    });

        });
    }


    /**
     * This update the firbase LotteryList and WaitingList after the draw
     */
    public void updateFirebaseLottery(String eventId, Event eventToLoad) {
        db.collection("events").document(eventId)
                .update("lotteryList", (List<String>) eventToLoad.getLottery(),
                        "waitlist", (List<String>) eventToLoad.getWaitingList(),
                        "DrawAmount", (long) eventToLoad.getDrawAmount(),
                        "maxCapacity", (long) eventToLoad.getFinalEntrantsNum())
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "Array updated successfully!");
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error updating array: " + e.getMessage());
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
