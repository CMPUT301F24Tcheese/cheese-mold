package com.example.myapplication;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.action.ViewActions.click;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * UI test for EventDetailActivity to verify functionality and UI elements
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class EventDetailActivityTest {

    @Rule
    public ActivityScenarioRule<EventDetailActivity> activityRule =
            new ActivityScenarioRule<>(EventDetailActivity.class);

    @Test
    public void testEventDetailsDisplayed() {
        // Verify that the event name, description, and image are displayed
        onView(withId(R.id.eventDetailName)).check(matches(isDisplayed()));
        onView(withId(R.id.eventDetailDescription)).check(matches(isDisplayed()));
        onView(withId(R.id.imageView)).check(matches(isDisplayed()));
    }


    @Test
    public void testCancelButtonFunctionality() {
        // Check if the cancel button is displayed and works
        onView(withId(R.id.eventDetailCancel)).check(matches(isDisplayed()));
        onView(withId(R.id.eventDetailCancel)).perform(click());
    }
}
