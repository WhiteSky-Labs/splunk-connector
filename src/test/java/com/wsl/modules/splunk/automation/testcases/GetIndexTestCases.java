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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.api.MessagingException;
//import org.mule.modules.tests.ConnectorTestUtils;

import java.util.Map;

import static org.junit.Assert.*;

public class GetIndexTestCases extends SplunkTestParent {

    private Map<String, Object> expectedBean;

//    @Before
//    public void setup() throws Exception {
//        initializeTestRunMessage("getIndexTestData");
//        expectedBean = getBeanFromContext("getIndexTestData");
//        runFlowAndGetPayload("create-index");
//    }
//
//    @After
//    public void tearDown() throws Exception {
//        upsertOnTestRunMessage("indexIdentifier", expectedBean.get("indexIdentifier"));
//        runFlowAndGetPayload("remove-index");
//    }
//
//    @Category({
//            RegressionTests.class,
//            SmokeTests.class
//    })
//    @Test
//    public void testGetIndex() {
//        try {
//            Object result = runFlowAndGetPayload("get-index");
//            assertNotNull(result);
//            Map<String, Object> index = (Map<String, Object>) result;
//            assertTrue(((String) index.get("homePath")).contains((String) expectedBean.get("indexName")));
//        } catch (Exception e) {
//            fail(ConnectorTestUtils.getStackTrace(e));
//        }
//    }
//
//    @Category({
//            RegressionTests.class
//    })
//    @Test
//    public void testGetInvalidIndex() {
//        try {
//            upsertOnTestRunMessage("indexIdentifier", "Not a real index");
//            Object result = runFlowAndGetPayload("get-index");
//            fail("Should throw exception for invalid index name");
//        } catch (MessagingException me) {
//            assertTrue(me.getCause().getMessage().contains("You must provide a valid index name"));
//        } catch (Exception e){
//            fail(ConnectorTestUtils.getStackTrace(e));
//        }
//    }
}
