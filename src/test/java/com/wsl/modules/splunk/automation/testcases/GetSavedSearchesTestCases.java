/**
 *
 * (c) 2015 WhiteSky Labs, Pty Ltd. This software is protected under international
 * copyright law. All use of this software is subject to WhiteSky Labs' Master
 * Subscription Agreement (or other Terms of Service) separately entered
 * into between you and WhiteSky Labs. If such an agreement is not in
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

    private Map<String, Object> expectedBean;


    @Rule
    public Timeout globalTimeout = new Timeout(200000);

    @Before
    public void setup() throws Exception {
        initializeTestRunMessage("getSavedSearchesTestData");
        expectedBean = getBeanFromContext("getSavedSearchesTestData");
        runFlowAndGetPayload("create-saved-search");
    }

    @After
    public void tearDown() throws Exception {
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
            boolean foundSavedSearch = false;
            for (Map<String, Object> map : savedSearchList) {
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    if (entry.getKey().equalsIgnoreCase("search")){
                        if (((String)entry.getValue()).equalsIgnoreCase((String)expectedBean.get("searchQuery"))){
                            foundSavedSearch = true;
                        }
                    }
                }
            }

            assertTrue(savedSearchList.size() > 0);
            assertTrue(foundSavedSearch);
        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        }
    }

}