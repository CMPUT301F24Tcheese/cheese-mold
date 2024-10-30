package com.example.myapplication.objects;

import java.util.ArrayList;

public class Users {
    private String userId;
    private String firstName;
    private String lastName;
    private String email;
    private String profilePicture;
    private String role;
    private ArrayList<String> eventList = new ArrayList<>();

    public Users(String userId, String firstName, String lastName, String email, String profilePicture, String role) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.profilePicture = profilePicture;
        this.role = role;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String id) {
        this.userId = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public ArrayList<String> getEventList () {
        return this.eventList;
    }

    public void setEventList(ArrayList<String> eventList) {
        this.eventList = eventList;
    }

    public String getName() {
        return firstName + ' ' + lastName;
    }

    public void addEvent(String eventId) {
        this.eventList.add(eventId);
    }

    public void removeEvent (String eventId) {
        this.eventList.remove(eventId);
    }
}
