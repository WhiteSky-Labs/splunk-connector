/**
 *
 * (c) 2003-2012 MuleSoft, Inc. This software is protected under international
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
            List<Map<String, Object>> savedSearchList = (List<Map<String, Object>>) result;
            assertTrue(savedSearchList.size() > 0);
        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        }
    }

}
