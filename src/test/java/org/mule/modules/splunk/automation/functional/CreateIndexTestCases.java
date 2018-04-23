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
import org.junit.Test;
import org.mule.modules.splunk.exception.SplunkConnectorException;

public class CreateIndexTestCases extends SplunkAbstractTestCases {

    private static final String INDEX_NAME = "create_index_test_index";
    private boolean doTearDown = false;

    @After
    public void tearDown() {
        if (doTearDown) {
            try {
                getConnector().removeIndex(INDEX_NAME);
                Thread.sleep(5000);
            } catch (Exception e) {
                fail("Cleanup failed with exception: " + e.getMessage());
            }
        }
    }

    @Test
    public void testCreateIndex() {
        try {
            Map<String, Object> result = getConnector().createIndex(INDEX_NAME, null);
            doTearDown = true;
            assertNotNull(result);
            assertTrue(((String) result.get("homePath")).contains(INDEX_NAME));
        } catch (Exception e) {
            fail("Exception not expected: " + e.getMessage());
        }
    }

    @Test
    public void testCreateIndexWithArgs() {
        try {
            Map<String, Object> args = new HashMap<String, Object>();
            args.put("maxDataSize", "750");
            Map<String, Object> result = getConnector().createIndex(INDEX_NAME, args);
            doTearDown = true;
            assertNotNull(result);
            assertTrue(((String) result.get("homePath")).contains(INDEX_NAME));
        } catch (SplunkConnectorException e) {
            fail("Exception not expected: " + e.getMessage());
        }
    }

    @Test
    public void testCreateIndexWithInvalidArgs() {
        try {
            Map<String, Object> args = new HashMap<String, Object>();
            args.put("Invalid", "true");
            getConnector().createIndex(INDEX_NAME, args);
            fail("Error should be thrown with invalid args");
        } catch (SplunkConnectorException sce) {
            assertTrue(sce.getMessage()
                    .contains("is not supported by this handler"));
        } catch (Exception e) {
            fail("Exception type not expected: " + e.getMessage());
        }
    }

}