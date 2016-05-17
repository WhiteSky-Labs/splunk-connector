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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.nio.file.Paths;
import java.util.Map;

import org.junit.Test;

import com.splunk.InputKind;

public class RemoveInputTestCases extends SplunkAbstractTestCases {

    private String inputIdentifier = "";

    @Test
    public void testRemoveInputMonitor() {
        try {
            inputIdentifier = Paths.get("").toAbsolutePath().toString();
            Map<String, Object> result = getConnector().createInput(inputIdentifier, InputKind.Monitor, null);
            assertNotNull(result);
            assertEquals("default", result.get("index"));

            assertTrue(getConnector().removeInput(inputIdentifier));
        } catch (Exception e) {
            fail("Exception not expected: " + e.getMessage());
        }
    }

    @Test
    public void testRemoveInputTcp() {
        try {
            inputIdentifier = "9293";
            Map<String, Object> result = getConnector().createInput(inputIdentifier, InputKind.Tcp, null);
            assertNotNull(result);
            assertEquals("default", result.get("index"));

            assertTrue(getConnector().removeInput(inputIdentifier));
        } catch (Exception e) {
            fail("Exception not expected: " + e.getMessage());
        }
    }

    @Test
    public void testRemoveInputUdp() {
        try {
            inputIdentifier = "9293";
            Map<String, Object> result = getConnector().createInput(inputIdentifier, InputKind.Udp, null);
            assertNotNull(result);
            assertEquals("default", result.get("index"));

            assertTrue(getConnector().removeInput(inputIdentifier));
        } catch (Exception e) {
            fail("Exception not expected: " + e.getMessage());
        }
    }

    @Test
    public void testRemoveInputThatDoesNotExist() {
        try {
            inputIdentifier = "9293";
            assertFalse(getConnector().removeInput(inputIdentifier));
        } catch (Exception e) {
            fail("Exception not expected: " + e.getMessage());
        }
    }
}
