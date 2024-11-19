package com.example.myapplication.controllers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.administrator.AdministratorMainActivity;
import com.example.myapplication.entrant.EntrantMainActivity;
import com.example.myapplication.objects.Users;
import com.example.myapplication.organizer.OrganizerMainActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.UUID;

public class UserController {
    private Context context;
    private FirebaseFirestore db;
    private StorageReference storageReference;

    public UserController(Context context) {
        this.context = context;
        storageReference = FirebaseStorage.getInstance().getReference();
        db = FirebaseFirestore.getInstance();
    }

    public interface UserProfileCallback {
        void onUserProfileLoaded(String picUrl);
        void onError(String errMsg);
    }


    /**
     * Stores the profile photo into firebase storage in the profile image directory
     * If upload is successful, generates a URL for the image which is passed into
     * uploadUserData function which then goes on to upload all the data in firestore
     * @param image URI of the selected profile image
     * @param user User object to upload to firebase
     */
    public void uploadImageAndData(Uri image, Users user) {
        StorageReference reference = storageReference.child("profile_images/" + UUID.randomUUID() + ".jpg"); // Create a unique path for the profile image
        reference.putFile(image) // Upload the image to Firebase Storage
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        reference.getDownloadUrl().addOnSuccessListener(uri -> {
                            String downloadUrl = uri.toString();
                            user.setProfilePicture(downloadUrl);// Get the download URL of the uploaded image
                            uploadUserData(user); // Upload user data along with the image URL
                        });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to upload image", Toast.LENGTH_SHORT).show(); // Show error if image upload fails
                });
    }

    /**
     * Function takes the user data and uploads them into firestore database
     * Redirects the user to login if storing is successful.
     * @param user User object which will be uploaded to firebase
     */
    public void uploadUserData(Users user) {
        HashMap<String, Object> data = new HashMap<>(); // Create a HashMap to store user data
        data.put("Email", user.getEmail()); // Add user's email to the map
        data.put("role", user.getRole());
        data.put("Firstname", user.getFirstName()); // Add user's first name to the map
        data.put("Lastname", user.getLastName()); // Add user's last name to the map
        data.put("Profile Picture", user.getProfilePicture()); // Add user's profile picture URL to the map
        data.put("Event List", user.getEventList()); // **(2) Add waitlist to the event map**

        db.collection("users").document(user.getUserId()).set(data) // Store the user data in Firestore under the user's unique ID
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "User data added successfully!"); // Log success message
                    if (user.getRole().equals("Entrant")) {
                        context.startActivity(new Intent(context, EntrantMainActivity.class)); // navigate to main screen
                    } else if (user.getRole().equals("Organizer")) {
                        context.startActivity(new Intent(context, OrganizerMainActivity.class)); // navigate to main screen
                    } else if (user.getRole().equals("Administrator")) {
                        context.startActivity(new Intent(context, AdministratorMainActivity.class)); // Navigate to main screen
                    }
                    ((Activity) context).finish();
                })
                .addOnFailureListener(e -> Log.w("Firestore", "Error adding user data", e)); // Log error if data upload fails
    }


    public boolean isValidEmail(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        return email.matches(emailPattern);
    }

    public void setImageInView(ImageView view, String picUrl) {
        // Load the user's profile picture using Glide, a third-party image loading library
        Glide.with(context)
                .load(picUrl) // Load the image from the URL obtained from Firestore
                .placeholder(R.drawable.baseline_person_outline_24) // Display a default placeholder while the image loads
                .error(R.drawable.baseline_person_outline_24) // Show a default image if loading the picture fails
                .into(view); // Set the loaded image into the ImageView
    }


    /**
     * Loads the user info from the database and then fills out the
     * edit texts for the user
     * @param device device Id of the user
     */
    public void loadUserProfile(String device, TextView ViewFirstname, TextView ViewLastname, TextView ViewEmail, ImageView profilePic, UserProfileCallback callback) {
        // Fetch the user data from Firestore using the userId
        db.collection("users").document(device).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) { // Check if the document exists
                    String firstname = documentSnapshot.getString("Firstname"); // Retrieve first name
                    String lastname = documentSnapshot.getString("Lastname"); // Retrieve last name
                    String email = documentSnapshot.getString("Email"); // Retrieve email address
                    String picUrl = documentSnapshot.getString("Profile Picture"); // Get the current profile picture URL

                    // Set the EditText fields with retrieved data
                    ViewFirstname.setText(firstname);
                    ViewLastname.setText(lastname);
                    ViewEmail.setText(email);

                    // Load the current profile picture using Glide
                    if (picUrl != null && !picUrl.isEmpty()) {
                        setImageInView(profilePic, picUrl);
                        callback.onUserProfileLoaded(picUrl);
                    }

                }
            }
        }).addOnFailureListener(e -> {
            callback.onError("Error Loading User Data");
        });
    }

    /**
     * Removes the profile picture of the user. Does not actually change in db,
     * just changes the frontend. But this changes the currentProfilePicUrl to the default name profile pic
     * which will later be used to update the db.
     * @param device Device Id of the user
     */
    public void removeProfilePicture(String device, ImageView view, UserProfileCallback callback) {
        // Fetch the user's details from Firestore
        db.collection("users").document(device).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String firstname = documentSnapshot.getString("Firstname"); // Get the first name
                            String lastname = documentSnapshot.getString("Lastname"); // Get the last name
                            String picUrl = "https://avatar.iran.liara.run/username?username=" + firstname + "+" + lastname; // Default API-generated profile pic URL

                            // Use Glide to update the profile picture with the default image
                            setImageInView(view, picUrl);
                            callback.onUserProfileLoaded(picUrl);
                        }
                    }
                }).addOnFailureListener(e -> {
                    callback.onError("Error Removing Profile Picture");
                });
    }


    /**
     * Saves the new profile picture in the firebase storage, creates an URL for the image
     * which is then passed onto the updateUserDataInFirestore to change the total user data.
     * @param device Device ID of the user
     * @param firstname Firstname of the user
     * @param lastname Lastname of the user
     * @param email Email of the user
     */
    public void updateProfileImageAndData(String device, String firstname, String lastname, String email, Uri imageUri) {
        // Create a unique file path for the new profile image
        StorageReference reference = storageReference.child("profile_images/" + UUID.randomUUID().toString() + ".jpg");
        reference.putFile(imageUri) // Upload the image to Firebase Storage
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        reference.getDownloadUrl().addOnSuccessListener(uri -> {
                            String downloadUrl = uri.toString(); // Get the URL of the uploaded image
                            updateUserDataInFirestore(device, firstname, lastname, email, downloadUrl); // Update Firestore with new image URL
                        });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to upload image", Toast.LENGTH_SHORT).show(); // Show error if upload fails
                });
    }

    /**
     * Main function which updates all the user data other than the email.
     * @param device Device ID of the user
     * @param firstname Firstname of the user
     * @param lastname Lastname of the user
     * @param email Email of the user
     * @param profilePicUrl ProfilePic of the user
     */
    public void updateUserDataInFirestore(String device, String firstname, String lastname, String email, String profilePicUrl) {
        // Update the user's first name, last name, and profile picture in Firestore
        db.collection("users").document(device)
                .update("Firstname", firstname, "Lastname", lastname, "Email", email,  "Profile Picture", profilePicUrl)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Profile updated successfully", Toast.LENGTH_SHORT).show(); // Show success message
                    ((Activity) context).finish(); // Close the current activity
                })
                .addOnFailureListener(e -> Toast.makeText(context, "Failed to update profile", Toast.LENGTH_SHORT).show()); // Show error if update fails
    }
}
