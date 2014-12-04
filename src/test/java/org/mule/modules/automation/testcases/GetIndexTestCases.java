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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.modules.automation.RegressionTests;
import org.mule.modules.automation.SmokeTests;
import org.mule.modules.automation.SplunkTestParent;
import org.mule.modules.tests.ConnectorTestUtils;
import org.mule.transport.NullPayload;

import static org.junit.Assert.*;

public class GetIndexTestCases extends SplunkTestParent {

    private String indexName = "get_index_testing";

    @Before
    public void setup() {
        try {
            initializeTestRunMessage("createIndexTestData");
            upsertOnTestRunMessage("indexName", indexName);

            Object result = runFlowAndGetPayload("create-index");
        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        }
    }

    @After
    public void tearDown() {
        try {
            initializeTestRunMessage("removeIndexTestData");
            upsertOnTestRunMessage("indexName", indexName);
            Object result = runFlowAndGetPayload("remove-index");
        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        }
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
            Index index = (Index) result;
            assertEquals(indexName, index.getName());
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
            assertTrue(result instanceof NullPayload);
        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        }
    }
}
