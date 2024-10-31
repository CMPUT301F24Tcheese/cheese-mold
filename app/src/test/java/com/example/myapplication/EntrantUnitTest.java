package com.example.myapplication;

import com.example.myapplication.objects.Event;
import com.example.myapplication.objects.WaitingList;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class EntrantUnitTest {
    private Event evnetTest = new Event("12345","Event","Description1","test.url", 100L);

    // Test for Event WaitingList
    @Test
    /**
     * Test to join event
     */
    public void testJoin_1(){

        evnetTest.addWaitingList( "1");
        WaitingList testWaiting = new WaitingList(new ArrayList<>());
        testWaiting.addUsers("1");
        assertEquals(testWaiting, evnetTest.getWaitingList());

    }

    @Test
    /**
     * Test for unjoin event
     */
    public void testUnjoin_2(){

        evnetTest.addWaitingList( "1");
        WaitingList testWaiting = new WaitingList(new ArrayList<>());

        evnetTest.removeWaitingList( "1");
        assertEquals(testWaiting, evnetTest.getWaitingList());

    }

    @Test
    public void testGetId(){
        assertEquals("12345",evnetTest.getId());
    }

}
