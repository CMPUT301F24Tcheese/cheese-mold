package com.example.myapplication.administrator;

public class Image {
    private String url;
    private String type;
    private String id;

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
