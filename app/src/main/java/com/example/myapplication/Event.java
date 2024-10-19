package com.example.myapplication;

public class Event {
    private String title;
    private String description;

    public Event() {
        // Default constructor required for calls to DataSnapshot.getValue(Event.class)
    }

    public Event(String title, String description) {
        this.title = title;
        this.description = description;
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
}
