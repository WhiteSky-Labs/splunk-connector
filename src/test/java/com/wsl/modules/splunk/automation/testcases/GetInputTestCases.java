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
//import org.mule.modules.tests.ConnectorTestUtils;

import java.util.Map;

import static org.junit.Assert.*;

public class GetInputTestCases extends SplunkTestParent {

    private Map<String, Object> expectedBean;

//    @Before
//    public void setup() throws Exception {
//        try {
//            initializeTestRunMessage("getInputTestData");
//            upsertOnTestRunMessage("kind", InputKind.Tcp);
//            expectedBean = getBeanFromContext("getInputTestData");
//            runFlowAndGetPayload("create-input");
//        } catch (Exception e) {
//            fail(ConnectorTestUtils.getStackTrace(e));
//        }
//    }
//
//    @After
//    public void tearDown() throws Exception {
//        try {
//            upsertOnTestRunMessage("inputIdentifier", expectedBean.get("inputIdentifier"));
//            runFlowAndGetPayload("remove-input");
//        } catch (Exception e) {
//            fail(ConnectorTestUtils.getStackTrace(e));
//        }
//    }
//
//    @Category({
//            RegressionTests.class,
//            SmokeTests.class
//    })
//    @Test
//    public void testGetInput() {
//        try {
//            Object result = runFlowAndGetPayload("get-input");
//            assertNotNull(result);
//            Map<String, Object> input = (Map<String, Object>) result;
//            assertEquals("default", input.get("index"));
//        } catch (Exception e) {
//            fail(ConnectorTestUtils.getStackTrace(e));
//        }
//    }
//
//    @Category({
//            RegressionTests.class
//    })
//    @Test
//    public void testGetInputWithInvalidIdentifier() {
//        try {
//            initializeTestRunMessage("getInputTestData");
//            upsertOnTestRunMessage("inputIdentifier", "An Invalid Identifier");
//            Object result = runFlowAndGetPayload("get-input");
//            fail("Exception should be thrown for an invalid input identifier");
//        } catch (Exception e) {
//            assertEquals("You must provide a valid input identifier", e.getCause().getMessage());
//        }
//    }


}