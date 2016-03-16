/**
 *
 * (c) 2015 WhiteSky Labs, Pty Ltd. This software is protected under international
 * copyright law. All use of this software is subject to WhiteSky Labs' Master
 * Subscription Agreement (or other Terms of Service) separately entered
 * into between you and WhiteSky Labs. If such an agreement is not in
 * place, you may not use the software.
 */

package org.mule.modules.splunk.automation.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mule.modules.splunk.exception.SplunkConnectorException;

public class ViewSavedSearchPropertiesTestCases extends SplunkAbstractTestCase {

    private static final String SEARCH_NAME = "view_saved_search_properties_test_search";

    @Before
    public void setup() throws SplunkConnectorException {
        getConnector().createSavedSearch(SEARCH_NAME, "search * | head 100", null);
    }

    @After
    public void tearDown() {
        getConnector().deleteSavedSearch(SEARCH_NAME);
    }

    @Test
    public void testViewSavedSearchProperties() {
        try {
            Set<Map.Entry<String, Object>> result = getConnector().viewSavedSearchProperties(SEARCH_NAME, "search", "admin");
            assertNotNull(result);
            for (Map.Entry<String, Object> property : result) {
                if (property.getKey().equalsIgnoreCase("search")) {
                    assertEquals("search * | head 100", property.getValue());
                }
            }
        } catch (Exception e) {
            fail("Exception not expected: " + e.getMessage());
        }
    }

    @Test
    public void testViewSavedSearchPropertiesForInvalidSavedSearch() {
        try {
            getConnector().viewSavedSearchProperties("Invalid Saved Search Name", "search", "admin");
            fail("Exception should be thrown when getting properties for an invalid saved search");
        } catch (NullPointerException e) {
            assertTrue(e instanceof NullPointerException);
        } catch (Exception e) {
            fail("Exception type not expected: " + e.getMessage());
        }
    }
}
