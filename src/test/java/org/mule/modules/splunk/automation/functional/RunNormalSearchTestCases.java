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

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mule.modules.splunk.exception.SplunkConnectorException;

public class RunNormalSearchTestCases extends SplunkAbstractTestCase {

    @Before
    public void setup() throws Throwable {
        Object[] signature = {
                "search index=_internal | head 10",
                null,
                null
        };
        getDispatcher().initializeSource("runNormalSearch", signature);
    }

    @After
    public void tearDown() throws Throwable {
        getDispatcher().shutDownSource("runNormalSearch");
    }

    @Test
    public void testRunNormalSearch() throws SplunkConnectorException {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ex) {
            Thread.currentThread()
                    .interrupt();
        }
        List<Object> events = getDispatcher().getSourceMessages("runNormalSearch");
        assertNotNull(events);
    }
}
