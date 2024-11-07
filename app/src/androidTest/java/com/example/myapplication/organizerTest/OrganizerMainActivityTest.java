package com.example.myapplication.organizerTest;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.espresso.action.ViewActions;

import com.example.myapplication.R;
import com.example.myapplication.organizer.OrganizerMainActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class OrganizerMainActivityTest {

    @Rule
    public ActivityScenarioRule<OrganizerMainActivity> activityRule =
            new ActivityScenarioRule<>(OrganizerMainActivity.class);

    /**
     * Test to verify if the facility RecyclerView is displayed.
     */
    @Test
    public void testFacilityRecyclerViewIsDisplayed() {
        Espresso.onView(withId(R.id.organizerMainFacilityEventView))
                .check(ViewAssertions.matches(isDisplayed()));
    }

    @Test
    public void testJoinedEventsRecyclerViewIsDisplayed() throws InterruptedException {
        // Ensure the event view is displayed by clicking the eventsTextView
        Espresso.onView(withId(R.id.eventsTextView)).perform(ViewActions.click());

        // Add a short delay to allow view to load (not recommended for production tests)
        Thread.sleep(1000);

        // Check if the joined events RecyclerView is displayed
        Espresso.onView(withId(R.id.organizerMainEventView))
                .check(ViewAssertions.matches(isDisplayed()));
    }


    /**
     * Test to verify that the profile image is displayed and can be clicked to open the profile update screen.
     */
    @Test
    public void testProfileImageClick() {
        Espresso.onView(withId(R.id.updateProfileImg))
                .check(ViewAssertions.matches(isDisplayed()))
                .perform(ViewActions.click());

        // You may add an intent check here if using ActivityScenario or Intents library to verify if UpdateProfileActivity opens
    }

    /**
     * Test to verify that the notification button is displayed and can be clicked to open the notification screen.
     */
    @Test
    public void testNotificationButtonClick() {
        Espresso.onView(withId(R.id.notificationBtn))
                .check(ViewAssertions.matches(isDisplayed()))
                .perform(ViewActions.click());

        // Add intent verification if you want to check if NotificationActivity opens
    }




    @Test
    public void testEventViewToggleAndRecyclerViewDisplay() {
        // Check facility view is shown by default
        Espresso.onView(withId(R.id.viewFacilityLayout))
                .check(ViewAssertions.matches(isDisplayed()));

        // Switch to event view
        Espresso.onView(withId(R.id.eventsTextView)).perform(ViewActions.click());

        // Check if the joined events RecyclerView is displayed in the event view
        Espresso.onView(withId(R.id.organizerMainEventView))
                .check(ViewAssertions.matches(isDisplayed()));
    }
}

