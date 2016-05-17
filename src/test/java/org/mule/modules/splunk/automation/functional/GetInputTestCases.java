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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Map;

import org.junit.Test;
import org.mule.modules.splunk.exception.SplunkConnectorException;

import com.splunk.InputKind;

public class GetInputTestCases extends SplunkAbstractTestCases {

    @Test
    public void testGetInput() throws SplunkConnectorException {
        // setup
        getConnector().createInput("9906", InputKind.Tcp, null);

        Map<String, Object> result = getConnector().getInput("9906");
        assertNotNull(result);
        assertEquals("default", result.get("index"));

        // teardown
        getConnector().removeInput("9906");
    }

    @Test
    public void testGetInputWithEmptyIdentifier() {
        try {
            getConnector().getInput("");
            fail("Exception should be thrown for an invalid input identifier");
        } catch (SplunkConnectorException sce) {
            assertEquals("You must provide a valid input identifier",
                    sce.getMessage());
        } catch (Exception e) {
            fail("Exception type not expected: " + e.getMessage());
        }
    }

    @Test
    public void testGetInputWithInvalidIdentifier() {
        try {
            assertTrue(getConnector().getInput("An Invalid Identifier")
                    .size() == 0);
        } catch (Exception e) {
            fail("Exception type not expected: " + e.getMessage());
        }
    }

}