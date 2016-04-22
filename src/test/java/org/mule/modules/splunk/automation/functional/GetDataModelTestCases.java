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

import java.util.Map;

import org.junit.Test;
import org.mule.modules.splunk.exception.SplunkConnectorException;

public class GetDataModelTestCases extends SplunkAbstractTestCase {

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
