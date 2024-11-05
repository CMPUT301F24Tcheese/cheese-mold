package com.example.myapplication;



import static org.junit.Assert.*;


import com.example.myapplication.objects.Users;
import com.example.myapplication.objects.WaitingList;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;


/**
 * Test class for the WaitingList class.
 * It verifies the functionality of adding, removing, and managing users in a waiting list.
 */
public class WaitingListTest {

    /**
     * Creates a mock user with specific details.
     * @return a Users object representing a mock user.
     */
    private Users mockUser1() {
        return new Users("1", "first", "last", "email@test.com", null, null);
    }

    /**
     * Creates a second mock user with specific details.
     * @return a Users object representing a second mock user.
     */
    private Users mockUser2() {
        return new Users("2", "first2", "last2", "email2@test.com", null, null);
    }

    /**
     * Creates a third mock user with specific details.
     * @return a Users object representing a third mock user.
     */
    private Users mockUser3() {
        return new Users("3", "first3", "last3", "email3@test.com", null, null);
    }


    /**
     * Tests the addition of a single user to the waiting list.
     * Verifies that the user count and list contents are updated correctly.
     */
    @Test
    public void testAdd() {
        WaitingList waitingList = new WaitingList();
        Users user = mockUser1();
        assertEquals(0, waitingList.getCount());
        waitingList.addUsers(user.getUserId());
        assertEquals(1, waitingList.getCount());
        assertTrue(waitingList.getList().contains(user.getUserId()));
    }


    /**
     * Tests that adding a duplicate user ID throws an IllegalArgumentException.
     */
    @Test
    public void testAddDuplicateUserInList() {
        WaitingList waitingList = new WaitingList();
        Users user1 = mockUser1();
        Users user2 = mockUser1();
        assertEquals(0, waitingList.getCount());
        waitingList.addUsers(user1.getUserId());
        assertEquals(1, waitingList.getCount());
        assertThrows(IllegalArgumentException.class, () -> {
            waitingList.addUsers(user2.getUserId());
        });
    }


    /**
     * Tests the addition of multiple users to the waiting list.
     * Verifies that both users are added and the count is accurate.
     */
    @Test
    public void testAddMultipleUsers() {
        WaitingList waitingList = new WaitingList();
        Users user1 = mockUser1();
        Users user2 = mockUser2();

        waitingList.addUsers(user1.getUserId());
        waitingList.addUsers(user2.getUserId());

        assertEquals(2, waitingList.getCount());
        assertTrue(waitingList.getList().containsAll(Arrays.asList(user1.getUserId(), user2.getUserId())));
    }


    /**
     * Tests retrieving users by their position in the waiting list.
     * Verifies that the correct user ID is returned for each position.
     */
    @Test
    public void testGetUserByPosition() {
        WaitingList waitingList = new WaitingList();
        Users user1 = mockUser1();
        Users user2 = mockUser2();
        waitingList.addUsers(user1.getUserId());
        waitingList.addUsers(user2.getUserId());

        assertEquals(user1.getUserId(), waitingList.getUser(0));
        assertEquals(user2.getUserId(), waitingList.getUser(1));
    }


    /**
     * Tests removing a user from the waiting list.
     * Verifies that the user is removed and the count is updated correctly.
     */
    @Test
    public void testRemove() {
        WaitingList waitingList = new WaitingList();
        Users user = mockUser1();
        waitingList.addUsers(user.getUserId());
        assertEquals(1, waitingList.getCount());
        waitingList.removeUsers(user.getUserId());
        assertFalse(waitingList.getList().contains(user.getUserId()));
        assertEquals(0, waitingList.getCount());
    }


    /**
     * Tests attempting to remove a user not present in the list.
     * Verifies that an IllegalArgumentException is thrown.
     */
    @Test
    public void deleteUserNotInList () {
        WaitingList waitingList = new WaitingList();
        Users user1 = mockUser1();
        assertEquals(0, waitingList.getCount());
        assertThrows(IllegalArgumentException.class, () -> {
            waitingList.removeUsers(user1.getUserId());
        });
    }


    /**
     * Tests re-adding a user after they have been removed.
     * Verifies that the user can be successfully re-added and the count is updated.
     */
    @Test
    public void testAddUserAfterRemoval() {
        WaitingList waitingList = new WaitingList();
        Users user1 = mockUser1();
        waitingList.addUsers(user1.getUserId());
        waitingList.removeUsers(user1.getUserId());

        assertEquals(0, waitingList.getCount());
        waitingList.addUsers(user1.getUserId());
        assertEquals(1, waitingList.getCount());
    }



    /**
     * Tests setting a new list of users in the waiting list.
     * Verifies that the list is updated and the count matches the new list's size.
     */
    @Test
    public void testSetList() {
        WaitingList waitingList = new WaitingList();
        ArrayList<String> newList = new ArrayList<>(Arrays.asList("1", "2", "3"));
        waitingList.setList(newList);

        assertEquals(3, waitingList.getCount());
        assertTrue(waitingList.getList().containsAll(newList));
    }

}
