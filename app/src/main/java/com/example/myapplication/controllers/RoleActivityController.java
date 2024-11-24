package com.example.myapplication.controllers;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.EventAdapter;
import com.example.myapplication.objects.Event;
import com.example.myapplication.objects.Facility;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.ArrayList;

public class RoleActivityController {

    private FirebaseFirestore db;
    private Context context;
    private UserController userController;

    public RoleActivityController(Context context) {
        this.context = context;
        db = FirebaseFirestore.getInstance();
        userController = new UserController(context);
    }

    /**
     * Retrieves user data from Firestore and loads it into the activity UI components
     * @param device The unique ID of the logged-in user
     */
    public void getData(String device, ImageView view) {
        // Access the "users" collection in Firestore and get the document corresponding to the userId
        db.collection("users").document(device).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) { // Check if the data retrieval task was successful
                        DocumentSnapshot document = task.getResult(); // Get the document snapshot from Firestore

                        if(document.exists()) { // Verify that the document exists
                            String profilePicUrl = document.getString("Profile Picture"); // Retrieve the URL of the user's profile picture

                            // Load the user's profile picture using Glide, a third-party image loading library
                            userController.setImageInView(view, profilePicUrl);
                        } else {
                            Log.d("MainActivity", "No such document"); // Log a message if the document does not exist
                        }
                    } else {
                        Log.w("MainActivity", "Error getting documents.", task.getException()); // Log a warning if there was an error retrieving the document
                    }
                });
    }

    /**
     * Retrieves the list of events associated with the user from Firestore.
     */
    public void loadJoinedEvents(String userId, EventAdapter eventAdapter, ArrayList<Event> eventList) {
        eventList.clear();
        eventAdapter.notifyDataSetChanged();
        db.collection("users").document(userId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        ArrayList<String> eventListFromDb = (ArrayList<String>) documentSnapshot.get("Event List");
                        if (eventListFromDb != null && !eventListFromDb.isEmpty()) {
                            for (String event : eventListFromDb) {
                                Log.d("getEventsFromFirestore", event);
                                loadEventDetails(event, eventAdapter, eventList);
                            }
                        } else {
                            Log.d("getEventsFromFirestore", "No events found in the user's event list.");
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("getEventsFromFirestore", "Error fetching user event list: " + e.getMessage());
                });
    }


    /**
     * Loads the event details for a specific event ID from Firestore and adds it to the event list.
     * @param eventId The ID of the event to be loaded.
     */
    public void loadEventDetails(String eventId, EventAdapter eventAdapter, ArrayList<Event> eventList) {
        db.collection("events").document(eventId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    Event event = documentSnapshot.toObject(Event.class);
                    if (event != null) {
                        event.setId(documentSnapshot.getId());
                        event.setId(documentSnapshot.getId());
                        event.setWaitingList((ArrayList<String>) documentSnapshot.get("waitlist"));
                        event.setFinalEntrantsNum(documentSnapshot.getLong("maxCapacity"));
                        event.setFirstDraw(documentSnapshot.getBoolean("firstDraw"));
                        event.setLottery((ArrayList<String>) documentSnapshot.get("lotteryList"));

                        Log.d("Local", "Lottery list:" + event.getLottery());
                        eventList.add(event);
                        eventAdapter.notifyDataSetChanged();
                    }
                });
    }

    /**
     * Retrieves the facility data from Firestore and updates the UI.
     */
    public void getFacilityData(String facilityId, TextView name, TextView address, EventAdapter facilityEventAdapter, ArrayList<Event> facilityEventList) {
        db.collection("Facilities").document(facilityId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Facility facility = documentSnapshot.toObject(Facility.class);
                        if (facility != null) {
                            name.setText(facility.getName());
                            address.setText(facility.getAdress());
                            loadFacilityEvents(facilityId, facilityEventAdapter, facilityEventList);
                        }
                    }
                });
    }


    /**
     * Loads events associated with the facility from Firestore.
     */
    public void loadFacilityEvents(String facilityId, EventAdapter facilityEventAdapter, ArrayList<Event> facilityEventList) {
        facilityEventList.clear();
        facilityEventAdapter.notifyDataSetChanged();
        db.collection("events").whereEqualTo("creatorID", facilityId).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                            Event event = doc.toObject(Event.class);
                            if (event != null) {
                                event.setId(doc.getId());
                                facilityEventList.add(event);
                            }
                        }
                        facilityEventAdapter.notifyDataSetChanged();
                    } else {
                        Log.d("Load Facility Events", "No events found for this creator.");
                    }
                }).addOnFailureListener(e -> Log.e("Event Data", "Error fetching events", e));
    }
}
