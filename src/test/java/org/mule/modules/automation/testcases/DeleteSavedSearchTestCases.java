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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DeleteSavedSearchTestCases
        extends SplunkTestParent {

    private String searchName = "";

    @Before
    public void setup() throws Exception {
        initializeTestRunMessage("createSavedSearchTestData");
        Object result = runFlowAndGetPayload("create-saved-search");
        searchName = getTestRunMessageValue("searchName");
        initializeTestRunMessage("deleteSavedSearchTestData");
    }

    @Category({
            RegressionTests.class,
            SmokeTests.class
    })
    @Test
    public void testDeleteSavedSearch()
            throws Exception {
        upsertOnTestRunMessage("searchName", searchName);
        Object result = runFlowAndGetPayload("delete-saved-search");
        assertNotNull(result);
        assertEquals(true, result);

    }

}
