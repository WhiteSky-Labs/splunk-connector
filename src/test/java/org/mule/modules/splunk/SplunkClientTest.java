/**
 *
 * (c) 2003-2012 MuleSoft, Inc. This software is protected under international
 * copyright law. All use of this software is subject to MuleSoft's Master
 * Subscription Agreement (or other Terms of Service) separately entered
 * into between you and MuleSoft. If such an agreement is not in
 * place, you may not use the software.
 */

package org.mule.modules.splunk;

import com.splunk.*;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mule.common.metadata.MetaDataKey;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

/**
 * Test {@link org.mule.modules.splunk.SplunkConnector} internals
 * <p/>
 */
public class SplunkClientTest {
    SplunkClient client;

    @Mock
    Service service;
    @Mock
    SplunkConnector connector;
    @Mock
    EntityCollection<Application> apps;
    @Mock
    SavedSearchCollection searchCollection;
    @Mock
    SavedSearch search;
    @Mock
    DataModelCollection dataModelCollection;
    @Mock
    DataModel model;
    @Mock
    JobCollection jobs;
    @Mock
    Job job;
    @Mock
    InputStream stream;

    @Before
    public void setUp() throws Exception {
        this.client = spy(new SplunkClient(connector));
        MockitoAnnotations.initMocks(this);
        client.setService(service);

    }

    @Test
    public void testGetApplications() throws Exception {
        List<Application> applist = new ArrayList<Application>();
        when(apps.values()).thenReturn(applist);
        when(service.getApplications()).thenReturn(apps);
        assertEquals(applist, client.getApplications());
    }

    @Test
    public void testCreateSavedSearch() throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("Test", "Test");

        when(searchCollection.create("Unit Testing", "Search * | head 100")).thenReturn(search);
        when(searchCollection.create("Unit Testing", "Search * | head 100", map)).thenReturn(search);
        when(service.getSavedSearches()).thenReturn(searchCollection);

        assertEquals(search, client.createSavedSearch("Unit Testing", "Search * | head 100", map));
        assertEquals(search, client.createSavedSearch("Unit Testing", "Search * | head 100", null));
    }

    @Test
    public void testDeleteSavedSearch() throws Exception {
        boolean result = true;
        doNothing().when(search).remove();
        when(searchCollection.get("Test")).thenReturn(search);
        when(service.getSavedSearches()).thenReturn(searchCollection);
        assertEquals(result, client.deleteSavedSearch("Test"));
    }

    @Test
    public void testGetDataModel() throws Exception {
        when(dataModelCollection.get("Test")).thenReturn(model);
        when(service.getDataModels()).thenReturn(dataModelCollection);
        assertEquals(model, client.getDataModel("Test"));
    }

    @Test
    public void testGetJobs() throws Exception {
        when(service.getJobs()).thenReturn(jobs);
        List<Job> jobList = new ArrayList<Job>();
        assertEquals(jobList, client.getJobs());
    }

    @Test
    public void testGetSavedSearches() throws Exception {
        List<SavedSearch> savedSearches = new ArrayList<SavedSearch>();
        when(service.getSavedSearches()).thenReturn(searchCollection);

        assertEquals(savedSearches, client.getSavedSearches(null, null));
    }

    @Test
    public void testGetSavedSearchHistory() throws Exception {
        List<Job> jobs = new ArrayList<Job>();
        Job[] history = new Job[0];
        ServiceArgs namespace = new ServiceArgs();
        namespace.setApp("search");
        namespace.setOwner("admin");
        when(service.getSavedSearches(namespace)).thenReturn(searchCollection);
        when(searchCollection.get("Test")).thenReturn(search);
        when(search.history()).thenReturn(history);
        assertEquals(jobs, client.getSavedSearchHistory("Test", "search", "admin"));
    }

    @Test
    public void testModifySavedSearchProperties() throws Exception {
        doNothing().when(search).update();
        when(searchCollection.get("Test")).thenReturn(search);
        when(service.getSavedSearches()).thenReturn(searchCollection);

        Map<String, Object> props = new HashMap<String, Object>();
        props.put("description", "test");
        assertEquals(search, client.modifySavedSearchProperties("Test", props));
    }

    @Test
    public void testRunBlockingSearchTestCase() throws Exception {

        Map<String, Object> searchResponse = new HashMap<String, Object>();
        Job job = null;
        List<Map<String, Object>> eventResponse = new ArrayList<Map<String, Object>>();
        searchResponse.put("job", job);
        searchResponse.put("events", eventResponse);

        JobResultsArgs resultsArgs = new JobResultsArgs();
        resultsArgs.setOutputMode(JobResultsArgs.OutputMode.JSON);

        JobArgs jobArgs = new JobArgs();

        when(service.getJobs()).thenReturn(jobs);
        when(jobs.create("Test", jobArgs)).thenReturn(job);
        doReturn(eventResponse).when(client).populateEventResponse(null);


        assertEquals(searchResponse, client.runBlockingSearch("Test", null));
    }


    @Test
    public void testRunOneShotSearch() throws Exception {
        List<Map<String, Object>> oneshotResult = new ArrayList<Map<String, Object>>();

        Args oneshotSearchArgs = new Args();
        oneshotSearchArgs.put("earliest_time", "-1h");
        oneshotSearchArgs.put("latest_time", "now");

        InputStream stubInputStream =
                IOUtils.toInputStream("<xml><Test>Bob</Test></xml>");
        when(service.oneshotSearch("Test", oneshotSearchArgs)).thenReturn(stubInputStream);
        ResultsReaderXml resultsReader = new ResultsReaderXml(stubInputStream);

        doReturn(null).when(client).parseEvents(resultsReader);

        assertEquals(oneshotResult, client.runOneShotSearch("Test", "-1h", "now", null));
    }

    @Test
    public void testRunSavedSearch() throws Exception {
        List<Map<String, Object>> searchResult = new ArrayList<Map<String, Object>>();

        when(searchCollection.get("Test")).thenReturn(search);
        when(service.getSavedSearches()).thenReturn(searchCollection);
        when(search.dispatch()).thenReturn(job);
        when(job.isDone()).thenReturn(true);

        doReturn(searchResult).when(client).populateEventResponse(job);

        Map<String, Object> props = new HashMap<String, Object>();
        props.put("description", "test");
        assertEquals(searchResult, client.runSavedSearch("Test"));

    }

    @Test
    public void testRunSavedSearchWithArguments() throws Exception {

        Map<String, Object> customArgs = new HashMap<String, Object>();
        customArgs.put("alert.email", "Yes");
        customArgs.put("output_mode", "JSON");

        SavedSearchDispatchArgs searchArgs = new SavedSearchDispatchArgs();
        searchArgs.setDispatchLatestTime("now");
        searchArgs.setForceDispatch(true);

        List<Map<String, Object>> searchResult = new ArrayList<Map<String, Object>>();
        String queryParams = "";
        for (Map.Entry<String, Object> entry : customArgs.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue().toString();
            queryParams += " " + key + "=$args." + key + "$";
            searchArgs.add("args." + key, value);
        }

        when(searchCollection.get("Test")).thenReturn(search);
        when(service.getSavedSearches()).thenReturn(searchCollection);
        when(search.dispatch(searchArgs)).thenReturn(job);
        when(job.isDone()).thenReturn(true);

        doReturn(searchResult).when(client).populateEventResponse(job);

        assertEquals(searchResult, client.runSavedSearchWithArguments("Test", customArgs, searchArgs));
    }

    @Test
    public void testViewSavedSearchProperties() throws Exception {
        Map<String, Object> results = new HashMap<String, Object>();
        results.put("Result 1", "Test");
        results.put("Result 2", "Test 2");
        ServiceArgs namespace = new ServiceArgs();
        namespace.setApp("search");
        namespace.setOwner("admin");

        when(service.getSavedSearches(namespace)).thenReturn(searchCollection);
        when(searchCollection.get("Test")).thenReturn(search);
        when(search.entrySet()).thenReturn(results.entrySet());

        assertEquals(results.entrySet(), client.viewSavedSearchProperties("Test", "search", "admin"));
    }

    @Test
    public void testConstructor() throws Exception {
        SplunkConnector testConnector = new SplunkConnector();
        SplunkClient testClient = new SplunkClient(testConnector);
        assertNotNull(testClient);
    }

    @Test
    public void testMetaDataKeys() throws Exception {

        List<MetaDataKey> metaDataKeyList = new ArrayList<MetaDataKey>();
        metaDataKeyList.add(client.createKey(Application.class));
        metaDataKeyList.add(client.createKey(SavedSearch.class));
        metaDataKeyList.add(client.createKey(SavedSearchDispatchArgs.class));
        metaDataKeyList.add(client.createKey(Job.class));
        metaDataKeyList.add(client.createKey(SearchResults.class));
        metaDataKeyList.add(client.createKey(JobCollection.class));
        
        assertEquals(metaDataKeyList, client.getMetadata());
    }

}
