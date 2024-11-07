package com.example.myapplication.organizer;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.notifications.Notification;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class OrganizerNotificationActivity extends AppCompatActivity {

    private FirebaseFirestore db; // Firestore instance
    private Button buttonToChosenEntrants, buttonToEntrantsOnWaitlist, buttonSend,buttCancel,buttonReturn;
    private EditText editTextMessage;
    private String eventId;
    private String senderId; // Organizer's device ID
    private ArrayList<String> selectedEntrants = new ArrayList<>();
    private boolean isChosenEntrantsMode;

    private String list_to_send;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_notification);

        db = FirebaseFirestore.getInstance();
        senderId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        buttonToChosenEntrants = findViewById(R.id.buttonToChosenEntrants);
        buttonToEntrantsOnWaitlist = findViewById(R.id.buttonToEntrantsOnWaitlist);
        ///
        buttCancel = findViewById(R.id.buttonToCanceledEntrants);
        buttonReturn = findViewById(R.id.NotificationButtonCancel);
        ///
        buttonSend = findViewById(R.id.buttonSend);
        editTextMessage = findViewById(R.id.editTextMessage);

        buttonReturn.setOnClickListener(view -> {
            finish();
        });


        eventId = getIntent().getStringExtra("event_id");
        if (eventId == null) {
            Toast.makeText(this, "Event ID not provided", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        buttonToEntrantsOnWaitlist.setOnClickListener(view -> {
            get_list("waitlist", () -> this.list_to_send = "waitlist");

        });

        buttonToChosenEntrants.setOnClickListener(view -> {
            get_list("confirmedList", () -> this.list_to_send = "confirmedList");
        });

        buttCancel.setOnClickListener(view -> {
            get_list("cancelledList", () -> this.list_to_send = "cancelledList");

        });


        ///

        buttonSend.setOnClickListener(view -> {
            String message = editTextMessage.getText().toString().trim();
            if (!message.isEmpty() && !selectedEntrants.isEmpty()) {
                sendNotificationsToSelectedUsers(message, list_to_send); // Pass the correct list
                editTextMessage.setText("");
            } else if (selectedEntrants.isEmpty()) {
                Toast.makeText(this, "No users selected", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * Sends a notification message to each selected user by creating a new Notification instance.
     * @param message The message to send to selected users.
     */
    private void sendNotificationsToSelectedUsers(String message, String list_to_send) {
        db.collection("events").document(eventId).get()
                .addOnSuccessListener(eventSnapshot -> {
                    if (eventSnapshot.exists()) {
                        List<String> list = (List<String>) eventSnapshot.get(list_to_send);

                        for (String selectedEntrant : selectedEntrants) {
                            if (list != null && list.contains(selectedEntrant)) {
                                // Entrant is on the waitlist, use the device ID directly
                                Notification notification = new Notification(senderId, eventId, selectedEntrant, message);
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

    /// Kevin's implementation

    /**
     * This method get the cancel list.
     * @return
     *      the cancel list
     */
    public void get_list(String chosenList, Runnable onComplete) {
        DocumentReference eventRef = db.collection("events").document(eventId);
        eventRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot doc = task.getResult();
                        if (doc.exists()) {
                            ArrayList<String> FList = (ArrayList<String>) doc.get(chosenList);
                            Log.d("FirestoreData", "Fetched list for " + chosenList + ": " + FList);
                            Log.d("FirestoreData", "Event:" + eventId);

                            if (FList != null && !FList.isEmpty()) {
                                this.selectedEntrants.clear();
                                this.selectedEntrants.addAll(FList);
                                Log.d("FirestoreData", "Selected entrants after update: " + selectedEntrants);

                            } else {
                                Log.d("FirestoreData", "No users in the " + chosenList + " list.");
                            }
                        } else {
                            Toast.makeText(OrganizerNotificationActivity.this, "Event not found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.w("FirestoreData", "Error getting document", task.getException());
                    }
                    onComplete.run();
                });
    }


}



    


