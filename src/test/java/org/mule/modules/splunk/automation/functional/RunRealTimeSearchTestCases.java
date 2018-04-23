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

public class RunRealTimeSearchTestCases extends SplunkAbstractTestCases {

    @Before
    public void setup() throws Throwable {
        Object[] signature = {
                "search index=_internal | head 10",
                "rt",
                "rt",
                300,
                100,
                null
        };
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
            Thread.currentThread()
                    .interrupt();
        }
        List<Object> events = getDispatcher().getSourceMessages("runRealTimeSearch");
        assertNotNull(events);
    }
}
