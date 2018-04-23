/**
 * (c) 2018 WhiteSky Labs, Pty Ltd. The software in this package is published under the terms of the CPAL v1.0 license,
 * a copy of which has been included with this distribution in the LICENSE.md file.
 */
package org.mule.modules.splunk.automation.functional;

import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mule.modules.splunk.exception.SplunkConnectorException;

public class RunNormalSearchTestCases extends SplunkAbstractTestCases {

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
