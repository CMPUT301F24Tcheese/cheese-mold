package com.example.myapplication;



import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;

import com.example.myapplication.objects.Lottery;

public class LotteryTest {

    private Lottery lottery;
    private ArrayList<String> participants;

    @Before
    public void setUp() {
        participants = new ArrayList<>(Arrays.asList("Alice", "Bob", "Charlie", "David"));
        lottery = new Lottery(participants);
    }

    /**
     * Test that the lottery is initialized with the correct list of participants.
     */
    @Test
    public void testInitialization() {
        assertNotNull(lottery);
        assertEquals(4, participants.size());
        assertTrue(participants.contains("Alice"));
        assertTrue(participants.contains("Bob"));
        assertTrue(participants.contains("Charlie"));
        assertTrue(participants.contains("David"));
    }

    /**
     * Test that a participant is selected and removed from the lottery list.
     */
    @Test
    public void testLotterySelect() {
        String selectedParticipant = lottery.lotterySelect();

        // Check that the selected participant is no longer in the list.
        assertFalse(participants.contains(selectedParticipant));
        assertEquals(3, participants.size()); // There should be 3 participants left after selection.
    }

    /**
     * Test lotterySelect method when the lottery list is empty.
     */
    @Test(expected = IllegalStateException.class)
    public void testLotterySelect_EmptyList() {
        Lottery emptyLottery = new Lottery(new ArrayList<>());
        emptyLottery.lotterySelect(); // This should throw an IllegalStateException.
    }

    /**
     * Test lotterySelect method when the lottery list is null.
     */
    @Test(expected = IllegalStateException.class)
    public void testLotterySelect_NullList() {
        Lottery nullLottery = new Lottery(null);
        nullLottery.lotterySelect(); // This should throw an IllegalStateException.
    }

    /**
     * Test removeItem method to ensure it removes the correct participant.
     */
    @Test
    public void testRemoveItem() {
        lottery.removeItem(1); // Remove "Bob"
        assertEquals(3, participants.size()); // List should now have 3 participants
        assertFalse(participants.contains("Bob")); // "Bob" should be removed
    }

    /**
     * Test removeItem method for invalid index.
     */
    @Test(expected = IndexOutOfBoundsException.class)
    public void testRemoveItem_InvalidIndex() {
        lottery.removeItem(5); // This should throw IndexOutOfBoundsException
    }

    /**
     * Test that the lottery is still functional after several selections.
     */
    @Test
    public void testMultipleSelections() {
        ArrayList<String> originalList = new ArrayList<>(participants);
        String firstSelected = lottery.lotterySelect();
        System.out.println(originalList);
        String secondSelected = lottery.lotterySelect();

        // Check that the selected participants are removed from the list.
        assertTrue(originalList.contains(firstSelected));
        assertFalse(participants.contains(firstSelected));
        assertTrue(originalList.contains(secondSelected));
        assertFalse(participants.contains(secondSelected));

        // The remaining list should have 2 participants
        assertEquals(4, originalList.size());
        assertEquals(2, participants.size());
    }
}

