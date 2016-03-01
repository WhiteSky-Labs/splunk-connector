/**
 *
 * (c) 2015 WhiteSky Labs, Pty Ltd. This software is protected under international
 * copyright law. All use of this software is subject to WhiteSky Labs' Master
 * Subscription Agreement (or other Terms of Service) separately entered
 * into between you and WhiteSky Labs. If such an agreement is not in
 * place, you may not use the software.
 */

package org.mule.modules.splunk;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mule.api.ConnectionException;
import org.mule.api.callback.SourceCallback;
import org.mule.modules.splunk.OutputMode;
import org.mule.modules.splunk.SearchMode;
import org.mule.modules.splunk.SplunkClient;
import org.mule.modules.splunk.SplunkConnector;
import org.mule.modules.splunk.exception.SplunkConnectorException;
import org.mule.modules.splunk.strategy.ConnectionManagementStrategy;

import com.splunk.Application;
import com.splunk.Args;
import com.splunk.CollectionArgs;
import com.splunk.DataModel;
import com.splunk.DataModelCollection;
import com.splunk.EntityCollection;
import com.splunk.Event;
import com.splunk.HttpException;
import com.splunk.Index;
import com.splunk.IndexCollection;
import com.splunk.IndexCollectionArgs;
import com.splunk.Input;
import com.splunk.InputCollection;
import com.splunk.InputKind;
import com.splunk.Job;
import com.splunk.JobArgs;
import com.splunk.JobCollection;
import com.splunk.JobExportArgs;
import com.splunk.JobResultsArgs;
import com.splunk.JobResultsPreviewArgs;
import com.splunk.ResultsReaderJson;
import com.splunk.ResultsReaderXml;
import com.splunk.SavedSearch;
import com.splunk.SavedSearchCollection;
import com.splunk.SavedSearchDispatchArgs;
import com.splunk.Service;
import com.splunk.ServiceArgs;
import com.splunk.TcpInput;
import com.splunk.UdpInput;

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
    Application application;
    @Mock
    SavedSearchCollection searchCollection;
    @Mock
    SavedSearch savedSearch;
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
    @Mock
    ResultsReaderXml xmlreader;
    @Mock
    ResultsReaderJson jsonreader;
    @Mock
    Event event;
    @Mock
    SourceCallback cb;
    @Mock
    InputCollection coll;
    @Mock
    Input input;
    @Mock
    IndexCollection indexCollection;
    @Mock
    Index index;
    @Mock
    IOException ioe;
    @Mock
    TcpInput tcpInput;
    @Mock
    UdpInput udpInput;
    @Mock
    HttpException httpException;
    @Mock
    InputCollection inputs;
    @Mock
    ConnectionManagementStrategy connectionStrategy;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        this.client = spy(new SplunkClient());
        client.setService(service);

    }

    @Test
    public void testGetApplications() throws Exception {
        List<Application> applist = new ArrayList<Application>();
        applist.add(application);
        List<Map<String, Object>> returnList = new ArrayList<Map<String, Object>>();
        Map<String, Object> entry = new HashMap<>();
        entry.put("testKey", "testValue");
        Set<Map.Entry<String, Object>> set = entry.entrySet();
        when(apps.values()).thenReturn(applist);
        when(service.getApplications()).thenReturn(apps);
        when(application.entrySet()).thenReturn(set);
        
        returnList = client.getApplications();
        assertEquals(1, returnList.size());
        assertEquals(true, returnList.contains(entry));
    }

    @Test
    public void testCreateSavedSearch() throws Exception {
    		Map<String, Object> args = new HashMap<>();
    		args.put("Test", "Test");

        when(service.getSavedSearches()).thenReturn(searchCollection);
        when(searchCollection.create("Unit Testing", "Search * | head 100", args)).thenReturn(search);

        assertEquals(new HashMap<String, Object>(), client.createSavedSearch("Unit Testing", "Search * | head 100", args));
    }
    
    @Test
    public void testCreateSavedSearchWithNoArgs() throws Exception {
        List<DataModel> modelList = new ArrayList<>();
		modelList.add(model);
		Map<String, Object> entry = new HashMap<>();
		entry.put("testKey", "testValue");
		Set<Map.Entry<String, Object>> entrySet = entry.entrySet();

        when(service.getSavedSearches()).thenReturn(searchCollection);
        when(searchCollection.create("Unit Testing", "Search * | head 100")).thenReturn(search);
        when(search.entrySet()).thenReturn(entrySet);
        
        Map<String, Object> result = client.createSavedSearch("Unit Testing", "Search * | head 100", null);
		assertEquals(1, result.size());
		assertEquals(true, result.containsKey("testKey"));
		assertEquals(true, result.containsValue("testValue"));
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
    public void testDeleteSavedSearchWithException() throws Exception {
        boolean result = false;
        doThrow(httpException).when(search).remove();
        when(searchCollection.get("Test")).thenReturn(search);
        when(service.getSavedSearches()).thenReturn(searchCollection);
        assertEquals(result, client.deleteSavedSearch("Test"));
    }


	@Test
	public void testGetDataModel() throws Exception {
		List<DataModel> modelList = new ArrayList<>();
		modelList.add(model);
		Map<String, Object> entry = new HashMap<>();
		entry.put("testKey", "testValue");
		Set<Map.Entry<String, Object>> entrySet = entry.entrySet();

		when(dataModelCollection.get("Test")).thenReturn(model);
		when(service.getDataModels()).thenReturn(dataModelCollection);
		when(model.entrySet()).thenReturn(entrySet);

		Map<String, Object> result = client.getDataModel("Test");
		assertEquals(1, result.size());
		assertEquals(true, result.containsKey("testKey"));
		assertEquals(true, result.containsValue("testValue"));
	}

	@Test
	public void testGetDataModels() throws Exception {
		List<DataModel> modelList = new ArrayList<>();
		modelList.add(model);
		Map<String, Object> entry = new HashMap<>();
		entry.put("testKey", "testValue");
		Set<Map.Entry<String, Object>> entrySet = entry.entrySet();
		
		when(service.getDataModels()).thenReturn(dataModelCollection);
		when(dataModelCollection.values()).thenReturn(modelList);
		when(model.entrySet()).thenReturn(entrySet);

		List<Map<String, Object>> result = client.getDataModels();
		assertEquals(1, result.size());
		assertEquals(true, result.contains(entry));
	}

	@Test
	public void testGetJobs() throws Exception {
		List<Job> jobList = new ArrayList<>();
		jobList.add(job);
		Map<String, Object> entry = new HashMap<>();
		entry.put("testKey", "testValue");
		Set<Map.Entry<String, Object>> entrySet = entry.entrySet();
		when(service.getJobs()).thenReturn(jobs);
		when(jobs.values()).thenReturn(jobList);
		when(job.entrySet()).thenReturn(entrySet);

		List<Map<String, Object>> result = client.getJobs();
		assertEquals(1, result.size());
		assertEquals(true, result.contains(entry));
	}
    
    @Test
    public void testGetSavedSearches() throws Exception {
        List<Map<String, Object>> savedSearchList = new ArrayList<Map<String, Object>>();
        List<SavedSearch> savedSearches = new ArrayList<>();
        Map<String, Object> entry = new HashMap<>();
        entry.put("testKey", "testValue");
        Set<Map.Entry<String, Object>> set = entry.entrySet();
        savedSearches.add(savedSearch);
        when(service.getSavedSearches()).thenReturn(searchCollection);
        when(searchCollection.values()).thenReturn(savedSearches);
        when(savedSearch.entrySet()).thenReturn(set);

        savedSearchList = client.getSavedSearches(null, null);
        assertEquals(1, savedSearchList.size());
        assertEquals(true, savedSearchList.contains(entry));
    }

    @Test
    public void testGetSavedSearchesWithApp() throws Exception {
        List<SavedSearch> savedSearches = new ArrayList<SavedSearch>();
        when(service.getSavedSearches(any(ServiceArgs.class))).thenReturn(searchCollection);

        when(searchCollection.values()).thenReturn(savedSearches);
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        assertEquals(result, client.getSavedSearches("Test", null));
    }


    @Test
    public void testGetSavedSearchesWithOwner() throws Exception {
        List<SavedSearch> savedSearches = new ArrayList<SavedSearch>();
        when(service.getSavedSearches(any(ServiceArgs.class))).thenReturn(searchCollection);

        when(searchCollection.values()).thenReturn(savedSearches);
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

        assertEquals(result, client.getSavedSearches(null, "Test"));
    }

    @Test
    public void testGetSavedSearchesWithNameSpace() throws Exception {
        List<SavedSearch> savedSearches = new ArrayList<SavedSearch>();
        when(service.getSavedSearches(any(ServiceArgs.class))).thenReturn(searchCollection);

        when(searchCollection.values()).thenReturn(savedSearches);
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

        assertEquals(result, client.getSavedSearches("Test", "Test"));
    }

    @Test
    public void testGetSavedSearchHistory() throws Exception {
        List<Map<String, Object>> jobs = new ArrayList<Map<String, Object>>();
        Job[] history = new Job[0];
        ServiceArgs namespace = new ServiceArgs();
        namespace.setApp("search");
        namespace.setOwner("admin");
        when(service.getSavedSearches(namespace)).thenReturn(searchCollection);
        when(service.getSavedSearches(new ServiceArgs())).thenReturn(searchCollection);
        when(searchCollection.get("Test")).thenReturn(search);

        List<SavedSearch> savedSearches = new ArrayList<SavedSearch>();
        savedSearches.add(search);

        when(searchCollection.values()).thenReturn(savedSearches);

        when(search.history()).thenReturn(history);
        assertEquals(jobs, client.getSavedSearchHistory("Test", "search", "admin"));
        assertEquals(jobs, client.getSavedSearchHistory(null, null, null));
        assertEquals(jobs, client.getSavedSearchHistory("", null, null));
    }

    @Test
    public void testModifySavedSearchProperties() throws Exception {
        doNothing().when(search).update();
        when(searchCollection.get("Test")).thenReturn(search);
        when(service.getSavedSearches()).thenReturn(searchCollection);

        Map<String, Object> props = new HashMap<String, Object>();
        Map<String, Object> result = new HashMap<String, Object>();
        props.put("description", "test");
        assertEquals(result, client.modifySavedSearchProperties("Test", props));
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
    public void testRunBlockingSearchWithArgsTestCase() throws Exception {

        Map<String, Object> searchResponse = new HashMap<String, Object>();
        Job job = null;
        List<Map<String, Object>> eventResponse = new ArrayList<Map<String, Object>>();
        searchResponse.put("job", job);
        searchResponse.put("events", eventResponse);

        JobResultsArgs resultsArgs = new JobResultsArgs();
        resultsArgs.setOutputMode(JobResultsArgs.OutputMode.JSON);

        when(service.getJobs()).thenReturn(jobs);
        when(jobs.create(anyString(), any(JobArgs.class))).thenReturn(job);
        doReturn(eventResponse).when(client).populateEventResponse(null);

        Map<String, Object> validArgs = new HashMap<String, Object>();
        validArgs.put("auto_cancel", "60");

        assertEquals(searchResponse, client.runBlockingSearch("Test", new HashMap<String, Object>()));
        assertEquals(searchResponse, client.runBlockingSearch("Test", validArgs));
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
        for (Map.Entry<String, Object> entry : customArgs.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue().toString();
            searchArgs.add("args." + key, value);
        }

        when(searchCollection.get("Test")).thenReturn(search);
        when(service.getSavedSearches()).thenReturn(searchCollection);
        when(search.dispatch(any(SavedSearchDispatchArgs.class))).thenReturn(job);
        when(job.isDone()).thenReturn(true);

        doReturn(searchResult).when(client).populateEventResponse(job);

        assertEquals(searchResult, client.runSavedSearchWithArguments("Test", customArgs, searchArgs));
        assertEquals(searchResult, client.runSavedSearchWithArguments("Test", null, searchArgs));
        assertEquals(searchResult, client.runSavedSearchWithArguments("Test", customArgs, null));
        assertEquals(searchResult, client.runSavedSearchWithArguments("Test", null, null));
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
    public void testViewSavedSearchPropertiesWithoutNamespace() throws Exception {
        Map<String, Object> results = new HashMap<String, Object>();
        results.put("Result 1", "Test");
        results.put("Result 2", "Test 2");

        when(service.getSavedSearches(any(ServiceArgs.class))).thenReturn(searchCollection);
        when(searchCollection.get("Test")).thenReturn(search);
        when(search.entrySet()).thenReturn(results.entrySet());

        assertEquals(results.entrySet(), client.viewSavedSearchProperties("Test", null, null));
    }

    @Test
    public void testConnectOffline() {
        when(connectionStrategy.isValid()).thenReturn(true);
        when(connectionStrategy.getUsername()).thenReturn("Test");
        when(connectionStrategy.getPassword()).thenReturn("Test");
        when(connectionStrategy.getHost()).thenReturn("InvalidHost");
        when(connectionStrategy.getPort()).thenReturn("8089");
        when(connectionStrategy.getIntPort()).thenReturn(8089);
    	
    	try {
            client.connect(connectionStrategy);
            fail("Exception should be thrown");
        } catch (ConnectionException ex) {
            assertEquals("InvalidHost", ex.getMessage());
        }
    }

    @Test
    public void testConnectWithInvalidCredentials() throws Exception {
    	
        when(connector.getConnectionStrategy()).thenReturn(connectionStrategy);
        
        try {
            client.connect(connectionStrategy);
            fail("Exception should be thrown");
        } catch (ConnectionException ex) {
            assertEquals("Invalid credentials", ex.getMessage());
        }
    }

    @Test
    public void testConvertToJavaConvention() {
        String test = "hello_there";
        assertEquals("helloThere", client.convertToJavaConvention(test));
    }

    @Test
    public void testGetSavedSearch() {
        ServiceArgs namespace = new ServiceArgs();
        namespace.setApp("search");
        namespace.setOwner("admin");
        when(service.getSavedSearches(namespace)).thenReturn(searchCollection);
        when(searchCollection.get("Test")).thenReturn(search);
        assertEquals(search, client.getSavedSearch("Test", "search", "admin"));
    }


    @Test
    public void testParseEvents() throws Exception {
        List<Map<String, Object>> entries = new ArrayList<Map<String, Object>>();
        assertEquals(entries, client.parseEvents(this.jsonreader));
        assertEquals(entries, client.parseEvents(this.xmlreader));
    }


    /*
    * Currently disabled since there is no obvious way to test an infinite loop.
    @Test
    public void testRunRealTimeSearch() throws Exception {
        when(job.isReady()).thenReturn(true);
        when(service.search(anyString(), any(JobArgs.class))).thenReturn(job);

        InputStream stubInputStream =
                IOUtils.toInputStream("['test':'test']");
        when(job.getResultsPreview(any(JobResultsPreviewArgs.class))).thenReturn(stubInputStream);
        when(cb.process(any())).thenReturn(new Object());
        doReturn(null).when(client).parseEvents(any(ResultsReaderJson.class));

        try{
            client.runRealTimeSearch("Test", "rt-10m", "rt", 0, 0, cb);
        } catch (Exception e){
            e.printStackTrace();
        }

    }*/

    @Test
    public void testRunExportSearch() throws Exception {

        InputStream stubInputStream =
                IOUtils.toInputStream("<xml><Test>Bob</Test></xml>");
        when(service.export(anyString(), any(JobExportArgs.class))).thenReturn(stubInputStream);
        when(cb.process(any())).thenReturn(new Object());

        client.runExportSearch("Test", "rt-10m", "rt", SearchMode.NORMAL, OutputMode.XML, cb);

    }

    @Test
    public void testRunNormalSearch() throws Exception {
        when(service.getJobs()).thenReturn(jobs);
        when(jobs.create(anyString(), any(JobArgs.class))).thenReturn(job);
        when(job.isDone()).thenReturn(true);

        Map<String, Object> searchResponse = new HashMap<String, Object>();
        List<Map<String, Object>> eventResponse = new ArrayList<Map<String, Object>>();
        searchResponse.put("job", job);
        searchResponse.put("events", eventResponse);

        doReturn(eventResponse).when(client).populateEventResponse(job);
        when(cb.process(any())).thenReturn(new Object());

        client.runNormalSearch("Test", new HashMap<String, Object>(), cb);

    }

	@Test
	public void testGetInputs() throws Exception {
		List<Input> inputList = new ArrayList<>();
		inputList.add(input);
		Map<String, Object> entry = new HashMap<>();
		entry.put("testKey", "testValue");
		Set<Map.Entry<String, Object>> entrySet = entry.entrySet();
		when(service.getInputs()).thenReturn(inputs);
		when(inputs.values()).thenReturn(inputList);
		when(input.entrySet()).thenReturn(entrySet);

		List<Map<String, Object>> result = client.getInputs();
		assertEquals(1, result.size());
		assertEquals(true, result.contains(entry));
	}
    
    @Test
    public void testCreateInputWithoutProperties() throws Exception {
        when(service.getInputs()).thenReturn(coll);
        when(coll.create(anyString(), any(InputKind.class))).thenReturn(input);
        HashMap<String, Object> result = new HashMap<String, Object>();
        assertEquals(result, client.createInput("Test", InputKind.Tcp, null));
    }

    @Test
    public void testCreateInputWithProperties() throws Exception {
        when(service.getInputs()).thenReturn(coll);
        HashMap<String, Object> props = new HashMap<String, Object>();
        props.put("index", "text_index");

        when(coll.create(anyString(), any(InputKind.class), eq(props))).thenReturn(input);
        HashMap<String, Object> result = new HashMap<String, Object>();
        assertEquals(result, client.createInput("Test", InputKind.Tcp, props));
    }

	@Test
	public void testCreateInputWithEmptyProperties() throws Exception {
		HashMap<String, Object> props = new HashMap<String, Object>();
		List<Index> indexList = new ArrayList<>();
		indexList.add(index);
		Map<String, Object> entry = new HashMap<>();
		entry.put("testKey", "testValue");
		Set<Map.Entry<String, Object>> entrySet = entry.entrySet();

		when(service.getInputs()).thenReturn(coll);
		when(coll.create(anyString(), any(InputKind.class), eq(props)))
				.thenReturn(input);
		when(coll.create(anyString(), any(InputKind.class))).thenReturn(input);
		when(input.entrySet()).thenReturn(entrySet);

		Map<String, Object> result = client.createInput("Test", InputKind.Tcp,
				props);
		assertEquals(1, result.size());
		assertEquals(true, result.containsKey("testKey"));
		assertEquals(true, result.containsValue("testValue"));
	}

    @Test
    public void testModifyInputWithValidProperties() throws Exception {
        HashMap<String, Object> props = new HashMap<String, Object>();
        props.put("index", "text_index");
        List<Input> inputList = new ArrayList<>();
		inputList.add(input);
		Map<String, Object> entry = new HashMap<>();
		entry.put("testKey", "testValue");
		Set<Map.Entry<String, Object>> entrySet = entry.entrySet();
		
        when(service.getInputs()).thenReturn(coll);
        when(coll.get(anyString())).thenReturn(input);
        doNothing().when(input).putAll(props);
        doNothing().when(input).update();
        when(input.entrySet()).thenReturn(entrySet);

        Map<String, Object> result = client.modifyInput("Test", props);
		assertEquals(1, result.size());
		assertEquals(true, result.containsKey("testKey"));
		assertEquals(true, result.containsValue("testValue"));
    }
    
    @Test
    public void testModifyInputWithEmptyProperties() throws Exception {
        HashMap<String, Object> props = new HashMap<String, Object>();
        when(service.getInputs()).thenReturn(coll);
        when(coll.get(anyString())).thenReturn(input);

        try {
            client.modifyInput("Test", props);
            fail("Should throw an error when modifying an input with empty properties");
        } catch (Exception e) {
            assertEquals("You must provide some properties to modify", e.getMessage());
        }
    }

    @Test
    public void testModifyInputWithNullProperties() throws Exception {
        HashMap<String, Object> props = null;
        when(service.getInputs()).thenReturn(coll);
        when(coll.get(anyString())).thenReturn(input);

        try {
            client.modifyInput("Test", props);
            fail("Should throw an error when modifying an input with null properties");
        } catch (Exception e) {
            assertEquals("You must provide some properties to modify", e.getMessage());
        }
    }

    @Test
    public void testGetInput() throws Exception {
    	List<Input> inputList = new ArrayList<>();
		inputList.add(input);
		Map<String, Object> entry = new HashMap<>();
		entry.put("testKey", "testValue");
		Set<Map.Entry<String, Object>> entrySet = entry.entrySet();
		
        when(service.getInputs()).thenReturn(coll);
        when(coll.get("Test")).thenReturn(input);
        when(input.entrySet()).thenReturn(entrySet);

        Map<String, Object> result = client.getInput("Test");
		assertEquals(1, result.size());
		assertEquals(true, result.containsKey("testKey"));
		assertEquals(true, result.containsValue("testValue"));
    }

	@Test
	public void testGetIndexes() throws Exception {
		List<Index> indexList = new ArrayList<>();
		indexList.add(index);
		Map<String, Object> entry = new HashMap<>();
		entry.put("testKey", "testValue");
		Set<Map.Entry<String, Object>> entrySet = entry.entrySet();
		when(service.getIndexes()).thenReturn(indexCollection);
		when(indexCollection.values()).thenReturn(indexList);
		when(index.entrySet()).thenReturn(entrySet);

		List<Map<String, Object>> result = client.getIndexes(null, null, null);
		assertEquals(1, result.size());
		assertEquals(true, result.contains(entry));
	}

	@Test
    public void testGetIndexesWithParameters() throws Exception {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("assureUTF8", "true");
        IndexCollectionArgs args = new IndexCollectionArgs();
        args.setSortDirection(CollectionArgs.SortDirection.DESC);
        args.setSortKey("test");
        args.putAll(params);

        when(service.getIndexes(args)).thenReturn(indexCollection);
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        assertEquals(result, client.getIndexes("test", CollectionArgs.SortDirection.DESC, params));
    }

    @Test
    public void testGetIndexesWithEmptyParameters() throws Exception {
        HashMap<String, Object> params = new HashMap<String, Object>();
        IndexCollectionArgs args = new IndexCollectionArgs();
        args.setSortDirection(CollectionArgs.SortDirection.DESC);
        args.setSortKey("");
        args.putAll(params);

        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        when(service.getIndexes(any(IndexCollectionArgs.class))).thenReturn(indexCollection);
        params = new HashMap<String, Object>();
        assertEquals(result, client.getIndexes("", CollectionArgs.SortDirection.DESC, params));
    }

    @Test
    public void testCreateIndex() throws Exception {
        when(service.getIndexes()).thenReturn(indexCollection);
        when(indexCollection.create("Test")).thenReturn(index);
        Map<String, Object> result = new HashMap<String, Object>();
        assertEquals(result, client.createIndex("Test", null));
    }

    @Test
    public void testCreateIndexWithArgs() throws Exception {
        HashMap<String, Object> args = new HashMap<String, Object>();
        args.put("assureUTF8", "true");
        when(service.getIndexes()).thenReturn(indexCollection);
        when(indexCollection.create("Test", args)).thenReturn(index);
        Map<String, Object> result = new HashMap<String, Object>();
        assertEquals(result, client.createIndex("Test", args));
    }

    @Test
    public void testCreateIndexWithEmptyArgs() throws Exception {
        HashMap<String, Object> args = new HashMap<String, Object>();
        List<Index> indexList = new ArrayList<>();
		indexList.add(index);
		Map<String, Object> entry = new HashMap<>();
		entry.put("testKey", "testValue");
		Set<Map.Entry<String, Object>> entrySet = entry.entrySet();
		
        when(service.getIndexes()).thenReturn(indexCollection);
        when(indexCollection.create("Test", args)).thenReturn(index);
        when(indexCollection.create("Test")).thenReturn(index);
        when(index.entrySet()).thenReturn(entrySet);
        
        Map<String, Object> result = client.createIndex("Test", args);
		assertEquals(1, result.size());
		assertEquals(true, result.containsKey("testKey"));
		assertEquals(true, result.containsValue("testValue"));
    }


    @Test
    public void testModifyIndexWithValidProperties() throws Exception {
        HashMap<String, Object> props = new HashMap<String, Object>();
        props.put("assureUTF8", "true");
        List<Index> indexList = new ArrayList<>();
		indexList.add(index);
		Map<String, Object> entry = new HashMap<>();
		entry.put("testKey", "testValue");
		Set<Map.Entry<String, Object>> entrySet = entry.entrySet();
        
        when(service.getIndexes()).thenReturn(indexCollection);
        when(indexCollection.get(anyString())).thenReturn(index);
        doNothing().when(index).putAll(props);
        doNothing().when(index).update();
        when(index.entrySet()).thenReturn(entrySet);
        
        Map<String, Object> result = client.modifyIndex("Test", props);
		assertEquals(1, result.size());
		assertEquals(true, result.containsKey("testKey"));
		assertEquals(true, result.containsValue("testValue"));
    }
	
    @Test
    public void testModifyIndexWithEmptyProperties() throws Exception {
        HashMap<String, Object> props = new HashMap<String, Object>();
        when(service.getIndexes()).thenReturn(indexCollection);
        when(indexCollection.get(anyString())).thenReturn(index);

        try {
        		client.modifyIndex("Test", props);
            fail("Should throw an error when modifying an index with empty properties");
        } catch (Exception e) {
            assertEquals("You must provide some properties to modify", e.getMessage());
        }
    }

    @Test
    public void testModifyIndexWithNullProperties() throws Exception {
        HashMap<String, Object> props = null;
        when(service.getIndexes()).thenReturn(indexCollection);
        when(indexCollection.get(anyString())).thenReturn(index);

        try {
            client.modifyIndex("Test", props);
            fail("Should throw an error when modifying an index with null properties");
        } catch (Exception e) {
            assertEquals("You must provide some properties to modify", e.getMessage());
        }
    }

    @Test
    public void testGetIndex() throws Exception {
    	List<Index> indexList = new ArrayList<>();
		indexList.add(index);
		Map<String, Object> entry = new HashMap<>();
		entry.put("testKey", "testValue");
		Set<Map.Entry<String, Object>> entrySet = entry.entrySet();
		
        when(service.getIndexes()).thenReturn(indexCollection);
        when(indexCollection.get("Test")).thenReturn(index);
        when(index.entrySet()).thenReturn(entrySet);
        
        Map<String, Object> result = client.getIndex("Test");
		assertEquals(1, result.size());
		assertEquals(true, result.containsKey("testKey"));
		assertEquals(true, result.containsValue("testValue"));
    }
    
	@Test
	public void testCleanIndex() throws Exception {
		List<Index> indexList = new ArrayList<>();
		indexList.add(index);
		Map<String, Object> entry = new HashMap<>();
		entry.put("testKey", "testValue");
		Set<Map.Entry<String, Object>> entrySet = entry.entrySet();

		when(service.getIndexes()).thenReturn(indexCollection);
		when(indexCollection.get("Test")).thenReturn(index);
		when(index.clean(120)).thenReturn(index);
		when(index.entrySet()).thenReturn(entrySet);

		Map<String, Object> result = client.cleanIndex("Test", 120);
		assertEquals(1, result.size());
		assertEquals(true, result.containsKey("testKey"));
		assertEquals(true, result.containsValue("testValue"));
	}

    @Test
    public void testCleanIndexNegativeCases() throws Exception {
        when(service.getIndexes()).thenReturn(indexCollection);
        when(indexCollection.get("Test")).thenReturn(index);
        when(index.clean(120)).thenReturn(index);
        Map<String, Object> result = new HashMap<String, Object>();

        try {
            assertEquals(result, client.cleanIndex(null, 120));
            fail("Cleaning an index without a name should return an error");
        } catch (Exception e) {
            assertEquals("You must provide an index name", e.getMessage());
        }
        try {
            assertEquals(result, client.cleanIndex("", 120));
            fail("Cleaning an index without a name should return an error");
        } catch (Exception e) {
            assertEquals("You must provide an index name", e.getMessage());
        }
    }

    @Test
    public void testAddDataToIndex() throws Exception {
        when(service.getIndexes()).thenReturn(indexCollection);
        when(indexCollection.get(anyString())).thenReturn(index);

        doNothing().when(index).submit(anyString());
        Map<String, Object> result = new HashMap<String, Object>();
        assertEquals(result, client.addDataToIndex("Test", "Test", null));
    }

    @Test
    public void testAddDataToIndexWithProperties() throws Exception {

        HashMap<String, Object> args = new HashMap<String, Object>();
        args.put("Test", "test");
        Args eventArgs = new Args();
        eventArgs.putAll(args);

        when(service.getIndexes()).thenReturn(indexCollection);
        when(indexCollection.get(anyString())).thenReturn(index);
        Map<String, Object> result = new HashMap<String, Object>();

        doNothing().when(index).submit(eq(eventArgs), anyString());
        assertEquals(result, client.addDataToIndex("Test", "test", args));
    }

	@Test
	public void testAddDataToIndexWithEmptyProperties() throws Exception {
		Map<String, Object> entry = new HashMap<>();
		entry.put("testKey", "testValue");
		Set<Map.Entry<String, Object>> entrySet = entry.entrySet();
		HashMap<String, Object> args = new HashMap<String, Object>();
		Args eventArgs = new Args();
		eventArgs.putAll(args);

		when(service.getIndexes()).thenReturn(indexCollection);
		when(indexCollection.get(anyString())).thenReturn(index);
		doNothing().when(index).submit(eq(eventArgs), anyString());
		when(index.entrySet()).thenReturn(entrySet);

		Map<String, Object> result = client
				.addDataToIndex("Test", "test", args);
		assertEquals(1, result.size());
		assertEquals(true, result.containsKey("testKey"));
		assertEquals(true, result.containsValue("testValue"));
	}

    @Test
    public void testAddDataToTcpInput() throws Exception {
        when(service.getInputs()).thenReturn(coll);
        when(coll.get(anyString())).thenReturn(tcpInput);
        doNothing().when(tcpInput).submit(anyString());

        assertEquals(true, client.addDataToTcpInput("Test", "Test"));
    }

    @Test
    public void testAddDataToTcpInputWithError() throws Exception {
        when(service.getInputs()).thenReturn(coll);
        when(coll.get(anyString())).thenReturn(tcpInput);
        doThrow(ioe).when(tcpInput).submit(anyString());
        assertEquals(false, client.addDataToTcpInput("Test", "Test"));
    }

    @Test
    public void testAddDataToUdpInput() throws Exception {
        when(service.getInputs()).thenReturn(coll);
        when(coll.get(anyString())).thenReturn(udpInput);
        doNothing().when(udpInput).submit(anyString());
        assertEquals(true, client.addDataToUdpInput("Test", "Test"));
    }

    @Test
    public void testAddDataToUdpInputWithError() throws Exception {
        when(service.getInputs()).thenReturn(coll);
        when(coll.get(anyString())).thenReturn(udpInput);
        doThrow(ioe).when(udpInput).submit(anyString());
        assertEquals(false, client.addDataToUdpInput("Test", "Test"));
    }

    @Test
    public void testRemoveInput() throws Exception {
        when(service.getInputs()).thenReturn(coll);
        when(coll.get(anyString())).thenReturn(input);
        when(input.remove(anyString())).thenReturn(input);
        assertEquals(true, client.removeInput("Test"));
    }

    @Test
    public void testRemoveIndex() throws Exception {
        when(service.getIndexes()).thenReturn(indexCollection);
        when(indexCollection.get(anyString())).thenReturn(index);
        when(index.remove(anyString())).thenReturn(index);
        assertEquals(true, client.removeIndex("Test"));
    }

    @Test
    public void testGetService() throws Exception {
        assertEquals(service, client.getService());
    }

	@Test
	public void testPopulateEventResponseEmptyResult() {
		when(job.getResults(any(JobResultsArgs.class))).thenReturn(stream);
		
		try {
			client.populateEventResponse(job);
			fail("Exception should be thrown");
		} catch (SplunkConnectorException e) {
			assertEquals("Underlying input stream returned zero bytes",
					e.getMessage());
		}
	}
	
	@Test
	public void testRunRealTimeSearch() {
		
		try {
			when(service.search(any(String.class), any(JobArgs.class))).thenReturn(job);
			when(job.isReady()).thenReturn(true);
			when(job.getResultsPreview(any(JobResultsPreviewArgs.class))).thenReturn(stream);
			
			client.runRealTimeSearch(anyString(), anyString(), anyString(), anyInt(), anyInt(), any(SourceCallback.class));
			fail("Exception should be thrown");
		} catch (SplunkConnectorException e) {
			assertEquals("Underlying input stream returned zero bytes",
					e.getMessage());
		}
	}
}
