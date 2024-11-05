package com.example.myapplication;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;


/**
 * Test suite to run all test classes in the project.
 * This suite includes:
 * - EventTest
 * - FacilityTest
 * - LotteryTest
 * - WaitingListTest
 *
 * To add additional test classes, simply include them in the @Suite.SuiteClasses annotation.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        EventTest.class,
        FacilityTest.class,
        LotteryTest.class,
        WaitingListTest.class
        // Add more test classes here
})
public class AllTestSuite {
}
