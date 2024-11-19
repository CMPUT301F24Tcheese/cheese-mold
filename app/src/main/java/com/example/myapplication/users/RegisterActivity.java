package com.example.myapplication.users;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.controllers.UserController;
import com.example.myapplication.objects.Users;

/**
 * Register Activity that handles the user registration process
 */
public class RegisterActivity extends AppCompatActivity {
    private EditText registerEmail; // EditText for user to input email
    private EditText registerFirstname; // EditText for user to input first name
    private EditText registerLastname; // EditText for user to input last name
    private Spinner roleSelector; // Spinner for selecting user roles (not used in current implementation)
    private Button registerBtn; // Button to trigger registration
    private ImageView profilePicture; // ImageView to display profile picture
    Uri imageUri; // URI for the selected profile picture
    private String device;
    private UserController userController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Call to parent class onCreate
        setContentView(R.layout.activity_register); // Set the layout for the activity
        device = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        userController = new UserController(this);

        // Initialize UI elements
        registerEmail = findViewById(R.id.registerEmail); // Get reference to EditText for email
        registerFirstname = findViewById(R.id.registerFirstName); // Get reference to EditText for first name
        registerLastname = findViewById(R.id.registerLastName); // Get reference to EditText for last name
        registerBtn = findViewById(R.id.registerBtn); // Get reference to registration button
        profilePicture = findViewById(R.id.profilePicture); // Get reference to ImageView for profile picture
        roleSelector = findViewById(R.id.roleSpinner);

        // Set click listener for profile picture selection
        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK); // Create an Intent to pick an image from gallery
                intent.setType("image/*"); // Set the intent to filter for images only
                activityResultLauncher.launch(intent); // Launch the intent to select an image
            }
        });

        // Set click listener for registration button
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String firstname = registerFirstname.getText().toString().trim(); // Get the entered first name
                String lastname = registerLastname.getText().toString().trim(); // Get the entered last name
                String email = registerEmail.getText().toString().trim(); // Get the entered email
                String role = roleSelector.getSelectedItem().toString().trim(); // get the selected role

                // Check if any of the fields are empty
                if (firstname.isEmpty() || lastname.isEmpty() || email.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Please fill out all the fields", Toast.LENGTH_SHORT).show(); // Show error message
                } else if (!userController.isValidEmail(email)){
                    Toast.makeText(RegisterActivity.this, "Enter valid email", Toast.LENGTH_SHORT).show(); // Display error message
                } else {
                    if (imageUri == null) {
                        String defaultProfilePicUrl = "https://avatar.iran.liara.run/username?username=" + firstname + "+" + lastname; // URL for default profile picture
                        userController.uploadUserData(new Users(device, firstname, lastname, email, defaultProfilePicUrl, role)); // Upload user data with default profile picture
                        resetFields();
                    } else {
                        userController.uploadImageAndData(imageUri, new Users(device, firstname, lastname, email, "", role)); // Upload selected image and user data
                        resetFields();
                    }
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
            if (o.getResultCode() == RESULT_OK) { // Check if image selection was successful
                if (o.getData() != null) {
                    imageUri = o.getData().getData(); // Get the URI of the selected image
                    profilePicture.setImageURI(imageUri); // Set the ImageView to display the selected picture
                }
            } else {
                Toast.makeText(RegisterActivity.this, "Please select an image", Toast.LENGTH_SHORT).show(); // Show message if no image was selected
            }
        }
    });


    /**
     * Resets the input fields after successful user registration
     */
    private void resetFields() {
        registerEmail.setText(""); // Clear the email field
        registerFirstname.setText(""); // Clear the first name field
        registerLastname.setText(""); // Clear the last name field
        profilePicture.setImageResource(R.drawable.baseline_person_outline_24); // Reset the profile picture to default icon
    }

}
