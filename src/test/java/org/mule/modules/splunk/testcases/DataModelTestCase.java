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


import com.splunk.DataModel;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.api.MuleEvent;
import org.mule.api.processor.MessageProcessor;
import org.mule.modules.splunk.SmokeTests;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

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
