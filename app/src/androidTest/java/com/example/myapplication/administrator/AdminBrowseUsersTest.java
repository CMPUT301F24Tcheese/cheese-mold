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
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.myapplication.R;
import com.example.myapplication.objects.Users;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

/**
 * Large test class to test the buttons in the AdminBrowseUsers activity
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class AdminBrowseUsersTest {
    private FirebaseFirestore db;
    final CountDownLatch latchOne = new CountDownLatch(1);

    @Rule
    public ActivityScenarioRule<AdministratorMainActivity> scenario = new
            ActivityScenarioRule<AdministratorMainActivity>(AdministratorMainActivity.class);

    /**
     * defines user object for display
     */
    private void setTestItems() {
        db = FirebaseFirestore.getInstance(); // Initialize Firestore database
        String defaultProfilePicUrl = "https://avatar.iran.liara.run/username?username=" + "Test" + "+" + "Person"; // URL for default profile picture
        Users user = new Users("000000000", "Test", "Person", "0@0.com", defaultProfilePicUrl, "Entrant");
        uploadUserData(user);
    }

    /**
     * uploads test user data to firebase
     * @param user user object for display
     */
    private void uploadUserData(@NonNull Users user) {
        HashMap<String, Object> data = new HashMap<>(); // Create a HashMap to store user data
        data.put("Email", user.getEmail()); // Add user's email to the map
        data.put("role", user.getRole());
        data.put("Firstname", user.getFirstName()); // Add user's first name to the map
        data.put("Lastname", user.getLastName()); // Add user's last name to the map
        data.put("Profile Picture", user.getProfilePicture()); // Add user's profile picture URL to the map
        data.put("Event List", user.getEventList()); // **(2) Add waitlist to the event map**

        db.collection("users").document("000000000").set(data) // Store the user data in Firestore under the user's unique ID
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "User data added successfully!"); // Log success message
                    latchOne.countDown();
                })
                .addOnFailureListener(e -> Log.w("Firestore", "Error adding user data", e)); // Log error if data upload fails
    }

    /**
     * deletes the test user from firebase
     * @param id id for test user
     */
    private void deleteUser(String id) {
        db.collection("users").document(id).delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "User data removed successfully!");
                })
                .addOnFailureListener(e -> Log.w("Firestore", "Error removing user data", e));
    }

    /**
     * Testing the back button to confirm it sends the user back to the Admin Main Activity
     */
    @Test
    public void testBackButton() {
        onView(withId(R.id.browseProfilesBtn)).perform(click());
        onView(withId(R.id.back_button)).perform(click());
        //confirms we have arrived back at the administrator main activity
        onView(withId(R.id.browseProfilesBtn)).check(matches(isDisplayed()));
    }

    /**
     * Testing that selecting a user will open a new page that displays the user information
     */
    @Test
    public void testSelectUser() throws InterruptedException {
        // setting test user
        setTestItems();
        latchOne.await();

        onView(withId(R.id.browseProfilesBtn)).perform(click());

        // click on newly created test profile
        onData(anything()).inAdapterView(withId(R.id.contentListView)).atPosition(0).perform(click());

        // test to make sure individual user view is displayed
        onView(withId(R.id.ProfilePicture)).check(matches(isDisplayed()));
        onView(withId(R.id.UserName)).check(matches(isDisplayed()));
        onView(withId(R.id.UserRole)).check(matches(isDisplayed()));
        onView(withId(R.id.UserFacility)).check(matches(isDisplayed()));
        onView(withId(R.id.adminUserBack)).check(matches(isDisplayed()));

        // removing test user
        deleteUser("000000000");
    }

}
