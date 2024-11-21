package com.example.myapplication.controllers;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.myapplication.users.MainActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

public class NotificationController {
    private FirebaseFirestore db;
    private ListenerRegistration listenerRegistration;
    private Context context;

    public NotificationController(Context context) {
        db = FirebaseFirestore.getInstance();
        this.context = context;
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "eventNotification";
            CharSequence channelName = "Event Notifications";
            String channelDescription = "Notifications for events";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            channel.setDescription(channelDescription);

            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    public void startListening(String receiverId) {

        long listenerStartTime = System.currentTimeMillis()/1000;

        listenerRegistration = db.collection("notifications")
                .whereEqualTo("receiver", receiverId)
                .addSnapshotListener((snapshots, err) -> {
                    if (err != null) {
                        Log.w("Notification Controller listener", "Error starting listener");
                        return;
                    }

                    if (snapshots != null) {
                        for (DocumentChange documentChange: snapshots.getDocumentChanges()) {
                            if (documentChange.getType() == DocumentChange.Type.ADDED) {

                                Timestamp timestamp = documentChange.getDocument().getTimestamp("timestamp");
                                if (timestamp != null) {
                                    long createdAt = timestamp.getSeconds();
                                    if (createdAt > listenerStartTime) {
                                        String eventId = documentChange.getDocument().getString("event");
                                        String message = documentChange.getDocument().getString("message");
                                        assert eventId != null;
                                        db.collection("events").document(eventId).get()
                                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                        if (documentSnapshot.exists()) {
                                                            String eventName = documentSnapshot.getString("name");
                                                            Log.d("Firestore Notification Listener", "Message: " + message + " Event: " + eventName);
                                                            showNotification(eventName, message);
                                                        } else {
                                                            Log.d("Notification controller event finder", "No Event found");
                                                        }
                                                    }
                                                }).addOnFailureListener(e -> {
                                                    Log.w("Notification controller event finder", "Error loading event data");
                                                });
                                    }
                                }

                            }
                        }
                    }
                });
    }

    public void stopListening() {
        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }
    }

    private void showNotification(String title, String message) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create unique notification ID
        int notificationId = (int) System.currentTimeMillis();


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "eventNotification")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title != null ? title : "New Notification")
                .setContentText(message != null ? message : "You have new notification")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(Notification.DEFAULT_ALL);


        // Add an intent (optional)
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        builder.setContentIntent(pendingIntent);

        // Notify
        if (notificationManager != null) {
            notificationManager.notify(notificationId, builder.build());
        }

    }

}
