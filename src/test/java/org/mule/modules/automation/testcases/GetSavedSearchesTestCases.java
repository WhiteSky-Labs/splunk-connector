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

import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class GetSavedSearchesTestCases
        extends SplunkTestParent {

    private String searchName;

    @Before
    public void setup() throws Exception {
        initializeTestRunMessage("createSavedSearchTestData");
        searchName = getTestRunMessageValue("searchName");
        Object result = runFlowAndGetPayload("create-saved-search");
        initializeTestRunMessage("getSavedSearchesTestData");
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
    public void testGetSavedSearches()
            throws Exception {
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
    }

}
