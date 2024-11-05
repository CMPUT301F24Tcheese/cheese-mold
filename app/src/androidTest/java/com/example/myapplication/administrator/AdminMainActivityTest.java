package com.example.myapplication.administrator;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.myapplication.R;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Large test class to test the buttons in the Administrator main activity
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class AdminMainActivityTest {

    @Rule
    public ActivityScenarioRule<AdministratorMainActivity> scenario = new
            ActivityScenarioRule<AdministratorMainActivity>(AdministratorMainActivity.class);

    /**
     * testing the Facilities button functionality
     */
    @Test
    public void testFacilitiesButton() {
        onView(withId(R.id.browseFacilitiesBtn)).perform(click());
        onView(withId(R.id.browseHeader)).check(matches(isDisplayed()));
        onView(withId(R.id.contentListView)).check(matches(isDisplayed()));
    }

    /**
     * testing the Profiles button functionality
     */
    @Test
    public void testProfilesButton() {
        onView(withId(R.id.browseProfilesBtn)).perform(click());
        onView(withId(R.id.browseHeader)).check(matches(isDisplayed()));
        onView(withId(R.id.contentListView)).check(matches(isDisplayed()));
    }

    /**
     * testing the Events button functionality
     */
    @Test
    public void testEventsButton() {
        onView(withId(R.id.browseEventsBtn)).perform(click());
        onView(withId(R.id.browseHeader)).check(matches(isDisplayed()));
        onView(withId(R.id.contentListView)).check(matches(isDisplayed()));
    }

    /**
     * testing the Images button functionality
     */
    @Test
    public void testImagesButton() {
        onView(withId(R.id.browseImagesBtn)).perform(click());
        onView(withId(R.id.posterListView)).check(matches(isDisplayed()));
    }

    /**
     * testing the QR Codes button functionality
     */
    @Test
    public void testQRCodeButton() {
        onView(withId(R.id.browseQRcodesBtn)).perform(click());
        onView(withId(R.id.browseHeader)).check(matches(isDisplayed()));
        onView(withId(R.id.contentListView)).check(matches(isDisplayed()));
    }

}
