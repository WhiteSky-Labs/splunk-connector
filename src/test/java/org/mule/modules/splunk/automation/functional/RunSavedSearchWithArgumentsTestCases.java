/**
 *
 * (c) 2016 WhiteSky Labs, Pty Ltd. This software is protected under international
 * copyright law. All use of this software is subject to WhiteSky Labs' Master
 * Subscription Agreement (or other Terms of Service) separately entered
 * into between you and WhiteSky Labs. If such an agreement is not in
 * place, you may not use the software.
 */
package org.mule.modules.splunk.automation.functional;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.mule.modules.splunk.exception.SplunkConnectorException;

import com.splunk.SavedSearchDispatchArgs;

public class RunSavedSearchWithArgumentsTestCases extends SplunkAbstractTestCases {

    @Rule
    public Timeout globalTimeout = new Timeout(200000);

    private static final String SEARCH_NAME = "run_saved_search_with_arguments_test_search";
    private SavedSearchDispatchArgs searchDispatchArgs;

    @Before
    public void setup() throws SplunkConnectorException {
        Map<String, Object> args = new HashMap<>();
        args.put("dispatch.earliest_time", "-20h");
        args.put("dispatch.latest_time", "now");
        getConnector().createSavedSearch(SEARCH_NAME, "search * | head 100", args);

        searchDispatchArgs = new SavedSearchDispatchArgs();
        searchDispatchArgs.put("dispatch.earliest_time", "-20h");
        searchDispatchArgs.put("dispatch.latest_time", "now");
    }

    @After
    public void tearDown() {
        getConnector().deleteSavedSearch(SEARCH_NAME);
    }

    @Test
    public void testRunSavedSearchWithArguments() {

        try {
            Map<String, Object> customArgs = new HashMap<>();
            customArgs.put("mysourcetype", "*");
            List<Map<String, Object>> result = getConnector().runSavedSearchWithArguments(SEARCH_NAME, customArgs, searchDispatchArgs);
            assertNotNull(result);
        } catch (Exception e) {
            fail("Exception not expected: " + e.getMessage());
        }
    }

    @Test
    public void testRunSavedSearchWithInvalidArguments() {
        try {
            // invalid custom arguments are ignored by saved searches, should be
            // successful
            Map<String, Object> customArgs = new HashMap<>();
            customArgs.put("invalid", "invalid");
            List<Map<String, Object>> result = getConnector().runSavedSearchWithArguments(SEARCH_NAME, customArgs, searchDispatchArgs);
            assertNotNull(result);
        } catch (Exception e) {
            fail("Exception not expected: " + e.getMessage());
        }
    }

}
