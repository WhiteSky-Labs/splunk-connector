/**
 *
 * (c) 2003-2012 MuleSoft, Inc. This software is protected under international
 * copyright law. All use of this software is subject to MuleSoft's Master
 * Subscription Agreement (or other Terms of Service) separately entered
 * into between you and MuleSoft. If such an agreement is not in
 * place, you may not use the software.
 */


package org.mule.modules.automation.testcases;

import com.splunk.SearchResults;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.construct.Flow;

import java.util.List;

import static org.junit.Assert.assertNotNull;

public class RunExportSearchTestCases
        extends SplunkTestParent {


    @Before
    public void setup() {
        initializeTestRunMessage("runExportSearchTestData");
    }

    @Category({
            RegressionTests.class,
            SmokeTests.class
    })
    @Test
    public void testRunExportSearch()
            throws Exception {

        Flow flow = muleContext.getRegistry().get("run-export-search");
        flow.start();

        Object payload = muleContext.getClient().request("vm://receive", 100000).getPayload();


        /*Object result = runFlowAndWaitForResponseVM("run-export-search", "receive", 100000);


        */
        System.out.println(payload);
        assertNotNull(payload);
        List<SearchResults> results = (List<SearchResults>) payload;
        System.out.println(results);


        //List<Map<String, Object>> eventResponse = (List<Map<String, Object>>) result;
        //assertNotNull(eventResponse);
    }

}
