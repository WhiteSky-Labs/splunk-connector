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

import java.util.List;

import static org.junit.Assert.*;

public class GetSavedSearchesTestCases
        extends SplunkTestParent {

    private String searchName;

    @Before
    public void setup() {
        try {
            initializeTestRunMessage("createSavedSearchTestData");
            searchName = getTestRunMessageValue("searchName");
            Object result = runFlowAndGetPayload("create-saved-search");
            initializeTestRunMessage("getSavedSearchesTestData");
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
    public void testGetSavedSearches() {
        try {
            Object result = runFlowAndGetPayload("get-saved-searches");
            assertNotNull(result);
            List<SavedSearch> savedSearchList = (List<SavedSearch>) result;
            boolean foundTestSearch = false;
            for (SavedSearch savedSearch : savedSearchList) {
                assertTrue(savedSearch.getName().length() > 0);
                if (savedSearch.getName().equalsIgnoreCase(searchName)) {
                    foundTestSearch = true;
                }
            }
            assertTrue(savedSearchList.size() > 0);
            assertTrue("Must be able to detect created Saved Search", foundTestSearch);
        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        }
    }

}
