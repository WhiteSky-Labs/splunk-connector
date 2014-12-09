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
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.modules.tests.ConnectorTestUtils;

import java.util.Map;

import static org.junit.Assert.*;

public class CreateInputsTestCases extends SplunkTestParent {

    private String tcpRawPort = "9293";
    private String tcpCookedPort = "9112";
    private String udpPort = "9113";
    private String monitor = "/tmp";

    @Category({
            RegressionTests.class,
            SmokeTests.class
    })
    @Test
    public void testCreateTcpRawInput() {
        try {
            initializeTestRunMessage("createInputTestData");
            upsertOnTestRunMessage("inputIdentifier", tcpRawPort);
            upsertOnTestRunMessage("kind", InputKind.Tcp);
            Object result = runFlowAndGetPayload("create-input");
            assertNotNull(result);
            Map<String, Object> input = (Map<String, Object>) result;
            assertEquals("default", input.get("index"));
            tearDown(tcpRawPort);
        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        } finally {
            tearDown(tcpRawPort);
        }
    }

    @Category({
            RegressionTests.class
    })
    @Test
    public void testCreateTcpRawInputWithArgs() {
        try {
            initializeTestRunMessage("createInputWithArgsTestData");
            upsertOnTestRunMessage("inputIdentifier", tcpRawPort);
            upsertOnTestRunMessage("kind", InputKind.Tcp);
            Object result = runFlowAndGetPayload("create-input");
            assertNotNull(result);
            Map<String, Object> input = (Map<String, Object>) result;
            assertEquals("summary", input.get("index"));
        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        } finally {
            tearDown(tcpRawPort);
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
            upsertOnTestRunMessage("inputIdentifier", tcpCookedPort);
            upsertOnTestRunMessage("kind", InputKind.TcpSplunk);
            Object result = runFlowAndGetPayload("create-input");
            assertNotNull(result);
            Map<String, Object> input = (Map<String, Object>) result;
            assertEquals("default", input.get("index"));
        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        } finally {
            tearDown(tcpCookedPort);
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
            upsertOnTestRunMessage("inputIdentifier", udpPort);
            upsertOnTestRunMessage("kind", InputKind.Udp);
            Object result = runFlowAndGetPayload("create-input");
            assertNotNull(result);
            Map<String, Object> input = (Map<String, Object>) result;
            assertEquals("default", input.get("index"));
        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        } finally {
            tearDown(udpPort);
        }
    }

    @Category({
            RegressionTests.class
    })
    @Test
    public void testCreateUdpInputWithArgs() {
        try {
            initializeTestRunMessage("createInputWithArgsTestData");
            upsertOnTestRunMessage("inputIdentifier", udpPort);
            upsertOnTestRunMessage("kind", InputKind.Udp);
            Object result = runFlowAndGetPayload("create-input");
            assertNotNull(result);
            Map<String, Object> input = (Map<String, Object>) result;
            assertEquals("summary", input.get("index"));
            tearDown(udpPort);
        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        } finally {
            tearDown(udpPort);
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
            tearDown(monitor);
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
            tearDown(monitor);
        }
    }

    /**
     * Teardown values depend on input type so are invoked manually.
     *
     * @param inputIdentifier the Input Identifier
     * @throws Exception When there is a problem running the flow.
     */
    private void tearDown(String inputIdentifier) {
        try {
            upsertOnTestRunMessage("inputIdentifier", inputIdentifier);
            Object removedResult = runFlowAndGetPayload("remove-input");
        } catch (Exception e) {
            ConnectorTestUtils.getStackTrace(e);
        }
    }

}
