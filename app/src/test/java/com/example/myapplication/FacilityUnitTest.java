package com.example.myapplication;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.example.myapplication.objects.Facility;

public class FacilityUnitTest {

    private Facility facility;

    @Before
    public void setUp() {
        facility = new Facility("1", "Sample Facility", "A description", "123 Main St", "SampleCity", "SampleProvince");
    }

    @Test
    public void testGetName() {
        facility.setName("New Facility");
        assertEquals("New Facility", facility.getName());
    }

    @Test
    public void testGetDescription() {
        facility.setDescription("Updated Description");
        assertEquals("Updated Description", facility.getDescription());
    }

    @Test
    public void testGetStreet() {
        facility.setStreet("456 Another St");
        assertEquals("456 Another St", facility.getStreet());
    }

    @Test
    public void testGetCity() {
        facility.setCity("NewCity");
        assertEquals("NewCity", facility.getCity());
    }

    @Test
    public void testGetProvince() {
        facility.setProvince("NewProvince");
        assertEquals("NewProvince", facility.getProvince());
    }

    @Test
    public void testGetAddress() {
        facility.setStreet("789 Street");
        facility.setCity("AnotherCity");
        facility.setProvince("AnotherProvince");
        assertEquals("789 Street, AnotherCity, AnotherProvince", facility.getAdress());
    }

    @Test
    public void testIsValidFacility() {
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
