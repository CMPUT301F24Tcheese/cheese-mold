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

import android.util.Log;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.myapplication.R;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

/**
 * Large test class to test the buttons in the AdminBrowseImages activity
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class AdminBrowseImagesTest {
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
        onView(withId(R.id.browseImagesBtn)).perform(click());
        // verify we've entered the appropriate view before going back
        onView(withId(R.id.posterListView)).check(matches(isDisplayed()));
        onView(withId(R.id.back_button)).perform(click());
        // confirms we have left the events view
        onView(withId(R.id.posterListView)).check(doesNotExist());
        onView(withId(R.id.profileListView)).check(doesNotExist());
        onView(withId(R.id.browseHeader)).check(doesNotExist());
        //confirms we have arrived back at the administrator main activity
        onView(withId(R.id.welcomeTextView)).check(matches(isDisplayed()));
    }

    /**
     * Testing if the tab switching buttons successfully switch the tabs back and forth
     * between posters and profile pictures
     */
    @Test
    public void testProfileAndPosterTabs() {
        onView(withId(R.id.browseImagesBtn)).perform(click());
        // verify we've entered the appropriate view before going back
        onView(withId(R.id.posterListView)).check(matches(isDisplayed()));

        // click on the profiles tab
        onView(withId(R.id.profilesTextView)).perform(click());

        // verify tab has switched correctly
        onView(withId(R.id.profileListView)).check(matches(isDisplayed()));

        // switch back to the poster tab
        onView(withId(R.id.postersTextView)).perform(click());

        // verify tab has switched back
        onView(withId(R.id.posterListView)).check(matches(isDisplayed()));
    }

    /**
     * testing if selecting long click on a profile picture brings up the delete fragment
     */
    @Test
    public void testSelectProfilePicture() throws InterruptedException { // failed
        setTestItems();
        latchOne.await();
        latchTwo.await();
        onView(withId(R.id.browseImagesBtn)).perform(click());
        // click on the profiles tab
        onView(withId(R.id.profilesTextView)).perform(click());

        // long click for delete
        onData(anything()).inAdapterView(withId(R.id.profileListView)).atPosition(0).perform(longClick());

        // check if view is correct
        onView(withText("Cancel")).check(matches(isDisplayed()));
        onView(withText("Delete")).check(matches(isDisplayed()));
        onView(withText("Delete Profile Picture?")).check(matches(isDisplayed()));

        // delete test items
        deleteTestItems();
    }

    /**
     * testing if selecting long click on a profile picture brings up the delete fragment
     */
    @Test
    public void testSelectPoster() throws InterruptedException {
        setTestItems();
        latchOne.await();
        latchTwo.await();
        onView(withId(R.id.browseImagesBtn)).perform(click());

        // long click for delete
        onData(anything()).inAdapterView(withId(R.id.posterListView)).atPosition(0).perform(longClick());

        // check if view is correct
        onView(withText("Cancel")).check(matches(isDisplayed()));
        onView(withText("Delete")).check(matches(isDisplayed()));
        onView(withText("Delete Poster?")).check(matches(isDisplayed()));

        // delete test items
        deleteTestItems();
    }

    /**
     * Testing the cancel button when deleting a profile picture
     */
    @Test
    public void testSelectProfileCancel() throws InterruptedException {
        setTestItems();
        latchOne.await();
        latchTwo.await();
        onView(withId(R.id.browseImagesBtn)).perform(click());
        // click on the profiles tab
        onView(withId(R.id.profilesTextView)).perform(click());

        // long click for delete
        onData(anything()).inAdapterView(withId(R.id.profileListView)).atPosition(0).perform(longClick());

        // click the cancel button
        onView(withText("Cancel")).perform(click());

        // verify view hasn't changed
        onView(withId(R.id.profileListView)).check(matches(isDisplayed()));

        // delete test items
        deleteTestItems();
    }

    /**
     * Testing the cancel button when deleting a profile picture
     */
    @Test
    public void testSelectPosterCancel() throws InterruptedException {
        setTestItems();
        latchOne.await();
        latchTwo.await();
        onView(withId(R.id.browseImagesBtn)).perform(click());

        // long click for delete
        onData(anything()).inAdapterView(withId(R.id.posterListView)).atPosition(0).perform(longClick());

        // click the cancel button
        onView(withText("Cancel")).perform(click());

        // verify view hasn't changed
        onView(withId(R.id.posterListView)).check(matches(isDisplayed()));

        // delete test items
        deleteTestItems();
    }

    /**
     * Testing the delete profile picture button functionality
     */
    @Test
    public void testSelectProfileDelete() throws InterruptedException {
        setTestItems();
        latchOne.await();
        latchTwo.await();
        onView(withId(R.id.browseImagesBtn)).perform(click());
        // click on the profiles tab
        onView(withId(R.id.profilesTextView)).perform(click());

        // long click for delete
        onData(anything()).inAdapterView(withId(R.id.profileListView)).atPosition(0).perform(longClick());

        // click the delete button
        onView(withText("Delete")).perform(click());

        // checking if image has been removed
        onView(withId(R.id.profileListView)).check(matches(isDisplayed()));
        onView(withText("Test Person")).check(matches(isDisplayed()));

        // delete test items
        deleteTestItems();
    }

    /**
     * testing the delete poster button functionality
     */
    @Test
    public void testSelectPosterDelete() throws InterruptedException {
        setTestItems();
        latchOne.await();
        latchTwo.await();
        onView(withId(R.id.browseImagesBtn)).perform(click());

        // long click for delete
        onData(anything()).inAdapterView(withId(R.id.posterListView)).atPosition(0).perform(longClick());

        // click the delete button
        onView(withText("Delete")).perform(click());

        // checking if image has been removed
        onView(withId(R.id.posterListView)).check(matches(isDisplayed()));
        onView(withText("Test event")).check(doesNotExist());

        // delete test items
        deleteTestItems();
    }

    /**
     * setting test items for deletion and selection
     */
    private void setTestItems() {
        db = FirebaseFirestore.getInstance();

        //String defaultProfilePicUrl = "https://avatar.iran.liara.run/username?username=" + "Test" + "+" + "Person";

        HashMap<String, Object> data = new HashMap<>();
        data.put("Profile Picture", "https://firebasestorage.googleapis.com/v0/b/eventmate-v2-2a71b.appspot.com/o/profile_images%2F00000_qr.jpg?alt=media&token=383594d9-1f3b-4f0b-8ae2-faefc289ffd4");
        data.put("Firstname", "Test");
        data.put("Lastname", "Person");

        db.collection("users").document("0000").set(data)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "user data added successfully!");
                    latchOne.countDown();
                })
                .addOnFailureListener(e -> Log.w("Firestore", "Error adding user data", e));

        HashMap<String, Object> data2 = new HashMap<>();
        data2.put("posterUrl", "https://firebasestorage.googleapis.com/v0/b/eventmate-v2-2a71b.appspot.com/o/event_posters%2Fposter_images%2F00000_qr.jpg?alt=media&token=5476d89f-5d35-4a63-a1cd-a1bf558502bc");
        data2.put("name", "Test event");

        db.collection("events").document("0000").set(data2)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "event data added successfully!");
                    latchTwo.countDown();
                })
                .addOnFailureListener(e -> Log.w("Firestore", "Error adding event data", e));
    }

    /**
     * deleting test items
     */
    private void deleteTestItems() {
        db.collection("users").document("0000").delete();
        db.collection("events").document("0000").delete();
    }

}
