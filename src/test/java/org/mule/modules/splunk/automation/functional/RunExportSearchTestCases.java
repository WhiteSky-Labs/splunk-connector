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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class RunExportSearchTestCases extends SplunkAbstractTestCase {

    @Before
    public void setup() throws Throwable {
        Object[] signature = { "search index=_internal | head 10", "-1h", "now", null };
        getDispatcher().initializeSource("runExportSearch", signature);
    }

    @After
    public void tearDown() throws Throwable {
        getDispatcher().shutDownSource("runExportSearch");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testRunExportSearch() {
        // allow time to produce messages
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }

        List<Object> events = getDispatcher().getSourceMessages("runExportSearch");
        assertNotNull(events);
    }
}
