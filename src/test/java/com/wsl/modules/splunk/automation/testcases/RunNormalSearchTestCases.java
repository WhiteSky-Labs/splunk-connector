/**
 *
 * (c) 2015 WhiteSky Labs, Pty Ltd. This software is protected under international
 * copyright law. All use of this software is subject to WhiteSky Labs' Master
 * Subscription Agreement (or other Terms of Service) separately entered
 * into between you and WhiteSky Labs. If such an agreement is not in
 * place, you may not use the software.
 */

package com.wsl.modules.splunk.automation.testcases;

import com.wsl.modules.splunk.automation.RegressionTests;
import com.wsl.modules.splunk.automation.SmokeTests;
import com.wsl.modules.splunk.automation.SplunkTestParent;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.construct.Flow;
//import org.mule.modules.tests.ConnectorTestUtils;

import java.util.HashMap;

import static org.junit.Assert.*;

public class RunNormalSearchTestCases extends SplunkTestParent {

//    @Before
//    public void setup() throws Exception {
//        initializeTestRunMessage("runNormalSearchTestData");
//    }
//
//    @Category({
//            RegressionTests.class,
//            SmokeTests.class
//    })
//    @Test
//    public void testRunExportSearch() {
//        try {
//            Flow flow = muleContext.getRegistry().get("run-normal-search");
//            flow.start();
//
//            Object payload = muleContext.getClient().request("vm://receive", 100000).getPayload();
//
//            assertNotNull(payload);
//            HashMap<String, Object> results = (HashMap<String, Object>) payload;
//            assertTrue(results.size() > 0);
//            assertNotNull(results.get("events"));
//            assertNotNull(results.get("job"));
//        } catch (Exception e) {
//            fail(ConnectorTestUtils.getStackTrace(e));
//        }
//    }

}
