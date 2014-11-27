/**
 *
 * (c) 2003-2012 MuleSoft, Inc. This software is protected under international
 * copyright law. All use of this software is subject to MuleSoft's Master
 * Subscription Agreement (or other Terms of Service) separately entered
 * into between you and MuleSoft. If such an agreement is not in
 * place, you may not use the software.
 */


package org.mule.modules.automation.testcases;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class RunSavedSearchTestCases
        extends SplunkTestParent {
    private String searchName;

    @Before
    public void setup() throws Exception {
        initializeTestRunMessage("createSavedSearchTestData");
        searchName = getTestRunMessageValue("searchName");
        runFlowAndGetPayload("create-saved-search");
        initializeTestRunMessage("runSavedSearchTestData");
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
    public void testRunSavedSearch()
            throws Exception {
        upsertOnTestRunMessage("searchName", searchName);
        Object result = runFlowAndGetPayload("run-saved-search");
        assertNotNull(result);
        List<Map<String, Object>> listResponse = (List<Map<String, Object>>) result;
        assertTrue(listResponse.size() > 0);

    }

}
