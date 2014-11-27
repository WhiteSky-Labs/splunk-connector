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
import org.mule.modules.splunk.util.SplunkUtils;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Class SplunkClient, implements the Splunk Connector
 */
public class SplunkClient {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(SplunkClient.class);

    private SplunkConnector splunkConnector;

    private Service service;

    /**
     * Instantiate a SplunkClient, with a SplunkConnector object containing the configurable host and port.
     *
     * @param splunkConnector The instantiated SplunkConnector with hostname and port
     */
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
            LOGGER.error("HTTPException Connecting to Splunk", splunkException);
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
        final String entityPackage = "com.splunk";
        Class<?> clazz = Class.forName(String.format("%s.%s", entityPackage, key.getId()));
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
     * Get all the saved searches, optionally within a restricted namespace
     *
     * @param app The Application namespace to restrict the list of searches to
     * @param owner The user namespace to restrict the namespace to
     * @return List of Saved Searches
     */
    public List<SavedSearch> getSavedSearches(String app, String owner) {
        List<SavedSearch> savedSearchList = new ArrayList<SavedSearch>();
        SavedSearchCollection savedSearches;

        ServiceArgs namespace = new ServiceArgs();
        if ((app != null) || (owner != null)) {
            namespace.setApp(app);
            namespace.setOwner(owner);
            savedSearches = service.getSavedSearches(namespace);
        } else {
            savedSearches = service.getSavedSearches();
        }

        for (SavedSearch entity : savedSearches.values()) {
            savedSearchList.add(entity);
        }
        return savedSearchList;
    }

    /**
     * Create a saved search
     *
     * @param searchName  The name of query
     * @param searchQuery The query
     * @param searchArgs Optional Map of Key-Value Pairs of Saved Search Arguments
     * @return SavedSearch the SavedSearch object that can then be executed
     */
    public SavedSearch createSavedSearch(String searchName, String searchQuery, Map<String, Object> searchArgs) {
        Validate.notEmpty(searchName, "Search Name empty.");
        Validate.notEmpty(searchQuery, "Search Query empty.");
        SavedSearch createdSearch;
        if (searchArgs != null && !searchArgs.isEmpty()) {
            createdSearch = service.getSavedSearches().create(searchName, searchQuery, searchArgs);
        } else {
            createdSearch = service.getSavedSearches().create(searchName, searchQuery);
        }
        return createdSearch;
    }

    /**
     * Get Saved Search
     *
     * @param searchName The name of query
     * @param app The Optional app Namespace to restrict to
     * @param owner The Optional owner namespace to restrict to
     * @return The Saved Search
     */
    public SavedSearch getSavedSearch(String searchName, String app, String owner) {
        ServiceArgs namespace = new ServiceArgs();
        namespace.setOwner(owner);
        namespace.setApp(app);
        Validate.notEmpty(searchName, "Search Name empty.");
        return service.getSavedSearches(namespace).get(searchName);
    }

    /**
     * View Saved Search Properties
     *
     * @param searchName The Saved Search's name
     * @param app        The Optional app Namespace to restrict to
     * @param owner      The Optional owner namespace to restrict to
     * @return Map of the properties (the saved search's EntrySet)
     */
    public Set<Map.Entry<String, Object>> viewSavedSearchProperties(String searchName, String app, String owner) {
        ServiceArgs namespace = new ServiceArgs();
        if (owner != null) {
            namespace.setOwner(owner);
        }
        if (app != null) {
            namespace.setApp(app);
        }
        Validate.notEmpty(searchName, "Search Name empty.");
        return service.getSavedSearches(namespace).get(searchName).entrySet();
    }

    /**
     * Modify Saved Search Properties
     *
     * @param searchName The name of query
     * @param searchProperties The map of search properties to modify
     * @return The Modified Saved Search
     * @throws SplunkConnectorException when the properties aren't valid
     */
    public SavedSearch modifySavedSearchProperties(String searchName, Map<String, Object> searchProperties) throws SplunkConnectorException {
        Validate.notEmpty(searchName, "You must provide a search name to modify");
        Validate.notNull(searchProperties, "You must provide some properties to modify");
        Validate.notEmpty(searchProperties, "You must provide some properties to modify");
        SavedSearch savedSearch = service.getSavedSearches().get(searchName);

        savedSearch = SplunkUtils.setSearchProperties(searchProperties, savedSearch);
        savedSearch.update();
        return savedSearch;
    }

    /**
     * List the past and current instances (jobs) of the search.
     *
     * @param searchName The (Optional) name of query
     * @param app The (Optional) application of the namespace
     * @param owner The (Optional) owner of the namespace
     * @return List of Job
     */
    public List<Job> getSavedSearchHistory(String searchName, String app, String owner) {
        List<Job> jobList = new ArrayList<Job>();
        SavedSearchCollection savedSearches;

        // Set up the namespace first
        ServiceArgs namespace = new ServiceArgs();
        if ((app != null) || (owner != null)) {
            namespace.setApp(app);
            namespace.setOwner(owner);

        }
        // If there is no name provided, get all saved searches
        if (searchName == null || searchName.isEmpty()) {
            savedSearches = service.getSavedSearches(namespace);
            for (SavedSearch entity : savedSearches.values()) {
                Collections.addAll(jobList, entity.history());
            }
        } else {
            SavedSearch savedSearch = service.getSavedSearches(namespace).get(searchName);
            Validate.notNull(savedSearch, "SavedSearch doesn't exist.");
            Collections.addAll(jobList, savedSearch.history());
        }

        return jobList;
    }

    /**
     * Run a Saved Search
     *
     * @param searchName The name of query
     * @return List of Hashmaps
     * @throws org.mule.modules.splunk.exception.SplunkConnectorException when there is an issue executing
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
     * @param searchName         The name of the searh
     * @param customArgs         Custom Arguments, Optional list of custom arguments to supply
     * @param searchDispatchArgs Optional list of search dispatch arguments
     * @return The results as a List of Hashmaps
     * @throws SplunkConnectorException when there is an issue running the saved search
     */

    public List<Map<String, Object>> runSavedSearchWithArguments(String searchName,
                                                                 Map<String, Object> customArgs,
                                                                 SavedSearchDispatchArgs searchDispatchArgs)
            throws SplunkConnectorException {
        SavedSearch savedSearch = service.getSavedSearches().get(searchName);
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
                Thread.sleep(500);
            }
            return populateEventResponse(job);
        } catch (InterruptedException e) {
            LOGGER.info("Saved Search run interrupted", e);
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
                for (Map.Entry<String, String> entry : event.entrySet()) {
                    eventData.put(convertToJavaConvention(entry.getKey()), entry.getValue());
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
            LOGGER.info("Exception occurred deleting saved search", e);
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
    public void setService(Service service) {
        this.service = service;
    }

    /**
     * Get the current users Jobs
     *
     * @return A List of the current user's Job objects retrieved from the Splunk Server
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
     * @param searchArgs The optional search arguments
     * @param searchCallback The SourceCallback to return results to
     * @throws SplunkConnectorException when there is an error connecting to splunk
     *
     */
    public void runNormalSearch(String searchQuery, Map<String, Object> searchArgs, final SourceCallback searchCallback) throws SplunkConnectorException {
        Validate.notEmpty(searchQuery, "Search Query is empty.");
        JobArgs jobArgs = new JobArgs();
        if (searchArgs != null && !searchArgs.isEmpty()) {
            jobArgs = SplunkUtils.setJobArgs(searchArgs);
        }
        jobArgs.setExecutionMode(JobArgs.ExecutionMode.NORMAL);
        Job job = service.getJobs().create(searchQuery, jobArgs);

        while (!job.isDone()) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new SplunkConnectorException("Polling for Normal Search results was interrupted", e);
            }
        }

        Map<String, Object> searchResponse = new HashMap<String, Object>();
        searchResponse.put("job", job);
        searchResponse.put("events", populateEventResponse(job));
        try {
            searchCallback.process(searchResponse);
        } catch (Exception e) {
            throw new SplunkConnectorException(e.getMessage(), e);
        }

    }


    /**
     * Run a blocking search, which returns synchronously after waiting for the search to complete (blocking the Splunk Server)
     *
     * @param searchQuery The query string
     * @param searchArgs  An optional map of search arguments
     * @return The results as a HashMap
     * @throws SplunkConnectorException when the search cannot execute
     */
    public Map<String, Object> runBlockingSearch(String searchQuery, Map<String, Object> searchArgs) throws SplunkConnectorException {
        Validate.notEmpty(searchQuery, "Search Query is empty.");
        JobArgs jobArgs = new JobArgs();
        if (searchArgs != null && !searchArgs.isEmpty()) {
            jobArgs = SplunkUtils.setJobArgs(searchArgs);
        }
        jobArgs.setExecutionMode(JobArgs.ExecutionMode.BLOCKING);
        Job job = service.getJobs().create(searchQuery, jobArgs);

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
     * @param args Optional map of arguments
     * @return A List of HashMaps (The results of the oneshot search)
     * @throws SplunkConnectorException when there is an error running the search on Splunk
     */
    public List<Map<String, Object>> runOneShotSearch(String searchQuery, String earliestTime, String latestTime, Map<String, String> args)
            throws SplunkConnectorException {
        Args oneshotSearchArgs = new Args();
        oneshotSearchArgs.put("earliest_time", earliestTime);
        oneshotSearchArgs.put("latest_time", latestTime);
        if (args != null) {
            for (Map.Entry entry : args.entrySet()) {
                oneshotSearchArgs.put((String) entry.getKey(), entry.getValue());
            }
        }

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
     * @param earliestTime  The earliest time to search for, defaults to "rt"
     * @param latestTime    The latest time to search for, defaults to "rt"
     * @param statusBuckets the status buckets to use - defaults to 300
     * @param previewCount  the number of previews to retrieve - defaults to 100
     * @param callback The callback object to stream results
     * @throws SplunkConnectorException when there is a problem setting up the runtime search
     * @
     */
    public void runRealTimeSearch(String searchQuery,
                                  String earliestTime,
                                  String latestTime,
                                  int statusBuckets,
                                  int previewCount,
                                  final SourceCallback callback)
            throws SplunkConnectorException {

        JobArgs jobArgs = new JobArgs();
        jobArgs.setExecutionMode(JobArgs.ExecutionMode.NORMAL);
        jobArgs.setSearchMode(JobArgs.SearchMode.REALTIME);
        jobArgs.setEarliestTime(earliestTime);
        jobArgs.setLatestTime(latestTime);
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
     * @param earliestTime the earliest time for the search, defaults for an hour before now
     * @param latestTime the latest time, defaults to now
     * @param searchMode the searchmode, realtime or normal
     * @param outputMode the output mode, XML or JSON
     * @param exportArgs  The arguments to the search
     * @param callback The sourcecallback to return results to
     * @return A list of the Search Results found from the export search.
     * @throws org.mule.modules.splunk.exception.SplunkConnectorException when there is an issue running the search
     */
    public void runExportSearch(String searchQuery, String earliestTime, String latestTime, SearchMode searchMode, OutputMode outputMode, JobExportArgs exportArgs, final SourceCallback callback) throws SplunkConnectorException {
        if (exportArgs == null) {
            exportArgs = new JobExportArgs();
        }
        exportArgs.setEarliestTime(earliestTime);
        exportArgs.setLatestTime(latestTime);
        if (searchMode == SearchMode.NORMAL) {
            exportArgs.setSearchMode(JobExportArgs.SearchMode.NORMAL);
        } else {
            exportArgs.setSearchMode(JobExportArgs.SearchMode.REALTIME);
        }
        if (outputMode == OutputMode.JSON) {
            exportArgs.setOutputMode(JobExportArgs.OutputMode.JSON);
        } else {
            exportArgs.setOutputMode(JobExportArgs.OutputMode.XML);
        }
        List<SearchResults> searchResultsList = new ArrayList<SearchResults>();
        InputStream exportSearch = service.export(searchQuery, exportArgs);

        try {
            if (outputMode == OutputMode.JSON) {
                MultiResultsReaderJson multiResultsReader = new MultiResultsReaderJson(exportSearch);

                for (SearchResults searchResults : multiResultsReader) {
                    searchResultsList.add(searchResults);
                }
                callback.process(searchResultsList);
                multiResultsReader.close();
            } else {
                MultiResultsReaderXml multiResultsReader = new MultiResultsReaderXml(exportSearch);

                for (SearchResults searchResults : multiResultsReader) {
                    searchResultsList.add(searchResults);
                }
                callback.process(searchResultsList);
                multiResultsReader.close();
            }

        } catch (IOException e) {
            throw new SplunkConnectorException(e.getMessage(), e);
        } catch (Exception ex) {
            throw new SplunkConnectorException("Error processing callback", ex);
        }
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
