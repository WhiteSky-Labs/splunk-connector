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
import org.mule.modules.tests.ConnectorTestUtils;

import static org.junit.Assert.*;

public class DeleteSavedSearchTestCases
        extends SplunkTestParent {

    @Before
    public void setup() throws Exception {
            initializeTestRunMessage("deleteSavedSearchTestData");
    }

    @Category({
            RegressionTests.class,
            SmokeTests.class
    })
    @Test
    public void testDeleteSavedSearch() {
        try {
            runFlowAndGetPayload("create-saved-search");
            Object result = runFlowAndGetPayload("delete-saved-search");
            assertNotNull(result);
            assertEquals(true, result);
        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        }
    }

    @Category({
            RegressionTests.class
    })
    @Test
    public void testDeleteSavedSearchWithEmptyName() {
        try {
            upsertOnTestRunMessage("searchName", "");
            Object result = runFlowAndGetPayload("delete-saved-search");
            fail("Exception should be thrown when using an empty name to delete a Saved Search");
        } catch (MessagingException me) {
            assertEquals(null, me.getCause().getMessage());
        } catch (Exception e){
            fail(ConnectorTestUtils.getStackTrace(e));
        }
    }

    @Category({
            RegressionTests.class
    })
    @Test
    public void testDeleteSavedSearchWithNullName() {
        try {
            initializeTestRunMessage("deleteSavedSearchTestData");
            upsertOnTestRunMessage("searchName", null);
            runFlowAndGetPayload("delete-saved-search");
            fail("Exception should be thrown when using an null name to delete a Saved Search");
        } catch (MessagingException me) {
            assertEquals(null, me.getCause().getMessage());
        } catch (Exception e){
            fail(ConnectorTestUtils.getStackTrace(e));
        }
    }


}
