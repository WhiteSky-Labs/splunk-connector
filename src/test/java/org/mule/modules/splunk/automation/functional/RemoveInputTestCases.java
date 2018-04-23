/**
 * (c) 2018 WhiteSky Labs, Pty Ltd. The software in this package is published under the terms of the CPAL v1.0 license,
 * a copy of which has been included with this distribution in the LICENSE.md file.
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
