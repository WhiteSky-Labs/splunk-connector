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
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.modules.tests.ConnectorTestUtils;

import java.util.Map;

import static org.junit.Assert.*;

public class CreateIndexTestCases extends SplunkTestParent {

    private String indexName = "integration_testing_index";

    @Category({
            RegressionTests.class,
            SmokeTests.class
    })
    @Test
    public void testCreateIndex() {
        try {
            initializeTestRunMessage("createIndexTestData");
            upsertOnTestRunMessage("indexName", indexName);
            Object result = runFlowAndGetPayload("create-index");
            assertNotNull(result);
            Map<String, Object> index = (Map<String, Object>) result;
            assertEquals("main", index.get("defaultDatabase"));
            tearDown(indexName);
        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        }
    }

    @Category({
            RegressionTests.class
    })
    @Test
    public void testCreateIndexWithArgs() {
        try {
            initializeTestRunMessage("createIndexWithArgsTestData");
            upsertOnTestRunMessage("indexName", indexName);
            Object result = runFlowAndGetPayload("create-index");
            assertNotNull(result);
            Map<String, Object> index = (Map<String, Object>) result;
            assertEquals("main", index.get("defaultDatabase"));
            tearDown(indexName);
        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        }
    }

    @Category({
            RegressionTests.class
    })
    @Test
    public void testCreateIndexWithInvalidArgs() {
        try {
            initializeTestRunMessage("createIndexWithInvalidArgsTestData");
            upsertOnTestRunMessage("indexName", indexName);
            Object result = runFlowAndGetPayload("create-index");
            fail("Error should be thrown with invalid args");
        } catch (Exception e) {
            assertTrue(e.getCause().getMessage().contains("is not supported by this handler"));
        }
    }

    private void tearDown(String indexName) throws Exception {
        initializeTestRunMessage("removeIndexTestData");
        upsertOnTestRunMessage("indexName", indexName);
        Object removedResult = runFlowAndGetPayload("remove-index");
    }

}