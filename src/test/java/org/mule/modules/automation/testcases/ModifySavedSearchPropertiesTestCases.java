/**
 *
 * (c) 2003-2012 MuleSoft, Inc. This software is protected under international
 * copyright law. All use of this software is subject to MuleSoft's Master
 * Subscription Agreement (or other Terms of Service) separately entered
 * into between you and MuleSoft. If such an agreement is not in
 * place, you may not use the software.
 */


package org.mule.modules.automation.testcases;

import com.splunk.SavedSearch;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.modules.automation.RegressionTests;
import org.mule.modules.automation.SmokeTests;
import org.mule.modules.automation.SplunkTestParent;
import org.mule.modules.tests.ConnectorTestUtils;

import java.util.HashMap;

import static org.junit.Assert.*;

public class ModifySavedSearchPropertiesTestCases
        extends SplunkTestParent {

    private String searchName;

    @Before
    public void setup() {
        try {
            initializeTestRunMessage("createSavedSearchTestData");
            searchName = getTestRunMessageValue("searchName");
            Object result = runFlowAndGetPayload("create-saved-search");

            initializeTestRunMessage("modifySavedSearchPropertiesTestData");
        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        }
    }

    @After
    public void tearDown() {
        try {
            upsertOnTestRunMessage("searchName", searchName);
            runFlowAndGetPayload("delete-saved-search");
        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        }
    }

    @Category({
            RegressionTests.class,
            SmokeTests.class
    })
    @Test
    public void testModifySavedSearchProperties() {
        try {
            upsertOnTestRunMessage("searchName", searchName);
            Object result = runFlowAndGetPayload("modify-saved-search-properties");
            assertNotNull(result);
            SavedSearch savedSearch = (SavedSearch) result;
            assertEquals(getTestRunMessageValue("searchName"), savedSearch.getName());
            HashMap<String, String> searchProperties = getTestRunMessageValue("searchPropertiesRef");
            // convert type of is_scheduled
            boolean isScheduled = new Boolean(searchProperties.get("is_scheduled")).booleanValue();
            assertEquals(searchProperties.get("description"), savedSearch.getDescription());
            assertEquals(isScheduled, savedSearch.isScheduled());
            assertEquals(searchProperties.get("cron_schedule"), savedSearch.getCronSchedule());
        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        }
    }

    @Category({
            RegressionTests.class
    })
    @Test
    public void testModifyInvalidSearchProperties() {
        try {
            initializeTestRunMessage("modifySavedSearchPropertiesInvalidTestData");
            upsertOnTestRunMessage("searchName", searchName);
            Object result = runFlowAndGetPayload("modify-saved-search-properties");
            fail("Should throw exception when modifying invalid properties");
        } catch (Exception e) {
            assertEquals("No matching method, check your properties are correct", e.getCause().getMessage());
        }
    }

    @Category({
            RegressionTests.class
    })
    @Test
    public void testModifySearchPropertiesWithEmptyProperties() {
        try {
            initializeTestRunMessage("modifySavedSearchPropertiesEmptyTestData");
            upsertOnTestRunMessage("searchName", searchName);
            Object result = runFlowAndGetPayload("modify-saved-search-properties");
            fail("Should throw exception when modifying invalid properties");
        } catch (Exception e) {
            assertEquals("You must provide some properties to modify", e.getCause().getMessage());
        }
    }

}
