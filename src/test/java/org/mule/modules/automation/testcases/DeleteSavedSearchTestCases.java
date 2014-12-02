/**
 *
 * (c) 2003-2012 MuleSoft, Inc. This software is protected under international
 * copyright law. All use of this software is subject to MuleSoft's Master
 * Subscription Agreement (or other Terms of Service) separately entered
 * into between you and MuleSoft. If such an agreement is not in
 * place, you may not use the software.
 */


package org.mule.modules.automation.testcases;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.modules.automation.RegressionTests;
import org.mule.modules.automation.SmokeTests;
import org.mule.modules.automation.SplunkTestParent;
import org.mule.modules.tests.ConnectorTestUtils;

import static org.junit.Assert.*;

public class DeleteSavedSearchTestCases
        extends SplunkTestParent {

    private String searchName = "";

    @Before
    public void setup() {
        try {
            initializeTestRunMessage("createSavedSearchTestData");
            Object result = runFlowAndGetPayload("create-saved-search");
            searchName = getTestRunMessageValue("searchName");
            initializeTestRunMessage("deleteSavedSearchTestData");
        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        }
    }

    @Category({
            RegressionTests.class,
            SmokeTests.class
    })
    @Test
    public void testDeleteSavedSearch() {
        try {
            upsertOnTestRunMessage("searchName", searchName);
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
        } catch (Exception e) {
            assertEquals(null, e.getCause().getMessage());
        }
    }

    @Category({
            RegressionTests.class
    })
    @Test
    public void testDeleteSavedSearchWithNullName() {
        try {
            upsertOnTestRunMessage("searchName", null);
            Object result = runFlowAndGetPayload("delete-saved-search");
            fail("Exception should be thrown when using an null name to delete a Saved Search");
        } catch (Exception e) {
            assertEquals(null, e.getCause().getMessage());
        }
    }


}
