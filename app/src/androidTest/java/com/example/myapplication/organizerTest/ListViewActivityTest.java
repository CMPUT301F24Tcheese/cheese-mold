package com.example.myapplication.organizerTest;

import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.myapplication.organizer.ListViewActivity;
import com.example.myapplication.R;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class ListViewActivityTest {

    @Before
    public void setUp() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.putExtra("event_id", "testEvent123");
        intent.putExtra("listType", "confirmed");

        ActivityScenario<ListViewActivity> scenario = ActivityScenario.launch(ListViewActivity.class);
        scenario.onActivity(activity -> {
            activity.setIntent(intent);
            activity.recreate();
        });
    }

    @Test
    public void testTitleIsSetCorrectly() {
        onView(withId(R.id.titleTextView)).check(matches(withText("Confirmed List")));
    }

    @Test
    public void testRecyclerViewIsEmptyInitially() {
        onView(withId(R.id.recyclerViewLists))
                .check((view, noViewFoundException) -> {
                    RecyclerView recyclerView = (RecyclerView) view;
                    if (recyclerView != null) {
                        assert recyclerView.getAdapter() != null;
                        assert recyclerView.getAdapter().getItemCount() == 0;
                    }
                });
    }

    @Test
    public void testFooterIsEmptyInitially() {
        onView(withId(R.id.footerTextView)).check(matches(withText("Total: 0 people")));
    }
}
