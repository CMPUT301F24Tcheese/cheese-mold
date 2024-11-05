package com.example.myapplication.entrantTest;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.example.myapplication.R;
import com.example.myapplication.entrant.EntrantMainActivity;

import org.junit.Rule;
import org.junit.Test;

public class EntrantMainActivityTest {
    @Rule
    public ActivityScenarioRule<EntrantMainActivity> scenario = new ActivityScenarioRule<EntrantMainActivity>(EntrantMainActivity.class);

    @Test
    public void TestActivityLaunch() {
        onView(withId(R.id.entrantMainLayout)).check(matches(isDisplayed()));
        onView(withText("My Events")).check(matches(isDisplayed()));
        onView(withId(R.id.notificationBtn)).check(matches(isDisplayed()));
        onView(withId(R.id.qrCodeBtn)).check(matches(isDisplayed()));
        onView(withId(R.id.updateProfileImg)).check(matches(isDisplayed()));
    }

    @Test
    public void TestNotification() {
        onView(withId(R.id.entrantMainLayout)).check(matches(isDisplayed()));
        onView(withId(R.id.notificationBtn)).perform(click());
        onView(withId(R.id.notificationMainLayout)).check(matches(isDisplayed()));
        onView(withText("Notifications")).check(matches(isDisplayed()));
        onView(withId(R.id.notificationBackBtn)).perform(click());
        onView(withId(R.id.entrantMainLayout)).check(matches(isDisplayed()));
    }

    @Test
    public void TestUpdateProfile() {
        onView(withId(R.id.entrantMainLayout)).check(matches(isDisplayed()));
        onView(withId(R.id.updateProfileImg)).perform(click());
        onView(withId(R.id.updateProfileLayout)).check(matches(isDisplayed()));
        onView(withText("Update Profile")).check(matches(isDisplayed()));
        onView(withId(R.id.cancelUpdateText)).perform(click());
        onView(withId(R.id.entrantMainLayout)).check(matches(isDisplayed()));
    }

}
