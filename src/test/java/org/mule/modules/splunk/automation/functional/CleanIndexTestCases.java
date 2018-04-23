/**
 * (c) 2018 WhiteSky Labs, Pty Ltd. The software in this package is published under the terms of the CPAL v1.0 license,
 * a copy of which has been included with this distribution in the LICENSE.md file.
 */
package org.mule.modules.splunk.automation.functional;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mule.modules.splunk.exception.SplunkConnectorException;

public class CleanIndexTestCases extends SplunkAbstractTestCases {

    private static final String INDEX_NAME = "clean_index_test_index";

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
    public void testCleanIndex() {
        try {
            Map<String, Object> index = getConnector().cleanIndex(INDEX_NAME, 180);
            assertTrue(((String) index.get("homePath")).contains(INDEX_NAME));
        } catch (Exception e) {
            fail("Exception not expected: " + e.getMessage());
        }
    }

    @Test
    public void testCleanInvalidIndex() {
        try {
            getConnector().cleanIndex("Not a real index", 180);
            fail("Error should be thrown cleaning an invalid index");
        } catch (SplunkConnectorException sce) {
            assertTrue(sce instanceof SplunkConnectorException);
        } catch (Exception e) {
            fail("Exception type not expected: " + e.getMessage());
        }
    }
}
