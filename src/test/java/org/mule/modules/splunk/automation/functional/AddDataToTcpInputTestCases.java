/**
 * (c) 2018 WhiteSky Labs, Pty Ltd. The software in this package is published under the terms of the CPAL v1.0 license,
 * a copy of which has been included with this distribution in the LICENSE.md file.
 */
package org.mule.modules.splunk.automation.functional;

import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mule.modules.splunk.exception.SplunkConnectorException;

import com.splunk.InputKind;

public class AddDataToTcpInputTestCases extends SplunkAbstractTestCases {

    @Before
    public void setup() throws SplunkConnectorException {
        getConnector().createInput("9988", InputKind.Tcp, null);
    }

    @After
    public void tearDown() {
        getConnector().removeInput("9988");
    }

    @Test
    public void testAddDataToTcpInput() {
        boolean result = getConnector().addDataToTcpInput("9988",
                "addDataToTcpInputTestDataString");
        assertTrue(result);
    }
}
