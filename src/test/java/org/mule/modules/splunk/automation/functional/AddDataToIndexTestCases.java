/**
 * (c) 2018 WhiteSky Labs, Pty Ltd. The software in this package is published under the terms of the CPAL v1.0 license,
 * a copy of which has been included with this distribution in the LICENSE.md file.
 */
package org.mule.modules.splunk.automation.functional;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mule.modules.splunk.exception.SplunkConnectorException;

public class AddDataToIndexTestCases extends SplunkAbstractTestCases {

    private static final String INDEX_NAME = "add_data_to_index_test_index";

    @Before
    public void setup() throws SplunkConnectorException {
        getConnector().createIndex(INDEX_NAME, null);
    }

    @After
    public void tearDown() {
        getConnector().removeIndex(INDEX_NAME);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
        }
    }

    @Test
    public void testAddDataToIndex() {
        try {
            Map<String, Object> result = getConnector().addDataToIndex(INDEX_NAME, "addDataToIndexStringData", null);
            assertNotNull(result);
            assertTrue(((String) result.get("homePath")).contains(INDEX_NAME));
        } catch (SplunkConnectorException e) {
            fail("Exception not expected: " + e.getMessage());
        }
    }

    @Test
    public void testAddDataToIndexWithArgs() {
        try {
            Map<String, Object> args = new HashMap<String, Object>();
            args.put("maxDataSize", "750");
            Map<String, Object> result = getConnector().addDataToIndex(INDEX_NAME, "addDataToIndexStringData", args);
            assertNotNull(result);
            assertTrue(((String) result.get("homePath")).contains(INDEX_NAME));
        } catch (SplunkConnectorException e) {
            fail("Exception not expected: " + e.getMessage());
        }
    }

}