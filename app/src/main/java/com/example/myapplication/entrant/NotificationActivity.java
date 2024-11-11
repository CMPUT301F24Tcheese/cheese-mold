
/**
 * Activity for displaying notifications to the user.
 * This activity retrieves unread notifications from Firestore that are specific to the user's device ID.
 * Notifications are displayed in a ListView and can be dismissed by navigating back.
 */
package com.example.myapplication.entrant;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.R;
import com.example.myapplication.notifications.Notification;
import com.example.myapplication.notifications.NotificationArrayAdapter;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.units.qual.N;

import java.util.ArrayList;

/**
 * NotificationActivity is responsible for fetching and displaying notifications specific to the user.
 * The notifications are loaded from the Firestore database based on the device ID and displayed in a ListView.
 */
public class NotificationActivity extends AppCompatActivity {
    private ArrayList<Notification> dataList;
    private NotificationArrayAdapter notificationAdapter;
    private FirebaseFirestore db;
    private CollectionReference notificationRef;
    private String deviceId;
    private Button backBtn;

    /**
     * Initializes the activity, sets up the ListView, and fetches notifications from Firestore.
     *
     * @param savedInstanceState The saved instance state for restoring the activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notification);

        deviceId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        backBtn = findViewById(R.id.notificationActivityBackBtn);
        db = FirebaseFirestore.getInstance();

        dataList = new ArrayList<Notification>();
        ListView notificationList = findViewById(R.id.notificationList);
        notificationAdapter = new NotificationArrayAdapter(this, dataList);
        notificationList.setAdapter(notificationAdapter);

        backBtn.setOnClickListener(view -> {
            finish();
        });



        db.collection("notifications")
                .whereEqualTo("receiver", deviceId)
                .whereEqualTo("isRead", false)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.e("Firestore", error.toString());
                        }
                        if (value != null) {
                            Log.d("Firestore", "Number of documents in snapshot: " + value.size()); // Log the count of documents
                            dataList.clear();
                            for (QueryDocumentSnapshot doc : value) {
                                // Log the ID of each document
                                Log.d("Firestore", "Document ID: " + doc.getId());

                                String sender = doc.getString("sender");
                                String event = doc.getString("event");
                                String receiver = deviceId;
                                String message = doc.getString("message");
                                Log.d("Firestore", "Sender: " + sender + ", Event: " + event + ", Message: " + message);

                                Notification notification = new Notification(sender, event, receiver, message);
                                dataList.add(notification);

                            }

                            // Log the final size of dataList to check for duplicates or unexpected values
                            Log.d("Firestore", "Total notifications in dataList after update: " + dataList.size());
                            notificationAdapter.notifyDataSetChanged();

                        }
                    }
                });


    }


}