/**
 * (c) 2018 WhiteSky Labs, Pty Ltd. The software in this package is published under the terms of the CPAL v1.0 license,
 * a copy of which has been included with this distribution in the LICENSE.md file.
 */
package org.mule.modules.splunk.automation.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mule.modules.splunk.exception.SplunkConnectorException;

import com.splunk.InputKind;

public class ModifyInputTestCases extends SplunkAbstractTestCases {

    private static final String INPUT_IDENTIFIER = "9908";
    private Map<String, Object> args;

    @Before
    public void setup() throws SplunkConnectorException {
        args = new HashMap<String, Object>();
        getConnector().createInput(INPUT_IDENTIFIER, InputKind.Tcp, args);
    }

    @After
    public void tearDown() {
        getConnector().removeInput(INPUT_IDENTIFIER);
    }

    @Test
    public void testModifyInput() {
        try {
            args.put("rawTcpDoneTimeout", "60");
            Map<String, Object> result = getConnector().modifyInput(INPUT_IDENTIFIER, args);
            assertNotNull(result);
            assertEquals("60", result.get("rawTcpDoneTimeout"));
        } catch (Exception e) {
            fail("Exception not expected: " + e.getMessage());
        }
    }

    @Test
    public void testModifyInputWithInvalidArgs() {
        try {
            args.put("invalid", "not valid");
            getConnector().modifyInput(INPUT_IDENTIFIER, args);
            fail("Error should be thrown when using invalid arguments");
        } catch (SplunkConnectorException sce) {
            assertTrue(sce.getMessage()
                    .contains("is not supported by this handler"));
        } catch (Exception e) {
            fail("Exception type not expected: " + e.getMessage());
        }
    }

}
