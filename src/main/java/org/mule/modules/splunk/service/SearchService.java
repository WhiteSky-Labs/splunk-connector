package org.mule.modules.splunk.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mule.api.callback.SourceCallback;
import org.mule.modules.splunk.AbstractClient;
import org.mule.modules.splunk.OutputMode;
import org.mule.modules.splunk.SearchMode;

import com.splunk.Args;
import com.splunk.IgnoreFieldPropertyMultiResultsReaderJson;
import com.splunk.Job;
import com.splunk.JobArgs;
import com.splunk.JobExportArgs;
import com.splunk.JobResultsPreviewArgs;
import com.splunk.ResultsReaderXml;

/**
 * Class that provides Search specific functionality
 */
public class SearchService extends AbstractService {

    public SearchService(AbstractClient client) {
        super(client);
    }

    /**
     * Run a search and wait for response
     *
     * @param searchQuery
     *            The search query
     * @param searchArgs
     *            The optional search arguments
     * @param searchCallback
     *            The SourceCallback to return results to
     * @throws Exception
     *             on unsuccessful or interrupted response processing
     */
    public void runNormalSearch(String searchQuery, Map<String, Object> searchArgs, final SourceCallback searchCallback) throws Exception {
        JobArgs jobArgs = new JobArgs();
        if (searchArgs != null) {
            jobArgs.putAll(searchArgs);
        }
        jobArgs.setExecutionMode(JobArgs.ExecutionMode.NORMAL);
        Job job = getService().getJobs()
                .create(searchQuery, jobArgs);

        while (!job.isDone()) {
            Thread.sleep(500);
        }

        Map<String, Object> searchResponse = new HashMap<String, Object>();
        searchResponse.put("job", job);
        searchResponse.put("events", populateEventResponse(job));
        searchCallback.process(searchResponse);
    }

    /**
     * Run a blocking search, which returns synchronously after waiting for the search to complete (blocking the Splunk Server)
     *
     * @param searchQuery
     *            The query string
     * @param searchArgs
     *            An optional map of search arguments
     * @return The results as a HashMap
     * @throws IOException
     *             on unsuccessful response processing
     */
    public Map<String, Object> runBlockingSearch(String searchQuery, Map<String, Object> searchArgs) throws IOException {
        JobArgs jobArgs = new JobArgs();
        if (searchArgs != null) {
            jobArgs.putAll(searchArgs);
        }
        jobArgs.setExecutionMode(JobArgs.ExecutionMode.BLOCKING);
        Job job = getService().getJobs()
                .create(searchQuery, jobArgs);
        Map<String, Object> searchResponse = new HashMap<String, Object>();
        searchResponse.put("job", job);
        searchResponse.put("events", populateEventResponse(job));
        return searchResponse;
    }

    /**
     * Run an export search
     *
     * @param searchQuery
     *            the search query to run
     * @param earliestTime
     *            the earliest time for the search, defaults for an hour before now
     * @param latestTime
     *            the latest time, defaults to now
     * @param searchMode
     *            the searchmode, realtime or normal
     * @param outputMode
     *            the output mode, XML or JSON
     * @param callback
     *            The sourcecallback to return results to
     * @throws Exception
     *             on unsuccessful response processing
     */
    public void runExportSearch(String searchQuery, String earliestTime, String latestTime, SearchMode searchMode, OutputMode outputMode,
            final SourceCallback callback) throws Exception {
        InputStream exportSearch = null;
        JobExportArgs newExportArgs = new JobExportArgs();
        newExportArgs.setEarliestTime(earliestTime);
        newExportArgs.setLatestTime(latestTime);
        newExportArgs.setSearchMode(JobExportArgs.SearchMode.NORMAL);
        newExportArgs.setOutputMode(JobExportArgs.OutputMode.JSON);
        exportSearch = getService().export(searchQuery, newExportArgs);
        processCallback(new IgnoreFieldPropertyMultiResultsReaderJson(exportSearch), callback);
    }

    /**
     * Run a basic oneshot search and display results
     *
     * @param searchQuery
     *            The search query
     * @param earliestTime
     *            The earliest time
     * @param latestTime
     *            The latest time
     * @param args
     *            Optional map of arguments
     * @return A List of HashMaps (The results of the oneshot search)
     * @throws IOException
     *             on unsuccessful response processing
     */
    public List<Map<String, Object>> runOneShotSearch(String searchQuery, String earliestTime, String latestTime, Map<String, String> args) throws IOException {
        Args oneshotSearchArgs = new Args();
        oneshotSearchArgs.put("earliest_time", earliestTime);
        oneshotSearchArgs.put("latest_time", latestTime);
        if (args != null) {
            for (Map.Entry<String, String> entry : args.entrySet()) {
                oneshotSearchArgs.put((String) entry.getKey(), entry.getValue());
            }
        }
        InputStream resultsOneshot = getService().oneshotSearch(searchQuery, oneshotSearchArgs);
        return parseEvents(new ResultsReaderXml(resultsOneshot));
    }

    /**
     * Run a realtime search and process the response returns via a sourcecallback
     *
     * @param searchQuery
     *            The query to run in realtime
     * @param earliestTime
     *            The earliest time to search for, defaults to "rt"
     * @param latestTime
     *            The latest time to search for, defaults to "rt"
     * @param statusBuckets
     *            the status buckets to use - defaults to 300
     * @param previewCount
     *            the number of previews to retrieve - defaults to 100
     * @param callback
     *            The callback object to stream results
     * @throws Exception
     *             on unsuccessful or interrupted response processing
     */
    public void runRealTimeSearch(String searchQuery, String earliestTime, String latestTime, int statusBuckets, int previewCount, final SourceCallback callback)
            throws Exception {
        Job job = null;
        try {
            JobArgs jobArgs = new JobArgs();
            jobArgs.setExecutionMode(JobArgs.ExecutionMode.NORMAL);
            jobArgs.setSearchMode(JobArgs.SearchMode.REALTIME);
            jobArgs.setEarliestTime(earliestTime);
            jobArgs.setLatestTime(latestTime);
            jobArgs.setStatusBuckets(statusBuckets);

            JobResultsPreviewArgs previewArgs = new JobResultsPreviewArgs();
            previewArgs.setCount(previewCount);
            previewArgs.setOutputMode(JobResultsPreviewArgs.OutputMode.JSON);

            job = getService().search(searchQuery, jobArgs);
            while (!job.isReady()) {
                Thread.sleep(500);
            }

            processResults(job, previewArgs, callback);
        } catch (InterruptedException ie) {
            job.finish();
            throw ie;
        } catch (Exception e) {
            job.cancel();
            throw e;
        }
    }
}
