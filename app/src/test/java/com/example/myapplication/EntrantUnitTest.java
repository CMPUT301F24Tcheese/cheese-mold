package com.example.myapplication;

import com.example.myapplication.objects.Event;
import com.example.myapplication.objects.WaitingList;

import org.junit.Test;

import static org.junit.Assert.*;

import java.util.ArrayList;

public class EntrantUnitTest {
    private Event evnetTest = new Event("12345","Event","Description1","test.url", 100L);

    // Test for Event WaitingList
    @Test
    /**
     * Test to join event
     */
    public void testJoin_1(){

        evnetTest.addWaitingList("1");
        WaitingList testWaiting = new WaitingList();
        testWaiting.addUsers("1");
        assertEquals(testWaiting, evnetTest.getWaitingList());

    }

    @Test
    /**
     * Test for unjoin event
     */
    public void testUnjoin_2(){

        evnetTest.addWaitingList("1");
        WaitingList testWaiting = new WaitingList();

        evnetTest.removeWaitingList("1");
        assertEquals(testWaiting, evnetTest.getWaitingList());

    }
}
