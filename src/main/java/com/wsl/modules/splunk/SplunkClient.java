/**
 *
 * (c) 2015 WhiteSky Labs, Pty Ltd. This software is protected under international
 * copyright law. All use of this software is subject to WhiteSky Labs' Master
 * Subscription Agreement (or other Terms of Service) separately entered
 * into between you and WhiteSky Labs. If such an agreement is not in
 * place, you may not use the software.
 */

package com.wsl.modules.splunk;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.modeshape.common.text.Inflector;
import org.mule.api.ConnectionException;
import org.mule.api.ConnectionExceptionCode;
import org.mule.api.callback.SourceCallback;
import org.slf4j.LoggerFactory;

import com.splunk.Application;
import com.splunk.Args;
import com.splunk.CollectionArgs;
import com.splunk.DataModel;
import com.splunk.DataModelCollection;
import com.splunk.HttpException;
import com.splunk.HttpService;
import com.splunk.Index;
import com.splunk.IndexCollection;
import com.splunk.IndexCollectionArgs;
import com.splunk.Input;
import com.splunk.InputCollection;
import com.splunk.InputKind;
import com.splunk.Job;
import com.splunk.JobArgs;
import com.splunk.JobExportArgs;
import com.splunk.JobResultsArgs;
import com.splunk.JobResultsPreviewArgs;
import com.splunk.MultiResultsReaderJson;
import com.splunk.MultiResultsReaderXml;
import com.splunk.ResultsReader;
import com.splunk.ResultsReaderJson;
import com.splunk.ResultsReaderXml;
import com.splunk.SSLSecurityProtocol;
import com.splunk.SavedSearch;
import com.splunk.SavedSearchCollection;
import com.splunk.SavedSearchDispatchArgs;
import com.splunk.SearchResults;
import com.splunk.Service;
import com.splunk.ServiceArgs;
import com.splunk.TcpInput;
import com.splunk.UdpInput;
import com.wsl.modules.config.ConnectorConfig;
import com.wsl.modules.splunk.exception.SplunkConnectorException;

/**
 * Class SplunkClient, implements the Splunk Connector
 */
public class SplunkClient {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(SplunkClient.class);

    private SplunkConnector connector;

    private Service service;

    /**
     * Instantiate a SplunkClient, with a SplunkConnector object containing the configurable host and port.
     *
     * @param connector The instantiated SplunkConnector with hostname and port
     */
    public SplunkClient(SplunkConnector connector) {
        this.connector = connector;
    }

	/**
     * Connect to splunk instance
     *
     * @param username The username to connect to
     * @param password The password to use for connection
     * @param host     The host of the splunk server
     * @param port     The port of the splunk server
     */
    public void connect(String username, String password) throws ConnectionException {
        ConnectorConfig config = getConnector().getConfig();
        if (!isValid(username) || !isValid(password) || !config.isValid()) {
            throw new ConnectionException(
                    ConnectionExceptionCode.INCORRECT_CREDENTIALS,
                    "00",
                    "Invalid credentials"
            );
        }
        try {
            ServiceArgs loginArgs = new ServiceArgs();
            loginArgs.setUsername(username);
            loginArgs.setPassword(password);
            loginArgs.setHost(config.getHost());
            loginArgs.setPort(config.getIntPort());
            HttpService.setSslSecurityProtocol(SSLSecurityProtocol.TLSv1_2);

            // Create a Service instance and log in with the argument map
            service = Service.connect(loginArgs);
        } catch (com.splunk.HttpException splunkException) {
            LOGGER.error("HTTPException Connecting to Splunk", splunkException);
            throw new ConnectionException(
                    ConnectionExceptionCode.UNKNOWN_HOST,
                    Integer.toString(splunkException.getStatus()),
                    splunkException.getMessage()
            );
        } catch (RuntimeException e) {
            LOGGER.info("Error connecting to Splunk", e);
            throw new ConnectionException(
                    ConnectionExceptionCode.UNKNOWN_HOST,
                    "00",
                    e.getMessage()
            );
        } catch (Exception e) {
            LOGGER.info("Error connnecting to Splunk", e);
            throw new ConnectionException(
                    ConnectionExceptionCode.UNKNOWN_HOST,
                    "00",
                    e.getMessage()
            );
        }

    }

    /**
     * Get All the Applications
     *
     * @return A List of the Applications installed on the splunk instance
     */
    public List<Map<String, Object>> getApplications() {
        List<Map<String, Object>> returnList = new ArrayList<Map<String, Object>>();
        for (Application app : service.getApplications().values()) {
            Set<Map.Entry<String, Object>> set = app.entrySet();
            Map<String, Object> mapFromSet = new HashMap<String, Object>();
            for (Map.Entry<String, Object> entry : set) {
                mapFromSet.put(entry.getKey(), entry.getValue());
            }
            returnList.add(mapFromSet);
        }
        return returnList;
    }

    /**
     * Get all the saved searches, optionally within a restricted namespace
     *
     * @param app   The Application namespace to restrict the list of searches to
     * @param owner The user namespace to restrict the namespace to
     * @return List of Saved Searches
     */
    public List<Map<String, Object>> getSavedSearches(String app, String owner) {
        List<Map<String, Object>> savedSearchList = new ArrayList<Map<String, Object>>();
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
            Set<Map.Entry<String, Object>> set = entity.entrySet();
            Map<String, Object> mapFromSet = new HashMap<String, Object>();
            for (Map.Entry<String, Object> entry : set) {
                mapFromSet.put(entry.getKey(), entry.getValue());
            }
            savedSearchList.add(mapFromSet);
        }
        return savedSearchList;
    }

    /**
     * Create a saved search
     *
     * @param searchName  The name of query
     * @param searchQuery The query
     * @param searchArgs  Optional Map of Key-Value Pairs of Saved Search Arguments
     * @return A map of the SavedSearch object
     */
    public Map<String, Object> createSavedSearch(String searchName, String searchQuery, Map<String, Object> searchArgs) {
        Validate.notEmpty(searchName, "Search Name empty.");
        Validate.notEmpty(searchQuery, "Search Query empty.");
        SavedSearch createdSearch;
        if (searchArgs != null && !searchArgs.isEmpty()) {
            createdSearch = service.getSavedSearches().create(searchName, searchQuery, searchArgs);
        } else {
            createdSearch = service.getSavedSearches().create(searchName, searchQuery);
        }
        Set<Map.Entry<String, Object>> set = createdSearch.entrySet();
        Map<String, Object> mapFromSet = new HashMap<String, Object>();
        for (Map.Entry<String, Object> entry : set) {
            mapFromSet.put(entry.getKey(), entry.getValue());
        }
        return mapFromSet;
    }

    /**
     * Get Saved Search
     *
     * @param searchName The name of query
     * @param app        The Optional app Namespace to restrict to
     * @param owner      The Optional owner namespace to restrict to
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
     * @param searchName       The name of query
     * @param searchProperties The map of search properties to modify
     * @return The Modified Saved Search
     * @throws SplunkConnectorException when the properties aren't valid
     */
    public Map<String, Object> modifySavedSearchProperties(String searchName, Map<String, Object> searchProperties) throws SplunkConnectorException {
        Validate.notEmpty(searchName, "You must provide a search name to modify");
        Validate.notNull(searchProperties, "You must provide some properties to modify");
        Validate.notEmpty(searchProperties, "You must provide some properties to modify");
        SavedSearch savedSearch = service.getSavedSearches().get(searchName);
        savedSearch.update(searchProperties);
        Set<Map.Entry<String, Object>> set = savedSearch.entrySet();
        Map<String, Object> mapFromSet = new HashMap<String, Object>();
        for (Map.Entry<String, Object> entry : set) {
            mapFromSet.put(entry.getKey(), entry.getValue());
        }
        return mapFromSet;
    }

    /**
     * List the past and current instances (jobs) of the search.
     *
     * @param searchName The (Optional) name of query
     * @param app        The (Optional) application of the namespace
     * @param owner      The (Optional) owner of the namespace
     * @return List of Jobs as maps
     */
    public List<Map<String, Object>> getSavedSearchHistory(String searchName, String app, String owner) {
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
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        for (Job entity : jobList) {
            Set<Map.Entry<String, Object>> set = entity.entrySet();
            Map<String, Object> mapFromSet = new HashMap<String, Object>();
            for (Map.Entry<String, Object> entry : set) {
                mapFromSet.put(entry.getKey(), entry.getValue());
            }
            result.add(mapFromSet);
        }
        return result;
    }

    /**
     * Run a Saved Search
     *
     * @param searchName The name of query
     * @return List of Hashmaps
     * @throws com.wsl.modules.splunk.exception.SplunkConnectorException when there is an issue executing
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
        SavedSearchDispatchArgs notNullSearchDispatchArgs = new SavedSearchDispatchArgs();

        if (searchDispatchArgs != null) {
            notNullSearchDispatchArgs.putAll(searchDispatchArgs);
        }
        if (customArgs != null) {
            for (Map.Entry<String, Object> entry : customArgs.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue().toString();
                notNullSearchDispatchArgs.add("args." + key, value);
            }
        }

        try {
            Job job = savedSearch.dispatch(notNullSearchDispatchArgs);
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
    protected List<Map<String, Object>> populateEventResponse(Job job) throws SplunkConnectorException {
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
    protected List<Map<String, Object>> parseEvents(ResultsReader resultsReader) throws SplunkConnectorException {
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
    public Map<String, Object> getDataModel(String dataModelName) {
        Validate.notNull(dataModelName, "You must provide a data model name");
        Validate.notEmpty(dataModelName, "You must provide a data model name");
        DataModelCollection dataModelCollection = service.getDataModels();
        Set<Map.Entry<String, Object>> set = dataModelCollection.get(dataModelName).entrySet();
        Map<String, Object> mapFromSet = new HashMap<String, Object>();
        for (Map.Entry<String, Object> entry : set) {
            mapFromSet.put(entry.getKey(), entry.getValue());
        }
        return mapFromSet;
    }

    /**
     * Retrieve all data models available to the user
     *
     * @return All Data Models available
     */
    public List<Map<String, Object>> getDataModels() {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

        for (DataModel model : service.getDataModels().values()) {
            Map<String, Object> mapFromSet = new HashMap<String, Object>();
            for (Map.Entry<String, Object> entry : model.entrySet()) {
                mapFromSet.put(entry.getKey(), entry.getValue());
            }
            result.add(mapFromSet);

        }
        return result;
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
     */
    public void setService(Service service) {
        this.service = service;
    }

    /**
     * Get the current users Jobs
     *
     * @return A List of the current user's Job objects retrieved from the Splunk Server
     */
    public List<Map<String, Object>> getJobs() {
        List<Map<String, Object>> returnList = new ArrayList<Map<String, Object>>();
        for (Job job : service.getJobs().values()) {
            Set<Map.Entry<String, Object>> set = job.entrySet();
            Map<String, Object> mapFromSet = new HashMap<String, Object>();
            for (Map.Entry<String, Object> entry : set) {
                mapFromSet.put(entry.getKey(), entry.getValue());
            }
            returnList.add(mapFromSet);
        }
        return returnList;
    }

    /**
     * Run a  search and wait for response
     *
     * @param searchQuery    The search query
     * @param searchArgs     The optional search arguments
     * @param searchCallback The SourceCallback to return results to
     * @throws SplunkConnectorException when there is an error connecting to splunk
     */
    public void runNormalSearch(String searchQuery, Map<String, Object> searchArgs, final SourceCallback searchCallback) throws SplunkConnectorException {
        Validate.notEmpty(searchQuery, "Search Query is empty.");
        JobArgs jobArgs = new JobArgs();
        jobArgs.putAll(searchArgs);
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
        jobArgs.putAll(jobArgs);
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
     * @param args         Optional map of arguments
     * @return A List of HashMaps (The results of the oneshot search)
     * @throws SplunkConnectorException when there is an error running the search on Splunk
     */
    public List<Map<String, Object>> runOneShotSearch(String searchQuery, String earliestTime, String latestTime, Map<String, String> args)
            throws SplunkConnectorException {
        Args oneshotSearchArgs = new Args();
        oneshotSearchArgs.put("earliest_time", earliestTime);
        oneshotSearchArgs.put("latest_time", latestTime);
        if (args != null) {
            for (Map.Entry<String, String> entry : args.entrySet()) {
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
     * @param callback      The callback object to stream results
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
                job.finish();
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
                job.cancel();
                throw new SplunkConnectorException(e.getMessage(), e);
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                job.finish();
                throw new SplunkConnectorException(e.getMessage(), e);
            }
        }
    }

    /**
     * Run an export search
     *
     * @param searchQuery  the search query to run
     * @param earliestTime the earliest time for the search, defaults for an hour before now
     * @param latestTime   the latest time, defaults to now
     * @param searchMode   the searchmode, realtime or normal
     * @param outputMode   the output mode, XML or JSON
     * @param callback     The sourcecallback to return results to
     * @return A list of the Search Results found from the export search.
     * @throws com.wsl.modules.splunk.exception.SplunkConnectorException when there is an issue running the search
     */
    public void runExportSearch(String searchQuery, String earliestTime, String latestTime, SearchMode searchMode, OutputMode outputMode, final SourceCallback callback) throws SplunkConnectorException {
        JobExportArgs newExportArgs = new JobExportArgs();
        newExportArgs.setEarliestTime(earliestTime);
        newExportArgs.setLatestTime(latestTime);
        if (searchMode == SearchMode.NORMAL) {
            newExportArgs.setSearchMode(JobExportArgs.SearchMode.NORMAL);
        } else {
            newExportArgs.setSearchMode(JobExportArgs.SearchMode.REALTIME);
        }
        if (outputMode == OutputMode.JSON) {
            newExportArgs.setOutputMode(JobExportArgs.OutputMode.JSON);
        } else {
            newExportArgs.setOutputMode(JobExportArgs.OutputMode.XML);
        }
        List<SearchResults> searchResultsList = new ArrayList<SearchResults>();
        InputStream exportSearch = service.export(searchQuery, newExportArgs);

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
     * Get the Collection of Inputs
     *
     * @return InputCollection of all inputs available to the user
     */
    public List<Map<String, Object>> getInputs() {

        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

        for (Input input : service.getInputs().values()) {
            Map<String, Object> mapFromSet = new HashMap<String, Object>();
            for (Map.Entry<String, Object> entry : input.entrySet()) {
                mapFromSet.put(entry.getKey(), entry.getValue());
            }
            result.add(mapFromSet);

        }
        return result;
    }


    /**
     * Convert the java key
     *
     * @param key - the String key to convert to the java convention (camelCase)
     * @return A string converted from javascript (naming_conventions) to Java namingConventions
     */
    protected String convertToJavaConvention(String key) {
        return Inflector.getInstance().lowerCamelCase(key).replace("_", "");
    }

    /**
     * Creates an Input with a given identifier and kind
     *
     * @param inputIdentifier The name of the domain controller
     * @param kind The InputKind
     * @param args An Optional Key-Value Map of Properties to set
     * @return The input
     */
    public Map<String, Object> createInput(String inputIdentifier, InputKind kind, Map<String, Object> args) {
        InputCollection myInputs = service.getInputs();
        Input input;
        if ((args != null) && (!args.isEmpty())) {
            input = myInputs.create(inputIdentifier, kind, args);
        } else {
            input = myInputs.create(inputIdentifier, kind);
        }
        Map<String, Object> mapFromSet = new HashMap<String, Object>();
        for (Map.Entry<String, Object> entry : input.entrySet()) {
            mapFromSet.put(entry.getKey(), entry.getValue());
        }
        return mapFromSet;
    }

    /**
     * Modifies an input with the properties supplied.
     *
     * @param inputIdentifier      The identifier of the Input to modify
     * @param inputArgs The map of properties to update
     * @return Returns the modified input.
     */
    public Map<String, Object> modifyInput(String inputIdentifier, Map<String, Object> inputArgs) {
        Validate.notNull(inputArgs, "You must provide some properties to modify");
        Validate.notEmpty(inputArgs, "You must provide some properties to modify");
        Input input = service.getInputs().get(inputIdentifier);
        input.update(inputArgs);
        Map<String, Object> mapFromSet = new HashMap<String, Object>();
        for (Map.Entry<String, Object> entry : input.entrySet()) {
            mapFromSet.put(entry.getKey(), entry.getValue());
        }
        return mapFromSet;
    }

    /**
     * Retrieves an Input with the given identifier
     *
     * @param inputIdentifier The identifier, for example a file path if it is a Monitor Input
     * @return The Input specified.
     */
    public Map<String, Object> getInput(String inputIdentifier) {
        Validate.notNull(inputIdentifier, "You must provide a valid input identifier");
        Validate.notEmpty(inputIdentifier, "You must provide a valid input identifier");
        Input input = service.getInputs().get(inputIdentifier);
        Validate.notNull(input, "You must provide a valid input identifier");
        Map<String, Object> mapFromSet = new HashMap<String, Object>();
        for (Map.Entry<String, Object> entry : input.entrySet()) {
            mapFromSet.put(entry.getKey(), entry.getValue());
        }
        return mapFromSet;
    }

    /**
     * Retrieves a collection of indexes based on the criteria provided
     *
     * @param sortKey              The Key to sort by
     * @param sortDirection        The SortDirection to sort by
     * @param collectionParameters Optional Map of additional arguments to pass to the call
     * @return IndexCollection of indexes
     */
    public List<Map<String, Object>> getIndexes(String sortKey, CollectionArgs.SortDirection sortDirection, Map<String, Object> collectionParameters) {
        IndexCollectionArgs args = new IndexCollectionArgs();
        if ((sortKey != null) && !sortKey.isEmpty()) {
            args.setSortKey(sortKey);
        }
        if (sortDirection != null) {
            args.setSortDirection(sortDirection);
        }
        if ((collectionParameters != null) && (!collectionParameters.isEmpty())) {
            args.putAll(collectionParameters);
        }
        IndexCollection coll;
        if (args.isEmpty()) {
            coll = service.getIndexes();
        } else {
            coll = service.getIndexes(args);
        }
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        for (Index index : coll.values()) {
            Set<Map.Entry<String, Object>> set = index.entrySet();
            Map<String, Object> mapFromSet = new HashMap<String, Object>();
            for (Map.Entry<String, Object> entry : set) {
                mapFromSet.put(entry.getKey(), entry.getValue());
            }
            result.add(mapFromSet);
        }
        return result;
    }

    /**
     * Creates an Index with optional arguments
     *
     * @param indexName The name of the index to create
     * @param args      Optional key-value pairs of arguments to apply on creation
     * @return the new Index
     */
    public Map<String, Object> createIndex(String indexName, Map<String, Object> args) {
        Index index;
        if ((args != null) && !args.isEmpty()) {
            index = service.getIndexes().create(indexName, args);
        } else {
            index = service.getIndexes().create(indexName);
        }
        Set<Map.Entry<String, Object>> set = index.entrySet();
        Map<String, Object> mapFromSet = new HashMap<String, Object>();
        for (Map.Entry<String, Object> entry : set) {
            mapFromSet.put(entry.getKey(), entry.getValue());
        }
        return mapFromSet;
    }

    /**
     * Modifies an index with the properties supplied.
     *
     * @param indexName  A Splunk Index to modify.
     * @param indexArgs The map of arguments to update
     * @return Returns the modified index.
     */
    public Map<String, Object> modifyIndex(String indexName, Map<String, Object> indexArgs) {
        Validate.notNull(indexArgs, "You must provide some properties to modify");
        Validate.notEmpty(indexArgs, "You must provide some properties to modify");
        Index index = service.getIndexes().get(indexName);
        index.update(indexArgs);
        Set<Map.Entry<String, Object>> set = index.entrySet();
        Map<String, Object> mapFromSet = new HashMap<String, Object>();
        for (Map.Entry<String, Object> entry : set) {
            mapFromSet.put(entry.getKey(), entry.getValue());
        }
        return mapFromSet;
    }

    /**
     * Retrieves an Index with the given identifier
     *
     * @param indexIdentifier The identifier
     * @return The Index specified.
     */
    public Map<String, Object> getIndex(String indexIdentifier) {
        Index index = service.getIndexes().get(indexIdentifier);
        Validate.notNull(index, "You must provide a valid index name");
        Set<Map.Entry<String, Object>> set = index.entrySet();
        Map<String, Object> mapFromSet = new HashMap<String, Object>();
        for (Map.Entry<String, Object> entry : set) {
            mapFromSet.put(entry.getKey(), entry.getValue());
        }
        return mapFromSet;
    }

    /**
     * Clean the index, which removes all events from it
     *
     * @param indexName The name of the index to clean
     * @param maxSeconds Optional how long to wait, -1 is forever (not recommended on a Connector). Default is 180s
     * @return the cleaned index
     */
    public Map<String, Object> cleanIndex(String indexName, int maxSeconds) {
        Validate.notNull(indexName, "You must provide an index name");
        Validate.notEmpty(indexName, "You must provide an index name");
        Index index = service.getIndexes().get(indexName);
        index = index.clean(maxSeconds);
        Set<Map.Entry<String, Object>> set = index.entrySet();
        Map<String, Object> mapFromSet = new HashMap<String, Object>();
        for (Map.Entry<String, Object> entry : set) {
            mapFromSet.put(entry.getKey(), entry.getValue());
        }
        return mapFromSet;
    }

    /**
     * Add data to an index without an input, using HTTP to submit a string
     *
     * @param indexName  The name of the index to update
     * @param stringData The data string to send
     * @param indexArgs Optional map of arguments to apply to the update
     * @return The index that has been updated
     */
    public Map<String, Object> addDataToIndex(String indexName, String stringData, Map<String, Object> indexArgs) {
        Index index = service.getIndexes().get(indexName);
        if (indexArgs != null && !indexArgs.isEmpty()) {
            Args eventArgs = new Args();
            eventArgs.putAll(indexArgs);
            index.submit(eventArgs, stringData);
        } else {
            index.submit(stringData);
        }
        Set<Map.Entry<String, Object>> set = index.entrySet();
        Map<String, Object> mapFromSet = new HashMap<String, Object>();
        for (Map.Entry<String, Object> entry : set) {
            mapFromSet.put(entry.getKey(), entry.getValue());
        }
        return mapFromSet;
    }


    /**
     * Add data to a tcp input on a given port
     *
     * @param portNumber The TCP Port Number to use
     * @param stringData The data string to add
     * @return Success or Failure
     */
    public Boolean addDataToTcpInput(String portNumber, String stringData) throws SplunkConnectorException {
        InputCollection coll = service.getInputs();
        TcpInput input = (TcpInput) coll.get(portNumber);
        try {
            input.submit(stringData);
            return true;
        } catch (IOException e) {
            LOGGER.info("Unable to submit to that TCP Port", e);
            return false;
        }
    }

    /**
     * Add data to a udp input on a given port
     *
     * @param portNumber The UDP Port Number to use
     * @param data       The data string to add
     * @return Success or Failure
     */
    public Boolean addDataToUdpInput(String portNumber, String data) throws SplunkConnectorException {
        UdpInput input = (UdpInput) service.getInputs().get(portNumber);
        try {
            input.submit(data);
            return true;
        } catch (IOException e) {
            LOGGER.info("Unable to submit to that TCP Port", e);
            return false;
        }
    }

    /**
     * Remove an Input
     *
     * @param inputIdentifier the identifier of the input to remove, for example a port number or filename
     * @return Success or Failure
     */
    public Boolean removeInput(String inputIdentifier) {
        Input input = service.getInputs().get(inputIdentifier);
        input.remove();
        return true;
    }

    /**
     * Remove an index
     * <p/>
     * {@sample.xml ../../../doc/splunk-connector.xml.sample splunk:remove-index}
     *
     * @param indexName The name of the index to remove
     * @return Success or Failure
     */
    public Boolean removeIndex(String indexName) {
        IndexCollection coll = service.getIndexes();
        Index index = coll.get(indexName);
        index.remove();
        return true;
    }

    public SplunkConnector getConnector() {
        return connector;
	}

    public void setConnector(SplunkConnector connector) {
        this.connector = connector;
	}

//    private boolean isCredentialsValid(String username, String password) {
//        return (isValid(username) || isValid(password)) ? false : true;
//    }
    
    private boolean isValid(String value) {
        return (value == null || value.isEmpty()) ? false : true;
    }
}
