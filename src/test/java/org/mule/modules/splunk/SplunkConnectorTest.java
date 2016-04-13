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
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
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
import org.mule.api.callback.SourceCallback;
import org.mule.modules.splunk.strategy.ConnectionManagementStrategy;

import com.splunk.CollectionArgs;
import com.splunk.Index;
import com.splunk.Input;
import com.splunk.InputKind;
import com.splunk.SavedSearchDispatchArgs;
import com.splunk.Service;

/**
 * Test {@link org.mule.modules.splunk.SplunkConnector} internals
 * <p/>
 */
public class SplunkConnectorTest {

    @Mock
    SplunkClient client;
    @Mock
    Service service;
    @Mock
    Input input;
    @Mock
    Index index;
    @Mock
    ConnectionManagementStrategy connectionStrategy;

    private SplunkConnector connector;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        this.connector = spy(new SplunkConnector());
        this.connector.setConnectionStrategy(connectionStrategy);
    }

    @Test
    public void testGetClient() {
        when(connector.getConnectionStrategy()).thenReturn(connectionStrategy);
        when(connectionStrategy.getClient()).thenReturn(client);
        assertEquals(client, connector.getClient());
    }

    @Test
    public void testConnectionStrategy() {
        connector.setConnectionStrategy(connectionStrategy);
        assertEquals(connectionStrategy, connector.getConnectionStrategy());
    }

    @Test
    public void testGetApplications() throws Exception {
        List<Map<String, Object>> applist = new ArrayList<Map<String, Object>>();
        when(connectionStrategy.getClient()).thenReturn(client);
        when(client.getApplications()).thenReturn(applist);

        assertEquals(applist, connector.getApplications());
    }

    @Test
    public void testCreateSavedSearch() throws Exception {
        Map<String, Object> search = null;
        when(connectionStrategy.getClient()).thenReturn(client);
        when(client.createSavedSearch("Unit Testing", "Search * | head 100", null)).thenReturn(search);

        assertEquals(search, connector.createSavedSearch("Unit Testing",
                "Search * | head 100", null));
    }

    @Test
    public void testDeleteSavedSearch() throws Exception {
        boolean result = true;
        when(connectionStrategy.getClient()).thenReturn(client);
        when(client.deleteSavedSearch("Test")).thenReturn(result);

        assertEquals(result, connector.deleteSavedSearch("Test"));
    }

    @Test
    public void testGetDataModel() throws Exception {
        Map<String, Object> model = null;
        when(connectionStrategy.getClient()).thenReturn(client);
        when(client.getDataModel("Test")).thenReturn(model);

        assertEquals(model, connector.getDataModel("Test"));
    }

    @Test
    public void testGetDataModels() throws Exception {
        List<Map<String, Object>> models = null;
        when(connectionStrategy.getClient()).thenReturn(client);
        when(client.getDataModels()).thenReturn(models);

        assertEquals(models, connector.getDataModels());
    }

    @Test
    public void testGetJobs() throws Exception {
        List<Map<String, Object>> jobs = new ArrayList<Map<String, Object>>();
        when(connectionStrategy.getClient()).thenReturn(client);
        when(client.getJobs()).thenReturn(jobs);

        assertEquals(jobs, connector.getJobs());
    }

    @Test
    public void testGetSavedSearches() throws Exception {
        List<Map<String, Object>> savedSearches = new ArrayList<Map<String, Object>>();
        when(connectionStrategy.getClient()).thenReturn(client);
        when(client.getSavedSearches("search", "admin")).thenReturn(savedSearches);

        assertEquals(savedSearches, connector.getSavedSearches("search", "admin"));
    }

    @Test
    public void testGetSavedSearchHistory() throws Exception {
        List<Map<String, Object>> jobs = new ArrayList<Map<String, Object>>();
        when(connectionStrategy.getClient()).thenReturn(client);
        when(client.getSavedSearchHistory("Test", "search", "admin")).thenReturn(jobs);

        assertEquals(jobs, connector.getSavedSearchHistory("Test", "search", "admin"));
    }

    @Test
    public void testModifySavedSearchProperties() throws Exception {
        Map<String, Object> props = new HashMap<String, Object>();
        Map<String, Object> result = new HashMap<String, Object>();
        props.put("description", "test");

        when(connectionStrategy.getClient()).thenReturn(client);
        when(client.modifySavedSearchProperties("Test", props)).thenReturn(result);

        assertEquals(result, connector.modifySavedSearchProperties("Test", props));
    }

    @Test
    public void testRunBlockingSearchTestCase() throws Exception {
        Map<String, Object> results = new HashMap<String, Object>();
        results.put("Result 1", "Test");
        results.put("Result 2", "Test 2");

        when(connectionStrategy.getClient()).thenReturn(client);
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

        when(connectionStrategy.getClient()).thenReturn(client);
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

        when(connectionStrategy.getClient()).thenReturn(client);
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

        when(connectionStrategy.getClient()).thenReturn(client);
        when(client.runSavedSearchWithArguments("Test", customArgs, searchArgs)).thenReturn(searchResult);

        assertEquals(searchResult, connector.runSavedSearchWithArguments("Test", customArgs, searchArgs));
    }

    @Test
    public void testViewSavedSearchProperties() throws Exception {
        Map<String, Object> results = new HashMap<String, Object>();
        results.put("Result 1", "Test");
        results.put("Result 2", "Test 2");

        when(connectionStrategy.getClient()).thenReturn(client);
        when(client.viewSavedSearchProperties("Test", "search", "admin")).thenReturn(results.entrySet());

        assertEquals(results.entrySet(), connector.viewSavedSearchProperties("Test", "search", "admin"));
    }

    @Test
    public void testRunRealTimeSearch() throws Exception {
        SourceCallback cb = null;
        doNothing().when(client)
                .runRealTimeSearch(anyString(), anyString(), anyString(), anyInt(), anyInt(), eq(cb));
        when(connectionStrategy.getClient()).thenReturn(client);

        connector.runRealTimeSearch("Test", "rt-10m", "rt", 0, 0, cb);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testRunNormalSearch() throws Exception {
        SourceCallback cb = null;
        Map<String, Object> customArgs = new HashMap<String, Object>();
        customArgs.put("alert.email", "Yes");
        customArgs.put("output_mode", "JSON");

        doNothing().when(client)
                .runNormalSearch(anyString(), anyMap(), eq(cb));
        when(connectionStrategy.getClient()).thenReturn(client);

        connector.runNormalSearch("Test", customArgs, cb);
    }

    @Test
    public void testRunExportSearch() throws Exception {
        SourceCallback cb = null;
        when(connectionStrategy.getClient()).thenReturn(client);
        doNothing().when(client)
                .runExportSearch(anyString(), anyString(), anyString(), any(SearchMode.class), any(OutputMode.class), eq(cb));

        connector.runExportSearch("Test", "rt-10m", "rt", cb);
    }

    @Test
    public void testGetInputs() throws Exception {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        when(connectionStrategy.getClient()).thenReturn(client);
        when(client.getInputs()).thenReturn(result);

        assertEquals(result, connector.getInputs());
    }

    @Test
    public void testCreateInputWithoutProperties() throws Exception {
        HashMap<String, Object> nullProperties = null;
        when(connectionStrategy.getClient()).thenReturn(client);
        when(client.createInput(anyString(), any(InputKind.class), eq(nullProperties))).thenReturn(nullProperties);

        assertEquals(nullProperties, connector.createInput("Test", InputKind.Tcp, nullProperties));
    }

    @Test
    public void testCreateInputWithProperties() throws Exception {
        HashMap<String, Object> result = new HashMap<String, Object>();
        HashMap<String, Object> props = new HashMap<String, Object>();
        props.put("index", "text_index");
        when(connectionStrategy.getClient()).thenReturn(client);
        when(client.createInput(anyString(), any(InputKind.class), eq(props))).thenReturn(result);

        assertEquals(result, connector.createInput("Test", InputKind.Tcp, props));
    }

    @Test
    public void testCreateInputWithEmptyProperties() throws Exception {
        HashMap<String, Object> props = new HashMap<String, Object>();
        when(connectionStrategy.getClient()).thenReturn(client);
        when(client.createInput(anyString(), any(InputKind.class), eq(props))).thenReturn(props);

        assertEquals(props, connector.createInput("Test", InputKind.Tcp, props));
    }

    @Test
    public void testGetInput() throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        when(connectionStrategy.getClient()).thenReturn(client);
        when(client.getInput(anyString())).thenReturn(input);

        assertEquals(result, connector.getInput("Test"));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testModifyInput() throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        when(connectionStrategy.getClient()).thenReturn(client);
        when(client.modifyInput(anyString(), anyMap())).thenReturn(input);

        assertEquals(result, connector.modifyInput("Test", new HashMap<String, Object>()));
    }

    @Test
    public void testGetIndexes() throws Exception {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        when(connectionStrategy.getClient()).thenReturn(client);
        when(client.getIndexes(null, null, null)).thenReturn(result);

        assertEquals(result, connector.getIndexes(null, null, null));
    }

    @Test
    public void testGetIndexesWithParameters() throws Exception {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("assureUTF8", "true");
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

        when(connectionStrategy.getClient()).thenReturn(client);
        when(client.getIndexes(anyString(), any(CollectionArgs.SortDirection.class), eq(params))).thenReturn(result);

        assertEquals(result, connector.getIndexes("Test", CollectionArgs.SortDirection.DESC, params));
    }

    @Test
    public void testCreateIndex() throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        when(connectionStrategy.getClient()).thenReturn(client);
        when(client.createIndex("Test", null)).thenReturn(result);

        assertEquals(result, connector.createIndex("Test", null));
    }

    @Test
    public void testGetIndex() throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        when(connectionStrategy.getClient()).thenReturn(client);
        when(client.getIndex(anyString())).thenReturn(result);

        assertEquals(result, connector.getIndex("Test"));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testModifyIndex() throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        when(connectionStrategy.getClient()).thenReturn(client);
        when(client.modifyIndex(anyString(), anyMap())).thenReturn(result);

        assertEquals(result, connector.modifyIndex("Test", new HashMap<String, Object>()));
    }

    @Test
    public void testCleanIndex() throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        when(connectionStrategy.getClient()).thenReturn(client);
        when(client.cleanIndex("Test", 120)).thenReturn(result);

        assertEquals(result, connector.cleanIndex("Test", 120));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testAddDataToIndex() throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        when(connectionStrategy.getClient()).thenReturn(client);
        when(client.addDataToIndex(anyString(), anyString(), anyMap())).thenReturn(index);

        assertEquals(result, connector.addDataToIndex("Test", "Test", new HashMap<String, Object>()));
    }

    @Test
    public void testAddDataToTcpInput() throws Exception {
        when(connectionStrategy.getClient()).thenReturn(client);
        when(client.addDataToTcpInput(anyString(), anyString())).thenReturn(true);
        assertEquals(true, connector.addDataToTcpInput("Test", "Test"));
    }

    @Test
    public void testAddDataToUdpInput() throws Exception {
        when(connectionStrategy.getClient()).thenReturn(client);
        when(client.addDataToUdpInput(anyString(), anyString())).thenReturn(true);
        assertEquals(true, connector.addDataToUdpInput("Test", "Test"));
    }

    @Test
    public void testRemoveInput() throws Exception {
        when(connectionStrategy.getClient()).thenReturn(client);
        when(client.removeInput(anyString())).thenReturn(true);
        assertEquals(true, connector.removeInput("Test"));
    }

    @Test
    public void testRemoveIndex() throws Exception {
        when(connectionStrategy.getClient()).thenReturn(client);
        when(client.removeIndex(anyString())).thenReturn(true);
        assertEquals(true, connector.removeIndex("Test"));
    }

}