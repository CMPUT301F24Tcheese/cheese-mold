package com.example.myapplication.organizer;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.notifications.Notification;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

public class OrganizerNotificationActivity extends AppCompatActivity {

    private FirebaseFirestore db; // Firestore instance for sending notifications
    private Button buttonToChosenEntrants, buttonToEntrantsOnWaitlist, buttonToCanceledEntrants, buttonSend;
    private EditText editTextMessage;
    private String eventId;
    private String senderId; // Assume this is the organizer's ID
    private ArrayList<String> selectedEntrants = new ArrayList<>(); // Store selected entrants

    // Activity Result Launcher for EntrantNotificationListActivity
    private final ActivityResultLauncher<Intent> entrantListLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    ArrayList<String> selectedEntrants = result.getData().getStringArrayListExtra("selectedEntrants");
                    if (selectedEntrants != null && !selectedEntrants.isEmpty()) {
                        this.selectedEntrants = selectedEntrants;
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

        // Assume senderId is retrieved from logged-in user info
        senderId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        // Initialize UI elements
        buttonToChosenEntrants = findViewById(R.id.buttonToChosenEntrants);
        buttonToEntrantsOnWaitlist = findViewById(R.id.buttonToEntrantsOnWaitlist);
        buttonToCanceledEntrants = findViewById(R.id.buttonToCanceledEntrants);
        buttonSend = findViewById(R.id.buttonSend);
        editTextMessage = findViewById(R.id.editTextMessage);

        // Retrieve Event ID from Intent
        eventId = getIntent().getStringExtra("event_id");
        if (eventId == null) {
            Toast.makeText(this, "Event ID not provided", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set click listener for the "To Chosen Entrants" button
        buttonToEntrantsOnWaitlist.setOnClickListener(view -> {
            Intent intent = new Intent(OrganizerNotificationActivity.this, EntrantNotificationListActivity.class);
            intent.putExtra("event_id", eventId);
            intent.putStringArrayListExtra("selectedEntrants", selectedEntrants);
            entrantListLauncher.launch(intent); // Launch with ActivityResultLauncher
        });

        // Set click listener for "To Chosen Entrants" button
        buttonToChosenEntrants.setOnClickListener(view -> {
            Intent intent = new Intent(OrganizerNotificationActivity.this, EntrantNotificationListActivity.class);
            intent.putExtra("event_id", eventId);
            intent.putExtra("isChosenEntrantsMode", true); // True to indicate chosen entrants mode
            intent.putStringArrayListExtra("selectedEntrants", selectedEntrants);
            entrantListLauncher.launch(intent);
        });

        // Set click listener for the Send button
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
        for (String receiverId : selectedEntrants) {
            // Create a new Notification instance for each selected entrant
            Notification notification = new Notification(senderId, eventId, receiverId, message);

            // Use the sendNotification method to send the notification to Firestore
            notification.sendNotification();
        }

        // Provide feedback to the user
        Toast.makeText(this, "Notifications sent to selected entrants.", Toast.LENGTH_SHORT).show();
    }
}

