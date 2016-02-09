/**
 *
 * (c) 2015 WhiteSky Labs, Pty Ltd. This software is protected under international
 * copyright law. All use of this software is subject to WhiteSky Labs' Master
 * Subscription Agreement (or other Terms of Service) separately entered
 * into between you and WhiteSky Labs. If such an agreement is not in
 * place, you may not use the software.
 */


package com.wsl.modules.splunk.automation.testcases;

import com.wsl.modules.splunk.automation.RegressionTests;
import com.wsl.modules.splunk.automation.SmokeTests;
import com.wsl.modules.splunk.automation.SplunkTestParent;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.Timeout;
//import org.mule.modules.tests.ConnectorTestUtils;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class RunSavedSearchWithArgumentsTestCases
        extends SplunkTestParent {

    @Rule
    public Timeout globalTimeout = new Timeout(200000);

    private String searchName;

//    @Before
//    public void setup() {
//        try {
//            initializeTestRunMessage("runSavedSearchWithArgumentsTestData");
//            runFlowAndGetPayload("create-saved-search");
//        } catch (Exception e) {
//            fail(ConnectorTestUtils.getStackTrace(e));
//        }
//    }
//
//    @After
//    public void tearDown() {
//        try {
//            runFlowAndGetPayload("delete-saved-search");
//        } catch (Exception e) {
//            fail(ConnectorTestUtils.getStackTrace(e));
//        }
//    }
//
//    @Category({
//            RegressionTests.class,
//            SmokeTests.class
//    })
//    @Test
//    public void testRunSavedSearchWithArguments() {
//        try {
//            Object result = runFlowAndGetPayload("run-saved-search-with-arguments");
//            assertNotNull(result);
//            List<Map<String, Object>> listResponse = (List<Map<String, Object>>) result;
//            assertTrue(listResponse.size() > 0);
//        } catch (Exception e) {
//            fail(ConnectorTestUtils.getStackTrace(e));
//        }
//    }
//
//    @Category({
//            RegressionTests.class
//    })
//    @Test
//    public void testRunSavedSearchWithInvalidArguments() {
//        try {
//            initializeTestRunMessage("runSavedSearchWithInvalidArgumentsTestData");
//            Object result = runFlowAndGetPayload("run-saved-search-with-arguments");
//            // invalid arguments are ignored by saved searches, should be successful
//            assertNotNull(result);
//            List<Map<String, Object>> listResponse = (List<Map<String, Object>>) result;
//            assertTrue(listResponse.size() > 0);
//        } catch (Exception e) {
//            fail(ConnectorTestUtils.getStackTrace(e));
//        }
//    }

}
