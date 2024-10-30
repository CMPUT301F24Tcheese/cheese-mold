package com.example.myapplication.organizer;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class OrganizerNotificationActivity extends AppCompatActivity {

    private FirebaseFirestore db; // Firestore instance for sending notifications
    private Button buttonToChosenEntrants, buttonToEntrantsOnWaitlist, buttonToCanceledEntrants, buttonSend;
    private EditText editTextMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_notification); // Set the layout for this activity

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize UI elements
        buttonToChosenEntrants = findViewById(R.id.buttonToChosenEntrants);
        buttonToEntrantsOnWaitlist = findViewById(R.id.buttonToEntrantsOnWaitlist);
        buttonToCanceledEntrants = findViewById(R.id.buttonToCanceledEntrants);
        buttonSend = findViewById(R.id.buttonSend);
        editTextMessage = findViewById(R.id.editTextMessage);

        // Set click listeners for the notification target buttons
        buttonToChosenEntrants.setOnClickListener(view -> sendNotification("chosen"));
        buttonToEntrantsOnWaitlist.setOnClickListener(view -> sendNotification("waitlist"));
        buttonToCanceledEntrants.setOnClickListener(view -> sendNotification("canceled"));

        // Set click listener for the Send button
        buttonSend.setOnClickListener(view -> {
            String message = editTextMessage.getText().toString().trim();
            if (!message.isEmpty()) {
                // Implement sending the message based on selected target
                // For example, you might need to track which button was last clicked
                // This requires additional logic based on your app's requirements
                Toast.makeText(this, "Message sent: " + message, Toast.LENGTH_SHORT).show();
                // Clear the message after sending
                editTextMessage.setText("");
            } else {
                Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Sends a notification to the specified target group
     * @param targetGroup The group to which the notification should be sent ("chosen", "waitlist", "canceled")
     */
    private void sendNotification(String targetGroup) {
        // Implement the logic to fetch the target group entrants from Firestore
        // and send the notification message
        // This is a placeholder for demonstration purposes

        Toast.makeText(this, "Selected target: " + targetGroup, Toast.LENGTH_SHORT).show();

        // You can implement further logic here to handle the notification sending
    }
}

