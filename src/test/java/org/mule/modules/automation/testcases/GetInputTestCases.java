/**
 *
 * (c) 2003-2012 MuleSoft, Inc. This software is protected under international
 * copyright law. All use of this software is subject to MuleSoft's Master
 * Subscription Agreement (or other Terms of Service) separately entered
 * into between you and MuleSoft. If such an agreement is not in
 * place, you may not use the software.
 */

package org.mule.modules.automation.testcases;

import com.splunk.InputKind;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.modules.automation.RegressionTests;
import org.mule.modules.automation.SmokeTests;
import org.mule.modules.automation.SplunkTestParent;
import org.mule.modules.tests.ConnectorTestUtils;

import java.util.Map;

import static org.junit.Assert.*;

public class GetInputTestCases extends SplunkTestParent {

    private String inputIdentifier = "9906";

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
    public void testGetInput() {
        try {
            initializeTestRunMessage("getInputTestData");
            upsertOnTestRunMessage("inputIdentifier", inputIdentifier);
            Object result = runFlowAndGetPayload("get-input");
            assertNotNull(result);
            Map<String, Object> input = (Map<String, Object>) result;
            assertEquals("default", input.get("index"));
        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        }
    }

    @Category({
            RegressionTests.class
    })
    @Test
    public void testGetInputWithInvalidIdentifier() {
        try {
            initializeTestRunMessage("getInputTestData");
            upsertOnTestRunMessage("inputIdentifier", "An Invalid Identifier");
            Object result = runFlowAndGetPayload("get-input");
            fail("Exception should be thrown for an invalid input identifier");
        } catch (Exception e) {
            assertEquals("You must provide a valid input identifier", e.getCause().getMessage());
        }
    }


}