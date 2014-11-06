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


import com.splunk.Application;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.api.MuleEvent;
import org.mule.api.processor.MessageProcessor;
import org.mule.modules.splunk.SmokeTests;

import java.util.List;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

public class ApplicationsTestCase extends SplunkTestParent {


    /**
     * Test to get the current user Account Details
     *
     * @throws Exception
     */
    @Test
    @Category(SmokeTests.class)
    public void testGetApplications() throws Exception {
        MessageProcessor flow = lookupFlowConstruct("testGetApplications");
        MuleEvent response = flow.process(getTestEvent(null));
        assertNotNull(response.getMessage().getPayload());
        List<Application> applications = (List<Application>) response.getMessage().getPayload();
        assertTrue(applications.size() > 0);
    }


}
