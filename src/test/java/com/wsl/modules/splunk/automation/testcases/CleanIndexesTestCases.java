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
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.modules.tests.ConnectorTestUtils;

import java.util.Map;

import static org.junit.Assert.*;

public class CleanIndexesTestCases extends SplunkTestParent {

    private String indexName = "clean_index_testing";

    @Before
    public void setup() throws Exception {
        initializeTestRunMessage("createIndexTestData");
        upsertOnTestRunMessage("indexName", indexName);

        Object result = runFlowAndGetPayload("create-index");
    }

    @After
    public void tearDown() throws Exception {
        initializeTestRunMessage("removeIndexTestData");
        upsertOnTestRunMessage("indexName", indexName);
        Object result = runFlowAndGetPayload("remove-index");
    }

    @Category({
            RegressionTests.class,
            SmokeTests.class
    })
    @Test
    public void testCleanIndex() {
        try {
            initializeTestRunMessage("cleanIndexTestData");
            upsertOnTestRunMessage("indexName", indexName);
            Object result = runFlowAndGetPayload("clean-index");
            assertNotNull(result);
            Map<String, Object> index = (Map<String, Object>) result;
            assertEquals("main", index.get("defaultDatabase"));
        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        }
    }

    @Category({
            RegressionTests.class
    })
    @Test
    public void testCleanInvalidIndex() {
        try {
            initializeTestRunMessage("getIndexTestData");
            upsertOnTestRunMessage("indexName", "Not a real index");
            Object result = runFlowAndGetPayload("clean-index");
            fail("Error should be thrown cleaning an invalid index");
        } catch (Exception e) {
            assertTrue(e.getCause() instanceof NullPointerException);
        }
    }
}
