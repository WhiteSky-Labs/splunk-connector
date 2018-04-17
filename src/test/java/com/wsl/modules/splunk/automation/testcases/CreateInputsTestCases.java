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
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.modules.tests.ConnectorTestUtils;

import java.util.Map;

import static org.junit.Assert.*;

public class CreateInputsTestCases extends SplunkTestParent {

    private String monitor = "/tmp";

    @Category({
            RegressionTests.class,
            SmokeTests.class
    })
    @Test
    public void testCreateTcpRawInput() {
        try {
            initializeTestRunMessage("createInputTestData");
            upsertOnTestRunMessage("kind", InputKind.Tcp);
            Object result = runFlowAndGetPayload("create-input");
            assertNotNull(result);
            Map<String, Object> input = (Map<String, Object>) result;
            assertEquals("default", input.get("index"));
        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        } finally {
            tearDown();
        }
    }

    @Category({
            RegressionTests.class
    })
    @Test
    public void testCreateTcpRawInputWithArgs() {
        try {
            initializeTestRunMessage("createInputWithArgsTestData");
            upsertOnTestRunMessage("kind", InputKind.Tcp);
            Object result = runFlowAndGetPayload("create-input");
            assertNotNull(result);
            Map<String, Object> input = (Map<String, Object>) result;
            assertEquals("summary", input.get("index"));
        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        } finally {
            tearDown();
        }
    }

    @Category({
            RegressionTests.class,
            SmokeTests.class
    })
    @Test
    public void testCreateTcpCookedInput() {
        try {
            initializeTestRunMessage("createInputTestData");
            upsertOnTestRunMessage("kind", InputKind.TcpSplunk);
            Object result = runFlowAndGetPayload("create-input");
            assertNotNull(result);
            Map<String, Object> input = (Map<String, Object>) result;
            assertEquals("default", input.get("index"));
        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        } finally {
            tearDown();
        }
    }

    @Category({
            RegressionTests.class,
            SmokeTests.class
    })
    @Test
    public void testCreateUdpInput() {
        try {
            initializeTestRunMessage("createInputTestData");
            upsertOnTestRunMessage("kind", InputKind.Udp);
            Object result = runFlowAndGetPayload("create-input");
            assertNotNull(result);
            Map<String, Object> input = (Map<String, Object>) result;
            assertEquals("default", input.get("index"));
        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        } finally {
            tearDown();
        }
    }

    @Category({
            RegressionTests.class
    })
    @Test
    public void testCreateUdpInputWithArgs() {
        try {
            initializeTestRunMessage("createInputWithArgsTestData");
            upsertOnTestRunMessage("kind", InputKind.Udp);
            Object result = runFlowAndGetPayload("create-input");
            assertNotNull(result);
            Map<String, Object> input = (Map<String, Object>) result;
            assertEquals("summary", input.get("index"));
        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        } finally {
            tearDown();
        }
    }

    @Category({
            RegressionTests.class,
            SmokeTests.class
    })
    @Test
    public void testCreateMonitorInput() {
        try {
            initializeTestRunMessage("createInputTestData");
            upsertOnTestRunMessage("inputIdentifier", monitor);
            upsertOnTestRunMessage("kind", InputKind.Monitor);
            Object result = runFlowAndGetPayload("create-input");
            assertNotNull(result);
            Map<String, Object> input = (Map<String, Object>) result;
            assertEquals("default", input.get("index"));
        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        } finally {
            tearDown();
        }
    }

    @Category({
            RegressionTests.class
    })
    @Test
    public void testCreateMonitorInputWithArgs() {
        try {
            initializeTestRunMessage("createInputWithArgsTestData");
            upsertOnTestRunMessage("inputIdentifier", monitor);
            upsertOnTestRunMessage("kind", InputKind.Monitor);
            Object result = runFlowAndGetPayload("create-input");
            assertNotNull(result);
            Map<String, Object> input = (Map<String, Object>) result;
            assertEquals("summary", input.get("index"));
        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        } finally {
            tearDown();
        }
    }

    /**
     * Teardown values depend on input type so are invoked manually.
     *
     */
    private void tearDown(){
        try {
            runFlowAndGetPayload("remove-input");
        } catch (Exception e) {
            ConnectorTestUtils.getStackTrace(e);
        }
    }

}
