package com.example.myapplication;

import com.google.firebase.firestore.auth.User;

import java.io.Serializable;
import java.util.ArrayList;

public class Event implements Serializable {
    private String title;
    private String description;
    private String posterUrl;
    private String eventId; // unique id for event, the document id from firestore
    private ArrayList<Users> waitingList = new ArrayList<>(); // This is the waiting list of the event, which contains users in it.

    public Event() {
        // Empty constructor needed for Firestore
    }

    public Event(String title, String description, String posterUrl) {
        this.title = title;
        this.description = description;
        this.posterUrl = posterUrl;

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    /**
     * This method provides the waiting list of the event
     * @return
     *      return the waiting list of current event
     */
    public ArrayList<Users> getWaitingList(){
        return waitingList;
    }

    /**
     * This method takes a Users object and add to the waiting list of the current event.
     * @param users
     *      Users object
     */
    public void addWaitingList(Users users){
        this.waitingList.add(users);
    }

    /**
     * This method takes a Users object and remove it from the event waiting list.
     * @param users
     *      User object
     */
    public void removeWaitingList(Users users){
        this.waitingList.remove(users);
    }

    public void setEventId(String id){
        this.eventId = id;
    }

    public String getEventId(){
        return this.eventId;
    }
}

