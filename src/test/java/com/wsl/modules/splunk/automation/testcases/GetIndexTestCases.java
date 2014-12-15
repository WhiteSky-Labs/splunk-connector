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

public class GetIndexTestCases extends SplunkTestParent {

    private String indexName = "get_index_testing";

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
    public void testGetIndex() {
        try {
            initializeTestRunMessage("getIndexTestData");
            upsertOnTestRunMessage("indexIdentifier", indexName);
            Object result = runFlowAndGetPayload("get-index");
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
    public void testGetInvalidIndex() {
        try {
            initializeTestRunMessage("getIndexTestData");
            upsertOnTestRunMessage("indexIdentifier", "Not a real index");
            Object result = runFlowAndGetPayload("get-index");
            fail("Should throw exception for invalid index name");
        } catch (Exception e) {
            assertTrue(e.getCause().getMessage().contains("You must provide a valid index name"));
        }
    }
}
