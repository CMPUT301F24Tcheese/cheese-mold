package com.example.myapplication.organizerTest;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;

import com.example.myapplication.R;
import com.example.myapplication.organizer.FacilityActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.action.ViewActions.click;

@RunWith(AndroidJUnit4.class)
public class FacilityActivityTest {

    @Rule
    public ActivityScenarioRule<FacilityActivity> activityScenarioRule = new ActivityScenarioRule<>(FacilityActivity.class);

    @Test
    public void testRecyclerViewIsDisplayed() {
        // Check that the RecyclerView is displayed
        Espresso.onView(withId(R.id.recyclerViewFacilities))
                .check(ViewAssertions.matches(isDisplayed()));
    }



    @Test
    public void testFacilitiesLoadedIntoRecyclerView() throws InterruptedException {
        Thread.sleep(2000);

        Espresso.onView(withId(R.id.recyclerViewFacilities))
                .check(ViewAssertions.matches(isDisplayed()));

        Espresso.onView(withId(R.id.recyclerViewFacilities))
                .check(ViewAssertions.matches(ViewMatchers.hasMinimumChildCount(1)));
    }

}
