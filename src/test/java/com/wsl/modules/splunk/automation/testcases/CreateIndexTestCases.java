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
//import org.mule.modules.tests.ConnectorTestUtils;

import java.util.Map;

import static org.junit.Assert.*;

public class CreateIndexTestCases extends SplunkTestParent {

    private Map<String, Object> expectedBean;

//    @Before
//    public void setup() throws Exception{
//        expectedBean = getBeanFromContext("createIndexTestData");
//    }
//
//    @Category({
//            RegressionTests.class,
//            SmokeTests.class
//    })
//    @Test
//    public void testCreateIndex() {
//        try {
//            initializeTestRunMessage("createIndexTestData");
//            Object result = runFlowAndGetPayload("create-index");
//            assertNotNull(result);
//            Map<String, Object> index = (Map<String, Object>) result;
//            assertTrue(((String) index.get("homePath")).contains((String) expectedBean.get("indexName")));
//            tearDown();
//        } catch (Exception e) {
//            fail(ConnectorTestUtils.getStackTrace(e));
//        }
//    }
//
//    @Category({
//            RegressionTests.class
//    })
//    @Test
//    public void testCreateIndexWithArgs() {
//        try {
//            initializeTestRunMessage("createIndexWithArgsTestData");
//            Object result = runFlowAndGetPayload("create-index");
//            assertNotNull(result);
//            Map<String, Object> index = (Map<String, Object>) result;
//            assertTrue(((String) index.get("homePath")).contains((String) expectedBean.get("indexName")));
//            tearDown();
//        } catch (Exception e) {
//            fail(ConnectorTestUtils.getStackTrace(e));
//        }
//    }
//
//    @Category({
//            RegressionTests.class
//    })
//    @Test
//    public void testCreateIndexWithInvalidArgs() {
//        try {
//            initializeTestRunMessage("createIndexWithInvalidArgsTestData");
//            runFlowAndGetPayload("create-index");
//            fail("Error should be thrown with invalid args");
//        } catch (MessagingException me){
//            assertTrue(me.getCause().getMessage().contains("is not supported by this handler"));
//        } catch (Exception e) {
//            fail(ConnectorTestUtils.getStackTrace(e));
//        }
//    }
//
//    private void tearDown() throws Exception {
//        runFlowAndGetPayload("remove-index");
//    }

}