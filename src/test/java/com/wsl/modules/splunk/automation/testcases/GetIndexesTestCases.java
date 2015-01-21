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
import org.mule.api.MessagingException;
import org.mule.modules.tests.ConnectorTestUtils;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class GetIndexesTestCases extends SplunkTestParent {

    @Category({
            RegressionTests.class,
            SmokeTests.class
    })
    @Test
    public void testGetIndexes() {
        try {
            Object result = runFlowAndGetPayload("get-indexes");
            assertNotNull(result);
            List<Map<String, Object>> indexes = (List<Map<String, Object>>) result;
            assertTrue(indexes.size() > 0);
        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        }
    }

    @Category({
            RegressionTests.class
    })
    @Test
    public void testGetIndexesWithParametersTestData() {
        try {
            initializeTestRunMessage("getIndexesWithParametersTestData");
            Object result = runFlowAndGetPayload("get-indexes");
            assertNotNull(result);
            List<Map<String, Object>> indexes = (List<Map<String, Object>>) result;
            assertTrue(indexes.size() > 0);
        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        }
    }

    @Category({
            RegressionTests.class
    })
    @Test
    public void testGetIndexesWithInvalidParametersTestData() {
        try {
            initializeTestRunMessage("getIndexesWithInvalidParametersTestData");
            Object result = runFlowAndGetPayload("get-indexes");
            List<Map<String, Object>> indexes = (List<Map<String, Object>>) result;
            // attempt to instantiate
            assertTrue(indexes.toString() instanceof String);
            fail("Error should be thrown for invalid parameter");
        } catch (MessagingException me) {
            assertTrue(me.getCause().getMessage().contains("is not supported by this handler"));
        } catch (Exception e){
            fail(ConnectorTestUtils.getStackTrace(e));
        }
    }

}
