package org.mule.modules.splunk.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.io.InputStream;
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
import com.splunk.JobCollection;
import com.splunk.JobResultsArgs;
import com.splunk.ResultsReaderJson;
import com.splunk.SavedSearchDispatchArgs;
import com.splunk.Service;

public class JobServiceTest {

    private JobService service;

    @Mock
    Service clientService;
    @Mock
    SplunkClient client;
    @Mock
    JobCollection jobCollection;
    @Mock
    Job job;
    @Mock
    ResultsReaderJson resultsReaderJson;
    @Mock
    InputStream inputStream;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        this.service = spy(new JobService(client));
    }

    @Test
    public void testGetJobs() throws Exception {
        Map<String, Object> entry = new HashMap<>();
        entry.put("testKey", "testValue");
        List<Job> jobs = new ArrayList<>();
        jobs.add(job);
        when(service.getService()).thenReturn(clientService);
        when(clientService.getJobs()).thenReturn(jobCollection);
        when(jobCollection.values()).thenReturn(jobs);
        when(job.entrySet()).thenReturn(entry.entrySet());
        assertTrue(service.getJobs()
                .size() == 1);
    }

    @Test
    public void testGetService() throws Exception {
        assertEquals(service.getService(), client.getService());
    }

    @Test
    public void testConvertToJavaConvention() {
        String test = "hello_there";
        assertEquals("helloThere", service.convertToJavaConvention(test));
    }

    @Test
    public void testParseEvents() throws Exception {
        List<Map<String, Object>> entries = new ArrayList<>();
        assertEquals(entries, service.parseEvents(resultsReaderJson));
    }

    @Test
    public void testPopulateEventResponseEmptyResult() throws Exception {
        List<Map<String, Object>> entries = new ArrayList<>();
        when(job.getResults(any(JobResultsArgs.class))).thenReturn(inputStream);
        assertEquals(entries, service.parseEvents(resultsReaderJson));
    }

    @Test
    public void testProcessCustomArgs() {
        SavedSearchDispatchArgs savedSearchDispatchArgs = new SavedSearchDispatchArgs();
        Map<String, Object> args = new HashMap<>();
        args.put("testKey", "testValue");
        service.processCustomArgs(args, savedSearchDispatchArgs);
        assertTrue(savedSearchDispatchArgs.size() == 1);
    }

    @Test
    public void testProcessSet() {
        Map<String, Object> entry = new HashMap<>();
        entry.put("testKey", "testValue");
        assertTrue(service.processSet(entry.entrySet())
                .size() == 1);
    }

}
