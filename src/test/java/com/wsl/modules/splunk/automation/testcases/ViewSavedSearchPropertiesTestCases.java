/**
 *
 * (c) 2003-2015 MuleSoft, Inc. This software is protected under international
 * copyright law. All use of this software is subject to MuleSoft's Master
 * Subscription Agreement (or other Terms of Service) separately entered
 * into between you and MuleSoft. If such an agreement is not in
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
import org.mule.modules.tests.ConnectorTestUtils;

import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

public class ViewSavedSearchPropertiesTestCases
        extends SplunkTestParent {

    private String searchName;
    private String searchQuery;

    @Before
    public void setup() throws Exception {
        initializeTestRunMessage("createSavedSearchTestData");
        searchName = getTestRunMessageValue("searchName");
        runFlowAndGetPayload("create-saved-search");
        searchQuery = getTestRunMessageValue("searchQuery");
        initializeTestRunMessage("viewSavedSearchPropertiesTestData");
    }

    @After
    public void tearDown() throws Exception {
        upsertOnTestRunMessage("searchName", searchName);
        runFlowAndGetPayload("delete-saved-search");
    }

    @Category({
            RegressionTests.class,
            SmokeTests.class
    })
    @Test
    public void testViewSavedSearchProperties() {
        try {
            upsertOnTestRunMessage("searchName", searchName);
            Object result = runFlowAndGetPayload("view-saved-search-properties");
            assertNotNull(result);
            Set<Map.Entry<String, Object>> searchProperties = (Set<Map.Entry<String, Object>>) result;
            for (Map.Entry<String, Object> property : searchProperties) {
                if (property.getKey().equalsIgnoreCase("search")) {
                    assertEquals(searchQuery, property.getValue());
                }
            }
        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        }
    }

    @Category({
            RegressionTests.class
    })
    @Test
    public void testViewSavedSearchPropertiesForInvalidSavedSearch() {
        try {
            upsertOnTestRunMessage("searchName", "Invalid Saved Search Name");
            Object result = runFlowAndGetPayload("view-saved-search-properties");
            fail("Exception should be thrown when getting properties for an invalid saved search");
        } catch (Exception e) {
            assertTrue(e.getCause() instanceof NullPointerException);
        }
    }
}
