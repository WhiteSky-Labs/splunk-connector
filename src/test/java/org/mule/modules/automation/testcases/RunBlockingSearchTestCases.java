/**
 *
 * (c) 2003-2012 MuleSoft, Inc. This software is protected under international
 * copyright law. All use of this software is subject to MuleSoft's Master
 * Subscription Agreement (or other Terms of Service) separately entered
 * into between you and MuleSoft. If such an agreement is not in
 * place, you may not use the software.
 */


package org.mule.modules.automation.testcases;

import com.splunk.Job;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class RunBlockingSearchTestCases
        extends SplunkTestParent {


    @Before
    public void setup() {
        initializeTestRunMessage("runBlockingSearchTestData");
    }

    @Category({
            RegressionTests.class,
            SmokeTests.class
    })
    @Test
    public void testRunBlockingSearch()
            throws Exception {
        Object result = runFlowAndGetPayload("run-blocking-search");
        Map<String, Object> eventResponse = (Map<String, Object>) result;
        assertNotNull(eventResponse);
        List<HashMap> events = (List<HashMap>) eventResponse.get("events");
        assertNotNull(events);
        assertTrue(events.size() > 0);
        Job job = (Job) eventResponse.get("job");
        assertEquals("DONE", job.getDispatchState());
        assertEquals(getTestRunMessageValue("searchQuery"), job.getSearch());
    }

}
