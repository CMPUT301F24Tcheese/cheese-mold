package com.example.myapplication.organizerTest;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.pressBack;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.junit.Assert.assertTrue;
import static java.util.function.Predicate.not;
import static java.util.regex.Pattern.matches;

import android.content.Intent;
import android.view.View;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.myapplication.R;
import com.example.myapplication.organizer.EditEventDetailActivity;
import com.example.myapplication.organizer.EventEditActivity;
import com.example.myapplication.organizer.ListOptionsActivity;
import com.example.myapplication.organizer.MapActivity;
import com.example.myapplication.organizer.OrganizerNotificationActivity;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class EventEditActivityTest {
    @Rule
    public ActivityScenarioRule<EventEditActivity> activityRule =
            new ActivityScenarioRule<>(EventEditActivity.class);

    @Test
    public void testBackButtonFunctionality() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), EventEditActivity.class);
        intent.putExtra("event_id", "Zmb5fqyvE7MQK5cyOCr9");
        ActivityScenario<EventEditActivity> scenario = ActivityScenario.launch(intent);

        // Perform back button click
        onView(withId(R.id.buttonBack)).perform(click());

        // Verify the activity finishes
        scenario.close(); // Ensures the activity is properly closed
    }

}
