package com.example.myapplication.notifications;

import android.util.Log;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


/**
 * This class represents a notification which is used to send
 * and receive notifications in the app.
 *
 */
public class Notification {

    private String senderId;
    private String eventId;
    private String receiverId;
    private String msg;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    /**
     * Constructor method to create a new notification object
     * @param senderId id of the user to sent the notification
     * @param eventId id of the event for which the notification was sent
     * @param receiverId id of the user to whom the notification was sent
     * @param msg message of the notification
     */
    public Notification(String senderId, String eventId, String receiverId, String msg) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.eventId = eventId;
        this.msg = msg;
    }

    /**
     * Getter method to get the id of the sender
     * @return
     *      Returns the id of the sender
     */
    public String getSenderId() {
        return senderId;
    }

    /**
     * Setter method to set the senderId
     * @param senderId the new id to set
     */
    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    /**
     * Setter method to set the receiverId
     * @param receiverId the new id to set
     */
    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    /**
     * Getter method to get the id of the receiver
     * @return
     *      Returns the id of the receiver
     */
    public String getReceiverId() {
        return receiverId;
    }

    /**
     * Getter method to get the id of the event
     * @return
     *      Returns the id of the event
     */
    public String getEventId() {
        return eventId;
    }

    /**
     * Getter method to get the message of the notification
     * @return
     *      Returns the notification message
     */
    public String getMsg() {
        return msg;
    }

    /**
     * Setter method to set the notification message
     * @param msg the message to set
     */
    public void setMsg(String msg) {
        this.msg = msg;
    }

    /**
     * Method to send a notification to a user.
     * This method directly sends the notification to firebase
     * which is then used to retrieve notification from the
     * receiver side
     */
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
