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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Map;

import org.junit.After;
import org.junit.Test;
import org.mule.modules.splunk.exception.SplunkConnectorException;

public class CreateSavedSearchTestCases extends SplunkAbstractTestCases {

    private static final String SEARCH_NAME = "create_saved_search_test_search";
    private boolean doTearDown = false;

    @After
    public void tearDown() {
        if (doTearDown) {
            getConnector().deleteSavedSearch(SEARCH_NAME);
        }
    }

    @Test
    public void testCreateSavedSearch() {
        try {
            Map<String, Object> result = getConnector().createSavedSearch(SEARCH_NAME, "search * | head 100", null);
            assertEquals("full", result.get("display.events.list.drilldown"));
            doTearDown = true;
        } catch (Exception e) {
            fail("Exception not expected: " + e.getMessage());
        }
    }

    @Test
    public void testCreateExistingSavedSearch() {
        try {
            getConnector().createSavedSearch(SEARCH_NAME, "search * | head 100", null);
            doTearDown = true;
            getConnector().createSavedSearch(SEARCH_NAME, "search * | head 100", null);
            fail("Exception should be thrown when creating an existing saved search");
        } catch (SplunkConnectorException sce) {
            assertTrue(sce.getMessage()
                    .contains("A saved search with that name already exists."));
        } catch (Exception e) {
            fail("Exception type not expected: " + e.getMessage());
        }
    }

    @Test
    public void testCreateSavedSearchWithEmptyName() {
        try {
            getConnector().createSavedSearch("", "search * | head 100", null);
            fail("Exception should be thrown when using an empty name to create a Saved Search");
        } catch (SplunkConnectorException sce) {
            assertEquals("Search Name empty.", sce.getMessage());
        } catch (Exception e) {
            fail("Exception type not expected: " + e.getMessage());
        }
    }

    @Test
    public void testCreateSavedSearchWithNullName() {
        try {
            getConnector().createSavedSearch(null, "search * | head 100", null);
            fail("Exception should be thrown when using an empty name to create a Saved Search");
        } catch (SplunkConnectorException sce) {
            assertEquals("Search Name empty.", sce.getMessage());
        } catch (Exception e) {
            fail("Exception type not expected: " + e.getMessage());
        }
    }

}
