/**
 *
 * (c) 2003-2012 MuleSoft, Inc. This software is protected under international
 * copyright law. All use of this software is subject to MuleSoft's Master
 * Subscription Agreement (or other Terms of Service) separately entered
 * into between you and MuleSoft. If such an agreement is not in
 * place, you may not use the software.
 */


package org.mule.modules.automation.testcases;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class RunOneShotSearchTestCases
        extends SplunkTestParent {


    @Before
    public void setup() {
        initializeTestRunMessage("runOneShotSearchTestData");
    }

    @Category({
            RegressionTests.class,
            SmokeTests.class
    })
    @Test
    public void testRunOneShotSearch()
            throws Exception {
        Object result = runFlowAndGetPayload("run-one-shot-search");
        List<Map<String, Object>> searchResults = (List<Map<String, Object>>) result;
        assertNotNull(searchResults);
        assertTrue(searchResults.size() > 0);
    }

}
