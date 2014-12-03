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
import org.mule.api.ConnectionException;
import org.mule.api.callback.SourceCallback;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
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

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        this.client = spy(new SplunkClient(connector));

        connector.setHost("localhost");
        connector.setPort(8089);
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
    public void testGetDataModels() throws Exception {
        when(service.getDataModels()).thenReturn(dataModelCollection);
        assertEquals(dataModelCollection, client.getDataModels());
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
        when(service.getSavedSearches(new ServiceArgs())).thenReturn(searchCollection);
        when(searchCollection.get("Test")).thenReturn(search);

        List<SavedSearch> savedSearches = new ArrayList<SavedSearch>();
        savedSearches.add(search);

        when(searchCollection.values()).thenReturn(savedSearches);

        when(search.history()).thenReturn(history);
        assertEquals(jobs, client.getSavedSearchHistory("Test", "search", "admin"));
        assertEquals(jobs, client.getSavedSearchHistory(null, null, null));
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
    public void testConnect() throws Exception {
        when(connector.getPort()).thenReturn(8089);
        when(connector.getHost()).thenReturn("localhost");
        try {
            client.connect("Test", "Test", "localhost", 8089);
            fail("Exception should be thrown");
        } catch (ConnectionException ex) {
            assertEquals("Connection refused", ex.getMessage());
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

        client.runExportSearch("Test", "rt-10m", "rt", SearchMode.NORMAL, OutputMode.XML, new JobExportArgs(), cb);

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

        InputStream stubInputStream =
                IOUtils.toInputStream("<xml><Test>Bob</Test></xml>");
        when(cb.process(any())).thenReturn(new Object());

        client.runNormalSearch("Test", new HashMap<String, Object>(), cb);

    }

    @Test
    public void testGetInputs() throws Exception {
        when(service.getInputs()).thenReturn(coll);
        assertEquals(coll, client.getInputs());
    }

    @Test
    public void testCreateActiveDirectoryInputWithoutProperties() throws Exception {
        WindowsActiveDirectoryInput input = null;
        when(service.getInputs()).thenReturn(coll);
        when(coll.create(anyString(), eq(InputKind.WindowsActiveDirectory))).thenReturn(input);

        assertEquals(input, client.createActiveDirectoryInput("Test", null));
    }

    @Test
    public void testCreateActiveDirectoryInputWithProperties() throws Exception {
        WindowsActiveDirectoryInput input = null;
        when(service.getInputs()).thenReturn(coll);
        HashMap<String, Object> props = new HashMap<String, Object>();
        props.put("index", "text_index");

        when(coll.create(anyString(), eq(InputKind.WindowsActiveDirectory), eq(props))).thenReturn(input);

        assertEquals(input, client.createActiveDirectoryInput("Test", props));
    }

    @Test
    public void testCreateMonitorInputWithoutProperties() throws Exception {
        MonitorInput input = null;
        when(service.getInputs()).thenReturn(coll);
        when(coll.create(anyString(), eq(InputKind.Monitor))).thenReturn(input);

        assertEquals(input, client.createMonitorInput("Test", null));
    }

    @Test
    public void testCreateMonitorInputWithProperties() throws Exception {
        MonitorInput input = null;
        when(service.getInputs()).thenReturn(coll);
        HashMap<String, Object> props = new HashMap<String, Object>();
        props.put("index", "text_index");

        when(coll.create(anyString(), eq(InputKind.Monitor), eq(props))).thenReturn(input);

        assertEquals(input, client.createMonitorInput("Test", props));
    }

    @Test
    public void testCreateScriptInputWithoutProperties() throws Exception {
        ScriptInput input = null;
        when(service.getInputs()).thenReturn(coll);
        when(coll.create(anyString(), eq(InputKind.Script))).thenReturn(input);

        assertEquals(input, client.createScriptInput("Test", null));
    }

    @Test
    public void testCreateScriptInputWithProperties() throws Exception {
        ScriptInput input = null;
        when(service.getInputs()).thenReturn(coll);
        HashMap<String, Object> props = new HashMap<String, Object>();
        props.put("index", "text_index");

        when(coll.create(anyString(), eq(InputKind.Script), eq(props))).thenReturn(input);

        assertEquals(input, client.createScriptInput("Test", props));
    }

    @Test
    public void testCreateTcpCookedInputWithoutProperties() throws Exception {
        TcpSplunkInput input = null;
        when(service.getInputs()).thenReturn(coll);
        when(coll.create(anyString(), eq(InputKind.TcpSplunk))).thenReturn(input);

        assertEquals(input, client.createTcpCookedInput("Test", null));
    }

    @Test
    public void testCreateTcpCookedInputWithProperties() throws Exception {
        TcpSplunkInput input = null;
        when(service.getInputs()).thenReturn(coll);
        HashMap<String, Object> props = new HashMap<String, Object>();
        props.put("index", "text_index");

        when(coll.create(anyString(), eq(InputKind.TcpSplunk), eq(props))).thenReturn(input);

        assertEquals(input, client.createTcpCookedInput("Test", props));
    }

    @Test
    public void testCreateTcpRawInputWithoutProperties() throws Exception {
        TcpInput input = null;
        when(service.getInputs()).thenReturn(coll);
        when(coll.create(anyString(), eq(InputKind.Tcp))).thenReturn(input);

        assertEquals(input, client.createTcpCookedInput("Test", null));
    }

    @Test
    public void testCreateTcpRawInputWithProperties() throws Exception {
        TcpInput input = null;
        when(service.getInputs()).thenReturn(coll);
        HashMap<String, Object> props = new HashMap<String, Object>();
        props.put("index", "text_index");

        when(coll.create(anyString(), eq(InputKind.Tcp), eq(props))).thenReturn(input);

        assertEquals(input, client.createTcpRawInput("Test", props));
    }

    @Test
    public void testCreateUdpInputWithoutProperties() throws Exception {
        UdpInput input = null;
        when(service.getInputs()).thenReturn(coll);
        when(coll.create(anyString(), eq(InputKind.Udp))).thenReturn(input);

        assertEquals(input, client.createUdpInput("Test", null));
    }

    @Test
    public void testCreateUdpInputWithProperties() throws Exception {
        UdpInput input = null;
        when(service.getInputs()).thenReturn(coll);
        HashMap<String, Object> props = new HashMap<String, Object>();
        props.put("index", "text_index");

        when(coll.create(anyString(), eq(InputKind.Udp), eq(props))).thenReturn(input);

        assertEquals(input, client.createUdpInput("Test", props));
    }

    @Test
    public void testCreateWindowsEventLogInputWithoutProperties() throws Exception {
        WindowsEventLogInput input = null;
        when(service.getInputs()).thenReturn(coll);
        when(coll.create(anyString(), eq(InputKind.WindowsEventLog))).thenReturn(input);

        assertEquals(input, client.createWindowsEventLogInput("Test", null));
    }

    @Test
    public void testCreateWindowsEventLogWithProperties() throws Exception {
        WindowsEventLogInput input = null;
        when(service.getInputs()).thenReturn(coll);
        HashMap<String, Object> props = new HashMap<String, Object>();
        props.put("index", "text_index");

        when(coll.create(anyString(), eq(InputKind.WindowsEventLog), eq(props))).thenReturn(input);

        assertEquals(input, client.createWindowsEventLogInput("Test", props));
    }

    @Test
    public void testCreateWindowsPerfmonInputWithoutProperties() throws Exception {
        WindowsPerfmonInput input = null;
        when(service.getInputs()).thenReturn(coll);
        when(coll.create(anyString(), eq(InputKind.WindowsPerfmon))).thenReturn(input);

        assertEquals(input, client.createWindowsPerfmonInput("Test", null));
    }

    @Test
    public void testCreateWindowsPerfmonInputWithProperties() throws Exception {
        WindowsPerfmonInput input = null;
        when(service.getInputs()).thenReturn(coll);
        HashMap<String, Object> props = new HashMap<String, Object>();
        props.put("index", "text_index");

        when(coll.create(anyString(), eq(InputKind.WindowsPerfmon), eq(props))).thenReturn(input);

        assertEquals(input, client.createWindowsPerfmonInput("Test", props));
    }

    @Test
    public void testCreateWindowsWmiInputWithoutProperties() throws Exception {
        WindowsWmiInput input = null;
        when(service.getInputs()).thenReturn(coll);
        when(coll.create(anyString(), eq(InputKind.WindowsWmi))).thenReturn(input);

        assertEquals(input, client.createWindowsWmiInput("Test", null));
    }

    @Test
    public void testCreateWindowsWmiInputWithProperties() throws Exception {
        WindowsWmiInput input = null;
        when(service.getInputs()).thenReturn(coll);
        HashMap<String, Object> props = new HashMap<String, Object>();
        props.put("index", "text_index");

        when(coll.create(anyString(), eq(InputKind.WindowsWmi), eq(props))).thenReturn(input);

        assertEquals(input, client.createWindowsWmiInput("Test", props));
    }

    @Test
    public void testCreateWindowsRegistryInputWithoutProperties() throws Exception {
        WindowsRegistryInput input = null;
        when(service.getInputs()).thenReturn(coll);
        when(coll.create(anyString(), eq(InputKind.WindowsRegistry))).thenReturn(input);

        assertEquals(input, client.createWindowsRegistryInput("Test", null));
    }

    @Test
    public void testCreateWindowsRegistryInputWithProperties() throws Exception {
        WindowsRegistryInput input = null;
        when(service.getInputs()).thenReturn(coll);
        HashMap<String, Object> props = new HashMap<String, Object>();
        props.put("index", "text_index");

        when(coll.create(anyString(), eq(InputKind.WindowsRegistry), eq(props))).thenReturn(input);

        assertEquals(input, client.createWindowsRegistryInput("Test", props));
    }
}
