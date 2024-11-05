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

/**
 * Large test class to test the buttons in the AdminBrowseImages activity
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class AdminBrowseImagesTest {

    @Rule
    public ActivityScenarioRule<AdministratorMainActivity> scenario = new
            ActivityScenarioRule<AdministratorMainActivity>(AdministratorMainActivity.class);

    /**
     * Testing the back button to confirm it sends the user back to the Admin Main Activity
     */
    @Test
    public void testBackButton() {
        onView(withId(R.id.browseImagesBtn)).perform(click());
        // verify we've entered the appropriate view before going back
        onView(withId(R.id.posterListView)).check(matches(isDisplayed()));
        onView(withId(R.id.back_button)).perform(click());
        // confirms we have left the events view
        onView(withId(R.id.posterListView)).check(doesNotExist());
        onView(withId(R.id.profileListView)).check(doesNotExist());
        onView(withId(R.id.browseHeader)).check(doesNotExist());
        //confirms we have arrived back at the administrator main activity
        onView(withId(R.id.welcomeTextView)).check(matches(isDisplayed()));
    }

    /**
     * Testing if the tab switching buttons successfully switch the tabs back and forth
     * between posters and profile pictures
     */
    @Test
    public void testProfileAndPosterTabs() {
        onView(withId(R.id.browseImagesBtn)).perform(click());
        // verify we've entered the appropriate view before going back
        onView(withId(R.id.posterListView)).check(matches(isDisplayed()));

        // click on the profiles tab
        onView(withId(R.id.profilesTextView)).perform(click());

        // verify tab has switched correctly
        onView(withId(R.id.profileListView)).check(matches(isDisplayed()));

        // switch back to the poster tab
        onView(withId(R.id.postersTextView)).perform(click());

        // verify tab has switched back
        onView(withId(R.id.posterListView)).check(matches(isDisplayed()));
    }

}
