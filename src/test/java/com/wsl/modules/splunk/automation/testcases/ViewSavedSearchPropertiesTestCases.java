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
import org.mule.api.annotations.Connect;
//import org.mule.modules.tests.ConnectorTestUtils;

import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

public class ViewSavedSearchPropertiesTestCases
        extends SplunkTestParent {

    private Map<String, Object> expectedBean;

//    @Before
//    public void setup() throws Exception {
//        initializeTestRunMessage("viewSavedSearchPropertiesTestData");
//        expectedBean = getBeanFromContext("viewSavedSearchPropertiesTestData");
//        runFlowAndGetPayload("create-saved-search");
//    }
//
//    @After
//    public void tearDown() throws Exception {
//        upsertOnTestRunMessage("searchName", expectedBean.get("searchName"));
//        runFlowAndGetPayload("delete-saved-search");
//    }
//
//    @Category({
//            RegressionTests.class,
//            SmokeTests.class
//    })
//    @Test
//    public void testViewSavedSearchProperties() {
//        try {
//            Object result = runFlowAndGetPayload("view-saved-search-properties");
//            assertNotNull(result);
//            Set<Map.Entry<String, Object>> searchProperties = (Set<Map.Entry<String, Object>>) result;
//            for (Map.Entry<String, Object> property : searchProperties) {
//                if (property.getKey().equalsIgnoreCase("search")) {
//                    assertEquals(expectedBean.get("searchQuery"), property.getValue());
//                }
//            }
//        } catch (Exception e) {
//            fail(ConnectorTestUtils.getStackTrace(e));
//        }
//    }
//
//    @Category({
//            RegressionTests.class
//    })
//    @Test
//    public void testViewSavedSearchPropertiesForInvalidSavedSearch() {
//        try {
//            upsertOnTestRunMessage("searchName", "Invalid Saved Search Name");
//            Object result = runFlowAndGetPayload("view-saved-search-properties");
//            fail("Exception should be thrown when getting properties for an invalid saved search");
//        } catch (MessagingException me) {
//            assertTrue(me.getCause() instanceof NullPointerException);
//        } catch(Exception e){
//            fail(ConnectorTestUtils.getStackTrace(e));
//        }
//    }
}
