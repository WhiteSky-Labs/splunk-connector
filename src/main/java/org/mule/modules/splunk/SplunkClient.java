/**
 * (c) 2003-2012 MuleSoft, Inc. This software is protected under international
 * copyright law. All use of this software is subject to MuleSoft's Master
 * Subscription Agreement (or other Terms of Service) separately entered
 * into between you and MuleSoft. If such an agreement is not in
 * place, you may not use the software.
 **/
package org.mule.modules.splunk;

import com.splunk.*;
import org.apache.commons.lang.Validate;
import org.modeshape.common.text.Inflector;
import org.mule.api.ConnectionException;
import org.mule.api.ConnectionExceptionCode;
import org.mule.api.callback.SourceCallback;
import org.mule.common.metadata.*;
import org.mule.modules.splunk.exception.SplunkConnectorException;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class SplunkClient {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(SplunkClient.class);

    private SplunkConnector splunkConnector;

    private Service service;

    public SplunkClient(SplunkConnector splunkConnector) {
        this.splunkConnector = splunkConnector;
    }

    /**
     * Connect to splunk instance
     *
     * @param username The username to connect to
     * @param password The password to use for connection
     * @param host The host of the splunk server
     * @param port The port of the splunk server
     */
    public void connect(String username, String password, String host, int port) throws ConnectionException {
        try {
            ServiceArgs loginArgs = new ServiceArgs();
            loginArgs.setUsername(username);
            loginArgs.setPassword(password);
            loginArgs.setHost(splunkConnector.getHost());
            loginArgs.setPort(splunkConnector.getPort());

            // Create a Service instance and log in with the argument map
            service = Service.connect(loginArgs);
        } catch (com.splunk.HttpException splunkException) {
            switch (splunkException.getStatus()) {
                case 401:
                case 402:
                case 403:
                    // credentials no good

                    throw new ConnectionException(
                            ConnectionExceptionCode.INCORRECT_CREDENTIALS,
                            Integer.toString(splunkException.getStatus()),
                            splunkException.getMessage());
                case 404:
                    // can't reach
                    throw new ConnectionException(
                            ConnectionExceptionCode.CANNOT_REACH,
                            Integer.toString(splunkException.getStatus()),
                            splunkException.getMessage());
                case 405:
                case 409:
                case 500:
                case 503:
                    throw new ConnectionException(
                            ConnectionExceptionCode.UNKNOWN_HOST,
                            Integer.toString(splunkException.getStatus()),
                            splunkException.getMessage());
                default:
                    // unknown error
                    throw new ConnectionException(
                            ConnectionExceptionCode.UNKNOWN,
                            Integer.toString(splunkException.getStatus()),
                            splunkException.getMessage());
            }

        }

    }

    /**
     * This will return all the metadata
     *
     * @return List of MetaData
     */
    public List<MetaDataKey> getMetadata() {
        List<MetaDataKey> metaDataKeyList = new ArrayList<MetaDataKey>();
        metaDataKeyList.add(createKey(Application.class));
        metaDataKeyList.add(createKey(SavedSearch.class));
        metaDataKeyList.add(createKey(SavedSearchDispatchArgs.class));
        metaDataKeyList.add(createKey(Job.class));
        metaDataKeyList.add(createKey(SearchResults.class));
        metaDataKeyList.add(createKey(JobCollection.class));
        return metaDataKeyList;
    }

    /**
     * Get the MetaData
     *
     * @param key The metadata key
     * @return The MetaData Key
     */
    public MetaData getMetaDataKey(MetaDataKey key) throws ClassNotFoundException {
        final String ENTITY_PACKAGE = "com.splunk";
        Class<?> clazz = Class.forName(String.format("%s.%s", ENTITY_PACKAGE, key.getId()));
        return new DefaultMetaData(new DefaultPojoMetaDataModel(clazz));
    }

    /**
     * Metadata Key creator, takes the ClassName as key
     *
     * @param cls classname for the key
     * @return The MetaDataKey
     */
    private MetaDataKey createKey(Class<?> cls) {
        return new DefaultMetaDataKey(cls.getSimpleName(), cls.getSimpleName());
    }

    /**
     * Get All the Applications
     *
     * @return A List of the Applicaitions installed on the splunk instance
     */
    public List<Application> getApplications() {
        return (List<Application>) service.getApplications().values();
    }

    /**
     * Get All the saved searches
     *
     * @return List of Saved Searches
     */
    public List<SavedSearch> getSavedSearches() {
        List<SavedSearch> savedSearchList = new ArrayList<SavedSearch>();
        for (SavedSearch entity : service.getSavedSearches().values()) {
            savedSearchList.add(entity);
        }
        return savedSearchList;
    }

    /**
     * Create a saved search
     *
     * @param searchName  The name of query
     * @param searchQuery The query
     * @return SavedSearch the SavedSearch object that can then be executed
     * @throws com.splunk.HttpException when communications are interrupted
     */
    public SavedSearch createSavedSearch(String searchName, String searchQuery) throws com.splunk.HttpException {
        Validate.notEmpty(searchName, "Search Name empty.");
        Validate.notEmpty(searchQuery, "Search Query empty.");
        service.getSavedSearches().create(searchName, searchQuery);

        return service.getSavedSearches().get(searchName);
    }

    /**
     * Get Saved Search
     *
     * @param searchName The name of query
     * @return The Saved Search
     */
    public SavedSearch getSavedSearch(String searchName) {
        Validate.notEmpty(searchName, "Search Name empty.");
        return service.getSavedSearches().get(searchName);
    }

    /**
     * Modify Properties
     *
     * @param searchName The name of query
     * @return The Modified Saved Search
     */
    public SavedSearch modifySavedSearch(String searchName, String description,
                                         boolean isSetScheduled, String cronSchedule) {
        Validate.notEmpty(searchName, "Search Name empty.");
        Validate.notEmpty(description, "Description empty.");
        Validate.notNull(isSetScheduled, "Set schedule is null.");
        Validate.notEmpty(cronSchedule, "Cron schedule is empty.");
        SavedSearch savedSearch = service.getSavedSearches().get(searchName);
        savedSearch.setDescription(description);
        savedSearch.setIsScheduled(isSetScheduled);
        savedSearch.setCronSchedule(cronSchedule);
        savedSearch.update();
        return savedSearch;
    }

    /**
     * List the past and current instances (jobs) of the search.
     *
     * @param searchName The The name of query
     * @return List of Job
     */
    public List<Job> getSavedSearchHistory(String searchName) {
        List<Job> jobList = new ArrayList<Job>();
        SavedSearch savedSearch = service.getSavedSearches().get(searchName);
        Validate.notNull(savedSearch, "SavedSearch doesn't exist.");
        Collections.addAll(jobList, savedSearch.history());
        return jobList;
    }

    /**
     * Run a Saved Search
     *
     * @param searchName The name of query
     * @return List of Hashmaps
     * @throws org.mule.modules.splunk.exception.SplunkConnectorException when there is an issue executing
     * TODO switch to a sourcecallback
     */
    public List<Map<String, Object>> runSavedSearch(String searchName) throws SplunkConnectorException {
        SavedSearch savedSearch = service.getSavedSearches().get(searchName);
        try {
            Job job = savedSearch.dispatch();
            while (!job.isDone()) {
                Thread.sleep(500);
            }
            return populateEventResponse(job);
        } catch (InterruptedException e) {
            throw new SplunkConnectorException(e.getMessage(), e);
        }
    }

    /**
     * Run a Saved Search with arguments
     *
     * @param searchName         The name of the search
     * @param searchQuery        The query
     * @param customArgs         Custom Arguments, Optional list of custom arguments to supply
     * @param searchDispatchArgs Optional list of search dispatch arguments
     * @return The results as a List of Hashmaps
     * @throws SplunkConnectorException when there is an issue running the saved search
     *                                  TODO switch to a sourcecallback
     */

    public List<Map<String, Object>> runSavedSearchWithArguments(String searchName, String searchQuery,
                                                                 Map<String, Object> customArgs,
                                                                 SavedSearchDispatchArgs searchDispatchArgs)
            throws SplunkConnectorException {
        SavedSearch savedSearch = service.getSavedSearches().create(searchName, searchQuery);
        if (customArgs != null) {
            String queryParams = "";
            for (Map.Entry<String, Object> entry : customArgs.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue().toString();
                queryParams += " " + key + "=$args." + key + "$";
                searchDispatchArgs.add("args." + key, value);
            }
        }

        try {
            Job job = savedSearch.dispatch(searchDispatchArgs);
            while (!job.isDone()) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new SplunkConnectorException(e.getMessage(), e);
                }


            }
            return populateEventResponse(job);
        } catch (InterruptedException e) {
            throw new SplunkConnectorException(e.getMessage(), e);
        }
    }

    /**
     * Catch the result of the job response
     *
     * @param job The job response to parse
     * @return A List of Hashmaps (the results)
     * @throws SplunkConnectorException
     */
    private List<Map<String, Object>> populateEventResponse(Job job) throws SplunkConnectorException {
        JobResultsArgs resultsArgs = new JobResultsArgs();
        resultsArgs.setOutputMode(JobResultsArgs.OutputMode.JSON);
        InputStream results = job.getResults(resultsArgs);
        ResultsReaderJson resultsReader;
        try {
            resultsReader = new ResultsReaderJson(results);
            return parseEvents(resultsReader);
        } catch (IOException e) {
            throw new SplunkConnectorException(e.getMessage(), e);
        }
    }

    /**
     * Parse the events
     *
     * @param resultsReader A results reader (can be XML or JSON)
     * @return A List of HashMaps (the results)
     * @throws SplunkConnectorException
     */
    private List<Map<String, Object>> parseEvents(ResultsReader resultsReader) throws SplunkConnectorException {
        List<Map<String, Object>> searchResponseList = new ArrayList<Map<String, Object>>();
        try {
            Map<String, String> event;
            while ((event = resultsReader.getNextEvent()) != null) {
                Map<String, Object> eventData = new HashMap<String, Object>();
                for (String key : event.keySet()) {
                    LOGGER.debug("Something: " + key + " : " + event.get(key));
                    eventData.put(convertToJavaConvention(key), event.get(key));
                }
                searchResponseList.add(eventData);
            }
            resultsReader.close();
            return searchResponseList;
        } catch (IOException e) {
            throw new SplunkConnectorException(e.getMessage(), e);
        }
    }

    /**
     * Delete Saved Search
     *
     * @param searchName The name of query
     * @return Success/failure
     */
    public boolean deleteSavedSearch(String searchName) {
        try {
            SavedSearch savedSearch = service.getSavedSearches().get(searchName);
            savedSearch.remove();
            return true;
        } catch (HttpException e) {
            return false;
        }
    }

    /**
     * Retrieve an individual data model
     *
     * @param dataModelName The data model name to get
     * @return The Data Model requested
     */
    public DataModel getDataModel(String dataModelName) {
        DataModelCollection dataModelCollection = service.getDataModels();
        return dataModelCollection.get(dataModelName);
    }


    /**
     * Get the Service
     *
     * @return The Service object
     */
    public Service getService() {
        return service;
    }

    /**
     * Set the service
     *
     */
    public void setService() {
        this.service = null;
    }

    /**
     * List Jobs
     *
     * @return A List of the Job objects retrieved from the Splunk Server
     */
    public List<Job> getJobs() {
        JobCollection jobs = service.getJobs();
        List<Job> jobList = new ArrayList<Job>();
        for (Job job : jobs.values()) {
            jobList.add(job);
        }
        return jobList;
    }

    /**
     * Run a  search and wait for response
     *
     * @param searchQuery The search query
     * @return the search response
     * @throws SplunkConnectorException when there is an error connecting to splunk
     * TODO return as a sourcecallback
     */
    public Map<String, Object> runNormalSearch(String searchQuery) throws SplunkConnectorException {
        Validate.notEmpty(searchQuery, "Search Query is empty.");
        JobArgs jobargs = new JobArgs();
        jobargs.setExecutionMode(JobArgs.ExecutionMode.NORMAL);
        Job job = service.getJobs().create(searchQuery, jobargs);

        while (!job.isDone()) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new SplunkConnectorException(e.getMessage(), e);
            }
        }
        Map<String, Object> searchResponse = new HashMap<String, Object>();
        searchResponse.put("job", job);
        searchResponse.put("events", populateEventResponse(job));
        return searchResponse;
    }

    /**
     * Run a basic oneshot search and display results
     *
     * @param searchQuery  The search query
     * @param earliestTime The earliest time
     * @param latestTime   The latest time
     * @return A List of HashMaps (The results of the oneshot search)
     * @throws SplunkConnectorException when there is an error running the search on Splunk
     */
    public List<Map<String, Object>> runOneShotSearch(String searchQuery, String earliestTime, String latestTime)
            throws SplunkConnectorException {
        Args oneshotSearchArgs = new Args();
        oneshotSearchArgs.put("earliest_time", earliestTime);
        oneshotSearchArgs.put("latest_time", latestTime);

        InputStream resultsOneshot = service.oneshotSearch(searchQuery, oneshotSearchArgs);
        try {
            ResultsReaderXml resultsReader = new ResultsReaderXml(resultsOneshot);
            return parseEvents(resultsReader);
        } catch (IOException e) {
            throw new SplunkConnectorException(e.getMessage(), e);
        }
    }

    /**
     * Run a realtime search and process the response
     * returns via a sourcecallback
     *
     * @param searchQuery   The query to run in realtime
     * @param statusBuckets the status buckets to use - defaults to 300
     * @param previewCount  the number of previews to retrieve - defaults to 100
     * @throws SplunkConnectorException when there is a problem setting up the runtime search
     * @
     */
    public void runRealTimeSearch(String searchQuery,
                                  int statusBuckets,
                                  int previewCount,
                                  final SourceCallback callback)
            throws SplunkConnectorException {

        JobArgs jobArgs = new JobArgs();
        jobArgs.setExecutionMode(JobArgs.ExecutionMode.NORMAL);
        jobArgs.setSearchMode(JobArgs.SearchMode.REALTIME);
        jobArgs.setEarliestTime("rt");
        jobArgs.setLatestTime("rt");
        jobArgs.setStatusBuckets(statusBuckets);

        JobResultsPreviewArgs previewArgs = new JobResultsPreviewArgs();
        previewArgs.setCount(previewCount);
        previewArgs.setOutputMode(JobResultsPreviewArgs.OutputMode.JSON);
        Job job = service.search(searchQuery, jobArgs);

        while (!job.isReady()) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new SplunkConnectorException(e.getMessage(), e);
            }
        }

        while (true) {
            InputStream results = job.getResultsPreview(previewArgs);
            ResultsReaderJson resultsReader;
            try {
                resultsReader = new ResultsReaderJson(results);
                callback.process(parseEvents(resultsReader));
                results.close();
                resultsReader.close();
            } catch (Exception e) {
                throw new SplunkConnectorException(e.getMessage(), e);
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new SplunkConnectorException(e.getMessage(), e);
            }
        }
    }

    /**
     * Run an export search
     *
     * @param searchQuery the search query to run
     * @param exportArgs  The arguments to the search
     * @return A list of the Search Results found from the export search.
     * @throws org.mule.modules.splunk.exception.SplunkConnectorException when there is an issue running the search
     */
    public List<SearchResults> runExportSearch(String searchQuery, JobExportArgs exportArgs) throws SplunkConnectorException {
        List<SearchResults> searchResultsList = new ArrayList<SearchResults>();
        InputStream exportSearch = service.export(searchQuery, exportArgs);
        try {
            MultiResultsReaderXml multiResultsReader = new MultiResultsReaderXml(exportSearch);

            for (SearchResults searchResults : multiResultsReader) {
                searchResultsList.add(searchResults);
            }
            multiResultsReader.close();

        } catch (IOException e) {
            throw new SplunkConnectorException(e.getMessage(), e);
        }
        return searchResultsList;
    }

    /**
     * Convert the java key
     *
     * @param key - the String key to convert to the java convention (camelCase)
     * @return A string converted from javascript (naming_conventions) to Java namingConventions
     */
    private String convertToJavaConvention(String key) {
        return Inflector.getInstance().lowerCamelCase(key).replace("_", "");
    }

}
