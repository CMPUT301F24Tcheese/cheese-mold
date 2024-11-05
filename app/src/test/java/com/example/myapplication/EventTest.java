package com.example.myapplication;

import com.example.myapplication.objects.Event;

import org.junit.Test;

import static org.junit.Assert.*;

import java.util.ArrayList;

public class EventTest {

    private Event mockEvent() {
        return new Event("12345","Event","Description1","test.url", 100L);
    }


    // Test for Event WaitingList
    @Test
    /**
     * Test to join event
     */
    public void testJoinWaitingList(){
        Event event = mockEvent();
        assertEquals(0, event.getWaitingList().size());
        event.addWaitingList( "1");
        assertTrue(event.getWaitingList().contains("1"));
        assertEquals(1, event.getWaitingList().size());
    }

    @Test
    /**
     * Test for unjoin event
     */
    public void testUnjoinWaitingList(){
        Event event = mockEvent();
        event.addWaitingList( "2");
        assertTrue(event.getWaitingList().contains("2"));
        event.removeWaitingList("2");
        assertFalse(event.getWaitingList().contains("2"));
        assertEquals(0, event.getWaitingList().size());

    }

    /**
     * Tests setting and retrieving the waiting list.
     */
    @Test
    public void testSetWaitingList() {
        Event event = mockEvent();
        ArrayList<String> testList = new ArrayList<>();
        testList.add("User3");
        testList.add("User4");
        event.setWaitingList(testList);
        assertEquals(2, event.getWaitingList().size());
        assertTrue(event.getWaitingList().contains("User3"));
        assertTrue(event.getWaitingList().contains("User4"));
    }

    /**
     * Tests adding a user to the cancelled list and checks the list size and content.
     */
    @Test
    public void testAddToCancelledList() {
        Event event = mockEvent();
        event.addToCancelledList("User5");
        assertTrue(event.getCancelledList().contains("User5"));
        assertEquals(1, event.getCancelledList().size());
    }

    /**
     * Tests removing a user from the cancelled list and checks the list size and content.
     */
    @Test
    public void testRemoveFromCancelledList() {
        Event event = mockEvent();
        event.addToCancelledList("User6");
        assertTrue(event.getCancelledList().contains("User6"));
        event.removeFromCancelledList("User6");
        assertFalse(event.getCancelledList().contains("User6"));
        assertEquals(0, event.getCancelledList().size());
    }

    /**
     * Tests setting and retrieving the cancelled list.
     */
    @Test
    public void testSetCancelledList() {
        Event event = mockEvent();
        ArrayList<String> cancelledUsers = new ArrayList<>();
        cancelledUsers.add("User7");
        cancelledUsers.add("User8");
        event.setCancelledList(cancelledUsers);
        assertEquals(2, event.getCancelledList().size());
        assertTrue(event.getCancelledList().contains("User7"));
        assertTrue(event.getCancelledList().contains("User8"));
    }

    /**
     * Tests adding a user to the confirmed list and checks the list size and content.
     */
    @Test
    public void testAddToConfirmedList() {
        Event event = mockEvent();
        event.addToConfirmedList("User9");
        assertTrue(event.getConfirmedList().contains("User9"));
        assertEquals(1, event.getConfirmedList().size());
    }

    /**
     * Tests removing a user from the confirmed list and checks the list size and content.
     */
    @Test
    public void testRemoveFromConfirmedList() {
        Event event = mockEvent();
        event.addToConfirmedList("User10");
        assertTrue(event.getConfirmedList().contains("User10"));
        event.removeFromConfirmedList("User10");
        assertFalse(event.getConfirmedList().contains("User10"));
        assertEquals(0, event.getConfirmedList().size());
    }

    /**
     * Tests setting and retrieving the confirmed list.
     */
    @Test
    public void testSetConfirmedList() {
        Event event = mockEvent();
        ArrayList<String> confirmedUsers = new ArrayList<>();
        confirmedUsers.add("User11");
        confirmedUsers.add("User12");
        event.setConfirmedList(confirmedUsers);
        assertEquals(2, event.getConfirmedList().size());
        assertTrue(event.getConfirmedList().contains("User11"));
        assertTrue(event.getConfirmedList().contains("User12"));
    }

    /**
     * Tests retrieving the event ID.
     */
    @Test
    public void testGetId() {
        Event event = mockEvent();
        assertEquals("12345", event.getId());
    }

    /**
     * Tests setting and getting the event ID.
     */
    @Test
    public void testSetId() {
        Event event = mockEvent();
        event.setId("54321");
        assertEquals("54321", event.getId());
    }

    /**
     * Tests setting and getting the event title.
     */
    @Test
    public void testSetTitle() {
        Event event = mockEvent();
        event.setTitle("New Event Title");
        assertEquals("New Event Title", event.getTitle());
    }

    /**
     * Tests setting and getting the event description.
     */
    @Test
    public void testSetDescription() {
        Event event = mockEvent();
        event.setDescription("Updated Description");
        assertEquals("Updated Description", event.getDescription());
    }

    /**
     * Tests setting and getting the event poster URL.
     */
    @Test
    public void testSetPosterUrl() {
        Event event = mockEvent();
        event.setPosterUrl("new.url");
        assertEquals("new.url", event.getPosterUrl());
    }

    /**
     * Tests setting and getting the limit on the number of entrants.
     */
    @Test
    public void testSetLimitEntrants() {
        Event event = mockEvent();
        event.setLimitEntrants(150L);
        assertEquals(Long.valueOf(150), event.getLimitEntrants());
    }

    /**
     * Tests setting and getting the creator ID.
     */
    @Test
    public void testSetCreatorID() {
        Event event = mockEvent();
        event.setCreatorID("Creator123");
        assertEquals("Creator123", event.getCreatorID());
    }
}
