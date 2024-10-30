package com.example.myapplication.objects;

public class QRCode {
    private String url;
    private String eventID;
    private String eventName;

    public QRCode(String url, String eventID, String event_name) {
        this.url = url;
        this.eventID = eventID;
        this.eventName = event_name;
    }

    public String getUrl() {
        return url;
    }

    public String getEventID() {
        return eventID;
    }

    public String getEventName() {
        return eventName;
    }
}
