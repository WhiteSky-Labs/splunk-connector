/**
 * (c) 2003-2014 MuleSoft, Inc. The software in this package is published under the terms of the CPAL v1.0 license,
 * a copy of which has been included with this distribution in the LICENSE.md file.
 **/
package org.mule.modules.splunk.testcases;


import com.splunk.*;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.api.MuleEvent;
import org.mule.api.processor.MessageProcessor;
import org.mule.modules.splunk.SmokeTests;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

public class JobsTestCase extends SplunkTestParent {


    /**
     * Test to get the current user Account Details
     *
     * @throws Exception
     */
    @Test
    @Category(SmokeTests.class)
    public void testGetJobs() throws Exception {
        MessageProcessor flow = lookupFlowConstruct("testGetJobs");
        MuleEvent response = flow.process(getTestEvent(null));
        assertNotNull(response.getMessage().getPayload());
        List<Job> jobList = (List<Job>) response.getMessage().getPayload();
        assertNotNull(jobList);
    }

    /**
     * Test to run search
     *
     * @throws Exception
     */
    @Test
    @Category(SmokeTests.class)
    public void testRunSearchExecutionModeNormal() throws Exception {
        MessageProcessor flow = lookupFlowConstruct("testRunSearch");
        testObjects.put("searchQuery", "search * | head 100");
        testObjects.put("executionMode", JobArgs.ExecutionMode.NORMAL);
        MuleEvent response = flow.process(getTestEvent(testObjects));
        assertNotNull(response.getMessage().getPayload());
        Map<String, Object> eventResponse = (Map<String, Object>) response.getMessage().getPayload();
        assertNotNull(eventResponse);
    }

    /**
     * Test to run search
     *
     * @throws Exception
     */
    @Test
    @Category(SmokeTests.class)
    public void testRunSearchExecutionModeBlocking() throws Exception {
        MessageProcessor flow = lookupFlowConstruct("testRunSearch");
        testObjects.put("searchQuery", "search * | head 100");
        testObjects.put("executionMode", JobArgs.ExecutionMode.BLOCKING);
        MuleEvent response = flow.process(getTestEvent(testObjects));
        Map<String, Object> eventResponse = (Map<String, Object>) response.getMessage().getPayload();
        assertNotNull(eventResponse);
    }

    /**
     * Test to run search
     *
     * @throws Exception

     @Test
     @Category(SmokeTests.class)
     public void testRunSearchExecutionModeOneshot() throws Exception {
     MessageProcessor flow = lookupFlowConstruct("testRunSearch");
     testObjects.put("searchQuery", "search * | head 100");
     testObjects.put("executionMode", JobArgs.ExecutionMode.ONESHOT);
     MuleEvent response = flow.process(getTestEvent(testObjects));
     // assertNull(response.getMessage().getPayload());
     }
     */

    /**
     * Run one the shot search
     *
     * @throws Exception
     */
    @Test
    @Category(SmokeTests.class)
    public void testRunOneShotSearch() throws Exception {
        MessageProcessor flow = lookupFlowConstruct("testRunOneShotSearch");
        testObjects.put("searchQuery", "search index=_internal | head 10");
        testObjects.put("earliestTime", "-1h");
        testObjects.put("latestTime", "now");
        MuleEvent response = flow.process(getTestEvent(testObjects));
        assertNotNull(response.getMessage().getPayload());
        List<Map<String, Object>> mapList = (List<Map<String, Object>>) response.getMessage().getPayload();
        assertTrue(mapList.size() > 0);
    }

    /**
     * Test run export search
     * @throws Exception
     */
    @Test
    @Category(SmokeTests.class)
    public void testRunExportSearch() throws Exception {
        MessageProcessor flow = lookupFlowConstruct("testRunExportSearch");
        JobExportArgs exportArgs = new JobExportArgs();
        exportArgs.setEarliestTime("-1h");
        exportArgs.setLatestTime("now");
        exportArgs.setSearchMode(JobExportArgs.SearchMode.NORMAL);
        testObjects.put("searchQuery", "search index=_internal | head 10");
        testObjects.put("exportArgs", exportArgs);
        MuleEvent response = flow.process(getTestEvent(testObjects));
    }

    /**
     *  Run realtime search

     @Test
     @Category(SmokeTests.class)
     public void testRunRealtimeSearch() throws Exception {
     MessageProcessor flow = lookupFlowConstruct("testRunRealtimeSearch");
     String searchQuery = "search index=_internal";
     JobArgs jobArgs  = new JobArgs();
     jobArgs.setExecutionMode(JobArgs.ExecutionMode.NORMAL);
     jobArgs.setSearchMode(JobArgs.SearchMode.REALTIME);
     jobArgs.setEarliestTime("rt-1m");
     jobArgs.setLatestTime("rt");
     jobArgs.setStatusBuckets(300);

     JobResultsPreviewArgs previewArgs = new JobResultsPreviewArgs();
     previewArgs.setCount(300);

     MuleEvent response = flow.process(getTestEvent(null));

     }
     */


}
