package com.example.myapplication.objects;

import java.util.ArrayList;

/**
 * The class represents the Users objects
 */
public class Users {
    private String userId;
    private String firstName;
    private String lastName;
    private String email;
    private String profilePicture;
    private String role;
    private ArrayList<String> eventList = new ArrayList<>();


    /**
     * Constructor for the user object
     * @param userId
     * @param firstName
     * @param lastName
     * @param email
     * @param profilePicture
     * @param role
     */
    public Users(String userId, String firstName, String lastName, String email, String profilePicture, String role) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.profilePicture = profilePicture;
        this.role = role;
    }


    /**
     * Getter method to get the user Id
     * @return
     *      user Id
     */
    public String getUserId() {
        return userId;
    }


    /**
     * Setter method to set the user ID.
     * @param id the ID to set as the user ID.
     */
    public void setUserId(String id) {
        this.userId = id;
    }


    /**
     * Getter method to get the first name of the user.
     * @return the first name as a String.
     */
    public String getFirstName() {
        return firstName;
    }


    /**
     * Setter method to set the first name of the user.
     * @param firstName the first name to set.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }


    /**
     * Getter method to get the last name of the user.
     * @return the last name as a String.
     */
    public String getLastName() {
        return lastName;
    }


    /**
     * Setter method to set the last name of the user.
     * @param lastName the last name to set.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }


    /**
     * Getter method to get the email of the user.
     * @return the email as a String.
     */
    public String getEmail() {
        return email;
    }


    /**
     * Setter method to set the email of the user.
     * @param email the email to set.
     */
    public void setEmail(String email) {
        this.email = email;
    }


    /**
     * Getter method to get the profile picture URL of the user.
     * @return the profile picture URL as a String.
     */
    public String getProfilePicture() {
        return profilePicture;
    }


    /**
     * Setter method to set the profile picture URL of the user.
     * @param profilePicture the profile picture URL to set.
     */
    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }


    /**
     * Getter method to get the role of the user.
     * @return the role as a String.
     */
    public String getRole() {
        return role;
    }


    /**
     * Setter method to set the role of the user.
     * @param role the role to set.
     */
    public void setRole(String role) {
        this.role = role;
    }


    /**
     * Getter method to get the list of event IDs associated with the user.
     * @return an ArrayList of event IDs.
     */
    public ArrayList<String> getEventList () {
        return this.eventList;
    }


    /**
     * Setter method to set the list of event IDs for the user.
     * @param eventList an ArrayList of event IDs to set.
     */
    public void setEventList(ArrayList<String> eventList) {
        this.eventList = eventList;
    }


    /**
     * Getter method to get the full name of the user.
     * @return the full name as a concatenation of first and last name.
     */
    public String getName() {
        return firstName + ' ' + lastName;
    }


    /**
     * Method to add an event ID to the user's event list.
     * @param eventId the event ID to add.
     */
    public void addEvent(String eventId) {
        this.eventList.add(eventId);
    }


    /**
     * Method to remove an event ID from the user's event list.
     * @param eventId the event ID to remove.
     */
    public void removeEvent (String eventId) {
        this.eventList.remove(eventId);
    }

}
