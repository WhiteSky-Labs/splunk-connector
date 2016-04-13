/**
 *
 * (c) 2015 WhiteSky Labs, Pty Ltd. This software is protected under international
 * copyright law. All use of this software is subject to WhiteSky Labs' Master
 * Subscription Agreement (or other Terms of Service) separately entered
 * into between you and WhiteSky Labs. If such an agreement is not in
 * place, you may not use the software.
 */

package org.mule.modules.splunk.automation.functional;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.mule.modules.splunk.exception.SplunkConnectorException;

public class GetSavedSearchHistoryTestCases extends SplunkAbstractTestCase {

    private static final String SEARCH_NAME = "get_saved_search_history";

    @Rule
    public Timeout globalTimeout = new Timeout(200000);

    @Before
    public void setup() {
        try {
            getConnector().createSavedSearch(SEARCH_NAME,
                    "search * get_saved_search_history | head 100", null);
            getConnector().runSavedSearch(SEARCH_NAME);
        } catch (SplunkConnectorException e) {
            fail("Exception not expected: " + e.getMessage());
        }
    }

    @After
    public void tearDown() {
        getConnector().deleteSavedSearch(SEARCH_NAME);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
        }
    }

    @Test
    public void testGetSavedSearchHistory() {
        List<Map<String, Object>> result = getConnector()
                .getSavedSearchHistory(SEARCH_NAME, "search", "admin");
        assertNotNull(result);
        assertTrue(result.size() > 0);
        boolean foundSavedSearch = false;
        for (Map<String, Object> map : result) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                if (entry.getKey()
                        .equalsIgnoreCase("eventSearch")) {
                    if (((String) entry.getValue()).contains(SEARCH_NAME)) {
                        foundSavedSearch = true;
                    }
                }
            }
        }
        assertTrue(foundSavedSearch);
    }

}
