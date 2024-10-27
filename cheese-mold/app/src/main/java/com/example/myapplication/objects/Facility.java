package com.example.myapplication.objects;

public class Facility {
    private String id;
    private String street;
    private String city;
    private String province;
    private String postalCode;

    public Facility() {
        // Default constructor required for calls to DataSnapshot.getValue(Facility.class)
    }

    public Facility(String id, String street, String city, String province, String postalCode) {
        this.id = id;
        this.street = street;
        this.city = city;
        this.province = province;
        this.postalCode = postalCode;
    }

    public String getId() {
        return id;
    }

    public String getStreet() {
        return street;
    }

    public String getCity() {
        return city;
    }

    public String getProvince() {
        return province;
    }

    public String getPostalCode() {
        return postalCode;
    }

    // Added methods for adapter binding
    public String getName() {
        return street + ", " + city;
    }

    public String getDescription() {
        return province + " - " + postalCode;
    }
}