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
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.Timeout;
import org.mule.modules.tests.ConnectorTestUtils;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class GetSavedSearchesTestCases
        extends SplunkTestParent {

    @Rule
    public Timeout globalTimeout = new Timeout(100000);
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
    public void testGetSavedSearches() {
        try {
            Object result = runFlowAndGetPayload("get-saved-searches");
            assertNotNull(result);
            List<Map<String, Object>> savedSearchList = (List<Map<String, Object>>) result;
            assertTrue(savedSearchList.size() > 0);
        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        }
    }

}
