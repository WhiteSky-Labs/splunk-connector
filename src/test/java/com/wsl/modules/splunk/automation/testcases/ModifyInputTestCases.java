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
import org.mule.api.MessagingException;
import org.mule.modules.tests.ConnectorTestUtils;

import java.util.Map;

import static org.junit.Assert.*;

public class ModifyInputTestCases extends SplunkTestParent {

    private String inputIdentifier = "9908";

    @Before
    public void setup() throws Exception {
        initializeTestRunMessage("modifyInputTestData");
        upsertOnTestRunMessage("kind", InputKind.Tcp);
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
    public void testModifyInput() {
        try {
            Object result = runFlowAndGetPayload("modify-input");
            assertNotNull(result);
            Map<String, Object> input = (Map<String, Object>) result;
            assertEquals("60", input.get("rawTcpDoneTimeout"));
        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        }
    }

    @Category({
            RegressionTests.class
    })
    @Test
    public void testModifyInputWithInvalidArgs() {
        try {
            initializeTestRunMessage("modifyInputWithInvalidArgsTestData");
            runFlowAndGetPayload("modify-input");
            fail("Error should be thrown when using invalid arguments");
        } catch (MessagingException me) {
            assertTrue(me.getCause().getMessage().contains("is not supported by this handler"));
        } catch (Exception e){
            fail(ConnectorTestUtils.getStackTrace(e));
        }
    }

}
