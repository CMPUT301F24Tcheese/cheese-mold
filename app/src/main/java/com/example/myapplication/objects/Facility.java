package com.example.myapplication.objects;


/**
 * The Facility class represents a facility
 */
public class Facility {
    private String creatorId;
    private String id;
    private String street;
    private String city;
    private String province;
    private String postalCode;


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
     * @param postalCode postalCode of the facility
     */
    public Facility(String id, String street, String city, String province, String postalCode) {
        this.id = id;
        this.street = street;
        this.city = city;
        this.province = province;
        this.postalCode = postalCode;
    }


    /**
     * Getter method to get the Id of the creator
     * @return
     *      Creator Id
     */
    public String getCreatorId() {
        return creatorId;
    }


    /**
     * Setter method to set the Id of the creator
     * @param creatorId id of the creator
     */
    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }


    /**
     * Getter method to get the id of the facility
     * @return
     *      Facility Id
     */
    public String getId() {
        return id;
    }


    /**
     * Getter method to get the id of the facility
     * @return
     *      Street address
     */
    public String getStreet() {
        return street;
    }


    /**
     * Getter method to get the city of the facility
     * @return
     *      City of the facility
     */
    public String getCity() {
        return city;
    }


    /**
     * Getter method to get the province of the facility
     * @return
     *      Province of the facility
     */
    public String getProvince() {
        return province;
    }


    /**
     * Getter method to get the post code of the facility
     * @return
     *      Post Code of the facility
     */
    public String getPostalCode() {
        return postalCode;
    }


    /**
     * Method to get the street and city address of the facility
     * Added methods for adapter binding
     * @return
     *      Street and city combined in a string
     */
    public String getName() {
        return street + ", " + city;
    }


    /**
     * Method to get the province and post code of the facility
     * @return
     *      province and post code combined in a string
     */
    public String getDescription() {
        return province + " - " + postalCode;
    }
}