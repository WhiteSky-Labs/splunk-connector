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

import com.splunk.SavedSearch;
import com.splunk.SavedSearchDispatchArgs;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.api.MuleEvent;
import org.mule.api.processor.MessageProcessor;
import org.mule.modules.splunk.RegressionTests;
import org.mule.modules.splunk.SmokeTests;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static junit.framework.Assert.*;

public class SavedSearchesTestCase extends SplunkTestParent {


    /**
     * Get All The Saved Searches
     *
     * @throws Exception
     */
    @Test
    @Category({SmokeTests.class, RegressionTests.class})
    public void testGetAllSavedSearches() throws Exception {
        MessageProcessor flow = lookupFlowConstruct("testGetSavedSearches");
        MuleEvent response = flow.process(getTestEvent(null));
        assertNotNull(response.getMessage().getPayload());
        List<SavedSearch> savedSearchList = (List<SavedSearch>) response.getMessage().getPayload();
        for (SavedSearch savedSearch : savedSearchList) {
            assertTrue(savedSearch.getName().length() > 0);
        }
        assertTrue(savedSearchList.size() > 0);
    }

    /**
     * Get All The Saved Searches
     *
     * @throws Exception
     */
    @Test
    @Category(RegressionTests.class)
    public void testGetSavedSearchesWithNamespace() throws Exception {
        MessageProcessor flow = lookupFlowConstruct("testGetSavedSearches");
        testObjects.put("app", "search");
        testObjects.put("owner", "admin");
        MuleEvent response = flow.process(getTestEvent(testObjects));
        assertNotNull(response.getMessage().getPayload());
        List<SavedSearch> savedSearchList = (List<SavedSearch>) response.getMessage().getPayload();
        for (SavedSearch savedSearch : savedSearchList) {
            assertTrue(savedSearch.getName().length() > 0);
        }
        assertTrue(savedSearchList.size() > 0);
    }


    /**
     * Test for creating already exist saved search
     *
     * @throws Exception
     */
    @Test(expected = org.mule.api.MessagingException.class)
    @Category(SmokeTests.class)
    public void testCreateSavedSearchAlreadyExist() throws Exception {
        MessageProcessor flow = lookupFlowConstruct("testCreateSavedSearch");
        testObjects.put("searchName", "Test Search");
        testObjects.put("searchQuery", "* | head 10");
        MuleEvent response = flow.process(getTestEvent(testObjects));
        assertNotNull(response.getMessage().getPayload());
        SavedSearch savedSearch = (SavedSearch) response.getMessage().getPayload();
        logger.debug(savedSearch.getName());
        assertEquals(savedSearch.getName(), testObjects.get("searchName").toString());
    }

    /**
     * Test to create a random Saved Searche
     *
     * @throws Exception
     */
    @Test
    @Category(SmokeTests.class)
    public void testCreateSavedSearch() throws Exception {
        MessageProcessor flow = lookupFlowConstruct("testCreateSavedSearch");
        testObjects.put("searchName", UUID.randomUUID());
        testObjects.put("searchQuery", "* | head 10");
        MuleEvent response = flow.process(getTestEvent(testObjects));
        assertNotNull(response.getMessage().getPayload());
        SavedSearch savedSearch = (SavedSearch) response.getMessage().getPayload();
        assertEquals(savedSearch.getName(), testObjects.get("searchName").toString());
    }

    /**
     * Test to View Saved Search
     *
     * @throws Exception
     */
    @Test
    @Category(SmokeTests.class)
    public void testViewSavedSearch() throws Exception {
        SavedSearch dummySavedSearch = createSavedSearch();
        testObjects.put("searchName", dummySavedSearch.getName());
        MessageProcessor flow = lookupFlowConstruct("testViewSavedSearch");
        MuleEvent response = flow.process(getTestEvent(testObjects));
        assertNotNull(response.getMessage().getPayload());
        SavedSearch savedSearch = (SavedSearch) response.getMessage().getPayload();
        assertEquals(savedSearch.getName(), dummySavedSearch.getName());
    }

    /**
     * Test to modify the SavedSearch
     *
     * @throws Exception
     */
    @Test
    @Category(SmokeTests.class)
    public void testModifySavedSearch() throws Exception {
        SavedSearch dummySavedSearch = createSavedSearch();
        testObjects.put("searchName", dummySavedSearch.getName());
        testObjects.put("description", "Sample Description");
        testObjects.put("isSetScheduled", true);
        testObjects.put("cronSchedule", "15 4 * * 6");
        MessageProcessor flow = lookupFlowConstruct("testModifySavedSearch");
        MuleEvent response = flow.process(getTestEvent(testObjects));
        assertNotNull(response.getMessage().getPayload());
        SavedSearch savedSearch = (SavedSearch) response.getMessage().getPayload();
        assertEquals(savedSearch.getName(), dummySavedSearch.getName());
        assertEquals(savedSearch.getDescription(), testObjects.get("description"));
        assertEquals(savedSearch.isScheduled(), testObjects.get("isSetScheduled"));
        assertEquals(savedSearch.getCronSchedule(), testObjects.get("cronSchedule"));

    }

    /**
     * Test to List Job Search History
     *
     * @throws Exception
     */
    @Test
    @Category({SmokeTests.class, RegressionTests.class})
    public void testListJobSavedSearchHistory() throws Exception {
        SavedSearch dummySavedSearch = createSavedSearch();
        testObjects.put("searchName", dummySavedSearch.getName());
        MessageProcessor flow = lookupFlowConstruct("testGetSavedSearchHistory");
        MuleEvent response = flow.process(getTestEvent(testObjects));
        assertNotNull(response.getMessage().getPayload());
    }


    /**
     * Test for non existing SavedSearch
     *
     * @throws Exception
     */
    @Test(expected = org.mule.api.MessagingException.class)
    @Category({SmokeTests.class, RegressionTests.class})
    public void testListJobNotExistSavedSearchHistory() throws Exception {
        testObjects.put("searchName", "Sample");
        MessageProcessor flow = lookupFlowConstruct("testGetSavedSearchHistory");
        MuleEvent response = flow.process(getTestEvent(testObjects));
        assertNull(response.getMessage().getPayload());
    }

    /**
     * Test saved search history without a search name
     *
     * @throws Exception
     */
    @Test
    @Category(RegressionTests.class)
    public void testListAllSavedSearchHistory() throws Exception {
        MessageProcessor flow = lookupFlowConstruct("testGetSavedSearchHistory");
        MuleEvent response = flow.process(getTestEvent(testObjects));
        assertNotNull(response.getMessage().getPayload());
    }

    /**
     * Test to List Job Search with a namespace and a name
     *
     * @throws Exception
     */
    @Test
    @Category(RegressionTests.class)
    public void testListJobSavedSearchHistoryWithNamespace() throws Exception {
        SavedSearch dummySavedSearch = createSavedSearch();
        testObjects.put("searchName", dummySavedSearch.getName());
        testObjects.put("app", "search");
        testObjects.put("owner", "admin");
        MessageProcessor flow = lookupFlowConstruct("testGetSavedSearchHistory");
        MuleEvent response = flow.process(getTestEvent(testObjects));
        assertNotNull(response.getMessage().getPayload());
    }

    /**
     * Test to List Job Search History with namespace and no name
     *
     * @throws Exception
     */
    @Test
    @Category(RegressionTests.class)
    public void testListAllSavedSearchHistoryWithNamespace() throws Exception {
        SavedSearch dummySavedSearch = createSavedSearch();
        testObjects.put("app", "search");
        testObjects.put("owner", "admin");
        MessageProcessor flow = lookupFlowConstruct("testGetSavedSearchHistory");
        MuleEvent response = flow.process(getTestEvent(testObjects));
        assertNotNull(response.getMessage().getPayload());
    }


    /**
     * Test Sample
     *
     * @throws Exception
     */
    @Test(expected = org.mule.api.MessagingException.class)
    @Category(SmokeTests.class)
    public void test() throws Exception {
        testObjects.put("searchName", "Sample");
        MessageProcessor flow = lookupFlowConstruct("testGetSavedSearchHistory");
        MuleEvent response = flow.process(getTestEvent(testObjects));
        assertNull(response.getMessage().getPayload());
    }


    /**
     * Run a saved search
     *
     * @throws Exception
     */
    @Test
    @Category(SmokeTests.class)
    public void testRunSavedSearch() throws Exception {
        testObjects.put("searchName", createSavedSearch().getName());
        MessageProcessor flow = lookupFlowConstruct("testRunSavedSearch");
        MuleEvent response = flow.process(getTestEvent(testObjects));
        assertNotNull(response.getMessage().getPayload());
        List<Map<String, Object>> listResponse = (List<Map<String, Object>>) response.getMessage().getPayload();
        assertTrue(listResponse.size() > 0);
    }

    /**
     * Test for running saved search
     *
     * @throws Exception
     */
    @Test
    @Category(SmokeTests.class)
    public void testRunSavedSearchWithArgument() throws Exception {
        Map<String, Object> customArgs = new HashMap<String, Object>();
        SavedSearchDispatchArgs searchDispatchArgs = new SavedSearchDispatchArgs();
        customArgs.put("mysourcetype", "*");
        searchDispatchArgs.setDispatchEarliestTime("-20h@m");
        searchDispatchArgs.setDispatchLatestTime("now");

        testObjects.put("searchName", UUID.randomUUID());
        testObjects.put("searchQuery", "index=main");
        testObjects.put("customArgs", customArgs);
        testObjects.put("searchDispatchArgs", searchDispatchArgs);
        MessageProcessor flow = lookupFlowConstruct("testRunSavedSearchWithArgument");
        MuleEvent response = flow.process(getTestEvent(testObjects));
        assertNotNull(response.getMessage().getPayload());
        List<Map<String, Object>> listResponse = (List<Map<String, Object>>) response.getMessage().getPayload();
        assertTrue(listResponse.size() > 0);
    }

    /**
     * Test for deleting saved search
     *
     * @throws Exception
     */
    @Test
    @Category(SmokeTests.class)
    public void testDeleteSavedSearch() throws Exception {
        testObjects.put("searchName", createSavedSearch().getName());
        MessageProcessor flow = lookupFlowConstruct("testDeleteSavedSearch");
        MuleEvent response = flow.process(getTestEvent(testObjects));
        assertNotNull(response.getMessage().getPayload());
        //noinspection AssertEqualsBetweenInconvertibleTypes
        assertEquals(response.getMessage().getPayload(), true);

    }

    /**
     * Create a saved search
     *
     * @return The Saved Search pointer
     * @throws Exception
     */
    private SavedSearch createSavedSearch() throws Exception {
        MessageProcessor flow = lookupFlowConstruct("testCreateSavedSearch");
        testObjects.put("searchName", UUID.randomUUID());
        testObjects.put("searchQuery", ".db | head 100");
        MuleEvent response = flow.process(getTestEvent(testObjects));

        return (SavedSearch) response.getMessage().getPayload();
    }


}
