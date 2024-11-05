package com.example.myapplication.objects;


import com.google.type.DateTime;

/**
 * The Facility class represents a facility
 */
public class Facility {
    private String creatorId;
    private String name;
    private String id;
    private String street;
    private String city;
    private String province;
    private String description;



    /**
     * Default constructor required by firestore
     */
    public Facility() {
        // Default constructor required for calls to DataSnapshot.getValue(Facility.class)
    }

    /**
     * Constructor for a facility object
     * @param id id of the facility
     * @param street street address
     * @param city city of the facility
     * @param province province of the facility
     */
    public Facility(String id, String name, String description, String street, String city, String province) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.street = street;
        this.city = city;
        this.province = province;
    }


    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter method to get the id of the facility
     * @return
     *      Facility Id
     */
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * Getter method to get the id of the facility
     * @return
     *      Street address
     */
    public String getStreet() {
        return street;
    }


    public void setStreet(String street) {
        this.street = street;
    }

    /**
     * Getter method to get the city of the facility
     * @return
     *      City of the facility
     */
    public String getCity() {
        return city;
    }


    public void setCity(String city) {
        this.city = city;
    }

    /**
     * Getter method to get the province of the facility
     * @return
     *      Province of the facility
     */
    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Method to get the street and city address of the facility
     * Added methods for adapter binding
     * @return
     *      Street and city combined in a string
     */
    public String getAdress() {
        return street + ", " + city + ", " + province;
    }

    public boolean isValidFacility() {
        return name != null && !name.isEmpty()
                && city != null && !city.isEmpty()
                && province != null && !province.isEmpty();
    }
}
