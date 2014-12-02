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

import static org.junit.Assert.*;

public class CreateSavedSearchTestCases
        extends SplunkTestParent {

    private String searchName = "";
    private boolean searchCreated = false;
    private String duplicateSearchName = "";
    private boolean duplicateSearchCreated = false;

    @Before
    public void setup() {
        initializeTestRunMessage("createSavedSearchTestData");
        searchName = getTestRunMessageValue("searchName");
    }

    @After
    public void tearDown() {
        try {
            if ((searchName != null) && (searchName != "") && searchCreated) {
                // ensures we didn't only run the negative cases
                upsertOnTestRunMessage("searchName", searchName);
                runFlowAndGetPayload("delete-saved-search");
            }
            if ((duplicateSearchName != null) && (duplicateSearchName != "") && duplicateSearchCreated) {
                upsertOnTestRunMessage("searchName", duplicateSearchName);
                runFlowAndGetPayload("delete-saved-search");
            }
        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
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
            SavedSearch savedSearch = (SavedSearch) result;
            assertEquals(searchName, savedSearch.getName());
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
            SavedSearch savedSearch = (SavedSearch) result;
            assertEquals(duplicateSearchName, savedSearch.getName());
        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        }
        try {
            Object result = runFlowAndGetPayload("create-saved-search");
            SavedSearch savedSearch = (SavedSearch) result;
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
