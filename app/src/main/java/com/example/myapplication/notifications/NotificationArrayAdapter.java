package com.example.myapplication.notifications;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.entrant.EntrantMainActivity;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is a custom array adapter for notification
 * which is used to fill in each notification in user screen
 *
 */
public class NotificationArrayAdapter extends ArrayAdapter<Notification> {

    /**
     * Constructor for the NotificationArrayAdapter
     * @param context the context of the adapter
     * @param notifications Array of notification objects
     */
    public NotificationArrayAdapter(Context context, ArrayList<Notification> notifications) {
        super(context, 0, notifications);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.notification_content, parent, false);
        } else {
            view = convertView;
        }

        Notification notification = getItem(position);
        TextView mainText = view.findViewById(R.id.notificationMainText);
        TextView subText = view.findViewById(R.id.notificationSubText);
        ImageView profileImage = view.findViewById(R.id.notificationProfileImg);
        FirebaseFirestore db = FirebaseFirestore.getInstance();


        mainText.setText(""); // Clear previous name
        profileImage.setImageResource(R.drawable.baseline_person_outline_24); // Reset to placeholder

        assert notification != null;
        subText.setText(notification.getMsg());
        String senderId = notification.getSenderId();



        db.collection("users").document(senderId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String senderName = documentSnapshot.getString("Firstname") + " " + documentSnapshot.getString("Lastname");
                        String senderProfilePic = documentSnapshot.getString("Profile Picture");

                        Log.d("NotificationAdapter", "Position: " + position + ", Sender ID: " + senderId);
                        Log.d("NotificationAdapter", "Sender Name: " + senderName);
                        Log.d("NotificationAdapter", "Profile Pic URL: " + senderProfilePic);
                        mainText.setText(senderName);
                        setImageInView(profileImage, senderProfilePic);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("NotificationAdapter", "Failed to fetch user data", e);
                });
        return view;

    }


    /**
     * This method uses Glide to load image into ImageView
     * directly using a link
     * @param view the ImageView to load into
     * @param picUrl the link of the image
     */
    private void setImageInView(ImageView view, String picUrl) {
        // Load the user's profile picture using Glide, a third-party image loading library
        Glide.with(getContext())
                .load(picUrl) // Load the image from the URL obtained from Firestore
                .placeholder(R.drawable.baseline_person_outline_24) // Display a default placeholder while the image loads
                .error(R.drawable.baseline_person_outline_24) // Show a default image if loading the picture fails
                .into(view); // Set the loaded image into the ImageView
    }
}
