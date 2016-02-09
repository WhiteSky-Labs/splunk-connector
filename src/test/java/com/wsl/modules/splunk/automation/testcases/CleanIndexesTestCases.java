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

public class CleanIndexesTestCases extends SplunkTestParent {

    private Map<String, Object> expectedBean;

//    @Before
//    public void setup() throws Exception {
//        initializeTestRunMessage("cleanIndexTestData");
//        expectedBean = getBeanFromContext("cleanIndexTestData");
//        runFlowAndGetPayload("create-index");
//    }
//
//    @After
//    public void tearDown() throws Exception {
//        upsertOnTestRunMessage("indexName", expectedBean.get("indexName"));
//        Object result = runFlowAndGetPayload("remove-index");
//    }
//
//    @Category({
//            RegressionTests.class,
//            SmokeTests.class
//    })
//    @Test
//    public void testCleanIndex() {
//        try {
//            Object result = runFlowAndGetPayload("clean-index");
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
//    public void testCleanInvalidIndex() {
//        try {
//            upsertOnTestRunMessage("indexName", "Not a real index");
//            runFlowAndGetPayload("clean-index");
//            fail("Error should be thrown cleaning an invalid index");
//        } catch (MessagingException me){
//            assertTrue(me.getCause() instanceof NullPointerException);
//        } catch (Exception e) {
//            fail(ConnectorTestUtils.getStackTrace(e));
//        }
//    }
}
