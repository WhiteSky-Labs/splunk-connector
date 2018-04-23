/**
 * (c) 2018 WhiteSky Labs, Pty Ltd. The software in this package is published under the terms of the CPAL v1.0 license,
 * a copy of which has been included with this distribution in the LICENSE.md file.
 */
package org.mule.modules.splunk.automation.functional;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.mule.modules.splunk.exception.SplunkConnectorException;

public class RunOneShotSearchTestCases extends SplunkAbstractTestCases {

    @Test
    public void testRunOneShotSearch() {
        try {
            Map<String, String> args = new HashMap<String, String>();
            args.put("description", "Sample Description");
            List<Map<String, Object>> results = getConnector()
                    .runOneShotSearch("search index=_internal | head 10", "-10d", "now",
                            args);
            assertNotNull(results);
            assertTrue(results.size() > 0);
        } catch (Exception e) {
            fail("Exception not expected: " + e.getMessage());
        }
    }

    @Test
    public void testRunOneShotSearchWithInvalidQuery() {
        try {
            getConnector().runOneShotSearch("Invalid search query", "-10d",
                    "now", null);
            fail("An invalid search query should throw an exception");
        } catch (SplunkConnectorException sce) {
            assertTrue(sce.getMessage()
                    .contains("Unknown search command"));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception type not expected: " + e.getMessage());
        }
    }

    @Test
    public void testRunOneShotSearchWithInvalidSearchArgs() {
        try {
            Map<String, String> args = new HashMap<String, String>();
            args.put("invalid", "invalid value");
            List<Map<String, Object>> results = getConnector()
                    .runOneShotSearch("search index=_internal | head 10", "-10d", "now",
                            args);
            // invalid search args are ignored for a one-shot search, should
            // return successfully
            assertNotNull(results);
            assertTrue(results.size() > 0);
        } catch (Exception e) {
            fail("Exception not expected: " + e.getMessage());
        }
    }

    @Test
    public void testRunOneShotSearchWithoutSearchArgs() {
        try {
            List<Map<String, Object>> results = getConnector()
                    .runOneShotSearch("search index=_internal | head 10", "-10d", "now",
                            null);
            // missing search args are ignored for a one-shot search, should
            // return successfully
            assertNotNull(results);
            assertTrue(results.size() > 0);
        } catch (Exception e) {
            fail("Exception not expected: " + e.getMessage());
        }
    }

}
