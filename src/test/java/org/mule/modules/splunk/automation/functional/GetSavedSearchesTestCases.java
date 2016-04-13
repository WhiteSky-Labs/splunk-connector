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

import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.mule.modules.splunk.exception.SplunkConnectorException;

public class GetSavedSearchesTestCases extends SplunkAbstractTestCase {

    @Rule
    public Timeout globalTimeout = new Timeout(200000);

    @Before
    public void setup() throws SplunkConnectorException {
        getConnector().createSavedSearch("get_saved_search_test_search", "search get_saved_search_test_search | head 100", null);
    }

    @After
    public void tearDown() {
        getConnector().deleteSavedSearch("get_saved_search_test_search");
    }

    @Test
    public void testGetSavedSearches() {
        List<Map<String, Object>> result = getConnector().getSavedSearches("search", "admin");
        assertNotNull(result);
        assertTrue(result.size() > 0);
        boolean foundSavedSearch = false;
        for (Map<String, Object> map : result) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                if (entry.getKey()
                        .equalsIgnoreCase("search")) {
                    if (((String) entry.getValue()).equalsIgnoreCase("search get_saved_search_test_search | head 100")) {
                        foundSavedSearch = true;
                    }
                }
            }
        }
        assertTrue(foundSavedSearch);
    }

}
