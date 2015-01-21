/**
 *
 * (c) 2015 WhiteSky Labs, Pty Ltd. This software is protected under international
 * copyright law. All use of this software is subject to WhiteSky Labs' Master
 * Subscription Agreement (or other Terms of Service) separately entered
 * into between you and WhiteSky Labs. If such an agreement is not in
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

public class AddDataToUdpInputTestCases extends SplunkTestParent {

    @Before
    public void setup() throws Exception {
        initializeTestRunMessage("addDataToUdpInputTestData");
        upsertOnTestRunMessage("kind", InputKind.Udp);
        runFlowAndGetPayload("create-input");
    }

    @After
    public void tearDown() throws Exception {
        runFlowAndGetPayload("remove-input");
    }

    @Category({
            RegressionTests.class,
            SmokeTests.class
    })
    @Test
    public void testAddDataToUdpInput() {
        try {
            initializeTestRunMessage("addDataToUdpInputTestData");
            Object result = runFlowAndGetPayload("add-data-to-udp-input");
            assertNotNull(result);
            Boolean success = (Boolean) result;
            assertEquals(true, success);
        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        }
    }
}