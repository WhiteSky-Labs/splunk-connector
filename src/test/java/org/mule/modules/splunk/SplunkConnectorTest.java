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
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mule.api.callback.SourceCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

/**
 * Test {@link org.mule.modules.splunk.SplunkConnector} internals
 * <p/>
 */
public class SplunkConnectorTest {

    @Mock
    SplunkClient client;
    @Mock
    Service service;
    private SplunkConnector connector;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        this.connector = new SplunkConnector(client);
        this.connector.setHost("localhost");
        this.connector.setPort(8089);
        this.client.setService(service);
    }

    @Test
    public void testGetApplications() throws Exception {
        List<Application> applist = new ArrayList<Application>();
        when(client.getApplications()).thenReturn(applist);
        assertEquals(applist, connector.getApplications());
    }

    @Test
    public void testCreateSavedSearch() throws Exception {
        SavedSearch search = null;
        when(client.createSavedSearch("Unit Testing", "Search * | head 100", null)).thenReturn(search);
        assertEquals(search, connector.createSavedSearch("Unit Testing", "Search * | head 100", null));
    }

    @Test
    public void testDeleteSavedSearch() throws Exception {
        boolean result = true;
        when(client.deleteSavedSearch("Test")).thenReturn(result);
        assertEquals(result, connector.deleteSavedSearch("Test"));
    }

    @Test
    public void testGetDataModel() throws Exception {
        DataModel model = null;
        when(client.getDataModel("Test")).thenReturn(model);
        assertEquals(model, connector.getDataModel("Test"));
    }

    @Test
    public void testGetDataModels() throws Exception {
        DataModelCollection models = null;
        when(client.getDataModels()).thenReturn(models);
        assertEquals(models, connector.getDataModels());
    }

    @Test
    public void testGetJobs() throws Exception {
        List<Job> jobs = new ArrayList<Job>();
        when(client.getJobs()).thenReturn(jobs);
        assertEquals(jobs, connector.getJobs());
    }

    @Test
    public void testGetSavedSearches() throws Exception {
        List<SavedSearch> savedSearches = new ArrayList<SavedSearch>();
        when(client.getSavedSearches("search", "admin")).thenReturn(savedSearches);
        assertEquals(savedSearches, connector.getSavedSearches("search", "admin"));
    }

    @Test
    public void testGetSavedSearchHistory() throws Exception {
        List<Job> jobs = new ArrayList<Job>();
        when(client.getSavedSearchHistory("Test", "search", "admin")).thenReturn(jobs);
        assertEquals(jobs, connector.getSavedSearchHistory("Test", "search", "admin"));
    }

    @Test
    public void testModifySavedSearchProperties() throws Exception {
        SavedSearch search = null;
        Map<String, Object> props = new HashMap<String, Object>();
        props.put("description", "test");
        when(client.modifySavedSearchProperties("Test", props)).thenReturn(search);
        assertEquals(search, connector.modifySavedSearchProperties("Test", props));
    }

    @Test
    public void testRunBlockingSearchTestCase() throws Exception {
        Map<String, Object> results = new HashMap<String, Object>();
        results.put("Result 1", "Test");
        results.put("Result 2", "Test 2");
        when(client.runBlockingSearch("Test", null)).thenReturn(results);
        assertEquals(results, connector.runBlockingSearch("Test", null));
    }

    @Test
    public void testRunOneShotSearch() throws Exception {
        List<Map<String, Object>> oneshotResult = new ArrayList<Map<String, Object>>();
        Map<String, Object> results = new HashMap<String, Object>();
        results.put("Result 1", "Test");
        results.put("Result 2", "Test 2");
        oneshotResult.add(results);
        when(client.runOneShotSearch("Test", "-1h", "now", null)).thenReturn(oneshotResult);
        assertEquals(oneshotResult, connector.runOneShotSearch("Test", "-1h", "now", null));
    }

    @Test
    public void testRunSavedSearch() throws Exception {
        List<Map<String, Object>> searchResult = new ArrayList<Map<String, Object>>();
        Map<String, Object> results = new HashMap<String, Object>();
        results.put("Result 1", "Test");
        results.put("Result 2", "Test 2");
        searchResult.add(results);
        when(client.runSavedSearch("Test")).thenReturn(searchResult);
        assertEquals(searchResult, connector.runSavedSearch("Test"));
    }

    @Test
    public void testRunSavedSearchWithArguments() throws Exception {
        List<Map<String, Object>> searchResult = new ArrayList<Map<String, Object>>();
        Map<String, Object> results = new HashMap<String, Object>();
        results.put("Result 1", "Test");
        results.put("Result 2", "Test 2");
        searchResult.add(results);

        Map<String, Object> customArgs = new HashMap<String, Object>();
        customArgs.put("alert.email", "Yes");
        customArgs.put("output_mode", "JSON");

        SavedSearchDispatchArgs searchArgs = new SavedSearchDispatchArgs();
        searchArgs.setDispatchLatestTime("now");
        searchArgs.setForceDispatch(true);

        when(client.runSavedSearchWithArguments("Test", customArgs, searchArgs)).thenReturn(searchResult);
        assertEquals(searchResult, connector.runSavedSearchWithArguments("Test", customArgs, searchArgs));
    }

    @Test
    public void testViewSavedSearchProperties() throws Exception {
        Map<String, Object> results = new HashMap<String, Object>();
        results.put("Result 1", "Test");
        results.put("Result 2", "Test 2");
        when(client.viewSavedSearchProperties("Test", "search", "admin")).thenReturn(results.entrySet());
        assertEquals(results.entrySet(), connector.viewSavedSearchProperties("Test", "search", "admin"));
    }

    @Test
    public void testConstructor() throws Exception {
        SplunkConnector testConnector = new SplunkConnector();
        assertNotNull(testConnector);
    }

    @Test
    public void testConnectionIdentifier() throws Exception {
        assertEquals("001", connector.getConnectionIdentifier());
    }

    @Test
    public void testGetPort() throws Exception {
        assertEquals(8089, connector.getPort());
    }

    @Test
    public void testRunRealTimeSearch() throws Exception {
        SourceCallback cb = null;
        doNothing().when(client).runRealTimeSearch(anyString(), anyString(), anyString(), anyInt(), anyInt(), eq(cb));
        connector.runRealTimeSearch("Test", "rt-10m", "rt", 0, 0, cb);
    }

    @Test
    public void testRunNormalSearch() throws Exception {
        SourceCallback cb = null;
        doNothing().when(client).runNormalSearch(anyString(), anyMap(), eq(cb));
        Map<String, Object> customArgs = new HashMap<String, Object>();
        customArgs.put("alert.email", "Yes");
        customArgs.put("output_mode", "JSON");

        connector.runNormalSearch("Test", customArgs, cb);
    }

    @Test
    public void testRunExportSearch() throws Exception {
        SourceCallback cb = null;
        doNothing().when(client).runExportSearch(anyString(), anyString(), anyString(), any(SearchMode.class), any(OutputMode.class), any(JobExportArgs.class), eq(cb));
        connector.runExportSearch("Test", "rt-10m", "rt", cb);
    }

    @Test
    public void testGetInputs() throws Exception {
        InputCollection coll = null;
        when(client.getInputs()).thenReturn(coll);
        assertEquals(coll, connector.getInputs());
    }

    @Test
    public void testCreateActiveDirectoryInputWithoutProperties() throws Exception {
        WindowsActiveDirectoryInput input = null;
        HashMap<String, Object> nullProperties = null;
        when(client.createActiveDirectoryInput(anyString(), eq(nullProperties))).thenReturn(input);
        assertEquals(input, client.createActiveDirectoryInput("Test", nullProperties));
    }

    @Test
    public void testCreateActiveDirectoryInputWithProperties() throws Exception {
        WindowsActiveDirectoryInput input = null;
        HashMap<String, Object> props = new HashMap<String, Object>();
        props.put("index", "text_index");
        when(client.createActiveDirectoryInput(anyString(), eq(props))).thenReturn(input);
        assertEquals(input, client.createActiveDirectoryInput("Test", props));
    }

    @Test
    public void testCreateMonitorInputWithoutProperties() throws Exception {
        MonitorInput input = null;
        HashMap<String, Object> nullProperties = null;
        when(client.createMonitorInput(anyString(), eq(nullProperties))).thenReturn(input);
        assertEquals(input, client.createMonitorInput("Test", nullProperties));
    }

    @Test
    public void testCreateMonitorInputWithProperties() throws Exception {
        MonitorInput input = null;
        HashMap<String, Object> props = new HashMap<String, Object>();
        props.put("index", "text_index");
        when(client.createMonitorInput(anyString(), eq(props))).thenReturn(input);
        assertEquals(input, client.createMonitorInput("Test", props));
    }

    @Test
    public void testCreateScriptInputWithoutProperties() throws Exception {
        ScriptInput input = null;
        HashMap<String, Object> nullProperties = null;
        when(client.createScriptInput(anyString(), eq(nullProperties))).thenReturn(input);
        assertEquals(input, client.createScriptInput("Test", nullProperties));
    }

    @Test
    public void testCreateScriptInputWithProperties() throws Exception {
        ScriptInput input = null;
        HashMap<String, Object> props = new HashMap<String, Object>();
        props.put("index", "text_index");
        when(client.createScriptInput(anyString(), eq(props))).thenReturn(input);
        assertEquals(input, client.createScriptInput("Test", props));
    }

    @Test
    public void testCreateTcpCookedInputWithoutProperties() throws Exception {
        TcpSplunkInput input = null;
        HashMap<String, Object> nullProperties = null;
        when(client.createTcpCookedInput(anyString(), eq(nullProperties))).thenReturn(input);
        assertEquals(input, client.createTcpCookedInput("Test", nullProperties));
    }

    @Test
    public void testCreateTcpCookedInputWithProperties() throws Exception {
        TcpSplunkInput input = null;
        HashMap<String, Object> props = new HashMap<String, Object>();
        props.put("index", "text_index");
        when(client.createTcpCookedInput(anyString(), eq(props))).thenReturn(input);
        assertEquals(input, client.createTcpCookedInput("Test", props));
    }

    @Test
    public void testCreateTcpRawInputWithoutProperties() throws Exception {
        TcpInput input = null;
        HashMap<String, Object> nullProperties = null;
        when(client.createTcpRawInput(anyString(), eq(nullProperties))).thenReturn(input);
        assertEquals(input, client.createTcpRawInput("Test", nullProperties));
    }

    @Test
    public void testCreateTcpRawInputWithProperties() throws Exception {
        TcpInput input = null;
        HashMap<String, Object> props = new HashMap<String, Object>();
        props.put("index", "text_index");
        when(client.createTcpRawInput(anyString(), eq(props))).thenReturn(input);
        assertEquals(input, client.createTcpRawInput("Test", props));
    }

    @Test
    public void testCreateUdpInputWithoutProperties() throws Exception {
        UdpInput input = null;
        HashMap<String, Object> nullProperties = null;
        when(client.createUdpInput(anyString(), eq(nullProperties))).thenReturn(input);
        assertEquals(input, client.createUdpInput("Test", nullProperties));
    }

    @Test
    public void testCreateUdpInputWithProperties() throws Exception {
        UdpInput input = null;
        HashMap<String, Object> props = new HashMap<String, Object>();
        props.put("index", "text_index");
        when(client.createUdpInput(anyString(), eq(props))).thenReturn(input);
        assertEquals(input, client.createUdpInput("Test", props));
    }

    @Test
    public void testCreateWindowsEventLogInputWithoutProperties() throws Exception {
        WindowsEventLogInput input = null;
        HashMap<String, Object> nullProperties = null;
        when(client.createWindowsEventLogInput(anyString(), eq(nullProperties))).thenReturn(input);
        assertEquals(input, client.createWindowsEventLogInput("Test", nullProperties));
    }

    @Test
    public void testCreateWindowsEventLogInputWithProperties() throws Exception {
        WindowsEventLogInput input = null;
        HashMap<String, Object> props = new HashMap<String, Object>();
        props.put("index", "text_index");
        when(client.createWindowsEventLogInput(anyString(), eq(props))).thenReturn(input);
        assertEquals(input, client.createWindowsEventLogInput("Test", props));
    }

    @Test
    public void testCreatewindowsPerfmonInputWithoutProperties() throws Exception {
        WindowsPerfmonInput input = null;
        HashMap<String, Object> nullProperties = null;
        when(client.createWindowsPerfmonInput(anyString(), eq(nullProperties))).thenReturn(input);
        assertEquals(input, client.createWindowsPerfmonInput("Test", nullProperties));
    }

    @Test
    public void testCreateWindowsPerfmonInputWithProperties() throws Exception {
        WindowsPerfmonInput input = null;
        HashMap<String, Object> props = new HashMap<String, Object>();
        props.put("index", "text_index");
        when(client.createWindowsPerfmonInput(anyString(), eq(props))).thenReturn(input);
        assertEquals(input, client.createWindowsPerfmonInput("Test", props));
    }

    @Test
    public void testCreateWindowsWmiInputWithoutProperties() throws Exception {
        WindowsWmiInput input = null;
        HashMap<String, Object> nullProperties = null;
        when(client.createWindowsWmiInput(anyString(), eq(nullProperties))).thenReturn(input);
        assertEquals(input, client.createWindowsWmiInput("Test", nullProperties));
    }

    @Test
    public void testCreateWindowsWmiInputWithProperties() throws Exception {
        WindowsWmiInput input = null;
        HashMap<String, Object> props = new HashMap<String, Object>();
        props.put("index", "text_index");
        when(client.createWindowsWmiInput(anyString(), eq(props))).thenReturn(input);
        assertEquals(input, client.createWindowsWmiInput("Test", props));
    }

    @Test
    public void testCreateWindowsRegistryInputWithoutProperties() throws Exception {
        WindowsRegistryInput input = null;
        HashMap<String, Object> nullProperties = null;
        when(client.createWindowsRegistryInput(anyString(), eq(nullProperties))).thenReturn(input);
        assertEquals(input, client.createWindowsRegistryInput("Test", nullProperties));
    }

    @Test
    public void testCreateWindowsRegistryInputWithProperties() throws Exception {
        WindowsRegistryInput input = null;
        HashMap<String, Object> props = new HashMap<String, Object>();
        props.put("index", "text_index");
        when(client.createWindowsRegistryInput(anyString(), eq(props))).thenReturn(input);
        assertEquals(input, client.createWindowsRegistryInput("Test", props));
    }

    @Test
    public void testGetInput() throws Exception {
        Input input = null;
        when(client.getInput(anyString())).thenReturn(input);
        assertEquals(input, client.getInput("Test"));
    }

    @Test
    public void testModifyInput() throws Exception {
        Input input = null;
        when(client.modifyInput(eq(input), anyMap())).thenReturn(input);
        assertEquals(input, client.modifyInput(input, new HashMap<String, Object>()));
    }

}


