/**
 *
 * (c) 2016 WhiteSky Labs, Pty Ltd. This software is protected under international
 * copyright law. All use of this software is subject to WhiteSky Labs' Master
 * Subscription Agreement (or other Terms of Service) separately entered
 * into between you and WhiteSky Labs. If such an agreement is not in
 * place, you may not use the software.
 */
package org.mule.modules.splunk.automation.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Test;
import org.mule.modules.splunk.exception.SplunkConnectorException;

import com.splunk.InputKind;

public class CreateInputsTestCases extends SplunkAbstractTestCases {

    private String inputIdentifier = "";

    @After
    public void tearDown() {
        try {
            getConnector().removeInput(inputIdentifier);
        } catch (Exception e) {
            fail("Cleanup failed with exception: " + e.getMessage());
        }
    }

    @Test
    public void testCreateTcpRawInput() {

        inputIdentifier = "9293";
        Map<String, Object> result;
        try {
            result = getConnector().createInput(inputIdentifier, InputKind.Tcp, null);
            assertNotNull(result);
            assertEquals("default", result.get("index"));
        } catch (SplunkConnectorException e) {
            fail("Exception not expected: " + e.getMessage());
        }
    }

    @Test
    public void testCreateTcpRawInputWithArgs() {
        try {
            Map<String, Object> args = new HashMap<>();
            args.put("index", "summary");
            inputIdentifier = "9293";
            Map<String, Object> result;
            result = getConnector().createInput(inputIdentifier, InputKind.Tcp, args);
            assertNotNull(result);
            assertEquals("summary", result.get("index"));
        } catch (SplunkConnectorException e) {
            fail("Exception not expected: " + e.getMessage());
        }
    }

    @Test
    public void testCreateTcpCookedInput() {
        try {
            inputIdentifier = "9293";
            Map<String, Object> result = getConnector().createInput(inputIdentifier, InputKind.TcpSplunk, null);
            assertNotNull(result);
            assertEquals("default", result.get("index"));
        } catch (SplunkConnectorException e) {
            fail("Exception not expected: " + e.getMessage());
        }
    }

    @Test
    public void testCreateUdpInput() {
        try {
            inputIdentifier = "9293";
            Map<String, Object> result = getConnector().createInput(inputIdentifier, InputKind.Udp, null);
            assertNotNull(result);
            assertEquals("default", result.get("index"));
        } catch (SplunkConnectorException e) {
            fail("Exception not expected: " + e.getMessage());
        }
    }

    @Test
    public void testCreateUdpInputWithArgs() {
        try {
            Map<String, Object> args = new HashMap<>();
            args.put("index", "summary");
            inputIdentifier = "9293";
            Map<String, Object> result = getConnector().createInput(inputIdentifier, InputKind.Udp, args);
            assertNotNull(result);
            assertEquals("summary", result.get("index"));
        } catch (SplunkConnectorException e) {
            fail("Exception not expected: " + e.getMessage());
        }
    }

    @Test
    public void testCreateMonitorInput() {
        try {
            inputIdentifier = "/tmp";
            Map<String, Object> result = getConnector().createInput(inputIdentifier, InputKind.Monitor, null);
            assertNotNull(result);
            assertEquals("default", result.get("index"));
        } catch (SplunkConnectorException e) {
            fail("Exception not expected: " + e.getMessage());
        }
    }

    @Test
    public void testCreateMonitorInputWithArgs() {
        try {
            Map<String, Object> args = new HashMap<>();
            args.put("index", "summary");
            inputIdentifier = "/tmp";
            Map<String, Object> result = getConnector().createInput(inputIdentifier, InputKind.Monitor, args);
            assertNotNull(result);
            assertEquals("summary", result.get("index"));
        } catch (SplunkConnectorException e) {
            fail("Exception not expected: " + e.getMessage());
        }
    }

}
