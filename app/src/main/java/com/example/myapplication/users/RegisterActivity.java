package com.example.myapplication.users;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.administrator.AdministratorMainActivity;
import com.example.myapplication.entrant.EntrantMainActivity;
import com.example.myapplication.objects.Users;
import com.example.myapplication.organizer.OrganizerMainActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseFirestore db; // Firestore database object
    private StorageReference storageReference; // Firebase Storage reference for storing images
    private CollectionReference usersRef; // Reference to the "users" collection in Firestore
    private EditText registerEmail; // EditText for user to input email
    private EditText registerFirstname; // EditText for user to input first name
    private EditText registerLastname; // EditText for user to input last name
    private Spinner roleSelector; // Spinner for selecting user roles (not used in current implementation)
    private Button registerBtn; // Button to trigger registration
    private TextView loginRedirect; // TextView to redirect user to login page
    private ImageView profilePicture; // ImageView to display profile picture
    Uri imageUri; // URI for the selected profile picture
    private String device;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Call to parent class onCreate
        setContentView(R.layout.activity_register); // Set the layout for the activity
        device = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);


        // Initialize Firebase services
        db = FirebaseFirestore.getInstance(); // Initialize Firestore database
        usersRef = db.collection("users"); // Get reference to the "users" collection in Firestore
        storageReference = FirebaseStorage.getInstance().getReference(); // Initialize Firebase Storage reference

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
                } else {
                    if (imageUri == null) {
                        String defaultProfilePicUrl = "https://avatar.iran.liara.run/username?username=" + firstname + "+" + lastname; // URL for default profile picture
                        uploadUserData(new Users(device, firstname, lastname, email, defaultProfilePicUrl, role)); // Upload user data with default profile picture
                    } else {
                        uploadImageAndData(imageUri, new Users(device, firstname, lastname, email, "", role)); // Upload selected image and user data
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
     * Stores the profile photo into firebase storage in the profile image directory
     * If upload is successful, generates a URL for the image which is passed into
     * uploadUserData function which then goes on to upload all the data in firestore
     * @param image URI of the selected profile image
     * @param user User object to upload to firebase
     */
    private void uploadImageAndData(Uri image, Users user) {
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
                    Toast.makeText(RegisterActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show(); // Show error if image upload fails
                });
    }

    /**
     * Resets the input fields after successful user registration
     */
    private void resetFields() {
        registerEmail.setText(""); // Clear the email field
        registerFirstname.setText(""); // Clear the first name field
        registerLastname.setText(""); // Clear the last name field
        profilePicture.setImageResource(R.drawable.baseline_person_outline_24); // Reset the profile picture to default icon
    }

    /**
     * Function takes the user data and uploads them into firestore database
     * Redirects the user to login if storing is successful.
     * @param user User object which will be uploaded to firebase
     */
    private void uploadUserData(Users user) {
        HashMap<String, Object> data = new HashMap<>(); // Create a HashMap to store user data
        data.put("Email", user.getEmail()); // Add user's email to the map
        data.put("role", user.getRole());
        data.put("Firstname", user.getFirstName()); // Add user's first name to the map
        data.put("Lastname", user.getLastName()); // Add user's last name to the map
        data.put("Profile Picture", user.getProfilePicture()); // Add user's profile picture URL to the map
        data.put("Event List", user.getEventList()); // **(2) Add waitlist to the event map**

        usersRef.document(device).set(data) // Store the user data in Firestore under the user's unique ID
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "User data added successfully!"); // Log success message
                    resetFields(); // Reset input fields after successful registration
                    if (user.getRole().equals("Entrant")) {
                        startActivity(new Intent(this, EntrantMainActivity.class)); // navigate to main screen
                        finish(); // Close the MainActivity
                    } else if (user.getRole().equals("Organizer")) {
                        startActivity(new Intent(this, OrganizerMainActivity.class)); // navigate to main screen
                        finish(); // Close the MainActivity
                    } else if (user.getRole().equals("Administrator")) {
                        startActivity(new Intent(this, AdministratorMainActivity.class)); // Navigate to main screen
                        finish(); // Close the MainActivity
                    }
                })
                .addOnFailureListener(e -> Log.w("Firestore", "Error adding user data", e)); // Log error if data upload fails
    }
}
