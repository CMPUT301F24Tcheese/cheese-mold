package com.example.myapplication.notifications;

import android.util.Log;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Notification {
    private String senderId;
    private String eventId;
    private String receiverId;
    private String msg;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public Notification(String senderId, String eventId, String receiverId, String msg) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.eventId = eventId;
        this.msg = msg;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getEventId() {
        return eventId;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void sendNotification() {
        Map<String, Object> message = new HashMap<>();
        message.put("sender", this.senderId);
        message.put("event", this.eventId);
        message.put("receiver", this.receiverId);
        message.put("message", this.msg);
        message.put("timestamp", FieldValue.serverTimestamp());
        message.put("isRead", false);

        db.collection("notifications").add(message)
                .addOnSuccessListener(documentReference -> {
                    Log.d("SendNotification", "Notification sent");
                })
                .addOnFailureListener(e -> {
                    Log.w("SendNotification", "Error sending notification", e);
                });
    }
}
