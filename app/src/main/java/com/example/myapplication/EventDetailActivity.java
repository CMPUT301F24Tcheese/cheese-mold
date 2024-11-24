package com.example.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.myapplication.entrant.GeoAlertDialogFragment;
import com.example.myapplication.objects.Event;
import com.google.android.gms.location.CurrentLocationRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * This Activity represents the detail of an event after the user scanned the QR code.
 * The users are allowed to join or unjoin the waiting list of an event on this page.
 */
public class EventDetailActivity extends AppCompatActivity implements GeoAlertDialogFragment.GeolocationDialogListener {

    private FirebaseFirestore db;
    private Button joinEvent, cancel;
    private TextView eventName, eventDescription;
    private ImageView imageView;
    private String user;
    private Event eventToLoad;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private boolean needLocation;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_event);

        db = FirebaseFirestore.getInstance();
        joinEvent = findViewById(R.id.eventDetailJoin);
        cancel = findViewById(R.id.eventDetailCancel);
        eventName = findViewById(R.id.eventDetailName);
        eventDescription = findViewById(R.id.eventDetailDescription);
        imageView = findViewById(R.id.imageView);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        needLocation = false;

        Intent intent = getIntent();
        user = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        Uri data = intent.getData();

        if (data != null && "event".equals(data.getHost())) {
            // Extract the event ID from the deep link URL
            String eventId = data.getQueryParameter("id");

            if (eventId != null) {
                loadEventDetailsFromFirestore(eventId); // Method to load event data based on ID
            } else {
                Toast.makeText(this, "Event ID not found", Toast.LENGTH_SHORT).show();
                finish();
            }
        }

        cancel.setOnClickListener(v -> finish());
    }

    /**
     * Loads event details from Firestore using the provided event ID.
     *
     * @param eventId The ID of the event to be loaded.
     */
    private void loadEventDetailsFromFirestore(String eventId) {
        db.collection("events").document(eventId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();
                            if (doc.exists()) {
                                eventToLoad = doc.toObject(Event.class);
                                if (eventToLoad != null) {
                                    eventToLoad.setId(doc.getId());
                                    eventName.setText(eventToLoad.getTitle());
                                    eventDescription.setText(eventToLoad.getDescription());
                                    Picasso.get().load(eventToLoad.getPosterUrl()).into(imageView);

                                    setupJoinButton();
                                }
                            } else {
                                Toast.makeText(EventDetailActivity.this, "Event not found", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.w("Firestore", "Error getting document", task.getException());
                        }
                    }
                });
    }

    /**
     * Configures the join button based on the user's current status regarding the event.
     * Sets up the button to either join or unjoin the waiting list.
     */
    private void setupJoinButton() {
        if (eventToLoad.getWaitingList() != null && eventToLoad.getWaitingList().contains(user)) {
            joinEvent.setText("Unjoin Event");
            joinEvent.setOnClickListener(v -> {
                eventToLoad.removeWaitingList(user);
                FireStoreRemoveWaitingList(eventToLoad.getId(), user);
                FireStoreRemoveEventId(eventToLoad.getId(), user);
                Toast.makeText(EventDetailActivity.this, "Unjoined " + eventToLoad.getTitle(), Toast.LENGTH_SHORT).show();
                finish();
            });
        } else {
            joinEvent.setOnClickListener(v -> {
                if (eventToLoad.getGeo()) {
                    needLocation = true;
                    showGeolocationDialog(eventToLoad, user);
                } else {
                    attemptToJoinWaitingList(eventToLoad.getId(), user);
                }
            });
        }
    }

    /**
     * Attempts to add the user to the waiting list for the specified event.
     *
     * @param eventId The ID of the event to join.
     * @param device The user's device ID.
     */
    private void attemptToJoinWaitingList(String eventId, String device) {
        db.collection("events").document(eventId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Long limitEntrants = documentSnapshot.getLong("limitEntrants");
                if (limitEntrants == null) {
                    limitEntrants = Long.MAX_VALUE;
                }
                ArrayList<String> waitingList = (ArrayList<String>) documentSnapshot.get("waitlist");

                if (waitingList != null && waitingList.size() >= limitEntrants) {
                    Toast.makeText(this, "The waiting list is full. You cannot join.", Toast.LENGTH_SHORT).show();
                } else {
                    db.collection("events").document(eventId)
                            .update("waitlist", FieldValue.arrayUnion(device))
                            .addOnSuccessListener(aVoid -> {
                                Log.d("Firestore", "User added to waiting list.");
                                Toast.makeText(this, "You have been added to the waiting list.", Toast.LENGTH_SHORT).show();
                                if (needLocation) {
                                    FirestoreGetLocatioAndAddEventId(eventId, device);
                                } else {
                                    FireStoreAddEventId(eventId, device);
                                }
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                Log.w("Firestore", "Error adding user to waiting list", e);
                                Toast.makeText(this, "Failed to join waiting list. Please try again.", Toast.LENGTH_SHORT).show();
                            });
                }
            } else {
                Toast.makeText(this, "Event not found.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Error accessing event data. Please try again.", Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * Adds the event ID to the user's list of events in Firestore.
     *
     * @param eventId The ID of the event to add.
     * @param device The user's device ID.
     */
    private void FireStoreAddEventId(String eventId, String device) {
        db.collection("users").document(device)
                .update("Event List", FieldValue.arrayUnion(eventId), "location")
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Element added to array"))
                .addOnFailureListener(e -> Log.w("Firestore", "Error adding element to array", e));
    }


    /**
     * Retrieves the current location of the device and updates the Firestore database with the event ID and location.
     *
     * @param eventId The ID of the event to be added to the user's event list in Firestore.
     * @param device  The unique identifier of the device or user document in the Firestore "users" collection.
     */
    private void FirestoreGetLocatioAndAddEventId(String eventId, String device) {
        try {
            // Define the current location request with a maximum age of 10 seconds
            CurrentLocationRequest currentLocationRequest = new CurrentLocationRequest.Builder()
                    .setMaxUpdateAgeMillis(10000)
                    .build();

            // Fetch the current location using the Fused Location Provider
            fusedLocationProviderClient.getCurrentLocation(currentLocationRequest, null)
                    .addOnSuccessListener(location -> {
                        if (location != null) {
                            // Create a GeoPoint with the retrieved latitude and longitude
                            GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());

                            db.collection("users").document(device)
                                    .update("Event List", FieldValue.arrayUnion(eventId), "location", geoPoint)
                                    .addOnSuccessListener(aVoid -> Log.d("Get location firestore", "Element and location added to firestore"))
                                    .addOnFailureListener(e -> Log.w("Get location firestore", "Error adding location and element to firestore", e));

                        } else {
                            Log.e("LocationError", "Location is null");
                        }
                    }).addOnFailureListener(e -> {
                        Log.e("LocationError", "Failed to get location: " + e.getMessage());
                    });
        } catch (SecurityException e) {
            Log.e("Get Location", "Missing permission " + e.getMessage());
        }


    }

    /**
     * Removes the user's device ID from the event's waiting list in Firestore.
     *
     * @param eventId The ID of the event to update.
     * @param device The user's device ID.
     */
    private void FireStoreRemoveWaitingList(String eventId, String device) {
        db.collection("events").document(eventId)
                .update("waitlist", FieldValue.arrayRemove(device))
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Element removed from array"))
                .addOnFailureListener(e -> Log.w("Firestore", "Error removing element from array", e));
    }

    /**
     * Removes the event ID from the user's list of events in Firestore.
     *
     * @param eventId The ID of the event to remove.
     * @param device The user's device ID.
     */
    private void FireStoreRemoveEventId(String eventId, String device) {
        db.collection("users").document(device)
                .update("Event List", FieldValue.arrayRemove(eventId))
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Element removed from array"))
                .addOnFailureListener(e -> Log.w("Firestore", "Error removing element from array", e));
    }

    /**
     * Shows a geolocation dialog to the user for the specified event.
     *
     * @param event The event for which geolocation is needed.
     * @param userId The ID of the user.
     */
    public void showGeolocationDialog(Event event, String userId) {
        GeoAlertDialogFragment dialog = GeoAlertDialogFragment.newInstance(event, userId);
        dialog.show(getSupportFragmentManager(), "GeoAlertDialog");
    }

    /**
     * Callback method when the user clicks to join the event from the geolocation dialog.
     *
     * @param event The event to join.
     * @param userId The ID of the user.
     */
    @Override
    public void onJoinClicked(Event event, String userId) {
        if (event != null) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
            }
        } else {
            Log.e("EventDetailActivity", "Event is null in onJoinClicked");
        }
    }


    /**
     * Handles the result of a permission request.
     *
     * @param requestCode  The request code passed in {@code requestPermissions(String[], int)}.
     * @param permissions  The requested permissions. This value cannot be null.
     * @param grantResults The grant results for the corresponding permissions, which are either
     *                     {@link PackageManager#PERMISSION_GRANTED} or {@link PackageManager#PERMISSION_DENIED}.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                attemptToJoinWaitingList(eventToLoad.getId(), user);
            } else {
                Toast.makeText(this, "Location permission needed to join", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
