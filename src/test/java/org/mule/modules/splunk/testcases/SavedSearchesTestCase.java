/**
 * (c) 2003-2014 MuleSoft, Inc. The software in this package is published under the terms of the CPAL v1.0 license,
 * a copy of which has been included with this distribution in the LICENSE.md file.
 **/
package org.mule.modules.splunk.testcases;

import com.splunk.SavedSearch;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.api.MuleEvent;
import org.mule.api.processor.MessageProcessor;
import org.mule.modules.splunk.SmokeTests;

import java.util.List;
import java.util.UUID;

import static junit.framework.Assert.*;

public class SavedSearchesTestCase extends SplunkTestParent {


    /**
     * Get All The Saved Searches
     *
     * @throws Exception
     */
    @Test
    @Category(SmokeTests.class)
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
     * Test to create a random Saved Searches
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
    @Category(SmokeTests.class)
    public void testListJobSavedSearchHistory() throws Exception {
        SavedSearch dummySavedSearch = createSavedSearch();
        testObjects.put("searchName", dummySavedSearch.getName());
        MessageProcessor flow = lookupFlowConstruct("testGetSavedSearchHistory");
        MuleEvent response = flow.process(getTestEvent(testObjects));
        assertNotNull(response.getMessage().getPayload());
    }

    /**
     * Test for non existing SavedSearch
     * @throws Exception
     */
    @Test(expected = org.mule.api.MessagingException.class)
    @Category(SmokeTests.class)
    public void testListJobNotExistSavedSearchHistory() throws Exception {
        testObjects.put("searchName", "Sample");
        MessageProcessor flow = lookupFlowConstruct("testGetSavedSearchHistory");
        MuleEvent response = flow.process(getTestEvent(testObjects));
        assertNull(response.getMessage().getPayload());
    }

    /**
     * Test Sample
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
     * Create a saved search
     *
     * @return
     * @throws Exception
     */
    private SavedSearch createSavedSearch() throws Exception {
        MessageProcessor flow = lookupFlowConstruct("testCreateSavedSearch");
        testObjects.put("searchName", UUID.randomUUID());
        testObjects.put("searchQuery", "* | head 10");
        MuleEvent response = flow.process(getTestEvent(testObjects));
        assertNotNull(response.getMessage().getPayload());
        SavedSearch savedSearch = (SavedSearch) response.getMessage().getPayload();
        return savedSearch;
    }



}
