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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OrganizerNotificationActivity extends AppCompatActivity {

    private FirebaseFirestore db; // Firestore instance
    private Button buttonToChosenEntrants, buttonToEntrantsOnWaitlist, buttonSend;
    private EditText editTextMessage;
    private String eventId;
    private String senderId; // Organizer's device ID
    private ArrayList<String> selectedEntrants = new ArrayList<>();
    private boolean isChosenEntrantsMode;

    // Activity Result Launcher for EntrantNotificationListActivity
    private final ActivityResultLauncher<Intent> entrantListLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedEntrants = result.getData().getStringArrayListExtra("selectedEntrants");
                    if (selectedEntrants != null && !selectedEntrants.isEmpty()) {
                        Toast.makeText(this, "Selected Entrants: " + selectedEntrants, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "No entrants selected", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_notification);

        db = FirebaseFirestore.getInstance();
        senderId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        buttonToChosenEntrants = findViewById(R.id.buttonToChosenEntrants);
        buttonToEntrantsOnWaitlist = findViewById(R.id.buttonToEntrantsOnWaitlist);
        buttonSend = findViewById(R.id.buttonSend);
        editTextMessage = findViewById(R.id.editTextMessage);

        eventId = getIntent().getStringExtra("event_id");
        if (eventId == null) {
            Toast.makeText(this, "Event ID not provided", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        buttonToEntrantsOnWaitlist.setOnClickListener(view -> {
            Intent intent = new Intent(this, EntrantNotificationListActivity.class);
            intent.putExtra("event_id", eventId);
            intent.putStringArrayListExtra("selectedEntrants", selectedEntrants);
            intent.putExtra("isChosenEntrantsMode", false);
            entrantListLauncher.launch(intent);
        });

        buttonToChosenEntrants.setOnClickListener(view -> {
            Intent intent = new Intent(this, EntrantNotificationListActivity.class);
            intent.putExtra("event_id", eventId);
            intent.putStringArrayListExtra("selectedEntrants", selectedEntrants);
            intent.putExtra("isChosenEntrantsMode", true);
            entrantListLauncher.launch(intent);
        });

        buttonSend.setOnClickListener(view -> {
            String message = editTextMessage.getText().toString().trim();
            if (!message.isEmpty() && !selectedEntrants.isEmpty()) {
                sendNotificationsToSelectedUsers(message);
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
    private void sendNotificationsToSelectedUsers(String message) {
        for (String selectedEntrant : selectedEntrants) {
            db.collection("users")
                    .whereEqualTo("Firstname", selectedEntrant.split(" ")[0])  // Assuming selectedEntrant format is "First Last"
                    .whereEqualTo("Lastname", selectedEntrant.split(" ")[1])
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        if (!querySnapshot.isEmpty()) {
                            String receiverDeviceId = querySnapshot.getDocuments().get(0).getId(); // Get document ID as device ID
                            Notification notification = new Notification(senderId, eventId, receiverDeviceId, message);
                            notification.sendNotification();
                            Log.d("NotificationProcess", "Notification sent to " + receiverDeviceId);
                        } else {
                            Log.w("NotificationError", "No user found with the name " + selectedEntrant);
                        }
                    })
                    .addOnFailureListener(e -> Log.e("NotificationError", "Failed to fetch user document", e));
        }

        Toast.makeText(this, "Notifications sent to selected entrants.", Toast.LENGTH_SHORT).show();
    }



    private void sendNotification(String receiverId, String message) {
        Notification notification = new Notification(senderId, eventId, receiverId, message);
        notification.sendNotification();
    }
}

