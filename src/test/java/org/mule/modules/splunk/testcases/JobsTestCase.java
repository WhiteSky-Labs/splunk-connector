/**
 * (c) 2003-2014 MuleSoft, Inc. The software in this package is published under the terms of the CPAL v1.0 license,
 * a copy of which has been included with this distribution in the LICENSE.md file.
 **/
package org.mule.modules.splunk.testcases;


import com.splunk.Application;
import com.splunk.Job;
import com.splunk.JobArgs;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.api.MuleEvent;
import org.mule.api.processor.MessageProcessor;
import org.mule.modules.splunk.SmokeTests;

import java.util.Collection;
import java.util.List;

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
        //assertNull(response.getMessage().getPayload());
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


    @Test
    @Category(SmokeTests.class)
    public void testRunOneShotSearch() throws Exception {
        MessageProcessor flow = lookupFlowConstruct("testRunOneShotSearch");
        testObjects.put("searchQuery", "search * | head 100");
        testObjects.put("earliestTime", "2014-10-22T12:00:00.000-07:00");
        testObjects.put("latestTime", "2014-10-20T12:00:00.000-08:00");
        MuleEvent response = flow.process(getTestEvent(testObjects));

    }



}
