/**
 *
 * (c) 2003-2012 MuleSoft, Inc. This software is protected under international
 * copyright law. All use of this software is subject to MuleSoft's Master
 * Subscription Agreement (or other Terms of Service) separately entered
 * into between you and MuleSoft. If such an agreement is not in
 * place, you may not use the software.
 */

/**
 * (c) 2003-2014 MuleSoft, Inc. The software in this package is published under the terms of the CPAL v1.0 license,
 * a copy of which has been included with this distribution in the LICENSE.md file.
 **/
package org.mule.modules.splunk.testcases;


import com.splunk.Job;
import com.splunk.JobExportArgs;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.api.MuleEvent;
import org.mule.api.processor.MessageProcessor;
import org.mule.modules.splunk.SmokeTests;

import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertNotNull;
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
        MessageProcessor flow = lookupFlowConstruct("testRunNormalSearch");
        testObjects.put("searchQuery", "search * | head 100");
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
    /*@Test
    @Category(SmokeTests.class)
    public void testRunSearchExecutionModeBlocking() throws Exception {
        MessageProcessor flow = lookupFlowConstruct("testRunSearch");
        testObjects.put("searchQuery", "search * | head 100");
        MuleEvent response = flow.process(getTestEvent(testObjects));
        Map<String, Object> eventResponse = (Map<String, Object>) response.getMessage().getPayload();
        assertNotNull(eventResponse);
    }*/

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
        List<Map<String, Object>> eventResponse = (List<Map<String, Object>>) response.getMessage().getPayload();
        assertNotNull(eventResponse);
    }

    /**
     *  Run realtime search

     */
    @Test
    @Category(SmokeTests.class)
    public void testRunRealTimeSearch() throws Exception {
        MessageProcessor flow = lookupFlowConstruct("testRunRealTimeSearch");
        String searchQuery = "search index=_internal";
        testObjects.put("searchQuery", searchQuery);
         /*JobArgs jobArgs  = new JobArgs();
         jobArgs.setStatusBuckets(300);

         JobResultsPreviewArgs previewArgs = new JobResultsPreviewArgs();
         previewArgs.setCount(300);*/

        MuleEvent response = flow.process(getTestEvent(testObjects));
        Map<String, Object> eventResponse = (Map<String, Object>) response.getMessage().getPayload();
        assertNotNull(eventResponse);
     }


}
