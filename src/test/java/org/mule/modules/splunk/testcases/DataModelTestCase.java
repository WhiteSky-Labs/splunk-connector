/**
 * (c) 2003-2014 MuleSoft, Inc. The software in this package is published under the terms of the CPAL v1.0 license,
 * a copy of which has been included with this distribution in the LICENSE.md file.
 **/
package org.mule.modules.splunk.testcases;


import com.splunk.Application;
import com.splunk.DataModel;
import com.splunk.DataModelObject;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.api.MuleEvent;
import org.mule.api.processor.MessageProcessor;
import org.mule.modules.splunk.SmokeTests;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

public class DataModelTestCase extends SplunkTestParent {


    /**
     * Test to get the data model
     *
     * @throws Exception
     */
    @Test
    @Category(SmokeTests.class)
    public void testGetDataModel() throws Exception {
        MessageProcessor flow = lookupFlowConstruct("testGetDataModel");
        String dataModelName = "internal_audit_logs";
        testObjects.put("dataModelName", dataModelName);
        MuleEvent response = flow.process(getTestEvent(testObjects));
        assertNotNull(response.getMessage().getPayload());
        DataModel dataModel = (DataModel) response.getMessage().getPayload();
        assertEquals(dataModel.getName(), dataModelName);

    }


}
