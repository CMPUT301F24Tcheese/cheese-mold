package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.UUID;

public class UpdateProfileActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private StorageReference storageReference;
    private EditText updateFirstname;
    private EditText updateLastname;
    private EditText updateEmail;
    private Button confirmUpdateBtn;
    private ImageView updateProfilePic;
    private TextView cancelText;
    Uri imageUri;
    private String currentProfilePicUrl;
    private EditText password;
    private Button updateEmailBtn;
    private Button removeProfilePictureBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_profile);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        updateFirstname = findViewById(R.id.updateFirstName);
        updateLastname = findViewById(R.id.updateLastName);
        updateEmail = findViewById(R.id.updateEmail);
        confirmUpdateBtn = findViewById(R.id.updateBtn);
        updateProfilePic = findViewById(R.id.updateProfilePicture);
        cancelText = findViewById(R.id.cancelUpdateText);
        password = findViewById(R.id.updateEmailPassword);
        updateEmailBtn = findViewById(R.id.updateEmailBtn);
        removeProfilePictureBtn = findViewById(R.id.removeProfileBtn);

        FirebaseUser currentUser = auth.getCurrentUser();
        loadUserProfile(currentUser.getUid());

        updateProfilePic.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            activityResultLauncher.launch(intent);
        });

        cancelText.setOnClickListener(view -> {
            startActivity(new Intent(UpdateProfileActivity.this, MainActivity.class));
            finish();
        });

        removeProfilePictureBtn.setOnClickListener(view -> {
            removeProfilePicture(currentUser.getUid());
        });

        // Update email not working properly, only changes in firestore but not in authentication
        // Need to check!!!
        updateEmailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = updateEmail.getText().toString().trim();
                String pass = password.getText().toString().trim();

                if(email.isEmpty() || pass.isEmpty()) {
                    Toast.makeText(UpdateProfileActivity.this, "Please enter password and email", Toast.LENGTH_SHORT).show();
                } else {
                    AuthCredential credential = EmailAuthProvider.getCredential(currentUser.getEmail(), pass);
                    currentUser.reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        currentUser.updateEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                updateEmailFirestore(currentUser.getUid(), email);
                                                Toast.makeText(UpdateProfileActivity.this, "Email updated successfully", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    } else {
                                        Toast.makeText(UpdateProfileActivity.this, "Email failed to update", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }


            }
        });

        confirmUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String firstname = updateFirstname.getText().toString().trim();
                String lastname = updateLastname.getText().toString().trim();


                if (firstname.isEmpty() || lastname.isEmpty()) {
                    Toast.makeText(UpdateProfileActivity.this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
                } else {
                    updateUserProfile(currentUser.getUid(), firstname, lastname);
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
            if (o.getResultCode() == RESULT_OK) {
                if (o.getData() != null) {
                    imageUri = o.getData().getData();
                    updateProfilePic.setImageURI(imageUri); // sets the profile pic to the selected picture
                }

            } else {
                Toast.makeText(UpdateProfileActivity.this, "Please select an image", Toast.LENGTH_SHORT).show();
            }
        }
    });


    /**
     * Loads the user info from the database and then fills out the
     * edit texts for the user
     * @param userId
     *      Id of the user
     */
    private void loadUserProfile(String userId) {
        db.collection("users").document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String firstname = documentSnapshot.getString("Firstname");
                    String lastname = documentSnapshot.getString("Lastname");
                    String email = documentSnapshot.getString("Email");
                    currentProfilePicUrl = documentSnapshot.getString("Profile Picture");

                    updateFirstname.setText(firstname);
                    updateLastname.setText(lastname);
                    updateEmail.setText(email);


                    if (currentProfilePicUrl != null && !currentProfilePicUrl.isEmpty()) {
                        // Load the current profile picture from the URL
                        // Glide is a third party framework. Not built into android studio
                        // It's in the dependencies
                        // Sets the user profile image in the imageview
                        Glide.with(UpdateProfileActivity.this)
                                .load(currentProfilePicUrl)
                                .placeholder(R.drawable.baseline_person_outline_24) // While loading there is a default picture template
                                .error(R.drawable.baseline_person_outline_24) // In case picture fails to load from db, uses default template
                                .into(updateProfilePic);
                    }
                }
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(UpdateProfileActivity.this, "Error loading user data", Toast.LENGTH_SHORT).show();
        });
    }


    /**
     * Removes the profile picture of the user. Does not actually change in db,
     * just changes the frontend. But this changes the currentProfilePicUrl to the default name profile pic
     * which will later be used to update the db.
     * @param userId
     */
    private void removeProfilePicture(String userId) {
        db.collection("users").document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String firstname = documentSnapshot.getString("Firstname");
                    String lastname = documentSnapshot.getString("Lastname");
                    currentProfilePicUrl = "https://avatar.iran.liara.run/username?username=" + firstname + "+" + lastname; // API link for default profile photo

                    Glide.with(UpdateProfileActivity.this)
                            .load(currentProfilePicUrl)
                            .placeholder(R.drawable.baseline_person_outline_24)
                            .error(R.drawable.baseline_person_outline_24)
                            .into(updateProfilePic);
                }
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(UpdateProfileActivity.this, "Error loading user data", Toast.LENGTH_SHORT).show();
        });
    }


    /**
     * Helper function which goes on to call updateImageAndData if the profile picture was updated
     * or calls updateUserDataInFirestore if picture was not updated.
     * @param userId
     * @param firstname
     * @param lastname
     */
    private void updateUserProfile(String userId, String firstname, String lastname) {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            // If user selected a new profile picture, upload it
            if (imageUri != null) {
                updateProfileImageAndData(userId, firstname, lastname);
            } else {
                // Otherwise, just update Firestore data
                updateUserDataInFirestore(userId, firstname, lastname, currentProfilePicUrl);
            }
        }
    }


    /**
     * Saves the new profile picture in the firebase storage, creates an URL for the image
     * which is then passed onto the updateUserDataInFirestore to change the total user data.
     * @param userId
     * @param firstname
     * @param lastname
     */
    private void updateProfileImageAndData(String userId, String firstname, String lastname) {
        StorageReference reference = storageReference.child("profile_images/" + UUID.randomUUID().toString() + ".jpg"); //file path for the photo
        reference.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        reference.getDownloadUrl().addOnSuccessListener(uri -> {
                            String downloadUrl = uri.toString();
                            updateUserDataInFirestore(userId, firstname, lastname, downloadUrl);
                        });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(UpdateProfileActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                });
    }


    /**
     * Main function which updates all the user data other than the email.
     * @param userId
     * @param firstname
     * @param lastname
     * @param profilePicUrl
     */
    private void updateUserDataInFirestore(String userId, String firstname, String lastname, String profilePicUrl) {
        db.collection("users").document(userId)
                .update("Firstname", firstname, "Lastname", lastname, "Profile Picture", profilePicUrl)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(UpdateProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(UpdateProfileActivity.this, MainActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(UpdateProfileActivity.this, "Failed to update profile", Toast.LENGTH_SHORT).show());
    }

    /**
     * Updates the email in the firestore after the user has been reauthenticated. This works but the email is not
     * changing in the firebase authentication even if I reauthenticate. Need to take a look at it. If someones finds a solution
     * let me know!
     * @param userId
     * @param email
     */
    private void updateEmailFirestore(String userId, String email) {
        db.collection("users").document(userId)
                .update("Email", email);

    }
}