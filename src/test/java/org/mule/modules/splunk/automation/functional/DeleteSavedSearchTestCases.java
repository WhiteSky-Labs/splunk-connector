/**
 * (c) 2018 WhiteSky Labs, Pty Ltd. The software in this package is published under the terms of the CPAL v1.0 license,
 * a copy of which has been included with this distribution in the LICENSE.md file.
 */
package org.mule.modules.splunk.automation.functional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

public class DeleteSavedSearchTestCases extends SplunkAbstractTestCases {

    @Test
    public void testDeleteSavedSearch() {
        try {
            getConnector().createSavedSearch("delete_saved_search_test_search", "search * | head 100", null);

            boolean result = getConnector().deleteSavedSearch("delete_saved_search_test_search");
            assertTrue(result);
        } catch (Exception e) {
            fail("Exception not expected: " + e.getMessage());
        }
    }

    @Test
    public void testDeleteSavedSearchWithEmptyName() {
        boolean result = getConnector().deleteSavedSearch("");
        assertFalse(result);
    }

    @Test
    public void testDeleteSavedSearchWithNullName() {
        boolean result = getConnector().deleteSavedSearch("");
        assertFalse(result);
    }

}
