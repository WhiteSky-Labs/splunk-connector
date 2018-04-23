/**
 * (c) 2018 WhiteSky Labs, Pty Ltd. The software in this package is published under the terms of the CPAL v1.0 license,
 * a copy of which has been included with this distribution in the LICENSE.md file.
 */
package org.mule.modules.splunk.automation.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Map;

import org.junit.Test;
import org.mule.modules.splunk.exception.SplunkConnectorException;

public class GetDataModelTestCases extends SplunkAbstractTestCases {

    @Test
    public void testGetDataModel() throws SplunkConnectorException {
        Map<String, Object> result = getConnector().getDataModel(
                "internal_audit_logs");
        assertNotNull(result);
        assertEquals("internal_audit_logs", result.get("modelName"));
    }

    @Test
    public void testGetDataModelWithEmptyName() {
        try {
            getConnector().getDataModel("");
            fail("SplunkConnectorException should be thrown");
        } catch (SplunkConnectorException sce) {
            assertEquals("You must provide a data model name", sce.getMessage());
        } catch (Exception e) {
            fail("Exception type not expected: " + e.getMessage());
        }
    }

    @Test
    public void testGetDataModelWithNullName() {

        try {
            getConnector().getDataModel(null);
            fail("SplunkConnectorException should be thrown");
        } catch (SplunkConnectorException sce) {
            assertEquals("You must provide a data model name", sce.getMessage());
        } catch (Exception e) {
            fail("Exception type not expected: " + e.getMessage());
        }
    }
}
