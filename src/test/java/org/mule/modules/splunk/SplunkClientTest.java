/**
 *
 * (c) 2016 WhiteSky Labs, Pty Ltd. This software is protected under international
 * copyright law. All use of this software is subject to WhiteSky Labs' Master
 * Subscription Agreement (or other Terms of Service) separately entered
 * into between you and WhiteSky Labs. If such an agreement is not in
 * place, you may not use the software.
 */
package org.mule.modules.splunk;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mule.api.ConnectionException;
import org.mule.modules.splunk.exception.SplunkConnectorException;
import org.mule.modules.splunk.service.ApplicationService;
import org.mule.modules.splunk.service.DataModelService;
import org.mule.modules.splunk.service.IndexService;
import org.mule.modules.splunk.service.InputService;
import org.mule.modules.splunk.service.JobService;
import org.mule.modules.splunk.service.SavedSearchService;
import org.mule.modules.splunk.service.SearchService;
import org.mule.modules.splunk.strategy.ConnectionManagementStrategy;

import com.splunk.InputKind;
import com.splunk.Service;

/**
 * Test {@link org.mule.modules.splunk.SplunkConnector} internals
 * <p/>
 */
public class SplunkClientTest {

    SplunkClient client;

    @Mock
    SplunkConnector connector;
    @Mock
    ConnectionManagementStrategy connectionStrategy;
    @Mock
    Service service;
    @Mock
    ApplicationService applicationService;
    @Mock
    DataModelService dataModelService;
    @Mock
    IndexService indexService;
    @Mock
    InputService inputService;
    @Mock
    JobService jobService;
    @Mock
    SavedSearchService savedSearchService;
    @Mock
    SearchService searchService;
    @Mock
    Map<String, Object> entry;
    @Mock
    Map.Entry<String, Object> mapEntry;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        this.client = spy(new SplunkClient());
        client.setService(service);
    }

    @Test
    public void testGetApplications() {
        List<Map<String, Object>> expected = new ArrayList<Map<String, Object>>();
        expected.add(entry);
        when(client.getApplicationService()).thenReturn(applicationService);
        when(applicationService.getApplications()).thenReturn(expected);
        List<Map<String, Object>> actual = client.getApplications();
        assertEquals(1, actual.size());
        assertEquals(true, actual.contains(entry));
    }

    @Test
    public void testGetDataModel() throws Exception {
        Map<String, Object> expected = new HashMap<String, Object>();
        expected.put("testKey", "testValue");
        when(client.getDataModelService()).thenReturn(dataModelService);
        when(dataModelService.getDataModel("Test")).thenReturn(expected);
        Map<String, Object> actual = client.getDataModel("Test");
        assertEquals(1, actual.size());
        assertEquals(expected.get("testKey"), actual.get("testKey"));
    }

    @Test
    public void testGetDataModelEmptyDataModelName() {
        try {
            client.getDataModel(null);
            fail("Exception expected.");
        } catch (SplunkConnectorException e) {
            assertEquals("You must provide a data model name", e.getMessage());
        } catch (Exception e) {
            fail("Exception type not expected.");
        }
    }

    @Test
    public void testGetDataModels() {
        List<Map<String, Object>> expected = new ArrayList<Map<String, Object>>();
        expected.add(entry);
        when(client.getDataModelService()).thenReturn(dataModelService);
        when(dataModelService.getDataModels()).thenReturn(expected);
        List<Map<String, Object>> actual = client.getDataModels();
        assertEquals(1, actual.size());
        assertEquals(true, actual.contains(entry));
    }

    @Test
    public void testAddDataToIndex() throws Exception {
        Map<String, Object> expected = new HashMap<String, Object>();
        expected.put("testKey", "testValue");
        when(client.getIndexService()).thenReturn(indexService);
        when(indexService.addDataToIndex("indexName", "stringData", null)).thenReturn(expected);
        Map<String, Object> actual = client.addDataToIndex("indexName", "stringData", null);
        assertEquals(1, actual.size());
        assertEquals(expected.get("testKey"), actual.get("testKey"));
    }

    @Test
    public void testAddDataToIndexEmptyIndexName() {
        try {
            client.addDataToIndex(null, "stringData", null);
            fail("Exception expected.");
        } catch (SplunkConnectorException e) {
            assertEquals("You must provide an index name", e.getMessage());
        } catch (Exception e) {
            fail("Exception type not expected.");
        }
    }

    @Test
    public void testAddDataToIndexEmptyStringData() {
        try {
            client.addDataToIndex("indexName", null, null);
            fail("Exception expected.");
        } catch (SplunkConnectorException e) {
            assertEquals("You must provide some string data", e.getMessage());
        } catch (Exception e) {
            fail("Exception type not expected.");
        }
    }

    @Test
    public void testAddDataToIndexNotFoundIndexName() {
        when(indexService.addDataToIndex("indexName", "stringData", null)).thenThrow(new NullPointerException());
        try {
            client.addDataToIndex("indexName", "stringData", null);
            fail("Exception expected.");
        } catch (SplunkConnectorException e) {
            assertEquals("Unable to find Index with indexName=indexName", e.getMessage());
        } catch (Exception e) {
            fail("Exception type not expected.");
        }
    }

    @Test
    public void testCleanIndex() throws Exception {
        Map<String, Object> expected = new HashMap<String, Object>();
        expected.put("testKey", "testValue");
        when(client.getIndexService()).thenReturn(indexService);
        when(indexService.cleanIndex("indexName", 120)).thenReturn(expected);
        Map<String, Object> actual = client.cleanIndex("indexName", 120);
        assertEquals(1, actual.size());
        assertEquals(expected.get("testKey"), actual.get("testKey"));
    }

    @Test
    public void testCleanIndexEmptyIndexName() {
        try {
            client.cleanIndex("", 120);
            fail("Exception expected.");
        } catch (SplunkConnectorException e) {
            assertEquals("You must provide an index name", e.getMessage());
        } catch (Exception e) {
            fail("Exception type not expected.");
        }
    }

    @Test
    public void testCleanIndexNotFoundIndexName() {
        when(indexService.cleanIndex("indexName", 120)).thenThrow(new NullPointerException());
        try {
            client.cleanIndex("indexName", 120);
            fail("Exception expected.");
        } catch (SplunkConnectorException e) {
            assertEquals("Unable to find Index with indexName=indexName", e.getMessage());
        } catch (Exception e) {
            fail("Exception type not expected.");
        }
    }

    @Test
    public void testCreateIndex() throws Exception {
        Map<String, Object> expected = new HashMap<String, Object>();
        expected.put("testKey", "testValue");
        when(client.getIndexService()).thenReturn(indexService);
        when(indexService.createIndex("indexName", null)).thenReturn(expected);
        Map<String, Object> actual = client.createIndex("indexName", null);
        assertEquals(1, actual.size());
        assertEquals(expected.get("testKey"), actual.get("testKey"));
    }

    @Test
    public void testGetIndex() throws Exception {
        Map<String, Object> expected = new HashMap<String, Object>();
        expected.put("testKey", "testValue");
        when(client.getIndexService()).thenReturn(indexService);
        when(indexService.getIndex("indexName")).thenReturn(expected);
        Map<String, Object> actual = client.getIndex("indexName");
        assertEquals(1, actual.size());
        assertEquals(expected.get("testKey"), actual.get("testKey"));
    }

    @Test
    public void testGetIndexEmptyIndexName() {
        try {
            client.getIndex("");
            fail("Exception expected.");
        } catch (SplunkConnectorException e) {
            assertEquals("You must provide a valid index name", e.getMessage());
        } catch (Exception e) {
            fail("Exception type not expected.");
        }
    }

    @Test
    public void testGetIndexes() {
        List<Map<String, Object>> expected = new ArrayList<Map<String, Object>>();
        expected.add(entry);
        when(client.getIndexService()).thenReturn(indexService);
        when(indexService.getIndexes(null, null, null)).thenReturn(expected);
        List<Map<String, Object>> actual = client.getIndexes(null, null, null);
        assertEquals(1, actual.size());
        assertEquals(true, actual.contains(entry));
    }

    @Test
    public void testModifyIndex() throws Exception {
        Map<String, Object> expected = new HashMap<String, Object>();
        expected.put("assureUTF8", "true");
        when(client.getIndexService()).thenReturn(indexService);
        when(indexService.modifyIndex("indexName", expected)).thenReturn(expected);
        Map<String, Object> actual = client.modifyIndex("indexName", expected);
        assertEquals(1, actual.size());
        assertEquals(expected.get("assureUTF8"), actual.get("assureUTF8"));
    }

    @Test
    public void testModifyIndexWithEmptyIndexName() {
        Map<String, Object> expected = new HashMap<String, Object>();
        expected.put("assureUTF8", "true");
        try {
            client.modifyIndex("", expected);
            fail("Exception expected.");
        } catch (SplunkConnectorException e) {
            assertEquals("You must provide a valid index name", e.getMessage());
        } catch (Exception e) {
            fail("Exception type not expected.");
        }
    }

    @Test
    public void testModifyIndexWithEmptyProperties() {
        try {
            client.modifyIndex("indexName", null);
            fail("Exception expected.");
        } catch (SplunkConnectorException e) {
            assertEquals("You must provide some properties to modify", e.getMessage());
        } catch (Exception e) {
            fail("Exception type not expected.");
        }
    }

    @Test
    public void testRemoveIndex() {
        when(client.getIndexService()).thenReturn(indexService);
        when(indexService.removeIndex("indexName")).thenReturn(true);
        assertEquals(true, client.removeIndex("indexName"));
    }

    @Test
    public void testAddDataToTcpInput() {
        when(client.getInputService()).thenReturn(inputService);
        when(inputService.addDataToTcpInput("8888", "Test Data")).thenReturn(true);
        assertEquals(true, client.addDataToTcpInput("8888", "Test Data"));
    }

    @Test
    public void testAddDataToUdpInput() {
        when(client.getInputService()).thenReturn(inputService);
        when(inputService.addDataToUdpInput("8888", "Test Data")).thenReturn(true);
        assertEquals(true, client.addDataToUdpInput("8888", "Test Data"));
    }

    @Test
    public void testCreateInput() throws Exception {
        Map<String, Object> expected = new HashMap<String, Object>();
        expected.put("input_key", "text_input");
        when(client.getInputService()).thenReturn(inputService);
        when(inputService.createInput("inputName", InputKind.TcpSplunk, expected)).thenReturn(expected);
        Map<String, Object> actual = client.createInput("inputName", InputKind.TcpSplunk, expected);
        assertEquals(1, actual.size());
        assertEquals(expected.get("input_key"), actual.get("input_key"));
    }

    @Test
    public void testGetInput() throws Exception {
        Map<String, Object> expected = new HashMap<String, Object>();
        expected.put("input_key", "text_input");
        when(client.getInputService()).thenReturn(inputService);
        when(inputService.getInput("inputName")).thenReturn(expected);
        Map<String, Object> actual = client.getInput("inputName");
        assertEquals(1, actual.size());
        assertEquals(expected.get("input_key"), actual.get("input_key"));
    }

    @Test
    public void testGetInputNoInputIdentifier() {
        Map<String, Object> expected = new HashMap<String, Object>();
        expected.put("input_key", "text_input");
        try {
            client.getInput("");
            fail("Exception expected.");
        } catch (SplunkConnectorException e) {
            assertEquals("You must provide a valid input identifier", e.getMessage());
        } catch (Exception e) {
            fail("Exception type not expected.");
        }
    }

    @Test
    public void testGetInputs() {
        List<Map<String, Object>> expected = new ArrayList<Map<String, Object>>();
        expected.add(entry);
        when(client.getInputService()).thenReturn(inputService);
        when(inputService.getInputs()).thenReturn(expected);
        List<Map<String, Object>> actual = client.getInputs();
        assertEquals(1, actual.size());
        assertEquals(true, actual.contains(entry));
    }

    @Test
    public void testModifyInput() throws Exception {
        Map<String, Object> expected = new HashMap<String, Object>();
        expected.put("input_key", "text_input");
        when(client.getInputService()).thenReturn(inputService);
        when(inputService.modifyInput("inputName", expected)).thenReturn(expected);
        Map<String, Object> actual = client.modifyInput("inputName", expected);
        assertEquals(1, actual.size());
        assertEquals(expected.get("input_key"), actual.get("input_key"));
    }

    @Test
    public void testModifyInputWithEmptyProperties() {
        try {
            client.modifyInput("inputName", null);
            fail("Exception expected.");
        } catch (SplunkConnectorException e) {
            assertEquals("You must provide some properties to modify", e.getMessage());
        } catch (Exception e) {
            fail("Exception type not expected.");
        }
    }

    @Test
    public void testModifyInputWithEmptyInputName() {
        Map<String, Object> expected = new HashMap<String, Object>();
        expected.put("input_key", "text_input");
        try {
            client.modifyInput("", expected);
            fail("Exception expected.");
        } catch (SplunkConnectorException e) {
            assertEquals("You must provide a valid input identifier", e.getMessage());
        } catch (Exception e) {
            fail("Exception type not expected.");
        }
    }

    @Test
    public void testModifyInputWithInvalidInputName() {
        Map<String, Object> expected = new HashMap<String, Object>();
        expected.put("input_key", "text_input");
        when(inputService.modifyInput("invalidName", expected)).thenThrow(new NullPointerException());
        try {
            client.modifyInput("invalidName", expected);
            fail("Exception expected.");
        } catch (SplunkConnectorException e) {
            assertEquals("Unable to find Input with inputIdentifier=invalidName", e.getMessage());
        } catch (Exception e) {
            fail("Exception type not expected.");
        }
    }

    @Test
    public void testRemoveInput() throws Exception {
        when(client.getInputService()).thenReturn(inputService);
        when(inputService.removeInput("inputName")).thenReturn(true);
        assertEquals(true, client.removeInput("inputName"));
    }

    @Test
    public void testGetJobs() throws Exception {
        List<Map<String, Object>> expected = new ArrayList<Map<String, Object>>();
        expected.add(entry);
        when(client.getJobService()).thenReturn(jobService);
        when(jobService.getJobs()).thenReturn(expected);
        List<Map<String, Object>> actual = client.getJobs();
        assertEquals(1, actual.size());
        assertEquals(true, actual.contains(entry));
    }

    @Test
    public void testCreateSavedSearch() throws Exception {
        Map<String, Object> expected = new HashMap<String, Object>();
        expected.put("input_key", "text_input");
        when(client.getSavedSearchService()).thenReturn(savedSearchService);
        when(savedSearchService.createSavedSearch("searchName", "searchQuery", expected)).thenReturn(expected);
        Map<String, Object> actual = client.createSavedSearch("searchName", "searchQuery", expected);
        assertEquals(1, actual.size());
        assertEquals(expected.get("input_key"), actual.get("input_key"));
    }

    @Test
    public void testCreateSavedSearchWithNoSearchName() {
        try {
            client.createSavedSearch("", "searchQuery", null);
            fail("Exception expected.");
        } catch (SplunkConnectorException e) {
            assertEquals("Search Name empty.", e.getMessage());
        } catch (Exception e) {
            fail("Exception type not expected.");
        }
    }

    @Test
    public void testCreateSavedSearchWithNoSearchQuery() {
        try {
            client.createSavedSearch("searchName", "", null);
            fail("Exception expected.");
        } catch (SplunkConnectorException e) {
            assertEquals("Search Query empty.", e.getMessage());
        } catch (Exception e) {
            fail("Exception type not expected.");
        }
    }

    @Test
    public void testDeleteSavedSearch() throws Exception {
        when(client.getSavedSearchService()).thenReturn(savedSearchService);
        when(savedSearchService.deleteSavedSearch("searchName")).thenReturn(true);
        assertEquals(true, client.deleteSavedSearch("searchName"));
    }

    @Test
    public void testGetSavedSearches() throws Exception {
        List<Map<String, Object>> expected = new ArrayList<Map<String, Object>>();
        expected.add(entry);
        when(client.getSavedSearchService()).thenReturn(savedSearchService);
        when(savedSearchService.getSavedSearches("app", "owner")).thenReturn(expected);
        List<Map<String, Object>> actual = client.getSavedSearches("app", "owner");
        assertEquals(1, actual.size());
        assertEquals(true, actual.contains(entry));
    }

    @Test
    public void testGetSavedSearchHistory() throws Exception {
        List<Map<String, Object>> expected = new ArrayList<Map<String, Object>>();
        expected.add(entry);
        when(client.getSavedSearchService()).thenReturn(savedSearchService);
        when(savedSearchService.getSavedSearchHistory("searchName", "app", "owner")).thenReturn(expected);
        List<Map<String, Object>> actual = client.getSavedSearchHistory("searchName", "app", "owner");
        assertEquals(1, actual.size());
        assertEquals(true, actual.contains(entry));
    }

    @Test
    public void testModifySavedSearchProperties() throws Exception {
        Map<String, Object> expected = new HashMap<String, Object>();
        expected.put("input_key", "text_input");
        when(client.getSavedSearchService()).thenReturn(savedSearchService);
        when(savedSearchService.modifySavedSearchProperties("searchName", expected)).thenReturn(expected);
        Map<String, Object> actual = client.modifySavedSearchProperties("searchName", expected);
        assertEquals(1, actual.size());
        assertEquals(expected.get("input_key"), actual.get("input_key"));
    }

    @Test
    public void testModifySavedSearchPropertiesNoSearchName() {
        Map<String, Object> expected = new HashMap<String, Object>();
        expected.put("input_key", "text_input");
        try {
            client.modifySavedSearchProperties("", expected);
            fail("Exception expected.");
        } catch (SplunkConnectorException e) {
            assertEquals("You must provide a search name to modify", e.getMessage());
        } catch (Exception e) {
            fail("Exception type not expected.");
        }
    }

    @Test
    public void testModifySavedSearchPropertiesNoProperties() throws Exception {
        try {
            client.modifySavedSearchProperties("searchName", null);
            fail("Exception expected.");
        } catch (SplunkConnectorException e) {
            assertEquals("You must provide some properties to modify", e.getMessage());
        } catch (Exception e) {
            fail("Exception type not expected.");
        }
    }

    @Test
    public void testModifySavedSearchPropertiesInvalidSearchName() {
        Map<String, Object> expected = new HashMap<String, Object>();
        expected.put("input_key", "text_input");
        when(savedSearchService.modifySavedSearchProperties("invalidName", expected)).thenThrow(new NullPointerException());
        try {
            client.modifySavedSearchProperties("invalidName", expected);
            fail("Exception expected.");
        } catch (SplunkConnectorException e) {
            assertEquals("Unable to find Saved Search with searchName=invalidName", e.getMessage());
        } catch (Exception e) {
            fail("Exception type not expected.");
        }
    }

    @Test
    public void testRunSavedSearch() throws Exception {
        List<Map<String, Object>> expected = new ArrayList<Map<String, Object>>();
        expected.add(entry);
        when(client.getSavedSearchService()).thenReturn(savedSearchService);
        when(savedSearchService.runSavedSearch("searchName")).thenReturn(expected);
        List<Map<String, Object>> actual = client.runSavedSearch("searchName");
        assertEquals(1, actual.size());
        assertEquals(true, actual.contains(entry));
    }

    @Test
    public void testRunSavedSearchInvalidSearchName() {
        try {
            when(savedSearchService.runSavedSearch("searchName")).thenThrow(new NullPointerException());
            client.runSavedSearch("invalidName");
            fail("Exception expected.");
        } catch (SplunkConnectorException e) {
            assertEquals("Unable to find Saved Search with searchName=invalidName", e.getMessage());
        } catch (Exception e) {
            fail("Exception type not expected.");
        }
    }

    @Test
    public void testRunSavedSearchNoSearchName() {
        try {
            client.runSavedSearch("");
            fail("Exception expected.");
        } catch (SplunkConnectorException e) {
            assertEquals("You must provide a valid search name", e.getMessage());
        } catch (Exception e) {
            fail("Exception type not expected.");
        }
    }

    @Test
    public void testRunSavedSearchWithArguments() throws Exception {
        List<Map<String, Object>> expected = new ArrayList<Map<String, Object>>();
        expected.add(entry);
        when(client.getSavedSearchService()).thenReturn(savedSearchService);
        when(savedSearchService.runSavedSearchWithArguments("searchName", null, null)).thenReturn(expected);
        List<Map<String, Object>> actual = client.runSavedSearchWithArguments("searchName", null, null);
        assertEquals(1, actual.size());
        assertEquals(true, actual.contains(entry));
    }

    @Test
    public void testRunSavedSearchWithArgumentsInvalidSearchName() {
        try {
            when(savedSearchService.runSavedSearchWithArguments("invalidName", null, null)).thenThrow(new NullPointerException());
            client.runSavedSearchWithArguments("invalidName", null, null);
            fail("Exception expected.");
        } catch (SplunkConnectorException e) {
            assertEquals("Unable to find Saved Search with searchName=invalidName", e.getMessage());
        } catch (Exception e) {
            fail("Exception type not expected.");
        }
    }

    @Test
    public void testRunSavedSearchWithArgumentsNoSearchName() {
        try {
            client.runSavedSearchWithArguments("", null, null);
            fail("Exception expected.");
        } catch (SplunkConnectorException e) {
            assertEquals("You must provide a valid search name", e.getMessage());
        } catch (Exception e) {
            fail("Exception type not expected.");
        }
    }

    @Test
    public void testViewSavedSearchProperties() throws Exception {
        Set<Map.Entry<String, Object>> expected = new HashSet<Map.Entry<String, Object>>();
        expected.add(mapEntry);
        when(client.getSavedSearchService()).thenReturn(savedSearchService);
        when(savedSearchService.viewSavedSearchProperties("searchName", "app", "owner")).thenReturn(expected);
        Set<Map.Entry<String, Object>> actual = client.viewSavedSearchProperties("searchName", "app", "owner");
        assertEquals(1, actual.size());
        assertTrue(actual.contains(mapEntry));
    }

    @Test
    public void testViewSavedSearchPropertiesNoSearchName() {
        try {
            client.viewSavedSearchProperties("", null, null);
            fail("Exception expected.");
        } catch (SplunkConnectorException e) {
            assertEquals("Search Name empty.", e.getMessage());
        } catch (Exception e) {
            fail("Exception type not expected.");
        }
    }

    @Test
    public void testViewSavedSearchPropertiesNoResult() throws Exception {
        when(client.getSavedSearchService()).thenReturn(savedSearchService);
        when(savedSearchService.viewSavedSearchProperties("noResult", null, null)).thenThrow(new NullPointerException());
        Set<Map.Entry<String, Object>> actual = client.viewSavedSearchProperties("noResult", null, null);
        assertTrue(actual.isEmpty());
    }

    @Test
    public void testRunBlockingSearch() throws Exception {
        Map<String, Object> expected = new HashMap<String, Object>();
        expected.put("input_key", "text_input");
        when(client.getSearchService()).thenReturn(searchService);
        when(searchService.runBlockingSearch("searchName", expected)).thenReturn(expected);
        Map<String, Object> actual = client.runBlockingSearch("searchName", expected);
        assertEquals(1, actual.size());
        assertEquals(expected.get("input_key"), actual.get("input_key"));
    }

    @Test
    public void testRunBlockingSearchNoSearchQuery() {
        try {
            client.runBlockingSearch("", null);
            fail("Exception expected.");
        } catch (SplunkConnectorException e) {
            assertEquals("Search Query is empty.", e.getMessage());
        } catch (Exception e) {
            fail("Exception type not expected.");
        }
    }

    @Test
    public void testRunExportSearch() throws Exception {
        when(client.getSearchService()).thenReturn(searchService);
        doNothing().when(searchService)
                .runExportSearch("searchName", "-1h", "now", SearchMode.NORMAL, OutputMode.JSON, null);
        client.runExportSearch("searchName", "-1h", "now", SearchMode.NORMAL, OutputMode.JSON, null);
    }

    @Test
    public void testRunExportSearchNoSearchQuery() {
        try {
            client.runExportSearch("", "-1h", "now", SearchMode.NORMAL, OutputMode.JSON, null);
            fail("Exception expected.");
        } catch (SplunkConnectorException e) {
            assertEquals("Search Query is empty.", e.getMessage());
        } catch (Exception e) {
            fail("Exception type not expected.");
        }
    }

    @Test
    public void testRunExportSearchError() {
        try {
            client.runExportSearch("searchName", "-1h", "now", SearchMode.NORMAL, OutputMode.JSON, null);
            fail("Exception expected.");
        } catch (SplunkConnectorException e) {
            assertEquals("Error processing callback", e.getMessage());
        } catch (Exception e) {
            fail("Exception type not expected.");
        }
    }

    @Test
    public void testRunNormalSearch() throws Exception {
        when(client.getSearchService()).thenReturn(searchService);
        doNothing().when(searchService)
                .runNormalSearch("searchName", null, null);
        client.runNormalSearch("searchName", null, null);
    }

    @Test
    public void testRunNormalSearchNoSearchQuery() {
        try {
            client.runNormalSearch("", null, null);
            fail("Exception expected.");
        } catch (SplunkConnectorException e) {
            assertEquals("Search Query is empty.", e.getMessage());
        } catch (Exception e) {
            fail("Exception type not expected.");
        }
    }

    @Test
    public void testRunNormalSearchError() {
        when(client.getSearchService()).thenReturn(searchService);
        try {
            doThrow(new InterruptedException()).when(searchService)
                    .runNormalSearch("searchName", null, null);
            client.runNormalSearch("searchName", null, null);
            fail("Exception expected.");
        } catch (SplunkConnectorException e) {
            assertEquals("Polling for Normal Search results was interrupted", e.getMessage());
        } catch (Exception e) {
            fail("Exception type not expected.");
        }
    }

    @Test
    public void testRunOneShotSearch() throws Exception {
        List<Map<String, Object>> expected = new ArrayList<Map<String, Object>>();
        expected.add(entry);
        when(client.getSearchService()).thenReturn(searchService);
        when(searchService.runOneShotSearch("searchQuery", "-1h", "now", null)).thenReturn(expected);
        List<Map<String, Object>> actual = client.runOneShotSearch("searchQuery", "-1h", "now", null);
        assertEquals(1, actual.size());
        assertEquals(true, actual.contains(entry));
    }

    @Test
    public void testRunOneShotSearchNoSearchQuery() throws Exception {
        try {
            client.runOneShotSearch("", "-1h", "now", null);
            fail("Exception expected.");
        } catch (SplunkConnectorException e) {
            assertEquals("Search Query is empty.", e.getMessage());
        } catch (Exception e) {
            fail("Exception type not expected.");
        }
    }

    @Test
    public void testRunOneShotSearchNoEarliestTime() throws Exception {
        try {
            client.runOneShotSearch("searchQuery", "", "now", null);
            fail("Exception expected.");
        } catch (SplunkConnectorException e) {
            assertEquals("Earliest Time is empty.", e.getMessage());
        } catch (Exception e) {
            fail("Exception type not expected.");
        }
    }

    @Test
    public void testRunOneShotSearchNoLatestTime() throws Exception {
        try {
            client.runOneShotSearch("searchQuery", "-1h", "", null);
            fail("Exception expected.");
        } catch (SplunkConnectorException e) {
            assertEquals("Latest Time is empty.", e.getMessage());
        } catch (Exception e) {
            fail("Exception type not expected.");
        }
    }

    @Test
    public void testRunRealTimeSearch() throws Exception {
        when(client.getSearchService()).thenReturn(searchService);
        doNothing().when(searchService)
                .runRealTimeSearch("searchQuery", "-1h", "now", 1, 1, null);
        client.runRealTimeSearch("searchQuery", "-1h", "now", 1, 1, null);
    }

    @Test
    public void testRunRealTimeSearchNoSearchQuery() throws Exception {
        try {
            client.runRealTimeSearch("", "-1h", "now", 1, 1, null);
            fail("Exception expected.");
        } catch (SplunkConnectorException e) {
            assertEquals("Search Query is empty.", e.getMessage());
        } catch (Exception e) {
            fail("Exception type not expected.");
        }
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
}
