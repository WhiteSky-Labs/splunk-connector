/**
 *
 * (c) 2003-2012 MuleSoft, Inc. This software is protected under international
 * copyright law. All use of this software is subject to MuleSoft's Master
 * Subscription Agreement (or other Terms of Service) separately entered
 * into between you and MuleSoft. If such an agreement is not in
 * place, you may not use the software.
 */

package org.mule.modules.automation.testcases;

import com.splunk.Index;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.modules.automation.RegressionTests;
import org.mule.modules.automation.SmokeTests;
import org.mule.modules.automation.SplunkTestParent;
import org.mule.modules.tests.ConnectorTestUtils;

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
            Index index = (Index) result;
            assertEquals(indexName, index.getName());
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
            Index index = (Index) result;
            assertEquals(indexName, index.getName());
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