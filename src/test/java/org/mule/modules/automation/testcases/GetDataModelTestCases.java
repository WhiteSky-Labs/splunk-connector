/**
 *
 * (c) 2003-2012 MuleSoft, Inc. This software is protected under international
 * copyright law. All use of this software is subject to MuleSoft's Master
 * Subscription Agreement (or other Terms of Service) separately entered
 * into between you and MuleSoft. If such an agreement is not in
 * place, you may not use the software.
 */


package org.mule.modules.automation.testcases;

import com.splunk.DataModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.modules.automation.RegressionTests;
import org.mule.modules.automation.SmokeTests;
import org.mule.modules.automation.SplunkTestParent;
import org.mule.modules.tests.ConnectorTestUtils;

import static org.junit.Assert.*;

public class GetDataModelTestCases
        extends SplunkTestParent {


    @Before
    public void setup() {
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
            DataModel dataModel = (DataModel) result;
            assertEquals(getTestRunMessageValue("dataModelName"), dataModel.getName());
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
            assertEquals("{NullPayload}", result.toString());
        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
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
            assertEquals("{NullPayload}", result.toString());
        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        }
    }

}
