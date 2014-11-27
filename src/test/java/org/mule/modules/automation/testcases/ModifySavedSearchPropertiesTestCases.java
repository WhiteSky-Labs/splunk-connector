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

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ModifySavedSearchPropertiesTestCases
        extends SplunkTestParent {

    private String searchName;

    @Before
    public void setup() throws Exception {
        initializeTestRunMessage("createSavedSearchTestData");
        searchName = getTestRunMessageValue("searchName");
        Object result = runFlowAndGetPayload("create-saved-search");

        initializeTestRunMessage("modifySavedSearchPropertiesTestData");
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
    public void testModifySavedSearchProperties()
            throws Exception {
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

    }

}
