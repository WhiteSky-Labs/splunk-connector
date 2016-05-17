/**
 *
 * (c) 2016 WhiteSky Labs, Pty Ltd. This software is protected under international
 * copyright law. All use of this software is subject to WhiteSky Labs' Master
 * Subscription Agreement (or other Terms of Service) separately entered
 * into between you and WhiteSky Labs. If such an agreement is not in
 * place, you may not use the software.
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
