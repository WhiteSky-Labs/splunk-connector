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
import org.mule.modules.automation.RegressionTests;
import org.mule.modules.automation.SmokeTests;
import org.mule.modules.automation.SplunkTestParent;
import org.mule.modules.tests.ConnectorTestUtils;

import java.util.List;

import static org.junit.Assert.*;

public class GetSavedSearchHistoryTestCases
        extends SplunkTestParent {
    private String searchName;
    private final String searchQuery = "search " + searchName + " | head 100";

    @Before
    public void setup() {
        // create and run a saved search
        try {
            initializeTestRunMessage("createSavedSearchTestData");
            searchName = getTestRunMessageValue("searchName");
            upsertOnTestRunMessage("searchQuery", searchQuery);
            Object result = runFlowAndGetPayload("create-saved-search");
            initializeTestRunMessage("runSavedSearchTestData");
            upsertOnTestRunMessage("searchName", searchName);
            result = runFlowAndGetPayload("run-saved-search");
            initializeTestRunMessage("getSavedSearchHistoryTestData");
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
    public void testGetSavedSearchHistory() {
        try {
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
        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        }
    }

}
