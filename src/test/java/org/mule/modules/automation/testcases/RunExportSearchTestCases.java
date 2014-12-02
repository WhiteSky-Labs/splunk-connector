/**
 *
 * (c) 2003-2012 MuleSoft, Inc. This software is protected under international
 * copyright law. All use of this software is subject to MuleSoft's Master
 * Subscription Agreement (or other Terms of Service) separately entered
 * into between you and MuleSoft. If such an agreement is not in
 * place, you may not use the software.
 */


package org.mule.modules.automation.testcases;

import com.splunk.ResultsReaderJson;
import com.splunk.SearchResults;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.construct.Flow;
import org.mule.modules.automation.RegressionTests;
import org.mule.modules.automation.SmokeTests;
import org.mule.modules.automation.SplunkTestParent;
import org.mule.modules.tests.ConnectorTestUtils;

import java.util.List;

import static org.junit.Assert.*;

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
    public void testRunExportSearch() {
        try {
            Flow flow = muleContext.getRegistry().get("run-export-search");
            flow.start();

            Object payload = muleContext.getClient().request("vm://receive", 100000).getPayload();

            assertNotNull(payload);
            List<SearchResults> results = (List<SearchResults>) payload;
            assertTrue(results.size() > 0);
            ResultsReaderJson resultsReader = (com.splunk.ResultsReaderJson) results.get(0);
            assertFalse(resultsReader.isPreview());
        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        }
    }

}
