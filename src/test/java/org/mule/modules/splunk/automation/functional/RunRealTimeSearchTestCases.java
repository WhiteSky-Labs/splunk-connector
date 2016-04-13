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

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class RunRealTimeSearchTestCases extends SplunkAbstractTestCase {

    @Before
    public void setup() throws Throwable {
        Object[] signature = { "search index=_internal | head 10", "rt", "rt", 300, 100, null };
        getDispatcher().initializeSource("runRealTimeSearch", signature);
    }

    @After
    public void tearDown() throws Throwable {
        getDispatcher().shutDownSource("runRealTimeSearch");
    }

    @Test
    public void testRunRealTimeSearch() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        List<Object> events = getDispatcher().getSourceMessages("runRealTimeSearch");
        assertNotNull(events);
    }
}
