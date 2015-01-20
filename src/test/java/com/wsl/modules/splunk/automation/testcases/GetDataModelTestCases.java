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
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.modules.tests.ConnectorTestUtils;

import java.util.Map;

import static org.junit.Assert.*;

public class GetDataModelTestCases
        extends SplunkTestParent {


    @Before
    public void setup() throws Exception {
        initializeTestRunMessage("getDataModelTestData");
    }

    @Category({
            RegressionTests.class,
            SmokeTests.class
    })
    @Test
    public void testGetDataModel() {
        try {
            Object result = runFlowAndGetPayload("get-data-model");
            assertNotNull(result);
            Map<String, Object> dataModel = (Map<String, Object>) result;
            assertEquals("internal_audit_logs", dataModel.get("modelName"));
        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        }
    }

    @Category({
            RegressionTests.class
    })
    @Test
    public void testGetDataModelWithEmptyName() {
        try {
            upsertOnTestRunMessage("dataModelName", "");
            Object result = runFlowAndGetPayload("get-data-model");
            // expected result is a null payload.
            fail("InvalidArgumentException should be thrown");
        } catch (Exception e) {
            assertEquals("You must provide a data model name", e.getCause().getMessage());
        }
    }

    @Category({
            RegressionTests.class
    })
    @Test
    public void testGetDataModelWithNullName() {
        try {
            upsertOnTestRunMessage("dataModelName", null);
            Object result = runFlowAndGetPayload("get-data-model");
            // expected result is a null payload.
            fail("InvalidArgumentException should be thrown");
        } catch (Exception e) {
            assertEquals("You must provide a data model name", e.getCause().getMessage());
        }
    }
}
