/**
 * class for holding QRCode and related data
 */

package com.example.myapplication.objects;

public class QRCode {
    private String url;
    private String eventID;
    private String eventName;

    /**
     * constructor for QRCode object
     * @param url url for QRCode in Firebase
     * @param eventID ID for the event linked to this QRCode
     * @param event_name name of the Event
     */
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
