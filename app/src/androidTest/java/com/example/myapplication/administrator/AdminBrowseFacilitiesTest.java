package com.example.myapplication.administrator;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.myapplication.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
/*

*/
/**
 * Large test class to test the buttons in the AdminBrowseFacilities activity
 *//*

@RunWith(AndroidJUnit4.class)
@LargeTest
public class AdminBrowseFacilitiesTest {

    @Rule
    public ActivityScenarioRule<AdministratorMainActivity> scenario = new
            ActivityScenarioRule<AdministratorMainActivity>(AdministratorMainActivity.class);

    */
/**
     * Testing the back button to confirm it sends the user back to the Admin Main Activity
     * temporarily removed since Facility Intent has been temporarily removed
     * TODO fix test case when facility button is re-added
     *//*

    @Test
    public void testBackButton() {
        onView(withId(R.id.browseFacilitiesBtn)).perform(click());
        // verify we've entered the appropriate view before going back
        onView(withText("FACILITIES")).check(matches(isDisplayed()));
        onView(withId(R.id.back_button)).perform(click());
        // confirms we have left the events view
        onView(withText("FACILITIES")).check(doesNotExist());
        onView(withId(R.id.browseHeader)).check(doesNotExist());
        //confirms we have arrived back at the administrator main activity
        onView(withId(R.id.welcomeTextView)).check(matches(isDisplayed()));
    }

}
*/
