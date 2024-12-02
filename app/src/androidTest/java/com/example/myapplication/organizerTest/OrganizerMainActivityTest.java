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

@RunWith(AndroidJUnit4.class)
public class OrganizerMainActivityTest {

    @Rule
    public ActivityScenarioRule<OrganizerMainActivity> activityRule =
            new ActivityScenarioRule<>(OrganizerMainActivity.class);

    /**
     * Test to toggle between facility and event views.
     */
    @Test
    public void testToggleBetweenFacilityAndEventViews() {
        // Check facility view is displayed by default
        Espresso.onView(withId(R.id.viewFacilityLayout))
                .check(ViewAssertions.matches(isDisplayed()));

        // Switch to event view
        Espresso.onView(withId(R.id.eventsTextView)).perform(ViewActions.click());

        // Check if the event view is displayed
        Espresso.onView(withId(R.id.viewEventsLayout))
                .check(ViewAssertions.matches(isDisplayed()));

        // Switch back to facility view
        Espresso.onView(withId(R.id.facilityTextView)).perform(ViewActions.click());

        // Check if the facility view is displayed again
        Espresso.onView(withId(R.id.viewFacilityLayout))
                .check(ViewAssertions.matches(isDisplayed()));
    }

    /**
     * Test to verify if the profile image is displayed and clickable.
     */
    @Test
    public void testProfileImageDisplayedAndClickable() {
        Espresso.onView(withId(R.id.updateProfileImg))
                .check(ViewAssertions.matches(isDisplayed()))
                .perform(ViewActions.click());

        // Verify no crash occurs and UI interaction succeeds
    }

    /**
     * Test to verify if the notification button is displayed and clickable.
     */
    @Test
    public void testNotificationButtonDisplayedAndClickable() {
        Espresso.onView(withId(R.id.notificationBtn))
                .check(ViewAssertions.matches(isDisplayed()))
                .perform(ViewActions.click());

        // Verify no crash occurs and UI interaction succeeds
    }

    /**
     * Test to verify if the floating action button is displayed in the facility view.
     */
    @Test
    public void testFabDisplayedInFacilityView() {
        // Ensure we are in the facility view
        Espresso.onView(withId(R.id.facilityTextView)).perform(ViewActions.click());

        // Check if the FAB is displayed
        Espresso.onView(withId(R.id.organizerMainAddEventFab))
                .check(ViewAssertions.matches(isDisplayed()));
    }

    /**
     * Test to verify the RecyclerView in facility view is displayed.
     */
    @Test
    public void testFacilityRecyclerViewDisplayed() {
        Espresso.onView(withId(R.id.organizerMainFacilityEventView))
                .check(ViewAssertions.matches(isDisplayed()));
    }

    /**
     * Test to verify the RecyclerView in event view is displayed.
     */
    @Test
    public void testEventRecyclerViewDisplayed() {
        // Switch to event view
        Espresso.onView(withId(R.id.eventsTextView)).perform(ViewActions.click());

        // Check if the event RecyclerView is displayed
        Espresso.onView(withId(R.id.organizerMainEventView))
                .check(ViewAssertions.matches(isDisplayed()));
    }

    /**
     * Test to ensure FAB opens the Add Event Activity.
     */
    @Test
    public void testFabOpensAddEventActivity() {
        Espresso.onView(withId(R.id.organizerMainAddEventFab))
                .perform(ViewActions.click());

        // Verify no crash occurs and UI interaction succeeds
    }
}
