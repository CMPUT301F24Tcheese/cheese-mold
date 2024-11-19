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

import com.example.myapplication.R;
import com.example.myapplication.controllers.UserController;


/**
 * Update Activity that handles the user profile update process
 */
public class UpdateProfileActivity extends AppCompatActivity implements UserController.UserProfileCallback {
    private EditText updateFirstname; // EditText for updating user's first name
    private EditText updateLastname; // EditText for updating user's last name
    private EditText updateEmail; // EditText for updating user's email address
    private Button confirmUpdateBtn; // Button to confirm profile updates
    private ImageView updateProfilePic; // ImageView to display and update profile picture
    private TextView cancelText; // TextView that acts as a button to cancel and return to the main activity
    private Uri imageUri; // URI for storing the selected image from the device
    private String currentProfilePicUrl; // String to hold the current profile picture URL
    private Button removeProfilePictureBtn; // Button to remove the user's profile picture
    private String device;
    private UserController userController;

    @Override
    public void onUserProfileLoaded(String picUrl) {
        this.currentProfilePicUrl = picUrl;
    }

    @Override
    public void onError(String errMsg) {
        Toast.makeText(UpdateProfileActivity.this, errMsg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Call to parent class to initialize activity
        setContentView(R.layout.activity_update_profile); // Set the layout for this activity

        userController = new UserController(this);

        // Initialize views
        updateFirstname = findViewById(R.id.updateFirstName); // Get reference to the EditText for first name
        updateLastname = findViewById(R.id.updateLastName); // Get reference to the EditText for last name
        updateEmail = findViewById(R.id.updateEmail); // Get reference to the EditText for email
        confirmUpdateBtn = findViewById(R.id.updateBtn); // Get reference to the button to confirm profile update
        updateProfilePic = findViewById(R.id.updateProfilePicture); // Get reference to the ImageView for profile picture
        cancelText = findViewById(R.id.cancelUpdateText); // Get reference to the cancel button (TextView)
        removeProfilePictureBtn = findViewById(R.id.removeProfileBtn); // Get reference to the button to remove profile picture

        device = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        userController.loadUserProfile(device, updateFirstname, updateLastname, updateEmail, updateProfilePic, this);

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
            userController.removeProfilePicture(device, updateProfilePic, this); // Call method to remove the profile picture
        });

        // Set a click listener on the confirm update button to update the user profile
        confirmUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String firstname = updateFirstname.getText().toString().trim(); // Get updated first name from EditText
                String lastname = updateLastname.getText().toString().trim(); // Get updated last name from EditText
                String email = updateEmail.getText().toString().trim();

                // Check if both fields are not empty
                if (firstname.isEmpty() || lastname.isEmpty() || email.isEmpty()) {
                    Toast.makeText(UpdateProfileActivity.this, "Please fill out all fields", Toast.LENGTH_SHORT).show(); // Display error message
                } else if (!userController.isValidEmail(email)){
                    Toast.makeText(UpdateProfileActivity.this, "Enter valid email", Toast.LENGTH_SHORT).show(); // Display error message
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
     * Helper function which goes on to call updateImageAndData if the profile picture was updated
     * or calls updateUserDataInFirestore if picture was not updated.
     * @param device Device ID of the user
     * @param firstname Firstname of the user
     * @param lastname Lastname of the user
     * @param email Email of the user
     */
    private void updateUserProfile(String device, String firstname, String lastname, String email) {
            if (imageUri != null) {
                userController.updateProfileImageAndData(device, firstname, lastname, email, imageUri); // Upload new image and update data
            } else {
                userController.updateUserDataInFirestore(device, firstname, lastname, email, currentProfilePicUrl); // Update Firestore without image change//
            }
    }

}
