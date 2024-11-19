package com.example.myapplication.administrator;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;

import android.util.Log;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.myapplication.R;
import com.example.myapplication.objects.Event;
import com.example.myapplication.objects.Facility;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.concurrent.CountDownLatch;


/**
 * Large test class to test the buttons in the AdminBrowseFacilities activity
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class AdminBrowseFacilitiesTest {
    private FirebaseFirestore db;
    final CountDownLatch latchOne = new CountDownLatch(1);
    final CountDownLatch latchTwo = new CountDownLatch(1);


    @Rule
    public ActivityScenarioRule<AdministratorMainActivity> scenario = new
            ActivityScenarioRule<AdministratorMainActivity>(AdministratorMainActivity.class);


    /**
     * Testing the back button to confirm it sends the user back to the Admin Main Activity
     */
    @Test
    public void testBackButton() {
        onView(withId(R.id.browseFacilitiesBtn)).perform(click());
        // verify we've entered the appropriate view before going back
        onView(withText("FACILITIES")).check(matches(isDisplayed()));
        onView(withId(R.id.back_button)).perform(click());
        // confirms we have left the events view
        onView(withText("FACILITIES")).check(doesNotExist());
        onView(withId(R.id.browseHeader)).check(doesNotExist());
        //confirms we have arrived back at the administrator main activity
        onView(withId(R.id.welcomeTextView)).check(matches(isDisplayed()));
    }

    /**
     * Testing the selection of a facility with the intent to delete
     */
    @Test
    public void testSelectFacility() {
        onView(withId(R.id.browseFacilitiesBtn)).perform(click());
        onData(anything()).inAdapterView(withId(R.id.contentListView)).atPosition(0).perform(longClick());
        onView(withText("Cancel")).check(matches(isDisplayed()));
        onView(withText("Delete")).check(matches(isDisplayed()));
        onView(withText("Delete Facility?")).check(matches(isDisplayed()));
    }

    /**
     * Testing the cancel button when deleting a facility
     */
    @Test
    public void testDeleteFacilityCancel() {
        onView(withId(R.id.browseFacilitiesBtn)).perform(click());
        onData(anything()).inAdapterView(withId(R.id.contentListView)).atPosition(0).perform(longClick());
        onView(withText("Cancel")).perform(click());
        onView(withText("Cancel")).check(doesNotExist());
        onView(withText("Delete")).check(doesNotExist());
        onView(withText("Delete Facility?")).check(doesNotExist());
    }

    /**
     * Testing the delete facility button functionality
     */
    @Test
    public void testDeleteFacilityDelete() throws InterruptedException {
        setTestItems();
        latchOne.await();
        latchTwo.await();
        onView(withId(R.id.browseFacilitiesBtn)).perform(click());
        onView(withText("invalidName")).check(matches(isDisplayed()));
        onData(anything()).inAdapterView(withId(R.id.contentListView)).atPosition(0).perform(longClick());
        onView(withText("Delete")).perform(click());
        onView(withText("Cancel")).check(doesNotExist());
        onView(withText("Delete")).check(doesNotExist());
        onView(withText("Delete Facility?")).check(doesNotExist());
        onView(withText("invalidName")).check(doesNotExist());
        deleteData();
    }

    /**
     * Setting test item for deletion
     */
    private void setTestItems() {
        db = FirebaseFirestore.getInstance();
        HashMap<String, Object> data = new HashMap<>();
        data.put("address", "1");
        data.put("city", "2");
        data.put("description", "thing");
        data.put("id", "00000");
        data.put("name", "invalidName");
        data.put("province", "4");
        data.put("street", "5");
        data.put("validFacility", true);
        db.collection("Facilities").document("00000").set(data)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "Facility data added successfully!");
                    latchOne.countDown();
                })
                .addOnFailureListener(e -> Log.w("Firestore", "Error adding event data", e));

        HashMap<String, Object> data2 = new HashMap<>();
        data2.put("Firstname", "Test");
        data2.put("Lastname", "Person");
        db.collection("users").document("00000").set(data2)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "User data added successfully!");
                    latchTwo.countDown();
                })
                .addOnFailureListener(e -> Log.w("Firestore", "Error adding user data", e));
    }

    /**
     * deletes test data added in upload event
     */
    private void deleteData() {
        db.collection("users").document("00000").delete();
    }
}

