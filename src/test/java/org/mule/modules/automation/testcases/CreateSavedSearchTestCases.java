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

import static org.junit.Assert.assertEquals;

public class CreateSavedSearchTestCases
        extends SplunkTestParent {

    private String searchName = "";

    @Before
    public void setup() {
        initializeTestRunMessage("createSavedSearchTestData");
        searchName = getTestRunMessageValue("searchName");
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
    public void testCreateSavedSearch()
            throws Exception {
        Object result = runFlowAndGetPayload("create-saved-search");
        SavedSearch savedSearch = (SavedSearch) result;
        assertEquals(searchName, savedSearch.getName());
    }

}
