/**
 *
 * (c) 2015 WhiteSky Labs, Pty Ltd. This software is protected under international
 * copyright law. All use of this software is subject to WhiteSky Labs' Master
 * Subscription Agreement (or other Terms of Service) separately entered
 * into between you and WhiteSky Labs. If such an agreement is not in
 * place, you may not use the software.
 */


package com.wsl.modules.splunk.automation.testcases;

import com.splunk.Job;
import com.wsl.modules.splunk.automation.RegressionTests;
import com.wsl.modules.splunk.automation.SmokeTests;
import com.wsl.modules.splunk.automation.SplunkTestParent;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
//import org.mule.modules.tests.ConnectorTestUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class RunBlockingSearchTestCases
        extends SplunkTestParent {

//    @Before
//    public void setup() throws Exception {
//        initializeTestRunMessage("runBlockingSearchTestData");
//    }
//
//    @Category({
//            RegressionTests.class,
//            SmokeTests.class
//    })
//    @Test
//    public void testRunBlockingSearch() {
//        try {
//            Object result = runFlowAndGetPayload("run-blocking-search");
//            Map<String, Object> eventResponse = (Map<String, Object>) result;
//            assertNotNull(eventResponse);
//            List<HashMap> events = (List<HashMap>) eventResponse.get("events");
//            assertNotNull(events);
//            assertTrue(events.size() > 0);
//            Job job = (Job) eventResponse.get("job");
//            assertEquals("DONE", job.getDispatchState());
//            assertEquals(getTestRunMessageValue("searchQuery"), job.getSearch());
//        } catch (Exception e) {
//            fail(ConnectorTestUtils.getStackTrace(e));
//        }
//    }
//
//    @Category({
//            RegressionTests.class
//    })
//    @Test
//    public void testRunBlockingSearchWithInvalidQuery() {
//        try {
//            upsertOnTestRunMessage("searchQuery", "invalid search query");
//            Object result = runFlowAndGetPayload("run-blocking-search");
//            fail("Exception should be thrown when attempting an invalid search query");
//        } catch (Exception e) {
//            assertTrue(e.getCause().getMessage().contains("Unknown search command"));
//        }
//    }
//
//    @Category({
//            RegressionTests.class
//    })
//    @Test
//    public void testRunBlockingSearchWithEmptyQuery() {
//        try {
//            upsertOnTestRunMessage("searchQuery", "");
//            Object result = runFlowAndGetPayload("run-blocking-search");
//            fail("Exception should be thrown when attempting an invalid search query");
//        } catch (Exception e) {
//            assertEquals("Search Query is empty.", e.getCause().getMessage());
//        }
//    }
//
//    @Category({
//            RegressionTests.class
//    })
//    @Test
//    public void testRunBlockingSearchWithNullQuery() {
//        try {
//            upsertOnTestRunMessage("searchQuery", null);
//            Object result = runFlowAndGetPayload("run-blocking-search");
//            fail("Exception should be thrown when attempting an invalid search query");
//        } catch (Exception e) {
//            assertEquals("Search Query is empty.", e.getCause().getMessage());
//        }
//    }
//
//    @Category({
//            RegressionTests.class
//    })
//    @Test
//    public void testRunBlockingSearchWithValidSearchArgs() {
//        try {
//            initializeTestRunMessage("runBlockingSearchTestDataWithValidProperties");
//            Object result = runFlowAndGetPayload("run-blocking-search");
//            Map<String, Object> eventResponse = (Map<String, Object>) result;
//            assertNotNull(eventResponse);
//            List<HashMap> events = (List<HashMap>) eventResponse.get("events");
//            assertNotNull(events);
//            assertTrue(events.size() > 0);
//            Job job = (Job) eventResponse.get("job");
//            assertEquals("DONE", job.getDispatchState());
//            assertEquals(getTestRunMessageValue("searchQuery"), job.getSearch());
//        } catch (Exception e) {
//            fail(ConnectorTestUtils.getStackTrace(e));
//        }
//    }

}
