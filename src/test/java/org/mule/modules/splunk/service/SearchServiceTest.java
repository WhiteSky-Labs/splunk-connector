package org.mule.modules.splunk.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mule.api.callback.SourceCallback;
import org.mule.modules.splunk.OutputMode;
import org.mule.modules.splunk.SearchMode;
import org.mule.modules.splunk.SplunkClient;

import com.splunk.Args;
import com.splunk.IgnoreFieldPropertyMultiResultsReaderJson;
import com.splunk.Job;
import com.splunk.JobArgs;
import com.splunk.JobCollection;
import com.splunk.JobExportArgs;
import com.splunk.JobResultsPreviewArgs;
import com.splunk.ResultsReaderXml;
import com.splunk.Service;

public class SearchServiceTest {

    private SearchService service;

    @Mock
    Service clientService;
    @Mock
    SplunkClient client;
    @Mock
    JobCollection jobCollection;
    @Mock
    Job job;
    @Mock
    InputStream inputStream;
    @Mock
    SourceCallback sourceCallback;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        this.service = spy(new SearchService(client));
    }

    @Test
    public void testRunBlockingSearch() throws Exception {
        List<Map<String, Object>> searchResult = new ArrayList<Map<String, Object>>();
        Map<String, Object> searchArgs = new HashMap<>();
        searchArgs.put("key", "value");
        when(service.getService()).thenReturn(clientService);
        when(clientService.getJobs()).thenReturn(jobCollection);
        when(jobCollection.create(eq("searchQuery"), any(JobArgs.class))).thenReturn(job);
        doReturn(searchResult).when(service).populateEventResponse(job);
        Map<String, Object> result = service.runBlockingSearch("searchQuery", searchArgs);
        assertEquals(result.get("job"), job);
    }

    @Test
    public void testRunExportSearch() {
        when(service.getService()).thenReturn(clientService);
        when(clientService.export(eq("searchQuery"), any(JobExportArgs.class))).thenReturn(inputStream);
        try {
            doNothing().when(service).processCallback(any(IgnoreFieldPropertyMultiResultsReaderJson.class), any(SourceCallback.class));
            service.runExportSearch("searchQuery", "-1h", "now", SearchMode.NORMAL, OutputMode.JSON, null);
        } catch (Exception e) {
            fail("Exception not expected");
        }
    }

    @Test
    public void testRunNormalSearch() throws Exception {
        List<Map<String, Object>> jobResult = new ArrayList<Map<String, Object>>();
        Map<String, Object> searchArgs = new HashMap<>();
        searchArgs.put("key", "value");
        when(service.getService()).thenReturn(clientService);
        when(clientService.getJobs()).thenReturn(jobCollection);
        when(jobCollection.create(anyString(), any(JobArgs.class))).thenReturn(job);
        when(job.isDone()).thenReturn(true);
        doReturn(jobResult).when(service).populateEventResponse(job);
        when(sourceCallback.process(any(Map.class))).thenReturn(any(Object.class));
        service.runNormalSearch(anyString(), eq(searchArgs), sourceCallback);
    }

    @Test
    public void testRunOneShotSearch() throws Exception {
        List<Map<String, Object>> searchResult = new ArrayList<Map<String, Object>>();
        Map<String, String> searchArgs = new HashMap<>();
        searchArgs.put("key", "value");
        InputStream stubInputStream = IOUtils.toInputStream("<xml><Test>Bob</Test></xml>");
        when(service.getService()).thenReturn(clientService);
        when(clientService.oneshotSearch(eq("searchQuery"), any(Args.class))).thenReturn(stubInputStream);
        ResultsReaderXml resultsReader;
        resultsReader = new ResultsReaderXml(stubInputStream);
        doReturn(searchResult).when(service).parseEvents(resultsReader);
        assertEquals(searchResult, service.runOneShotSearch("searchQuery", "-1h", "now", searchArgs));
    }

    @Test
    public void testRunRealTimeSearchNullPointerException() {
        when(service.getService()).thenReturn(clientService);
        when(clientService.search(eq("searchQuery"), any(JobArgs.class))).thenReturn(job);
        when(job.isReady()).thenReturn(true);
        try {
            doThrow(new NullPointerException()).when(service).processResults(any(Job.class), any(JobResultsPreviewArgs.class), eq(null));
            service.runRealTimeSearch("searchQuery", "-1h", "now", 1, 1, null);
            fail("Exception Expected");
        } catch (Exception e) {
            if (!(e instanceof NullPointerException)) {
                fail("Exception type not expected");
            }
        }
    }

    @Test
    public void testRunRealTimeSearchInterruptedException() {
        when(service.getService()).thenReturn(clientService);
        when(clientService.search(eq("searchQuery"), any(JobArgs.class))).thenReturn(job);
        when(job.isReady()).thenReturn(true);
        try {
            doThrow(new InterruptedException()).when(service).processResults(any(Job.class), any(JobResultsPreviewArgs.class), eq(null));
            service.runRealTimeSearch("searchQuery", "-1h", "now", 1, 1, null);
            fail("Exception Expected");
        } catch (Exception e) {
            if (!(e instanceof InterruptedException)) {
                fail("Exception type not expected");
            }
        }
    }
}
