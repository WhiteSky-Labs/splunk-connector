/**
 *
 * (c) 2003-2012 MuleSoft, Inc. This software is protected under international
 * copyright law. All use of this software is subject to MuleSoft's Master
 * Subscription Agreement (or other Terms of Service) separately entered
 * into between you and MuleSoft. If such an agreement is not in
 * place, you may not use the software.
 */


package org.mule.modules.automation.testcases;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ViewSavedSearchPropertiesTestCases
        extends SplunkTestParent {

    private String searchName;
    private String searchQuery;

    @Before
    public void setup() throws Exception {
        initializeTestRunMessage("createSavedSearchTestData");
        searchName = getTestRunMessageValue("searchName");
        runFlowAndGetPayload("create-saved-search");
        searchQuery = getTestRunMessageValue("searchQuery");
        initializeTestRunMessage("viewSavedSearchPropertiesTestData");
    }

    @After
    public void tearDown() throws Exception {
        upsertOnTestRunMessage("searchName", searchName);
        runFlowAndGetPayload("delete-saved-search");
    }

    @Category({
            RegressionTests.class,
            SmokeTests.class
    })
    @Test
    public void testViewSavedSearchProperties()
            throws Exception {
        upsertOnTestRunMessage("searchName", searchName);
        Object result = runFlowAndGetPayload("view-saved-search-properties");
        assertNotNull(result);
        Set<Map.Entry<String, Object>> searchProperties = (Set<Map.Entry<String, Object>>) result;
        for (Map.Entry<String, Object> property : searchProperties) {
            if (property.getKey() == "search") {
                assertEquals(searchQuery, property.getValue());
            }
        }
    }

}
