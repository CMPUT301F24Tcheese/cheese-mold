package com.example.myapplication.administrator;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.anything;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.myapplication.R;
import com.example.myapplication.objects.Event;
import com.example.myapplication.objects.Users;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;

/**
 * Large test class to test the buttons in the AdminViewUser activity
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class AdminViewEventTest {
    private FirebaseFirestore db;

    @Rule
    public ActivityScenarioRule<AdministratorMainActivity> scenario = new
            ActivityScenarioRule<AdministratorMainActivity>(AdministratorMainActivity.class);

    /**
     * defines event object for display
     */
    private void setTestItems() {
        db = FirebaseFirestore.getInstance();
        String defaultProfilePicUrl = "https://avatar.iran.liara.run/username?username=" + "Test" + "+" + "Person";// URL for default profile picture
        Event event = new Event("000000000", "Fun Test Event", "Test description", defaultProfilePicUrl, defaultProfilePicUrl, "000000000");
        uploadData(event);
    }

    /**
     * uploads test event data to firebase
     * @param event user object for display
     */
    private void uploadData(@NonNull Event event) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("name", event.getTitle());
        data.put("description", event.getDescription());
        data.put("posterUrl", event.getPosterUrl());
        data.put("qrCodeUrl", event.getQRcode());
        data.put("creatorID", event.getCreatorID());

        db.collection("events").document("000000000").set(data)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "event data added successfully!");
                })
                .addOnFailureListener(e -> Log.w("Firestore", "Error adding event data", e));

        HashMap<String, Object> data2 = new HashMap<>();
        data2.put("Firstname", "Test");
        data2.put("Lastname", "Person");
        db.collection("users").document("000000000").set(data2)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "User data added successfully!");
                })
                .addOnFailureListener(e -> Log.w("Firestore", "Error adding user data", e));

        HashMap<String, Object> data3 = new HashMap<>();
        data3.put("name", "Test");
        db.collection("Facilities").document("000000000").set(data3)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "facility data added successfully!");
                })
                .addOnFailureListener(e -> Log.w("Firestore", "Error adding facility data", e));
    }

    /**
     * deletes the test event from firebase
     */
    private void deleteEvent() {
        db.collection("events").document("000000000").delete()
                        .addOnFailureListener(e -> Log.w("Firestore", "Error removing event data", e));
        db.collection("users").document("000000000").delete();
        db.collection("Facilities").document("000000000").delete();
    }

    /**
     * deletes the test event from firebase
     */
    private void deleteEventOther() {
        db.collection("users").document("000000000").delete();
        db.collection("Facilities").document("000000000").delete();
    }

    /**
     * tests if the back button
     */
    @Test
    public void testBackButton() {
        // setting test event
        setTestItems();

        onView(withId(R.id.browseEventsBtn)).perform(click());

        // click on newly created test profile
        onData(anything()).inAdapterView(withId(R.id.contentListView)).atPosition(0).perform(click());

        onView(withId(R.id.adminEventBack)).perform(click());

        // checking if the view is correct
        onView(withId(R.id.browseHeader)).check(matches(isDisplayed()));
        onView(withText("Fun Test Event")).check(matches(isDisplayed()));

        // removing test event
        deleteEvent();
    }

    /**
     * tests the delete button
     */
    @Test
    public void testDeleteButton() {
        // setting test event
        setTestItems();

        onView(withId(R.id.browseEventsBtn)).perform(click());

        // click on newly created test profile
        onData(anything()).inAdapterView(withId(R.id.contentListView)).atPosition(0).perform(click());

        onView(withId(R.id.adminEventDelete)).perform(click());

        // checking if view is correct
        onView(withText("Delete Event?")).check(matches(isDisplayed()));

        // removing test event
        deleteEvent();
    }

    /**
     * tests the cancel button that appears after clicking the delete button
     */
    @Test
    public void testDeleteButtonCancel() {
        // setting test event
        setTestItems();

        onView(withId(R.id.browseEventsBtn)).perform(click());

        // click on newly created test profile
        onData(anything()).inAdapterView(withId(R.id.contentListView)).atPosition(0).perform(click());

        onView(withId(R.id.adminEventDelete)).perform(click());
        onView(withText("Cancel")).perform(click());

        // checking if view is correct
        onView(withId(R.id.adminEventName)).check(matches(isDisplayed()));
        onView(withId(R.id.poster)).check(matches(isDisplayed()));
        onView(withId(R.id.organizer)).check(matches(isDisplayed()));
        onView(withId(R.id.facility)).check(matches(isDisplayed()));
        onView(withId(R.id.eventDetail)).check(matches(isDisplayed()));
        onView(withId(R.id.adminEventDelete)).check(matches(isDisplayed()));
        onView(withId(R.id.adminEventBack)).check(matches(isDisplayed()));

        // removing test event
        deleteEvent();
    }

    /**
     * tests the delete button that appears after clicking the delete button
     */
    @Test
    public void testDeleteButtonDelete() {
        // setting test event
        setTestItems();

        onView(withId(R.id.browseEventsBtn)).perform(click());

        // click on newly created test profile
        onData(anything()).inAdapterView(withId(R.id.contentListView)).atPosition(0).perform(click());

        onView(withId(R.id.adminEventDelete)).perform(click());
        onView(withText("Delete")).perform(click());

        // checking if view is correct
        onView(withText("Fun Test Event")).check(doesNotExist());

        // removing test event
        deleteEventOther();
    }
}
