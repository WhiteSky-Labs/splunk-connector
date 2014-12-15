/**
 *
 * (c) 2003-2015 MuleSoft, Inc. This software is protected under international
 * copyright law. All use of this software is subject to MuleSoft's Master
 * Subscription Agreement (or other Terms of Service) separately entered
 * into between you and MuleSoft. If such an agreement is not in
 * place, you may not use the software.
 */


package com.wsl.modules.splunk.automation.testcases;

import com.wsl.modules.splunk.automation.RegressionTests;
import com.wsl.modules.splunk.automation.SmokeTests;
import com.wsl.modules.splunk.automation.SplunkTestParent;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.modules.tests.ConnectorTestUtils;

import java.util.List;
import java.util.Map;

import static com.yourkit.util.Asserts.assertTrue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class GetJobsTestCases
        extends SplunkTestParent {


    @Before
    public void setup() throws Exception {
        initializeTestRunMessage("getJobsTestData");
    }

    @Category({
            RegressionTests.class,
            SmokeTests.class
    })
    @Test
    public void testGetJobs() {
        try {
            Object result = runFlowAndGetPayload("get-jobs");
            assertNotNull(result);
            List<Map<String, Object>> jobList = (List<Map<String, Object>>) result;
            assertTrue(jobList.size() > 0);
        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        }
    }

}
