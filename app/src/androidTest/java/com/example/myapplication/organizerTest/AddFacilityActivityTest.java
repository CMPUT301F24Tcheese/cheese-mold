package com.example.myapplication.organizerTest;

import android.content.Intent;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.core.app.ApplicationProvider;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.example.myapplication.R;
import com.example.myapplication.organizer.AddFacilityActivity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import android.content.Intent;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.core.app.ApplicationProvider;

import static org.junit.Assert.assertTrue;

/**
 * The test class was not working for its origin class was linked to firebase and the test requires
 * input to database. I tried providing the organizerID required to access the firebase but did not work
 */
@RunWith(AndroidJUnit4.class)
public class AddFacilityActivityTest {

    @Rule
    public ActivityScenarioRule<AddFacilityActivity> activityRule =
            new ActivityScenarioRule<>(AddFacilityActivity.class);

    @Test
    public void testEnterFacilityDetails() {
        // Create an intent with the required organizerId
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), AddFacilityActivity.class);
        intent.putExtra("organizerID", "0a5ee45fbb79dc91");

        // Enter facility name
        onView(withId(R.id.editTextFacilityName)).perform(typeText("Sample Facility"));
        onView(withId(R.id.editTextFacilityName)).check(matches(withText("Sample Facility")));

        // Enter facility description
        onView(withId(R.id.editTextFacilityDescription)).perform(typeText("This is a test description for the facility."));
        onView(withId(R.id.editTextFacilityDescription)).check(matches(withText("This is a test description for the facility.")));

        // Enter street
        onView(withId(R.id.editTextStreet)).perform(typeText("123 Test St"));
        onView(withId(R.id.editTextStreet)).check(matches(withText("123 Test St")));

        // Enter city (only letters)
        onView(withId(R.id.editTextCity)).perform(typeText("TestCity"));
        onView(withId(R.id.editTextCity)).check(matches(withText("TestCity")));

        // Enter province (only letters)
        onView(withId(R.id.editTextProvince)).perform(typeText("TestProvince"));
        onView(withId(R.id.editTextProvince)).check(matches(withText("TestProvince")));
    }

    @Test
    public void testNumericInputInCityAndProvince() {
        // Create an intent with the required organizerId
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), AddFacilityActivity.class);
        intent.putExtra("organizerID", "0a5ee45fbb79dc91");

        // Try entering numeric values in city
        onView(withId(R.id.editTextCity)).perform(typeText("TestCity123"));
        onView(withId(R.id.editTextCity)).check(matches(withText("TestCity")));

        // Try entering numeric values in province
        onView(withId(R.id.editTextProvince)).perform(typeText("TestProvince456"));
        onView(withId(R.id.editTextProvince)).check(matches(withText("TestProvince")));
    }

    @Test
    public void testCreateFacility_SuccessfulSave() {
        // Create an intent with the required organizerId
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), AddFacilityActivity.class);
        intent.putExtra("organizerID", "0a5ee45fbb79dc91");

        // Fill in valid facility details
        onView(withId(R.id.editTextFacilityName)).perform(typeText("Sample Facility"));
        onView(withId(R.id.editTextFacilityDescription)).perform(typeText("Facility Description"));
        onView(withId(R.id.editTextStreet)).perform(typeText("123 Test St"));
        onView(withId(R.id.editTextCity)).perform(typeText("TestCity"));
        onView(withId(R.id.editTextProvince)).perform(typeText("TestProvince"));

        // Click the create facility button
        onView(withId(R.id.buttonCreateFacility)).perform(click());

        // Assuming successful navigation after creation, you could check if the activity is finished
        activityRule.getScenario().onActivity(activity -> assertTrue(activity.isFinishing()));
    }

    @Test
    public void testBackButtonNavigatesToPreviousActivity() {
        // Create an intent with the required organizerId
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), AddFacilityActivity.class);
        intent.putExtra("organizerID", "0a5ee45fbb79dc91");

        // Click the back button
        onView(withId(R.id.buttonBackToFacility)).perform(click());

        // Check if the activity is finishing
        activityRule.getScenario().onActivity(activity -> assertTrue(activity.isFinishing()));
    }
}
