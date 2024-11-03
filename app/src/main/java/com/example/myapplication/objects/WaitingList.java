package com.example.myapplication.objects;

import java.util.ArrayList;
import java.util.Objects;

/**
 * The WaitingList class represents a list of users in a waiting list.
 * It extends ArrayList and includes methods for managing the list.
 */
public class WaitingList {
    private ArrayList<String> waitingList;

    // Default constructor is required for Firestore
    public WaitingList() {
        this.waitingList = new ArrayList<>();
    }

    /**
     * Constructor to create a new WaitingList with the provided list of users.
     * @param waitingList the initial list of user device IDs.
     */
    public WaitingList(ArrayList<String> waitingList){
        this.waitingList = waitingList;
    }


    /**
     * Getter method to retrieve the current waiting list.
     * @return the waiting list as an ArrayList of user device IDs.
     */
    public ArrayList<String> getList(){
        return this.waitingList;
    }

    public  void setList(ArrayList<String> list) {
        this.waitingList = list;
    }

    /**
     * Method to add a user's device ID to the waiting list.
     * @param device the device ID of the user to add.
     */
    public void addUsers(String device){
        this.waitingList.add(device);
    }


    /**
     * Method to remove a user's device ID from the waiting list.
     * @param device the device ID of the user to remove.
     */
    public void removeUsers(String device){
        this.waitingList.remove(device);
    }


    /**
     * Method to retrieve a user's device ID from the waiting list at a specific position.
     * @param position the index position of the user in the list.
     * @return the user's device ID at the specified position.
     */
    public String getUser(int position){
        return this.waitingList.get(position);
    }


    /**
     * Checks if this WaitingList is equal to another WaitingList.
     * @param o the WaitingList to compare with.
     * @return true if the objects are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WaitingList that = (WaitingList) o;
        return Objects.equals(waitingList, that.waitingList);
    }


    /**
     * Generates the hash code for the WaitingList.
     * @return the hash code as an integer.
     */
    @Override
    public int hashCode() {
        return Objects.hash(waitingList);
    }

}