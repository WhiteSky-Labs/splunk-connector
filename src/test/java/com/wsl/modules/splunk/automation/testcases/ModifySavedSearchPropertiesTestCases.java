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
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.api.MessagingException;
import org.mule.api.annotations.Connector;
//import org.mule.modules.tests.ConnectorTestUtils;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class ModifySavedSearchPropertiesTestCases
        extends SplunkTestParent {

//    @Before
//    public void setup() throws Exception {
//        initializeTestRunMessage("modifySavedSearchPropertiesTestData");
//        runFlowAndGetPayload("create-saved-search");
//    }
//
//    @After
//    public void tearDown() throws Exception {
//        runFlowAndGetPayload("delete-saved-search");
//    }
//
//    @Category({
//            RegressionTests.class,
//            SmokeTests.class
//    })
//    @Test
//    public void testModifySavedSearchProperties() {
//        try {
//            Object result = runFlowAndGetPayload("modify-saved-search-properties");
//            assertNotNull(result);
//            Map<String, Object> savedSearch = (Map<String, Object>) result;
//            assertEquals("list", savedSearch.get("display.events.type"));
//            HashMap<String, String> searchProperties = getTestRunMessageValue("searchPropertiesRef");
//            assertEquals(searchProperties.get("cron_schedule"), savedSearch.get("cron_schedule"));
//        } catch (Exception e) {
//            fail(ConnectorTestUtils.getStackTrace(e));
//        }
//    }
//
//    @Category({
//            RegressionTests.class
//    })
//    @Test
//    public void testModifySearchPropertiesWithEmptyProperties() {
//        try {
//            initializeTestRunMessage("modifySavedSearchPropertiesEmptyTestData");
//            Object result = runFlowAndGetPayload("modify-saved-search-properties");
//            fail("Should throw exception when modifying invalid properties");
//        } catch (MessagingException me) {
//            assertEquals("You must provide some properties to modify", me.getCause().getMessage());
//        } catch (Exception e){
//            fail(ConnectorTestUtils.getStackTrace(e));
//        }
//    }

}
