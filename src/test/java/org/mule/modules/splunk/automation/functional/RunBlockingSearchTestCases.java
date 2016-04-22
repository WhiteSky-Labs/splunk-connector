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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.mule.modules.splunk.exception.SplunkConnectorException;

import com.splunk.Job;

public class RunBlockingSearchTestCases extends SplunkAbstractTestCase {

    @SuppressWarnings("unchecked")
    @Test
    public void testRunBlockingSearch() {
        try {
            Map<String, Object> result = getConnector().runBlockingSearch(
                    "search * | head 100", null);
            assertNotNull(result);
            List<Map<String, Object>> events = (List<Map<String, Object>>) result
                    .get("events");
            assertTrue(events.size() > 0);
            Job job = (Job) result.get("job");
            assertEquals("DONE", job.getDispatchState());
            assertEquals("search * | head 100", job.getSearch());
        } catch (Exception e) {
            fail("Exception not expected: " + e.getMessage());
        }
    }

    @Test
    public void testRunBlockingSearchWithInvalidQuery() {
        try {
            getConnector().runBlockingSearch("invalid search query", null);
            fail("Exception should be thrown when attempting an invalid search query");
        } catch (SplunkConnectorException sce) {
            assertTrue(sce.getMessage()
                    .contains("Unknown search command"));
        } catch (Exception e) {
            fail("Exception type not expected: " + e.getMessage());
        }
    }

    @Test
    public void testRunBlockingSearchWithEmptyQuery() {
        try {
            getConnector().runBlockingSearch("", null);
            fail("Exception should be thrown when attempting an invalid search query");
        } catch (SplunkConnectorException sce) {
            assertEquals("Search Query is empty.", sce.getMessage());
        } catch (Exception e) {
            fail("Exception type not expected: " + e.getMessage());
        }
    }

    @Test
    public void testRunBlockingSearchWithNullQuery() {
        try {
            getConnector().runBlockingSearch(null, null);
            fail("Exception should be thrown when attempting an invalid search query");
        } catch (SplunkConnectorException sce) {
            assertEquals("Search Query is empty.", sce.getMessage());
        } catch (Exception e) {
            fail("Exception type not expected: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testRunBlockingSearchWithValidSearchArgs() {
        try {
            Map<String, Object> args = new HashMap<>();
            args.put("timeout", "60");
            args.put("auto_pause", "0");
            Map<String, Object> result = getConnector().runBlockingSearch(
                    "search * | head 100", args);
            assertNotNull(result);
            assertTrue(result.size() > 0);
            List<Map<String, Object>> events = ((List<Map<String, Object>>) result
                    .get("events"));
            assertTrue(events.size() > 0);
            Job job = (Job) result.get("job");
            assertEquals("DONE", job.getDispatchState());
            assertEquals("search * | head 100", job.getSearch());
        } catch (Exception e) {
            fail("Exception not expected: " + e.getMessage());
        }
    }
}
