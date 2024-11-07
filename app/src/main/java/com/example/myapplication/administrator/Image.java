/**
 * Image object for saving url and keeping data
 * @author Noah Vincent
 */

package com.example.myapplication.administrator;

public class Image {
    private String url;
    private String type;
    private String id;

    /**
     * constructor for the image object
     * @param url URL for the image in Firebase
     * @param type Type of image being displayed
     * @param id ID of the related object in Firebase
     */
    public Image(String url, String type, String id) {
        this.url = url;
        this.type = type;
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }
}
