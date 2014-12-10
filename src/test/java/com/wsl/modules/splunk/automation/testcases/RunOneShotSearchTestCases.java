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
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.modules.tests.ConnectorTestUtils;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class RunOneShotSearchTestCases
        extends SplunkTestParent {


    @Before
    public void setup() {
        initializeTestRunMessage("runOneShotSearchTestData");
    }

    @Category({
            RegressionTests.class,
            SmokeTests.class
    })
    @Test
    public void testRunOneShotSearch() {
        try {
            Object result = runFlowAndGetPayload("run-one-shot-search");
            List<Map<String, Object>> searchResults = (List<Map<String, Object>>) result;
            assertNotNull(searchResults);
            assertTrue(searchResults.size() > 0);
        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        }
    }

    @Category({
            RegressionTests.class
    })
    @Test
    public void testRunOneShotSearchWithInvalidQuery() {
        try {
            upsertOnTestRunMessage("searchQuery", "Invalid search query");
            Object result = runFlowAndGetPayload("run-one-shot-search");
            fail("An invalid search query should throw an exception");
        } catch (Exception e) {
            assertTrue(e.getCause().getMessage().contains("Unknown search command"));
        }
    }

    @Category({
            RegressionTests.class
    })
    @Test
    public void testRunOneShotSearchWithInvalidSearchArgs() {
        try {
            initializeTestRunMessage("runOneShotSearchTestDataWithInvalidArgs");
            Object result = runFlowAndGetPayload("run-one-shot-search");
            // invalid search args are ignored for a one-shot search, should return successfully
            List<Map<String, Object>> searchResults = (List<Map<String, Object>>) result;
            assertNotNull(searchResults);
            assertTrue(searchResults.size() > 0);
        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        }
    }

    @Category({
            RegressionTests.class
    })
    @Test
    public void testRunOneShotSearchWithoutSearchArgs() {
        try {
            upsertOnTestRunMessage("args", null);
            Object result = runFlowAndGetPayload("run-one-shot-search");
            // missing search args are ignored for a one-shot search, should return successfully
            List<Map<String, Object>> searchResults = (List<Map<String, Object>>) result;
            assertNotNull(searchResults);
            assertTrue(searchResults.size() > 0);
        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        }
    }


}
