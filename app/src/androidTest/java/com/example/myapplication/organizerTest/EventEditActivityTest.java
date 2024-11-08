package com.example.myapplication.organizerTest;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.filters.LargeTest;
import androidx.test.runner.lifecycle.Stage;

import com.example.myapplication.R;
import com.example.myapplication.organizer.EventEditActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.runner.lifecycle.Stage.*;
import static androidx.test.runner.lifecycle.Stage.DESTROYED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * The test class was not working for its origin class was linked to firebase and the test requires
 * input to database. I tried providing the eventID required to access the firebase but did not work
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class EventEditActivityTest {

    @Rule
    public ActivityScenarioRule<EventEditActivity> activityRule =
            new ActivityScenarioRule<>(EventEditActivity.class);

    @Test
    public void testEnterEventDetails() {

        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), EventEditActivity.class);
        intent.putExtra("event_id", "wy0hMRh7jyvqNk5tVJpS");  // Pass a valid event ID

        // Enter event title
        onView(withId(R.id.editTextTitle)).perform(typeText("Edited Event Title"));
        onView(withId(R.id.editTextTitle)).check(matches(withText("Edited Event Title")));

        // Enter event description
        onView(withId(R.id.editTextDescription)).perform(typeText("This is a test description for editing the event."));
        onView(withId(R.id.editTextDescription)).check(matches(withText("This is a test description for editing the event.")));

        // Set the event entrant limit
        onView(withId(R.id.editTextLimitEntrants)).perform(typeText("50"));
        onView(withId(R.id.editTextLimitEntrants)).check(matches(withText("50")));
    }

    @Test
    public void testSetEventDateTime() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), EventEditActivity.class);
        intent.putExtra("event_id", "wy0hMRh7jyvqNk5tVJpS");  // Pass a valid event ID

        // Click on date picker to set event date and time
        onView(withId(R.id.editTextDateTime)).perform(click());

        // Choose date (assuming today's date is selected for simplicity)
        onView(withText("OK")).perform(click());

        // Choose time (assuming default time is selected for simplicity)
        onView(withText("OK")).perform(click());

        // Verify that the date and time are set (assuming the date text is displayed)
        onView(withId(R.id.editTextDateTime)).check(matches(isDisplayed()));
    }

    @Test
    public void testOpenFileChooser() {
        // Click on upload poster button
        onView(withId(R.id.buttonUploadPoster)).perform(click());

    }

    @Test
    public void testSaveEvent() {
        // Enter event details
        onView(withId(R.id.editTextTitle)).perform(typeText("Edited Event Title"));
        onView(withId(R.id.editTextDescription)).perform(typeText("Updated description for the event."));

        // Set the event date and time
        onView(withId(R.id.editTextDateTime)).perform(click());
        onView(withText("OK")).perform(click());
        onView(withText("OK")).perform(click());

        // Set the entrant limit
        onView(withId(R.id.editTextLimitEntrants)).perform(replaceText("100"));

        // Click the save button
        onView(withId(R.id.buttonUpdateEvent)).perform(click());

    }

    @Test
    public void testFieldsResetAfterSave() {

        // Enter details and save event
        onView(withId(R.id.editTextTitle)).perform(typeText("Event After Save"));
        onView(withId(R.id.editTextLimitEntrants)).perform(replaceText("50"));
        onView(withId(R.id.buttonUpdateEvent)).perform(click());

        // Verify that fields reset after save
        onView(withId(R.id.editTextTitle)).check(matches(withText("")));
        onView(withId(R.id.editTextLimitEntrants)).check(matches(withText("")));
    }
}