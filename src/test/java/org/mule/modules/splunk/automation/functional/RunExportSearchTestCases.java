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

public class RunExportSearchTestCases extends SplunkAbstractTestCases {

    @Before
    public void setup() throws Throwable {
        Object[] signature = {
                "search index=_internal | head 10",
                "-1h",
                "now",
                null
        };
        getDispatcher().initializeSource("runExportSearch", signature);
    }

    @After
    public void tearDown() throws Throwable {
        getDispatcher().shutDownSource("runExportSearch");
    }

    @Test
    public void testRunExportSearch() {
        // allow time to produce messages
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ex) {
            Thread.currentThread()
                    .interrupt();
        }

        List<Object> events = getDispatcher().getSourceMessages("runExportSearch");
        assertNotNull(events);
    }
}
