package com.example.myapplication;

import android.content.Intent;
import android.credentials.CreateCredentialRequest;
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
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.administrator.AdministratorMainActivity;
import com.example.myapplication.entrant.EntrantMainActivity;
import com.example.myapplication.organizer.OrganizerMainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.UUID;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth auth; // Firebase authentication object
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
        auth = FirebaseAuth.getInstance(); // Initialize Firebase authentication
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
                    // Register the user with Firebase authentication

                    auth.createUserWithEmailAndPassword(email, device).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) { // Check if registration is successful
                                Toast.makeText(RegisterActivity.this, "SignUp Successful", Toast.LENGTH_SHORT).show(); // Show success message
                                String userId = auth.getCurrentUser().getUid(); // Get the unique userId for the newly registered user

                                // Check if a profile picture was selected, if not use a default one
                                if (imageUri == null) {
                                    String defaultProfilePicUrl = "https://avatar.iran.liara.run/username?username=" + firstname + "+" + lastname; // URL for default profile picture
                                    uploadUserData(userId, firstname, lastname, email, role, device, defaultProfilePicUrl); // Upload user data with default profile picture
                                } else {
                                    uploadImageAndData(imageUri, userId, firstname, lastname, email, device, role); // Upload selected image and user data
                                }

                            } else {
                                // Handle case where email is already registered
                                if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                    Toast.makeText(RegisterActivity.this, "Email already registered", Toast.LENGTH_SHORT).show(); // Show error for existing email
                                } else {
                                    // Handle other registration failures
                                    Toast.makeText(RegisterActivity.this, "SignUp Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show(); // Show general error
                                }
                            }
                        }
                    });
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
     * @param userId Unique user ID for the registered user
     * @param firstname User's first name
     * @param lastname User's last name
     * @param email User's email
     */
    private void uploadImageAndData(Uri image, String userId, String firstname, String lastname, String email, String role, String device) {
        StorageReference reference = storageReference.child("profile_images/" + UUID.randomUUID() + ".jpg"); // Create a unique path for the profile image
        reference.putFile(image) // Upload the image to Firebase Storage
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        reference.getDownloadUrl().addOnSuccessListener(uri -> {
                            String downloadUrl = uri.toString(); // Get the download URL of the uploaded image
                            uploadUserData(userId, firstname, lastname, email, role, device, downloadUrl); // Upload user data along with the image URL
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
     * @param userId Unique user ID for the registered user
     * @param firstname User's first name
     * @param lastname User's last name
     * @param email User's email
     * @param profilePicUrl URL of the user's profile picture
     */
    private void uploadUserData(String userId, String firstname, String lastname, String email, String role, String device, String profilePicUrl) {
        HashMap<String, String> data = new HashMap<>(); // Create a HashMap to store user data
        data.put("Email", email); // Add user's email to the map
        data.put("role", role);
        data.put("sUID", device);
        data.put("Firstname", firstname); // Add user's first name to the map
        data.put("Lastname", lastname); // Add user's last name to the map
        data.put("Profile Picture", profilePicUrl); // Add user's profile picture URL to the map

        usersRef.document(userId).set(data) // Store the user data in Firestore under the user's unique ID
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "User data added successfully!"); // Log success message
                    resetFields(); // Reset input fields after successful registration
                    if (role.equals("Entrant")) {
                        startActivity(new Intent(this, EntrantMainActivity.class)); // navigate to main screen
                        finish(); // Close the MainActivity
                    } else if (role.equals("Organizer")) {
                        startActivity(new Intent(this, OrganizerMainActivity.class)); // navigate to main screen
                        finish(); // Close the MainActivity
                    } else if (role.equals("Administrator")) {
                        startActivity(new Intent(this, AdministratorMainActivity.class)); // Navigate to main screen
                        finish(); // Close the MainActivity
                    }
                })
                .addOnFailureListener(e -> Log.w("Firestore", "Error adding user data", e)); // Log error if data upload fails
    }
}
