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

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mule.modules.splunk.exception.SplunkConnectorException;

public class ModifySavedSearchPropertiesTestCases extends SplunkAbstractTestCases {

    private static final String SEARCH_NAME = "modify_saved_search_test_search";
    private Map<String, Object> props;

    @Before
    public void setup() throws SplunkConnectorException {
        props = new HashMap<>();
        getConnector().createSavedSearch(SEARCH_NAME, "search * | head 100", null);
    }

    @After
    public void tearDown() {
        getConnector().deleteSavedSearch(SEARCH_NAME);
    }

    @Test
    public void testModifySavedSearchProperties() {
        props.put("description", "Sample Description");
        props.put("is_scheduled", "true");
        props.put("cron_schedule", "15 4 * * 6");
        try {
            Map<String, Object> result = getConnector().modifySavedSearchProperties(SEARCH_NAME, props);
            assertNotNull(result);
            assertEquals("list", result.get("display.events.type"));
            assertEquals(props.get("cron_schedule"), result.get("cron_schedule"));
        } catch (Exception e) {
            fail("Exception not expected: " + e.getMessage());
        }
    }

    @Test
    public void testModifySearchPropertiesWithEmptyProperties() {
        try {
            getConnector().modifySavedSearchProperties(SEARCH_NAME, props);
            fail("Should throw exception when modifying invalid properties");
        } catch (SplunkConnectorException sce) {
            assertEquals("You must provide some properties to modify", sce.getMessage());
        } catch (Exception e) {
            fail("Exception type not expected: " + e.getMessage());
        }
    }

}
