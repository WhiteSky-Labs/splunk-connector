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

import static org.junit.Assert.*;

public class CreateSavedSearchTestCases
        extends SplunkTestParent {

    private String searchName = "";
    private boolean searchCreated = false;
    private String duplicateSearchName = "";
    private boolean duplicateSearchCreated = false;

    @Before
    public void setup() throws Exception {
        initializeTestRunMessage("createSavedSearchTestData");
        searchName = getTestRunMessageValue("searchName");
    }

    @After
    public void tearDown() throws Exception {
        if ((searchName != null) && (searchName != "") && searchCreated) {
            // ensures we didn't only run the negative cases
            upsertOnTestRunMessage("searchName", searchName);
            runFlowAndGetPayload("delete-saved-search");
        }
        if ((duplicateSearchName != null) && (duplicateSearchName != "") && duplicateSearchCreated) {
            upsertOnTestRunMessage("searchName", duplicateSearchName);
            runFlowAndGetPayload("delete-saved-search");
        }
    }

    @Category({
            RegressionTests.class,
            SmokeTests.class
    })
    @Test
    public void testCreateSavedSearch() {
        try {
            Object result = runFlowAndGetPayload("create-saved-search");
            searchCreated = true;
            Map<String, Object> savedSearch = (Map<String, Object>) result;
            assertEquals("full", savedSearch.get("display.events.list.drilldown"));
        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        }
    }

    @Category({
            RegressionTests.class
    })
    @Test
    public void testCreateExistingSavedSearch() {
        initializeTestRunMessage("createDuplicateSavedSearchTestData");
        this.duplicateSearchName = getTestRunMessageValue("searchName");
        try {
            Object result = runFlowAndGetPayload("create-saved-search");
            duplicateSearchCreated = true;
            Map<String, Object> savedSearch = (Map<String, Object>) result;
            assertEquals("full", savedSearch.get("display.events.list.drilldown"));
        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        }
        try {
            Object result = runFlowAndGetPayload("create-saved-search");
            Map<String, Object> savedSearch = (Map<String, Object>) result;
            fail("Exception should be thrown when creating an existing saved search");
        } catch (Exception e) {
            assertTrue(e.getCause().getMessage().contains("A saved search with that name already exists."));
        }
    }

    @Category({
            RegressionTests.class
    })
    @Test
    public void testCreateSavedSearchWithEmptyName() {
        try {
            upsertOnTestRunMessage("searchName", "");
            Object result = runFlowAndGetPayload("create-saved-search");
            fail("Exception should be thrown when using an empty name to create a Saved Search");
        } catch (Exception e) {
            assertEquals("Search Name empty.", e.getCause().getMessage());
        }
    }

    @Category({
            RegressionTests.class
    })
    @Test
    public void testCreateSavedSearchWithNullName() {
        try {
            upsertOnTestRunMessage("searchName", null);
            Object result = runFlowAndGetPayload("create-saved-search");
            fail("Exception should be thrown when using an null name to create a Saved Search");
        } catch (Exception e) {
            assertEquals("Search Name empty.", e.getCause().getMessage());
        }
    }


}
