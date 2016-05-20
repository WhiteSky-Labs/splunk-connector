/**
 *
 * (c) 2016 WhiteSky Labs, Pty Ltd. This software is protected under international
 * copyright law. All use of this software is subject to WhiteSky Labs' Master
 * Subscription Agreement (or other Terms of Service) separately entered
 * into between you and WhiteSky Labs. If such an agreement is not in
 * place, you may not use the software.
 */
package org.mule.modules.splunk.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mule.api.callback.SourceCallback;
import org.mule.modules.splunk.AbstractClient;

import com.google.common.base.CaseFormat;
import com.splunk.Event;
import com.splunk.Job;
import com.splunk.JobResultsArgs;
import com.splunk.JobResultsPreviewArgs;
import com.splunk.MultiResultsReader;
import com.splunk.ResultsReader;
import com.splunk.ResultsReaderJson;
import com.splunk.SavedSearchDispatchArgs;
import com.splunk.SearchResults;
import com.splunk.Service;

/**
 * Abstract class for Splunk Services
 *
 */
public abstract class AbstractService {

    private Service service;

    public AbstractService(AbstractClient client) {
        this.service = client.getService();
    }

    protected Service getService() {
        return service;
    }

    /**
     * Performs processing on a Set
     * 
     * @param set
     *            containing raw data
     * @return A map containing processed data
     */
    protected Map<String, Object> processSet(Set<Map.Entry<String, Object>> set) {
        Map<String, Object> mapFromSet = new HashMap<String, Object>();
        for (Map.Entry<String, Object> entry : set) {
            mapFromSet.put(entry.getKey(), entry.getValue());
        }
        return mapFromSet;
    }

    /**
     * Convert the java key
     *
     * @param key
     *            - the String key to convert to the java convention (camelCase)
     * @return A string converted from javascript (naming_conventions) to Java namingConventions
     */
    protected String convertToJavaConvention(String key) {
        String result = key.startsWith("_") ? key.substring(1) : key;
        return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, result);
    }

    /**
     * Parse the events
     *
     * @param resultsReader
     *            A results reader (can be XML or JSON)
     * @return A List of HashMaps (the results)
     * @throws IOException on processing error
     */
    protected List<Map<String, Object>> parseEvents(ResultsReader resultsReader) throws IOException {
        List<Map<String, Object>> searchResponseList = new ArrayList<Map<String, Object>>();
        Map<String, String> event = null;
        while ((event = resultsReader.getNextEvent()) != null) {
            Map<String, Object> eventData = new HashMap<String, Object>();
            for (Map.Entry<String, String> entry : event.entrySet()) {
                eventData.put(convertToJavaConvention(entry.getKey()), entry.getValue());
            }
            searchResponseList.add(eventData);
        }
        resultsReader.close();
        return searchResponseList;
    }

    /**
     * Catch the result of the job response
     *
     * @param job
     *            The job response to parse
     * @return A List of Maps (the results)
     * @throws IOException on processing error
     */
    protected List<Map<String, Object>> populateEventResponse(Job job) throws IOException {
        JobResultsArgs resultsArgs = new JobResultsArgs();
        resultsArgs.setOutputMode(JobResultsArgs.OutputMode.JSON);
        InputStream results = job.getResults(resultsArgs);
        ResultsReaderJson resultsReader;
        resultsReader = new ResultsReaderJson(results);
        return parseEvents(resultsReader);
    }

    /**
     * Performs continuous processing search results
     * 
     * @param job
     *            The job response to process
     * @param previewArgs
     *            Used to filter job results previews
     * @param callback
     *            Used by Mule to generate events
     * @throws Exception
     *             on processing interruptions or errors
     */
    protected void processResults(Job job, JobResultsPreviewArgs previewArgs, SourceCallback callback) throws Exception {
        while (true) {
            InputStream results = job.getResultsPreview(previewArgs);
            ResultsReaderJson resultsReader;
            resultsReader = new ResultsReaderJson(results);
            callback.process(parseEvents(resultsReader));
            results.close();
            resultsReader.close();
            Thread.sleep(500);
        }
    }

    /**
     * Process custom arguments used a saved search
     * 
     * @param customArgs
     *            Contains customization data
     * @param searchDispatchArgs
     *            Contains processed customization data
     */
    protected void processCustomArgs(Map<String, Object> customArgs, SavedSearchDispatchArgs searchDispatchArgs) {
        if (customArgs != null) {
            for (Map.Entry<String, Object> entry : customArgs.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue()
                        .toString();
                searchDispatchArgs.add("args." + key, value);
            }
        }
    }

    /**
     * Perform callback on processed data
     * 
     * @param multiResultsReader
     *            Contains the data to be processed
     * @param callback
     *            Used by Mule to generate events
     * @throws Exception
     *             on processing interruptions or errors
     */
    protected void processCallback(MultiResultsReader<?> multiResultsReader, SourceCallback callback) throws Exception {
        List<Event> events = new ArrayList<Event>();
        for (SearchResults searchResults : multiResultsReader) {
            for (Event event : searchResults) {
                events.add(event);
            }
        }
        callback.process(events);
        multiResultsReader.close();
    }
}
