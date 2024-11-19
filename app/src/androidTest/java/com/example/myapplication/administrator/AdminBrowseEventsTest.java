package com.example.myapplication.administrator;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isNotFocused;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.anything;

import android.content.Intent;
import android.util.Log;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.myapplication.R;
import com.example.myapplication.entrant.EntrantMainActivity;
import com.example.myapplication.objects.Event;
import com.example.myapplication.objects.Users;
import com.example.myapplication.organizer.OrganizerMainActivity;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

/**
 * Large test class to test the buttons in the AdminBrowseEvents activity
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class AdminBrowseEventsTest {
    private FirebaseFirestore db;
    final CountDownLatch latchOne = new CountDownLatch(1);
    final CountDownLatch latchTwo = new CountDownLatch(1);
    final CountDownLatch latchThree = new CountDownLatch(1);

    @Rule
    public ActivityScenarioRule<AdministratorMainActivity> scenario = new
            ActivityScenarioRule<AdministratorMainActivity>(AdministratorMainActivity.class);

    /**
     * defines the event that is for display
     */
    private void setTestItems() {
        db = FirebaseFirestore.getInstance();
        String defaultProfilePicUrl = "https://avatar.iran.liara.run/username?username=" + "Test" + "+" + "Person";// URL for default profile picture
        Event event = new Event("000000000", "Test", "Test description", defaultProfilePicUrl, defaultProfilePicUrl, "000000000");
        uploadData(event);
    }

    /**
     * uploads test data to firestore
     * @param event event object for display
     */
    private void uploadData(Event event) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("name", event.getTitle());
        data.put("description", event.getDescription());
        data.put("posterUrl", event.getPosterUrl());
        data.put("qrCodeUrl", event.getQRcode());
        data.put("creatorID", event.getCreatorID());

        db.collection("events").document("000000000").set(data)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "event data added successfully!");
                    latchOne.countDown();
                })
                .addOnFailureListener(e -> Log.w("Firestore", "Error adding event data", e));

        HashMap<String, Object> data2 = new HashMap<>();
        data2.put("Firstname", "Test");
        data2.put("Lastname", "Person");
        db.collection("users").document("000000000").set(data2)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "User data added successfully!");
                    latchTwo.countDown();
                })
                .addOnFailureListener(e -> Log.w("Firestore", "Error adding user data", e));

        HashMap<String, Object> data3 = new HashMap<>();
        data3.put("name", "Test");
        db.collection("Facilities").document("000000000").set(data3)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "facility data added successfully!");
                    latchThree.countDown();
                })
                .addOnFailureListener(e -> Log.w("Firestore", "Error adding facility data", e));
    }

    /**
     * deletes test data added in upload event
     */
    private void deleteData() {
        db.collection("events").document("000000000").delete();
        db.collection("users").document("000000000").delete();
        db.collection("Facilities").document("000000000").delete();
    }

    /**
     * Testing the back button to confirm it sends the user back to the Admin Main Activity
     */
    @Test
    public void testBackButton() {
        onView(withId(R.id.browseEventsBtn)).perform(click());
        // verify we've entered the appropriate view before going back
        onView(withId(R.id.browseHeader)).check(matches(isDisplayed()));
        onView(withId(R.id.back_button)).perform(click());
        // confirms we have left the events view
        onView(withId(R.id.browseHeader)).check(doesNotExist());
        onView(withId(R.id.browseHeader)).check(doesNotExist());
        //confirms we have arrived back at the administrator main activity
        onView(withId(R.id.welcomeTextView)).check(matches(isDisplayed()));
    }

    /**
     * Testing that selecting an event will open a new page that displays the event information
     */
    @Test
    public void testSelectEvent() throws InterruptedException {
        // setting event and corresponding creator and facility for display
        setTestItems();

        latchOne.await();
        latchTwo.await();
        latchThree.await();

        onView(withId(R.id.browseEventsBtn)).perform(click());

        // click on test data
        onData(anything()).inAdapterView(withId(R.id.contentListView)).atPosition(0).perform(click());

        // check if new view is displayed
        onView(withId(R.id.adminEventName)).check(matches(isDisplayed()));
        onView(withId(R.id.poster)).check(matches(isDisplayed()));
        onView(withId(R.id.organizer)).check(matches(isDisplayed()));
        onView(withId(R.id.facility)).check(matches(isDisplayed()));
        onView(withId(R.id.eventDetail)).check(matches(isDisplayed()));
        onView(withId(R.id.adminEventDelete)).check(matches(isDisplayed()));
        onView(withId(R.id.adminEventBack)).check(matches(isDisplayed()));

        // remove test data
        deleteData();

    }

}
