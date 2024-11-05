package com.example.myapplication;

import org.junit.Test;

import static org.junit.Assert.*;

import com.example.myapplication.objects.Facility;

/**
 * This class contains unit tests for the Facility class.
 */
public class FacilityTest {

    /**
     * Tests the default constructor of the Facility class.
     */
    @Test
    public void testDefaultConstructor() {
        Facility facility = new Facility();
        assertNotNull(facility);
    }

    /**
     * Tests the parameterized constructor of the Facility class.
     */
    @Test
    public void testParameterizedConstructor() {
        Facility facility = new Facility("1", "Gym", "A place for physical exercise", "123 Main St", "Anytown", "Anystate");
        assertEquals("1", facility.getId());
        assertEquals("Gym", facility.getName());
        assertEquals("A place for physical exercise", facility.getDescription());
        assertEquals("123 Main St", facility.getStreet());
        assertEquals("Anytown", facility.getCity());
        assertEquals("Anystate", facility.getProvince());
    }

    /**
     * Tests the setCreatorId and getCreatorId methods.
     */
    @Test
    public void testSetAndGetCreatorId() {
        Facility facility = new Facility();
        facility.setCreatorId("creator123");
        assertEquals("creator123", facility.getCreatorId());
    }

    /**
     * Tests the setName and getName methods.
     */
    @Test
    public void testSetAndGetName() {
        Facility facility = new Facility();
        facility.setName("Swimming Pool");
        assertEquals("Swimming Pool", facility.getName());
    }

    /**
     * Tests the setId and getId methods.
     */
    @Test
    public void testSetAndGetId() {
        Facility facility = new Facility();
        facility.setId("2");
        assertEquals("2", facility.getId());
    }

    /**
     * Tests the setStreet and getStreet methods.
     */
    @Test
    public void testSetAndGetStreet() {
        Facility facility = new Facility();
        facility.setStreet("456 Another St");
        assertEquals("456 Another St", facility.getStreet());
    }

    /**
     * Tests the setCity and getCity methods.
     */
    @Test
    public void testSetAndGetCity() {
        Facility facility = new Facility();
        facility.setCity("New City");
        assertEquals("New City", facility.getCity());
    }

    /**
     * Tests the setProvince and getProvince methods.
     */
    @Test
    public void testSetAndGetProvince() {
        Facility facility = new Facility();
        facility.setProvince("New Province");
        assertEquals("New Province", facility.getProvince());
    }

    /**
     * Tests the setDescription and getDescription methods.
     */
    @Test
    public void testSetAndGetDescription() {
        Facility facility = new Facility();
        facility.setDescription("A new facility for events.");
        assertEquals("A new facility for events.", facility.getDescription());
    }

    /**
     * Tests the getAddress method to ensure it combines street, city, and province correctly.
     */
    @Test
    public void testGetAddress() {
        Facility facility = new Facility("3", "Community Center", "A place for community events", "789 Local Rd", "Hometown", "Homestate");
        String expectedAddress = "789 Local Rd, Hometown, Homestate";
        assertEquals(expectedAddress, facility.getAdress());
    }

    @Test
    public void testIsValidFacility() {
        Facility facility = new Facility("1", "Sample Facility", "A description", "123 Main St", "SampleCity", "SampleProvince");
        // Valid facility
        assertTrue(facility.isValidFacility());

        // Invalid cases
        facility.setName("");
        assertFalse(facility.isValidFacility());

        facility.setName("Sample Facility");
        facility.setCity("");
        assertFalse(facility.isValidFacility());

        facility.setCity("SampleCity");
        facility.setProvince("");
        assertFalse(facility.isValidFacility());
    }
}

