/**
 *
 * (c) 2003-2012 MuleSoft, Inc. This software is protected under international
 * copyright law. All use of this software is subject to MuleSoft's Master
 * Subscription Agreement (or other Terms of Service) separately entered
 * into between you and MuleSoft. If such an agreement is not in
 * place, you may not use the software.
 */


package org.mule.modules.automation.testcases;

import com.splunk.Job;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class GetSavedSearchHistoryTestCases
        extends SplunkTestParent {
    private String searchName;
    private final String searchQuery = "search " + searchName + " | head 100";

    @Before
    public void setup() throws Exception {
        // create and run a saved search
        initializeTestRunMessage("createSavedSearchTestData");
        searchName = getTestRunMessageValue("searchName");
        upsertOnTestRunMessage("searchQuery", searchQuery);
        Object result = runFlowAndGetPayload("create-saved-search");
        initializeTestRunMessage("runSavedSearchTestData");
        upsertOnTestRunMessage("searchName", searchName);
        result = runFlowAndGetPayload("run-saved-search");
        initializeTestRunMessage("getSavedSearchHistoryTestData");
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
    public void testGetSavedSearchHistory()
            throws Exception {
        Object result = runFlowAndGetPayload("get-saved-search-history");
        List<Job> jobs = (List<Job>) result;
        assertNotNull(jobs);
        assertTrue(jobs.size() > 0);
        boolean foundTestSearch = false;
        for (Job job : jobs) {
            assertTrue(job.getName().length() > 0);
            if (job.getSearch().contains(searchQuery)) {
                foundTestSearch = true;
            }
        }
        assertTrue("Must be able to detect recently run test Saved Search", foundTestSearch);
    }

}
