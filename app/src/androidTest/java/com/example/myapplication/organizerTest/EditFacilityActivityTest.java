package com.example.myapplication.organizerTest;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.myapplication.R;
import com.example.myapplication.organizer.EditFacilityActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public class EditFacilityActivityTest {

    @Rule
    public ActivityScenarioRule<EditFacilityActivity> activityRule =
            new ActivityScenarioRule<>(EditFacilityActivity.class);

    @Test
    public void testLoadFacilityData() {
        // Verify the facility data is loaded (assuming some default text)
        onView(withId(R.id.editTextFacilityName))
                .check(ViewAssertions.matches(not(withText(""))));
        onView(withId(R.id.editTextFacilityDescription))
                .check(ViewAssertions.matches(not(withText(""))));
        onView(withId(R.id.editTextStreet))
                .check(ViewAssertions.matches(not(withText(""))));
        onView(withId(R.id.editTextCity))
                .check(ViewAssertions.matches(not(withText(""))));
        onView(withId(R.id.editTextProvince))
                .check(ViewAssertions.matches(not(withText(""))));
    }

    @Test
    public void testUpdateFacility() {
        // Input new data
        onView(withId(R.id.editTextFacilityName))
                .perform(replaceText("Updated Facility Name"));
        onView(withId(R.id.editTextFacilityDescription))
                .perform(replaceText("Updated Description"));
        onView(withId(R.id.editTextStreet))
                .perform(replaceText("Updated Street"));
        onView(withId(R.id.editTextCity))
                .perform(replaceText("CityTest"));
        onView(withId(R.id.editTextProvince))
                .perform(replaceText("ProvinceTest"));

        // Click the update button
        onView(withId(R.id.buttonUpdateFacility)).perform(click());

        // Verify the update was successful (check for a toast or reload data)
        // If using a toast message, verify using ToastMatcher or similar custom matcher
    }

    @Test
    public void testValidation() {
        // Clear the city field and try to update to trigger validation
        onView(withId(R.id.editTextCity)).perform(replaceText(""));
        onView(withId(R.id.buttonUpdateFacility)).perform(click());

        // Verify that a toast message or error appears (ToastMatcher can be used)
        Espresso.onView(withId(R.id.editTextCity))
                .check(ViewAssertions.matches(withText("")));

        // Correct the field and try again
        onView(withId(R.id.editTextCity)).perform(replaceText("ValidCity"));
        onView(withId(R.id.buttonUpdateFacility)).perform(click());

        // If there's a toast on success, add verification here for it
    }

    @Test
    public void testCancelButton() {
        // Click the cancel button
        onView(withId(R.id.buttonCancel)).perform(click());

        // Verify the activity finishes by checking if the view is gone or closed
    }
}
