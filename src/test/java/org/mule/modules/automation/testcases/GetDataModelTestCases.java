/**
 *
 * (c) 2003-2012 MuleSoft, Inc. This software is protected under international
 * copyright law. All use of this software is subject to MuleSoft's Master
 * Subscription Agreement (or other Terms of Service) separately entered
 * into between you and MuleSoft. If such an agreement is not in
 * place, you may not use the software.
 */


package org.mule.modules.automation.testcases;

import com.splunk.DataModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class GetDataModelTestCases
        extends SplunkTestParent {


    @Before
    public void setup() {
        initializeTestRunMessage("getDataModelTestData");
    }

    @Category({
            RegressionTests.class,
            SmokeTests.class
    })
    @Test
    public void testGetDataModel()
            throws Exception {
        Object result = runFlowAndGetPayload("get-data-model");
        assertNotNull(result);
        DataModel dataModel = (DataModel) result;
        assertEquals(getTestRunMessageValue("dataModelName"), dataModel.getName());
    }

}
