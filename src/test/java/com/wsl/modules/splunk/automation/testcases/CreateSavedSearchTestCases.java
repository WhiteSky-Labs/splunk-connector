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

public class CreateSavedSearchTestCases
        extends SplunkTestParent {

    boolean searchCreated = false;

//    @Before
//    public void setup() throws Exception {
//        initializeTestRunMessage("createSavedSearchTestData");
//    }
//
//    @After
//    public void tearDown() throws Exception {
//        if (searchCreated) {
//            runFlowAndGetPayload("delete-saved-search");
//        }
//    }
//
//    @Category({
//            RegressionTests.class,
//            SmokeTests.class
//    })
//    @Test
//    public void testCreateSavedSearch() {
//        try {
//            Object result = runFlowAndGetPayload("create-saved-search");
//            Map<String, Object> savedSearch = (Map<String, Object>) result;
//            assertEquals("full", savedSearch.get("display.events.list.drilldown"));
//            searchCreated = true;
//        } catch (Exception e) {
//            fail(ConnectorTestUtils.getStackTrace(e));
//        }
//    }
//
//    @Category({
//            RegressionTests.class
//    })
//    @Test
//    public void testCreateExistingSavedSearch() {
//        try {
//            Object result = runFlowAndGetPayload("create-saved-search");
//            searchCreated = true;
//            result = runFlowAndGetPayload("create-saved-search");
//            fail("Exception should be thrown when creating an existing saved search");
//        } catch (MessagingException me) {
//            assertTrue(me.getCause().getMessage().contains("A saved search with that name already exists."));
//        } catch (Exception e){
//            fail(ConnectorTestUtils.getStackTrace(e));
//        }
//    }
//
//    @Category({
//            RegressionTests.class
//    })
//    @Test
//    public void testCreateSavedSearchWithEmptyName() {
//        try {
//            upsertOnTestRunMessage("searchName", "");
//            Object result = runFlowAndGetPayload("create-saved-search");
//            fail("Exception should be thrown when using an empty name to create a Saved Search");
//        } catch (MessagingException me) {
//            assertEquals("Search Name empty.", me.getCause().getMessage());
//        } catch (Exception e){
//            fail(ConnectorTestUtils.getStackTrace(e));
//        }
//    }
//
//    @Category({
//            RegressionTests.class
//    })
//    @Test
//    public void testCreateSavedSearchWithNullName() {
//        try {
//            upsertOnTestRunMessage("searchName", null);
//            Object result = runFlowAndGetPayload("create-saved-search");
//            fail("Exception should be thrown when using an null name to create a Saved Search");
//        } catch (MessagingException me) {
//            assertEquals("Search Name empty.", me.getCause().getMessage());
//        } catch (Exception e){
//            fail(ConnectorTestUtils.getStackTrace(e));
//        }
//    }


}
