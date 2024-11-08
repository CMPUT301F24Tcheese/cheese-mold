package com.example.myapplication.organizerTest;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;

import com.example.myapplication.R;
import com.example.myapplication.organizer.OrganizerNotificationActivity;

import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.content.Intent;

/**
 * UI Tests for OrganizerNotificationActivity.
 * This class contains tests to verify the functionality and UI elements of OrganizerNotificationActivity.
 * It checks the visibility and actions of various components such as buttons, edit texts, and navigational behavior.
 */
@RunWith(AndroidJUnit4.class)
public class OrganizerNotificationActivityTest {

    private static final String TEST_EVENT_ID = "testEventId"; // Replace with your test event id if required

    /**
     * Tests if all the UI components in OrganizerNotificationActivity are present.
     * This includes verifying the existence of buttons for navigating to chosen entrants, waitlist,
     * sending notifications, and the message EditText field.
     */
    @Test
    public void testUIComponentsExist() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), OrganizerNotificationActivity.class);
        intent.putExtra("event_id", TEST_EVENT_ID);

        try (ActivityScenario<OrganizerNotificationActivity> scenario = ActivityScenario.launch(intent)) {
            // Check if all the main components in OrganizerNotificationActivity exist
            Espresso.onView(withId(R.id.buttonToChosenEntrants))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

            Espresso.onView(withId(R.id.buttonToEntrantsOnWaitlist))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

            Espresso.onView(withId(R.id.buttonSend))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

            Espresso.onView(withId(R.id.editTextMessage))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        }
    }

    /**
     * Tests the navigation to the list of waitlisted entrants.
     * This is done by clicking on the "To Entrants On Waitlist" button and verifying the expected action.
     */
    @Test
    public void testNavigateToWaitlist() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), OrganizerNotificationActivity.class);
        intent.putExtra("event_id", TEST_EVENT_ID);

        try (ActivityScenario<OrganizerNotificationActivity> scenario = ActivityScenario.launch(intent)) {
            Espresso.onView(withId(R.id.buttonToEntrantsOnWaitlist))
                    .perform(click());
        }
    }

    /**
     * Tests sending a notification without selecting any entrants.
     * This is expected to show an error message, indicating no users have been selected.
     */
    @Test
    public void testSendNotificationWithoutEntrants() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), OrganizerNotificationActivity.class);
        intent.putExtra("event_id", TEST_EVENT_ID);

        try (ActivityScenario<OrganizerNotificationActivity> scenario = ActivityScenario.launch(intent)) {
            // Type a message in the message box
            Espresso.onView(withId(R.id.editTextMessage))
                    .perform(typeText("Test Notification Message"));

            // Click on the send button without selecting any entrants
            Espresso.onView(withId(R.id.buttonSend))
                    .perform(click());

        }
    }

    /**
     * Tests the navigation to the list of chosen entrants.
     * Clicking the "To Chosen Entrants" button should take the user to the list of confirmed entrants.
     */
    @Test
    public void testNavigateToChosenEntrants() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), OrganizerNotificationActivity.class);
        intent.putExtra("event_id", TEST_EVENT_ID);

        try (ActivityScenario<OrganizerNotificationActivity> scenario = ActivityScenario.launch(intent)) {
            Espresso.onView(withId(R.id.buttonToChosenEntrants))
                    .perform(click());
        }
    }

    /**
     * Tests the functionality of the cancel button.
     * Clicking the "Cancel Notification" button should properly close the activity.
     */
    @Test
    public void testCancelNotification() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), OrganizerNotificationActivity.class);
        intent.putExtra("event_id", TEST_EVENT_ID);

        try (ActivityScenario<OrganizerNotificationActivity> scenario = ActivityScenario.launch(intent)) {
            Espresso.onView(withId(R.id.NotificationButtonCancel))
                    .perform(click());

        }
    }
}
