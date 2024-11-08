package com.example.myapplication.entrantTest;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Intent;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.myapplication.EntrantEventDetailActivity;
import com.example.myapplication.R;
import com.example.myapplication.entrant.EntrantMainActivity;
import com.example.myapplication.objects.Event;
import com.example.myapplication.objects.Users;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;

@RunWith(AndroidJUnit4.class)
@LargeTest

public class EntrantEventDetailActivityTest {
    private FirebaseFirestore db;


    @Rule
    public ActivityScenarioRule<EntrantEventDetailActivity> scenario =
            new ActivityScenarioRule<>(new Intent(ApplicationProvider.getApplicationContext(), EntrantEventDetailActivity.class)
                    .putExtra("event", (Parcelable) new Event("000000001", "abcd","testing","something",20L)));


    @Test
    public void TestActivityLaunch() {
        onView(withId(R.id.entrantDetailLayout)).check(matches(isDisplayed()));
        onView(withId(R.id.entrantEventDetailCancel)).check(matches(isDisplayed()));
        onView(withId(R.id.entrantEventDetailUnjoin)).check(matches(isDisplayed()));
        onView(withId(R.id.entrantEventPoster)).check(matches(isDisplayed()));
        onView(withId(R.id.entrantEventDetailName)).check(matches(isDisplayed()));
        onView(withId(R.id.entrantEventDetailDescription)).check(matches(isDisplayed()));
    }

    @Test
    /**
     * Test the cancel button , check if it direct back to entrantMain
     */
    public void TestCancel(){
        onView(withId(R.id.entrantEventDetailCancel)).check(matches(isDisplayed()));
        onView(withId(R.id.entrantEventDetailCancel)).perform(click());
        onView(withId(R.id.entrantMainLayout)).check(matches(isDisplayed()));
    }

    @Test
    /**
     * Test the uinjoin button , check if it direct back to entrantMain
     */
    public void TestUnjoin(){
        onView(withId(R.id.entrantEventDetailUnjoin)).check(matches(isDisplayed()));
        onView(withId(R.id.entrantEventDetailUnjoin)).perform(click());
        onView(withId(R.id.entrantMainLayout)).check(matches(isDisplayed()));
    }




}
