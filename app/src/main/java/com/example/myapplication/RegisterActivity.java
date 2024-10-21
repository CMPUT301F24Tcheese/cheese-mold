package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private StorageReference storageReference;
    private CollectionReference usersRef;
    private EditText registerEmail;
    private  EditText registerPassword;
    private EditText registerFirstname;
    private EditText registerLastname;
    private Spinner roleSelector;
    private Button registerBtn;
    private TextView loginRedirect;
    private ImageView profilePicture;
    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        usersRef = db.collection("users");
        storageReference = FirebaseStorage.getInstance().getReference();

        registerEmail = findViewById(R.id.registerEmail);
        registerPassword = findViewById(R.id.registerPassword);
        registerFirstname = findViewById(R.id.registerFirstName);
        registerLastname = findViewById(R.id.registerLastName);
        registerBtn = findViewById(R.id.registerBtn);
        loginRedirect = findViewById(R.id.loginRedirectText);
        profilePicture = findViewById(R.id.profilePicture);


        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                activityResultLauncher.launch(intent);
            }
        });

        loginRedirect.setOnClickListener(view -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String firstname = registerFirstname.getText().toString().trim();
                String lastname = registerLastname.getText().toString().trim();
                String email = registerEmail.getText().toString().trim();
                String password = registerPassword.getText().toString().trim();

                if (firstname.isEmpty() || lastname.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Please fill out all the fields", Toast.LENGTH_SHORT).show();
                } else {

                    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(RegisterActivity.this, "SignUp Successful", Toast.LENGTH_SHORT).show();
                                String userId = auth.getCurrentUser().getUid(); // gets the unique userId to store in the database

                                if (imageUri == null) {
                                    String defaultProfilePicUrl = "https://avatar.iran.liara.run/username?username=" + firstname + "+" + lastname; // API link for default profile photo
                                    uploadUserData(userId, firstname, lastname, email, defaultProfilePicUrl);
                                } else {
                                    uploadImageAndData(imageUri, userId, firstname, lastname, email);
                                }

                            } else {
                                if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                    // Handle case where the email is already registered
                                    Toast.makeText(RegisterActivity.this, "Email already registered", Toast.LENGTH_SHORT).show();
                                } else {
                                    // Handle the case if signup fails
                                    Toast.makeText(RegisterActivity.this, "SignUp Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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
            if (o.getResultCode() == RESULT_OK) {
                if (o.getData() != null) {
                    imageUri = o.getData().getData();
                    profilePicture.setImageURI(imageUri); // sets the profile pic to the selected picture
                }

            } else {
                Toast.makeText(RegisterActivity.this, "Please select an image", Toast.LENGTH_SHORT).show();
            }
        }
    });

    /**
     * Stores the profile photo into firebase storage in the profile image directory
     * If upload is successful, generates a URL for the image which is passed into
     * uploadUserData function which then goes on to upload all the data in firestore
     * @param image
     * @param userId
     * @param firstname
     * @param lastname
     * @param email
     */
    private void uploadImageAndData(Uri image, String userId, String firstname, String lastname, String email) {
        StorageReference reference = storageReference.child("profile_images/" + UUID.randomUUID() + ".jpg");
        reference.putFile(image)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                     @Override
                     public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                         reference.getDownloadUrl().addOnSuccessListener(uri -> {
                             String downloadUrl = uri.toString();
                             uploadUserData(userId, firstname, lastname, email, downloadUrl);
                         });
                     }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(RegisterActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Resets the input field after successful user registration
     */
    private void resetFields() {
        registerEmail.setText("");
        registerPassword.setText("");
        registerFirstname.setText("");
        registerLastname.setText("");
        profilePicture.setImageResource(R.drawable.baseline_person_outline_24);
    }

    /**
     * Function takes the user data and uploads them into firestore database
     * Redirects the user to login storing is successful.
     * @param userId
     * @param firstname
     * @param lastname
     * @param email
     * @param profilePicUrl
     */
    private void uploadUserData(String userId, String firstname, String lastname, String email, String profilePicUrl) {
        HashMap<String, String> data = new HashMap<>();
        data.put("Email", email);
        data.put("Firstname", firstname);
        data.put("Lastname", lastname);
        data.put("Profile Picture", profilePicUrl);

        usersRef.document(userId).set(data)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "User data added successfully!");
                    resetFields();
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class)); //Redirects to login after registration
                    finish();
                })
                .addOnFailureListener(e -> Log.w("Firestore", "Error adding user data", e));
    }
}