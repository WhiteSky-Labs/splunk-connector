/**
 *
 * (c) 2003-2012 MuleSoft, Inc. This software is protected under international
 * copyright law. All use of this software is subject to MuleSoft's Master
 * Subscription Agreement (or other Terms of Service) separately entered
 * into between you and MuleSoft. If such an agreement is not in
 * place, you may not use the software.
 */

package com.wsl.modules.splunk.automation.testcases;

import com.splunk.InputKind;
import com.wsl.modules.splunk.automation.RegressionTests;
import com.wsl.modules.splunk.automation.SmokeTests;
import com.wsl.modules.splunk.automation.SplunkTestParent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.modules.tests.ConnectorTestUtils;

import static org.junit.Assert.*;


public class AddDataToTcpInputTestCases extends SplunkTestParent {

    private String inputIdentifier = "9988";

    @Before
    public void setup() {
        try {
            initializeTestRunMessage("createInputTestData");
            upsertOnTestRunMessage("inputIdentifier", inputIdentifier);
            upsertOnTestRunMessage("kind", InputKind.Tcp);

            Object result = runFlowAndGetPayload("create-input");
        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        }
    }

    @After
    public void tearDown() {
        try {
            initializeTestRunMessage("removeInputTestData");
            upsertOnTestRunMessage("inputIdentifier", inputIdentifier);
            Object result = runFlowAndGetPayload("remove-input");
        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        }
    }

    @Category({
            RegressionTests.class,
            SmokeTests.class
    })
    @Test
    public void testAddDataToTcpInput() {
        try {
            initializeTestRunMessage("addDataToTcpInputTestData");
            upsertOnTestRunMessage("portNumber", inputIdentifier);
            Object result = runFlowAndGetPayload("add-data-to-tcp-input");
            assertNotNull(result);
            Boolean success = (Boolean) result;
            assertEquals(true, success);
        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        }
    }
}
