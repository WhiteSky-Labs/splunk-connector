/**
 * (c) 2018 WhiteSky Labs, Pty Ltd. The software in this package is published under the terms of the CPAL v1.0 license,
 * a copy of which has been included with this distribution in the LICENSE.md file.
 */
package org.mule.modules.splunk.automation.functional;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mule.modules.splunk.exception.SplunkConnectorException;

//import org.mule.modules.tests.ConnectorTestUtils;

public class RunSavedSearchTestCases extends SplunkAbstractTestCases {

    private static final String SEARCH_NAME = "run_saved_search_test_search";

    @Before
    public void setup() throws Exception {
        getConnector().createSavedSearch(SEARCH_NAME, "search * | head 100",
                null);
    }

    @After
    public void tearDown() throws Exception {
        getConnector().deleteSavedSearch(SEARCH_NAME);
    }

    @Test
    public void testRunSavedSearch() {
        try {
            List<Map<String, Object>> result = getConnector().runSavedSearch(
                    SEARCH_NAME);
            assertNotNull(result);
        } catch (Exception e) {
            fail("Exception not expected: " + e.getMessage());
        }
    }

    @Test
    public void testRunMissingSavedSearch() {
        try {
            getConnector().runSavedSearch("Not a valid search name");
            fail("Running a saved search that doesn't exist should throw an error");
        } catch (Exception e) {
            assertTrue(e instanceof SplunkConnectorException);
        }
    }
}
