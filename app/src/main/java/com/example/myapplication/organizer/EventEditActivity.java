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
import com.example.myapplication.notifications.Notification;
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
    private Button buttonViewLists;
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
        buttonViewLists = findViewById(R.id.buttonViewLists);
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

        buttonLottery.setOnClickListener(view -> {
            loadEventData(eventId);
            db.collection("events").document(eventId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();
                            if (doc != null && doc.exists()) {
                                handleLottery();
                            } else {
                                showToast("Event not found");
                            }
                        } else {
                            Log.w("Firestore", "Error getting document", task.getException());
                        }
                    });
        });
    }

    /***
     * This is the helper function to handle the lottery draw
     * if it is the first draw, it calls the Capacity dialogue to fill in, it it isn't it will make the draw.
     */
    private void handleLottery() {
            ArrayList<String> waitlist = eventToLoad.getWaitingList();


            if (waitlist == null || waitlist.isEmpty()) {
                showToast("Wait list is empty, cannot make draws");
                return;
            }

            if (eventToLoad.getFirstDraw()) {
                promptForCapacity();
            } else {
                processLottery();
            }
        }

    /**
     * A helper function to pop the Capacity dialogue
     * It records user's response and store in the event object
     * It will proceed with the draw once confirm button is hit
     */
    private void promptForCapacity() {
            EditText input = new EditText(this);
            input.setHint("Type something...");
            input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);

            new AlertDialog.Builder(this)
                    .setTitle("Draw Lottery")
                    .setMessage("Please enter the capacity for the event")
                    .setView(input)
                    .setPositiveButton("Confirm", (dialog, which) -> {
                        try {
                            long inputLong = Long.parseLong(input.getText().toString());
                            eventToLoad.setFinalEntrantsNum(inputLong);
                            eventToLoad.setFirstDraw(false);
                            processLottery();


                        } catch (NumberFormatException e) {
                            showToast("Invalid Input"); // gives error when something else are typed
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        }

    /**
     * this function check the size of the Lottery list.
     * It gives a warning message if the Lottery list is full, else it proceeds with the draw
     */
    private void processLottery() {
            if (eventToLoad.getLottery().size() >= eventToLoad.getFinalEntrantsNum() && eventToLoad.getFinalEntrantsNum() > 0) {
                showToast("The Lottery is full, Please wait for Entrant response");
            } else {
                eventToLoad.drawLottery();
                senNotificationToList("waitlist","Sorry, You lost the lottery");
                senNotificationToList("lotteryList","Congratulation! You won the lottery. Please confirm you attendance to the event.");
                Log.d("Local", "After draw: " + eventToLoad.getWaitingList().toString());
                updateFirebaseLottery(eventId, eventToLoad);
                showToast("Draw successful!");


            }
        }

    /**
     * Just a helper function to show message
     * @param message
     */
        private void showToast(String message) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }



    /**
     * This update the firebase fields after the draw
     */
    public void updateFirebaseLottery(String eventId, Event eventToLoad) {
        db.collection("events").document(eventId)
                .update("lotteryList", eventToLoad.getLottery(),
                        "waitlist", eventToLoad.getWaitingList(),
                        "firstDraw",  eventToLoad.getFirstDraw(),
                        "maxCapacity",  eventToLoad.getFinalEntrantsNum())
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
                DocumentSnapshot doc = task.getResult();
                eventToLoad = doc.toObject(Event.class);
                if (eventToLoad != null) {
                    qrCodeUrl = eventToLoad.getQRcode();

                    eventToLoad.setId(doc.getId());
                    eventToLoad.setWaitingList((ArrayList<String>) doc.get("waitlist"));
                    eventToLoad.setFinalEntrantsNum(doc.getLong("maxCapacity"));
                    eventToLoad.setFirstDraw(doc.getBoolean("firstDraw"));
                    eventToLoad.setLottery((ArrayList<String>) doc.get("lotteryList"));
                    Log.d("Local", "Retrieved waitlist: " + eventToLoad.getWaitingList() );


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


    /**
     * Send notification to desired list of users
     * @param list
     *      the name of the list in firebase
     * @param message
     *      the message to send
     */
    public void senNotificationToList(String list, String message){
        db.collection("events").document(eventId).get()
                .addOnSuccessListener(eventSnapshot -> {
                    if (eventSnapshot.exists()) {
                        List<String> listTosend = (List<String>) eventSnapshot.get(list);

                        for (String selectedEntrant : listTosend) {
                            if (listTosend != null && listTosend.contains(selectedEntrant)) {
                                // Entrant is on the waitlist, use the device ID directly
                                Notification notification = new Notification(eventToLoad.getCreatorID(),eventId,selectedEntrant,message);
                                notification.sendNotification();
                                Log.d("NotificationProcess", "Notification sent to waitlisted entrant: " + selectedEntrant);
//
                            }
                            else{
                                Toast.makeText(this, "Empty list.", Toast.LENGTH_SHORT).show();
                            }
                        }

                        Toast.makeText(this, "Notifications sent to selected entrants.", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e("NotificationError", "Event document not found for eventId: " + eventId);
                        Toast.makeText(this, "Event not found.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("NotificationError", "Error fetching event document for eventId: " + eventId, e);
                    Toast.makeText(this, "Failed to send notifications.", Toast.LENGTH_SHORT).show();
                });


        }

    }





