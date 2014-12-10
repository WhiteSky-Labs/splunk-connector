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
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.modules.tests.ConnectorTestUtils;

import java.util.Map;

import static org.junit.Assert.*;

public class AddDataToIndexTestCases extends SplunkTestParent {

    private String indexName = "add_data_to_index_testing";

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
    public void testAddDataToIndex() {
        try {
            initializeTestRunMessage("addDataToIndexTestData");
            upsertOnTestRunMessage("indexName", indexName);
            Object result = runFlowAndGetPayload("add-data-to-index");
            assertNotNull(result);
            Map<String, Object> index = (Map<String, Object>) result;
            assertEquals("auto", index.get("bucketRebuildMemoryHint"));
        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        }
    }

    @Category({
            RegressionTests.class
    })
    @Test
    public void testAddDataToIndexWithArgs() {
        try {
            initializeTestRunMessage("addDataToIndexWithArgsTestData");
            upsertOnTestRunMessage("indexName", indexName);
            Object result = runFlowAndGetPayload("add-data-to-index");
            assertNotNull(result);
            Map<String, Object> index = (Map<String, Object>) result;
            assertEquals("auto", index.get("bucketRebuildMemoryHint"));
        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        }
    }


}