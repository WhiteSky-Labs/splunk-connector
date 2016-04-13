package org.mule.modules.splunk.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mule.modules.splunk.SplunkClient;

import com.splunk.Job;
import com.splunk.SavedSearch;
import com.splunk.SavedSearchCollection;
import com.splunk.SavedSearchDispatchArgs;
import com.splunk.Service;
import com.splunk.ServiceArgs;

public class SavedSearchServiceTest {

    private SavedSearchService service;

    @Mock
    Service clientService;
    @Mock
    SplunkClient client;
    @Mock
    SavedSearchCollection savedSearchCollection;
    @Mock
    SavedSearch savedSearch;
    @Mock
    Job job;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        this.service = spy(new SavedSearchService(client));
    }

    @Test
    public void testCreateSavedSearch() {
        Map<String, Object> args = new HashMap<>();
        args.put("testKey", "testValue");
        when(service.getService()).thenReturn(clientService);
        when(clientService.getSavedSearches()).thenReturn(savedSearchCollection);
        when(savedSearchCollection.create("searchName", "search index=_internal | head 10", args)).thenReturn(savedSearch);
        when(savedSearch.entrySet()).thenReturn(args.entrySet());
        Map<String, Object> result = service.createSavedSearch("searchName", "search index=_internal | head 10", args);
        assertEquals(1, result.size());
    }

    @Test
    public void testCreateSavedSearchWithNoArgs() {
        Map<String, Object> args = new HashMap<>();
        args.put("testKey", "testValue");
        when(service.getService()).thenReturn(clientService);
        when(clientService.getSavedSearches()).thenReturn(savedSearchCollection);
        when(savedSearchCollection.create("searchName", "search index=_internal | head 10")).thenReturn(savedSearch);
        when(savedSearch.entrySet()).thenReturn(args.entrySet());
        Map<String, Object> result = service.createSavedSearch("searchName", "search index=_internal | head 10", null);
        assertEquals(1, result.size());
    }

    @Test
    public void testDeleteSavedSearch() {
        when(service.getService()).thenReturn(clientService);
        when(clientService.getSavedSearches()).thenReturn(savedSearchCollection);
        when(savedSearchCollection.get("searchName")).thenReturn(savedSearch);
        doNothing().when(savedSearch).remove();
        assertTrue(service.deleteSavedSearch("searchName"));
    }

    @Test
    public void testDeleteSavedSearchUnsuccessful() {
        when(service.getService()).thenReturn(clientService);
        when(clientService.getSavedSearches()).thenReturn(savedSearchCollection);
        when(savedSearchCollection.get("searchName")).thenReturn(null);
        assertTrue(!service.deleteSavedSearch("searchName"));
    }

    @Test
    public void testGetSavedSearchesWithNameSpace() {
        Map<String, Object> entry = new HashMap<>();
        entry.put("testKey", "testValue");
        List<SavedSearch> savedSearchList = new ArrayList<>();
        savedSearchList.add(savedSearch);
        when(service.getService()).thenReturn(clientService);
        when(clientService.getSavedSearches(any(ServiceArgs.class))).thenReturn(savedSearchCollection);
        when(savedSearchCollection.values()).thenReturn(savedSearchList);
        when(savedSearch.entrySet()).thenReturn(entry.entrySet());
        List<Map<String, Object>> result = service.getSavedSearches("app", "owner");
        assertEquals(1, result.size());
    }

    @Test
    public void testGetSavedSearchesWithNoNameSpace() {
        Map<String, Object> entry = new HashMap<>();
        entry.put("testKey", "testValue");
        List<SavedSearch> savedSearchList = new ArrayList<>();
        savedSearchList.add(savedSearch);
        when(service.getService()).thenReturn(clientService);
        when(clientService.getSavedSearches(any(ServiceArgs.class))).thenReturn(savedSearchCollection);
        when(savedSearchCollection.values()).thenReturn(savedSearchList);
        when(savedSearch.entrySet()).thenReturn(entry.entrySet());
        List<Map<String, Object>> result = service.getSavedSearches(null, null);
        assertEquals(1, result.size());
    }

    @Test
    public void testGetSavedSearchHistoryWithSearchNameWithNamespace() {
        Job[] jobs = { job };
        Map<String, Object> entry = new HashMap<>();
        entry.put("testKey", "testValue");
        List<SavedSearch> savedSearchList = new ArrayList<>();
        savedSearchList.add(savedSearch);
        when(service.getService()).thenReturn(clientService);
        when(clientService.getSavedSearches(any(ServiceArgs.class))).thenReturn(savedSearchCollection);
        when(savedSearchCollection.get("searchName")).thenReturn(savedSearch);
        when(savedSearch.history()).thenReturn(jobs);
        when(job.entrySet()).thenReturn(entry.entrySet());
        List<Map<String, Object>> result = service.getSavedSearchHistory("searchName", "app", "owner");
        assertEquals(1, result.size());
    }

    @Test
    public void testGetSavedSearchHistoryWithSearchNameNoNamespace() {
        Job[] jobs = { job };
        Map<String, Object> entry = new HashMap<>();
        entry.put("testKey", "testValue");
        when(service.getService()).thenReturn(clientService);
        when(clientService.getSavedSearches(any(ServiceArgs.class))).thenReturn(savedSearchCollection);
        when(savedSearchCollection.get("searchName")).thenReturn(savedSearch);
        when(savedSearch.history()).thenReturn(jobs);
        when(job.entrySet()).thenReturn(entry.entrySet());
        List<Map<String, Object>> result = service.getSavedSearchHistory("searchName", null, null);
        assertEquals(1, result.size());
    }

    @Test
    public void testGetSavedSearchHistoryNoSearchNameWithNamespace() {
        Job[] jobs = { job };
        Map<String, Object> entry = new HashMap<>();
        entry.put("testKey", "testValue");
        List<SavedSearch> savedSearchList = new ArrayList<>();
        savedSearchList.add(savedSearch);
        when(service.getService()).thenReturn(clientService);
        when(clientService.getSavedSearches(any(ServiceArgs.class))).thenReturn(savedSearchCollection);
        when(savedSearchCollection.values()).thenReturn(savedSearchList);
        when(savedSearch.history()).thenReturn(jobs);
        when(job.entrySet()).thenReturn(entry.entrySet());
        List<Map<String, Object>> result = service.getSavedSearchHistory(null, "app", "owner");
        assertEquals(1, result.size());
    }

    @Test
    public void testGetSavedSearchHistoryNoSearchNameNoNamespace() {
        Job[] jobs = { job };
        Map<String, Object> entry = new HashMap<>();
        entry.put("testKey", "testValue");
        List<SavedSearch> savedSearchList = new ArrayList<>();
        savedSearchList.add(savedSearch);
        when(service.getService()).thenReturn(clientService);
        when(clientService.getSavedSearches(any(ServiceArgs.class))).thenReturn(savedSearchCollection);
        when(savedSearchCollection.values()).thenReturn(savedSearchList);
        when(savedSearch.history()).thenReturn(jobs);
        when(job.entrySet()).thenReturn(entry.entrySet());
        List<Map<String, Object>> result = service.getSavedSearchHistory(null, null, null);
        assertEquals(1, result.size());
    }

    @Test
    public void testModifySavedSearchProperties() {
        Map<String, Object> args = new HashMap<>();
        args.put("testKey", "testValue");
        when(service.getService()).thenReturn(clientService);
        when(clientService.getSavedSearches()).thenReturn(savedSearchCollection);
        when(savedSearchCollection.get("searchName")).thenReturn(savedSearch);
        doNothing().when(savedSearch).update(args);
        when(savedSearch.entrySet()).thenReturn(args.entrySet());
        Map<String, Object> result = service.modifySavedSearchProperties("searchName", args);
        assertEquals(1, result.size());
    }

    @Test
    public void testRunSavedSearch() throws Exception {
        List<Map<String, Object>> searchResult = new ArrayList<Map<String, Object>>();
        when(service.getService()).thenReturn(clientService);
        when(clientService.getSavedSearches()).thenReturn(savedSearchCollection);
        when(savedSearchCollection.get("searchName")).thenReturn(savedSearch);
        when(savedSearch.dispatch()).thenReturn(job);
        when(job.isDone()).thenReturn(true);
        doReturn(searchResult).when(service).populateEventResponse(job);
        assertEquals(searchResult, service.runSavedSearch("searchName"));
    }

    @Test
    public void testRunSavedSearchWithArguments() throws Exception {
        List<Map<String, Object>> processed = new ArrayList<>();
        Map<String, Object> customArgs = new HashMap<>();
        customArgs.put("testKey", "testValue");
        when(service.getService()).thenReturn(clientService);
        when(clientService.getSavedSearches()).thenReturn(savedSearchCollection);
        when(savedSearchCollection.get(anyString())).thenReturn(savedSearch);
        doNothing().when(service).processCustomArgs(eq(customArgs), any(SavedSearchDispatchArgs.class));
        when(savedSearch.dispatch(any(SavedSearchDispatchArgs.class))).thenReturn(job);
        when(job.isDone()).thenReturn(true);
        doReturn(processed).when(service).populateEventResponse(job);
        assertEquals(processed, service.runSavedSearchWithArguments(anyString(), eq(customArgs), any(SavedSearchDispatchArgs.class)));
    }

    @Test
    public void testViewSavedSearchProperties() {
        when(service.getService()).thenReturn(clientService);
        when(clientService.getSavedSearches(any(ServiceArgs.class))).thenReturn(savedSearchCollection);
        when(savedSearchCollection.get(anyString())).thenReturn(savedSearch);
        assertEquals(savedSearch.entrySet(), service.viewSavedSearchProperties("searchName", "app", "owner"));
    }
}
