/**
 * (c) 2018 WhiteSky Labs, Pty Ltd. The software in this package is published under the terms of the CPAL v1.0 license,
 * a copy of which has been included with this distribution in the LICENSE.md file.
 */
package org.mule.modules.splunk.automation.functional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Map;

import org.junit.Test;

public class RemoveIndexTestCases extends SplunkAbstractTestCases {

    private static final String INDEX_NAME = "remove_index_test_index";

    @Test
    public void testRemoveIndex() {
        try {
            Map<String, Object> result = getConnector().createIndex(INDEX_NAME, null);
            assertNotNull(result);
            assertTrue(((String) result.get("homePath")).contains(INDEX_NAME));

            assertTrue(getConnector().removeIndex(INDEX_NAME));
        } catch (Exception e) {
            fail("Exception not expected: " + e.getMessage());
        }
    }

    @Test
    public void testRemoveIndexThatDoesNotExist() {
        try {
            assertFalse(getConnector().removeIndex(INDEX_NAME));
        } catch (Exception e) {
            fail("Exception not expected: " + e.getMessage());
        }
    }
}
