package com.example.myapplication.users;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.UUID;

public class UpdateProfileActivity extends AppCompatActivity {

    private FirebaseFirestore db; // FirebaseFirestore instance for accessing Firestore database
    private StorageReference storageReference; // Reference to Firebase Storage for storing profile images
    private EditText updateFirstname; // EditText for updating user's first name
    private EditText updateLastname; // EditText for updating user's last name
    private EditText updateEmail; // EditText for updating user's email address
    private Button confirmUpdateBtn; // Button to confirm profile updates
    private ImageView updateProfilePic; // ImageView to display and update profile picture
    private TextView cancelText; // TextView that acts as a button to cancel and return to the main activity
    Uri imageUri; // URI for storing the selected image from the device
    private String currentProfilePicUrl; // String to hold the current profile picture URL
    private Button removeProfilePictureBtn; // Button to remove the user's profile picture
    private String device;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Call to parent class to initialize activity
        setContentView(R.layout.activity_update_profile); // Set the layout for this activity

        // Initialize Firebase services
        db = FirebaseFirestore.getInstance(); // Get instance of Firestore to access database
        storageReference = FirebaseStorage.getInstance().getReference(); // Get reference to Firebase Storage

        // Initialize views
        updateFirstname = findViewById(R.id.updateFirstName); // Get reference to the EditText for first name
        updateLastname = findViewById(R.id.updateLastName); // Get reference to the EditText for last name
        updateEmail = findViewById(R.id.updateEmail); // Get reference to the EditText for email
        confirmUpdateBtn = findViewById(R.id.updateBtn); // Get reference to the button to confirm profile update
        updateProfilePic = findViewById(R.id.updateProfilePicture); // Get reference to the ImageView for profile picture
        cancelText = findViewById(R.id.cancelUpdateText); // Get reference to the cancel button (TextView)
        removeProfilePictureBtn = findViewById(R.id.removeProfileBtn); // Get reference to the button to remove profile picture

        device = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        loadUserProfile(device);

        // Set a click listener on the profile picture to allow changing it
        updateProfilePic.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK); // Create an intent to pick an image
            intent.setType("image/*"); // Set intent to filter for images only
            activityResultLauncher.launch(intent); // Launch the activity to pick an image
        });

        // Set a click listener on the cancel button to return to the main activity
        cancelText.setOnClickListener(view -> {
            finish(); // Close the current activity
        });

        // Set a click listener on the remove profile picture button to remove the current profile picture
        removeProfilePictureBtn.setOnClickListener(view -> {
            removeProfilePicture(device); // Call method to remove the profile picture
        });

        // Set a click listener on the confirm update button to update the user profile
        confirmUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String firstname = updateFirstname.getText().toString().trim(); // Get updated first name from EditText
                String lastname = updateLastname.getText().toString().trim(); // Get updated last name from EditText
                String email = updateEmail.getText().toString().trim();

                // Check if both fields are not empty
                if (firstname.isEmpty() || lastname.isEmpty()) {
                    Toast.makeText(UpdateProfileActivity.this, "Please fill out all fields", Toast.LENGTH_SHORT).show(); // Display error message
                } else {
                    updateUserProfile(device, firstname, lastname, email); // Proceed to update user profile
                }
            }
        });
    }

    /**
     * Launcher is used to launch the gallery when selecting photo. It takes the image URI
     * and stores it in the imageUri variable. Have no idea how it works!
     */
    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult o) {
            if (o.getResultCode() == RESULT_OK) { // Check if the result is successful
                if (o.getData() != null) {
                    imageUri = o.getData().getData(); // Get the URI of the selected image
                    updateProfilePic.setImageURI(imageUri); // Set the selected image as the profile picture
                }
            } else {
                Toast.makeText(UpdateProfileActivity.this, "Please select an image", Toast.LENGTH_SHORT).show(); // Show message if no image is selected
            }
        }
    });

    /**
     * Loads the user info from the database and then fills out the
     * edit texts for the user
     * @param device device Id of the user
     */
    private void loadUserProfile(String device) {
        // Fetch the user data from Firestore using the userId
        db.collection("users").document(device).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) { // Check if the document exists
                    String firstname = documentSnapshot.getString("Firstname"); // Retrieve first name
                    String lastname = documentSnapshot.getString("Lastname"); // Retrieve last name
                    String email = documentSnapshot.getString("Email"); // Retrieve email address
                    currentProfilePicUrl = documentSnapshot.getString("Profile Picture"); // Get the current profile picture URL

                    // Set the EditText fields with retrieved data
                    updateFirstname.setText(firstname);
                    updateLastname.setText(lastname);
                    updateEmail.setText(email);

                    // Load the current profile picture using Glide
                    if (currentProfilePicUrl != null && !currentProfilePicUrl.isEmpty()) {
                        Glide.with(UpdateProfileActivity.this)
                                .load(currentProfilePicUrl)
                                .placeholder(R.drawable.baseline_person_outline_24)
                                .error(R.drawable.baseline_person_outline_24)
                                .into(updateProfilePic);
                    }
                }
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(UpdateProfileActivity.this, "Error loading user data", Toast.LENGTH_SHORT).show(); // Show error message if data load fails
        });
    }

    /**
     * Removes the profile picture of the user. Does not actually change in db,
     * just changes the frontend. But this changes the currentProfilePicUrl to the default name profile pic
     * which will later be used to update the db.
     * @param device Device Id of the user
     */
    private void removeProfilePicture(String device) {
        // Fetch the user's details from Firestore
        db.collection("users").document(device).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String firstname = documentSnapshot.getString("Firstname"); // Get the first name
                            String lastname = documentSnapshot.getString("Lastname"); // Get the last name
                            currentProfilePicUrl = "https://avatar.iran.liara.run/username?username=" + firstname + "+" + lastname; // Default API-generated profile pic URL

                            // Use Glide to update the profile picture with the default image
                            Glide.with(UpdateProfileActivity.this)
                                .load(currentProfilePicUrl)
                                .placeholder(R.drawable.baseline_person_outline_24)
                                .error(R.drawable.baseline_person_outline_24)
                                .into(updateProfilePic);
                        }
                    }
        }).addOnFailureListener(e -> {
            Toast.makeText(UpdateProfileActivity.this, "Error loading user data", Toast.LENGTH_SHORT).show(); // Show error if unable to fetch data
        });
    }

    /**
     * Helper function which goes on to call updateImageAndData if the profile picture was updated
     * or calls updateUserDataInFirestore if picture was not updated.
     * @param device Device ID of the user
     * @param firstname Firstname of the user
     * @param lastname Lastname of the user
     * @param email Email of the user
     */
    private void updateUserProfile(String device, String firstname, String lastname, String email) {
            if (imageUri != null) {
                updateProfileImageAndData(device, firstname, lastname, email); // Upload new image and update data
            } else {
                updateUserDataInFirestore(device, firstname, lastname, email, currentProfilePicUrl); // Update Firestore without image change//
            }
    }

    /**
     * Saves the new profile picture in the firebase storage, creates an URL for the image
     * which is then passed onto the updateUserDataInFirestore to change the total user data.
     * @param device Device ID of the user
     * @param firstname Firstname of the user
     * @param lastname Lastname of the user
     * @param email Email of the user
     */
    private void updateProfileImageAndData(String device, String firstname, String lastname, String email) {
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
                    Toast.makeText(UpdateProfileActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show(); // Show error if upload fails
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
    private void updateUserDataInFirestore(String device, String firstname, String lastname, String email, String profilePicUrl) {
        // Update the user's first name, last name, and profile picture in Firestore
        db.collection("users").document(device)
                .update("Firstname", firstname, "Lastname", lastname, "Email", email,  "Profile Picture", profilePicUrl)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(UpdateProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show(); // Show success message
                    startActivity(new Intent(UpdateProfileActivity.this, MainActivity.class)); // Navigate back to MainActivity
                    finish(); // Close the current activity
                })
                .addOnFailureListener(e -> Toast.makeText(UpdateProfileActivity.this, "Failed to update profile", Toast.LENGTH_SHORT).show()); // Show error if update fails
    }
}
