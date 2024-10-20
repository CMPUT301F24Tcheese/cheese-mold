package com.example.myapplication;

public class Event {
    private String title;
    private String description;
    private String posterUrl;

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
}
