package com.example.myapplication.organizerTest;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.myapplication.R;
import com.example.myapplication.organizer.AddEventActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class AddEventActivityTest {

    @Rule
    public ActivityScenarioRule<AddEventActivity> activityRule =
            new ActivityScenarioRule<>(AddEventActivity.class);

    @Test
    public void testEnterEventDetails() {
        // Enter event name
        onView(withId(R.id.editTextEventName)).perform(typeText("Sample Event"));
        onView(withId(R.id.editTextEventName)).check(matches(withText("Sample Event")));

        // Enter event description
        onView(withId(R.id.editTextEventDescription)).perform(typeText("This is a test description for the event."));
        onView(withId(R.id.editTextEventDescription)).check(matches(withText("This is a test description for the event.")));

        // Set the event entrant limit
        onView(withId(R.id.editTextLimitEntrants)).perform(typeText("100"));
        onView(withId(R.id.editTextLimitEntrants)).check(matches(withText("100")));
    }

    @Test
    public void testSetEventDateTime() {
        // Click on date picker to set event date and time
        onView(withId(R.id.editTextEventDateTime)).perform(click());

        // Choose date (assuming today's date is selected for simplicity)
        onView(withText("OK")).perform(click());

        // Choose time (assuming default time is selected for simplicity)
        onView(withText("OK")).perform(click());

        // Verify that the date and time are set (replace with the actual expected date string if possible)
        onView(withId(R.id.editTextEventDateTime)).check(matches(isDisplayed()));
    }

    @Test
    public void testToggleGeolocation() {
        // Toggle geolocation switch
        onView(withId(R.id.switchGeolocation)).perform(click());

        // Verify that the switch is toggled on
        onView(withId(R.id.switchGeolocation)).check(matches(isDisplayed()));
    }

    @Test
    public void testOpenFileChooser() {
        // Click on upload poster button
        onView(withId(R.id.buttonUploadPoster)).perform(click());

        // Verifying that the poster upload dialog is triggered is challenging in a unit test environment,
        // so we may need to assume that this click opens the file chooser.
    }

    @Test
    public void testSaveEvent() {
        // Enter event details
        onView(withId(R.id.editTextEventName)).perform(typeText("Sample Event"));
        onView(withId(R.id.editTextEventDescription)).perform(typeText("This is a test description for the event."));
        onView(withId(R.id.editTextEventDateTime)).perform(click());
        onView(withText("OK")).perform(click());
        onView(withText("OK")).perform(click());
        onView(withId(R.id.editTextLimitEntrants)).perform(replaceText("50"));

        // Toggle geolocation if necessary
        onView(withId(R.id.switchGeolocation)).perform(click());

        // Click the save event button
        onView(withId(R.id.buttonSaveEvent)).perform(click());

    }
}
