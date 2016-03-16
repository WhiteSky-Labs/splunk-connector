/**
 *
 * (c) 2015 WhiteSky Labs, Pty Ltd. This software is protected under international
 * copyright law. All use of this software is subject to WhiteSky Labs' Master
 * Subscription Agreement (or other Terms of Service) separately entered
 * into between you and WhiteSky Labs. If such an agreement is not in
 * place, you may not use the software.
 */

package org.mule.modules.splunk;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.mule.api.ConnectionException;
import org.mule.api.ConnectionExceptionCode;
import org.mule.api.callback.SourceCallback;
import org.mule.modules.splunk.exception.SplunkConnectorException;
import org.mule.modules.splunk.strategy.ConnectionManagementStrategy;
import org.slf4j.LoggerFactory;

import com.google.common.base.CaseFormat;
import com.splunk.Application;
import com.splunk.Args;
import com.splunk.CollectionArgs;
import com.splunk.DataModel;
import com.splunk.DataModelCollection;
import com.splunk.EntityCollection;
import com.splunk.IgnoreFieldPropertyMultiResultsReaderJson;
import com.splunk.Index;
import com.splunk.IndexCollection;
import com.splunk.IndexCollectionArgs;
import com.splunk.Input;
import com.splunk.InputCollection;
import com.splunk.InputKind;
import com.splunk.Job;
import com.splunk.JobArgs;
import com.splunk.JobCollection;
import com.splunk.JobExportArgs;
import com.splunk.JobResultsArgs;
import com.splunk.JobResultsPreviewArgs;
import com.splunk.MultiResultsReader;
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

/**
 * Class SplunkClient, implements the Splunk Connector
 */
public class SplunkClient {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(SplunkClient.class);

    private Service service;

    /**
     * Connect to splunk instance
     *
     * @param strategy
     *            The ConnectionManagementStrategy used to connect to the Splunk
     *            Server
     * @throws ConnectionException
     *             on invalid credentials
     */
    public void connect(ConnectionManagementStrategy strategy) throws ConnectionException {
        if (strategy == null || !strategy.isValid()) {
            throw new ConnectionException(ConnectionExceptionCode.INCORRECT_CREDENTIALS, "00", "Invalid credentials");
        }
        try {
            ServiceArgs loginArgs = new ServiceArgs();
            loginArgs.setUsername(strategy.getUsername());
            loginArgs.setPassword(strategy.getPassword());
            loginArgs.setHost(strategy.getHost());
            loginArgs.setPort(strategy.getIntPort());
            Service.setSslSecurityProtocol(SSLSecurityProtocol.TLSv1_2);

            // Create a Service instance and log in with the argument map
            service = Service.connect(loginArgs);
        } catch (com.splunk.HttpException splunkException) {
            LOGGER.error("HTTPException Connecting to Splunk", splunkException);
            throw new ConnectionException(ConnectionExceptionCode.UNKNOWN_HOST, Integer.toString(splunkException.getStatus()), splunkException.getMessage());
        } catch (RuntimeException e) {
            LOGGER.info("Error connecting to Splunk", e);
            throw new ConnectionException(ConnectionExceptionCode.UNKNOWN_HOST, "00", e.getMessage());
        } catch (Exception e) {
            LOGGER.info("Error connnecting to Splunk", e);
            throw new ConnectionException(ConnectionExceptionCode.UNKNOWN_HOST, "00", e.getMessage());
        }
    }

    /**
     * Get All the Applications
     *
     * @return A List of the Applications installed on the splunk instance
     */
    public List<Map<String, Object>> getApplications() {
        List<Map<String, Object>> returnList = new ArrayList<Map<String, Object>>();
        EntityCollection<Application> applications = service.getApplications();
        if (applications != null) {
            for (Application app : applications.values()) {
                returnList.add(processSet(app.entrySet()));
            }
        }
        return returnList;
    }

    /**
     * Get all the saved searches, optionally within a restricted namespace
     *
     * @param app
     *            The Application namespace to restrict the list of searches to
     * @param owner
     *            The user namespace to restrict the searches to
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

        if (savedSearches != null) {
            for (SavedSearch entity : savedSearches.values()) {
                savedSearchList.add(processSet(entity.entrySet()));
            }
        }
        return savedSearchList;
    }

    /**
     * Create a saved search
     *
     * @param searchName
     *            The name of query
     * @param searchQuery
     *            The query
     * @param searchArgs
     *            Optional Map of Key-Value Pairs of Saved Search Arguments
     * @return A map of the SavedSearch object
     * @throws SplunkConnectorException
     *             on invalid searchName and/or searchQuery
     */
    public Map<String, Object> createSavedSearch(String searchName, String searchQuery, Map<String, Object> searchArgs) throws SplunkConnectorException {
        try {
            Validate.notEmpty(searchName, "Search Name empty.");
            Validate.notEmpty(searchQuery, "Search Query empty.");
            SavedSearch createdSearch;
            if (searchArgs != null && !searchArgs.isEmpty()) {
                createdSearch = service.getSavedSearches().create(searchName, searchQuery, searchArgs);
            } else {
                createdSearch = service.getSavedSearches().create(searchName, searchQuery);
            }
            return processSet(createdSearch.entrySet());
        } catch (Exception e) {
            throw new SplunkConnectorException(e.getMessage(), e);
        }
    }

    /**
     * View Saved Search Properties
     *
     * @param searchName
     *            The Saved Search's name
     * @param app
     *            The Optional app Namespace to restrict to
     * @param owner
     *            The Optional owner namespace to restrict to
     * @return Map of the properties (the saved search's EntrySet)
     * @throws SplunkConnectorException
     *             on invalid searchName
     */
    public Set<Map.Entry<String, Object>> viewSavedSearchProperties(String searchName, String app, String owner) throws SplunkConnectorException {
        try {
            Validate.notEmpty(searchName, "Search Name empty.");
            ServiceArgs namespace = new ServiceArgs();
            if (owner != null) {
                namespace.setOwner(owner);
            }
            if (app != null) {
                namespace.setApp(app);
            }
            return service.getSavedSearches(namespace).get(searchName).entrySet();
        } catch (IllegalArgumentException iae) {
            throw new SplunkConnectorException(iae.getMessage(), iae);
        } catch (Exception e) {
            LOGGER.info("No properties found for searchName=" + searchName + ", app=" + app + ", owner=" + owner, e);
            return new HashSet<Map.Entry<String, Object>>();
        }
    }

    /**
     * Modify Saved Search Properties
     *
     * @param searchName
     *            The name of query
     * @param searchProperties
     *            The map of search properties to modify
     * @return The Modified Saved Search
     * @throws SplunkConnectorException
     *             when the properties aren't valid
     */
    public Map<String, Object> modifySavedSearchProperties(String searchName, Map<String, Object> searchProperties) throws SplunkConnectorException {
        try {
            Validate.notEmpty(searchName, "You must provide a search name to modify");
            Validate.notEmpty(searchProperties, "You must provide some properties to modify");
            SavedSearch savedSearch = service.getSavedSearches().get(searchName);
            savedSearch.update(searchProperties);
            return processSet(savedSearch.entrySet());
        } catch (NullPointerException npe) {
            throw new SplunkConnectorException("Unable to find Saved Search with searchName=" + searchName, npe);
        } catch (Exception e) {
            throw new SplunkConnectorException(e.getMessage(), e);
        }
    }

    /**
     * List the past and current instances (jobs) of the search.
     *
     * @param searchName
     *            The (Optional) name of query
     * @param app
     *            The (Optional) application of the namespace
     * @param owner
     *            The (Optional) owner of the namespace
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
            if (savedSearch != null) {
                Collections.addAll(jobList, savedSearch.history());
            }
        }
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        for (Job entity : jobList) {
            result.add(processSet(entity.entrySet()));
        }
        return result;
    }

    /**
     * Run a Saved Search
     *
     * @param searchName
     *            The name of query
     * @return List of Hashmaps
     * @throws org.mule.modules.splunk.exception.SplunkConnectorException
     *             when there is an issue executing
     */
    public List<Map<String, Object>> runSavedSearch(String searchName) throws SplunkConnectorException {
        try {
            SavedSearch savedSearch = service.getSavedSearches().get(searchName);
            Job job = savedSearch.dispatch();
            while (!job.isDone()) {
                Thread.sleep(500);
            }
            return populateEventResponse(job);
        } catch (NullPointerException npe) {
            throw new SplunkConnectorException("Unable to find Saved Search with searchName=" + searchName, npe);
        } catch (Exception e) {
            throw new SplunkConnectorException(e.getMessage(), e);
        }
    }

    /**
     * Run a Saved Search with arguments
     *
     * @param searchName
     *            The name of the searh
     * @param customArgs
     *            Custom Arguments, Optional list of custom arguments to supply
     * @param searchDispatchArgs
     *            Optional list of search dispatch arguments
     * @return The results as a List of Hashmaps
     * @throws SplunkConnectorException
     *             when there is an issue running the saved search
     */

    public List<Map<String, Object>> runSavedSearchWithArguments(String searchName, Map<String, Object> customArgs,
            SavedSearchDispatchArgs searchDispatchArgsParam) throws SplunkConnectorException {
        try {
            Validate.notEmpty(searchName, "You must provide a valid search name");
            SavedSearch savedSearch = service.getSavedSearches().get(searchName);
            SavedSearchDispatchArgs searchDispatchArgs = new SavedSearchDispatchArgs();

            if (searchDispatchArgsParam != null) {
                searchDispatchArgs.putAll(searchDispatchArgsParam);
            }
            processCustomArgs(customArgs, searchDispatchArgs);

            Job job = savedSearch.dispatch(searchDispatchArgs);
            while (!job.isDone()) {
                Thread.sleep(500);
            }
            return populateEventResponse(job);
        } catch (NullPointerException npe) {
            throw new SplunkConnectorException("Unable to find Saved Search with searchName=" + searchName, npe);
        } catch (Exception e) {
            throw new SplunkConnectorException(e.getMessage(), e);
        }
    }

    private void processCustomArgs(Map<String, Object> customArgs, SavedSearchDispatchArgs searchDispatchArgs) {
        if (customArgs != null) {
            for (Map.Entry<String, Object> entry : customArgs.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue().toString();
                searchDispatchArgs.add("args." + key, value);
            }
        }
    }

    /**
     * Catch the result of the job response
     *
     * @param job
     *            The job response to parse
     * @return A List of Hashmaps (the results)
     * @throws SplunkConnectorException
     *             on invalid results
     */
    protected List<Map<String, Object>> populateEventResponse(Job job) throws SplunkConnectorException {
        JobResultsArgs resultsArgs = new JobResultsArgs();
        resultsArgs.setOutputMode(JobResultsArgs.OutputMode.JSON);
        InputStream results = job.getResults(resultsArgs);
        ResultsReaderJson resultsReader;
        try {
            resultsReader = new ResultsReaderJson(results);
            return parseEvents(resultsReader);
        } catch (Exception e) {
            throw new SplunkConnectorException(e.getMessage(), e);
        }
    }

    /**
     * Parse the events
     *
     * @param resultsReader
     *            A results reader (can be XML or JSON)
     * @return A List of HashMaps (the results)
     * @throws SplunkConnectorException
     *             on invalid results
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
        } catch (Exception e) {
            throw new SplunkConnectorException(e.getMessage(), e);
        }
    }

    /**
     * Delete Saved Search
     *
     * @param searchName
     *            The name of query
     * @return Success/failure
     */
    public boolean deleteSavedSearch(String searchName) {
        try {
            SavedSearch savedSearch = service.getSavedSearches().get(searchName);
            savedSearch.remove();
            return true;
        } catch (Exception e) {
            LOGGER.info("Invalid searchName=" + searchName, e);
            return false;
        }
    }

    /**
     * Retrieve an individual data model
     *
     * @param dataModelName
     *            The data model name to get
     * @return The Data Model requested
     * @throws SplunkConnectorException
     *             on invalid dataModelName
     */
    public Map<String, Object> getDataModel(String dataModelName) throws SplunkConnectorException {
        try {
            Validate.notEmpty(dataModelName, "You must provide a data model name");
            DataModelCollection dataModelCollection = service.getDataModels();
            Map<String, Object> mapFromSet = new HashMap<String, Object>();
            if (dataModelCollection != null && dataModelCollection.get(dataModelName) != null) {
                mapFromSet = processSet(dataModelCollection.get(dataModelName).entrySet());
            }
            return mapFromSet;
        } catch (Exception e) {
            throw new SplunkConnectorException(e.getMessage(), e);
        }
    }

    /**
     * Retrieve all data models available to the user
     *
     * @return All Data Models available
     */
    public List<Map<String, Object>> getDataModels() {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        DataModelCollection dataModels = service.getDataModels();
        if (dataModels != null) {
            for (DataModel model : dataModels.values()) {
                result.add(processSet(model.entrySet()));
            }
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
     * 
     * @param service
     *            The Splunk service
     */
    public void setService(Service service) {
        this.service = service;
    }

    /**
     * Get the current users Jobs
     *
     * @return A List of the current user's Job objects retrieved from the
     *         Splunk Server
     */
    public List<Map<String, Object>> getJobs() {
        List<Map<String, Object>> returnList = new ArrayList<Map<String, Object>>();
        JobCollection jobs = service.getJobs();
        if (jobs != null) {
            for (Job job : jobs.values()) {
                returnList.add(processSet(job.entrySet()));
            }
        }
        return returnList;
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
     * @throws SplunkConnectorException
     *             when there is an error connecting to splunk
     */
    public void runNormalSearch(String searchQuery, Map<String, Object> searchArgs, final SourceCallback searchCallback) throws SplunkConnectorException {
        try {
            Validate.notEmpty(searchQuery, "Search Query is empty.");
            JobArgs jobArgs = new JobArgs();
            if (searchArgs != null) {
                jobArgs.putAll(searchArgs);
            }
            jobArgs.setExecutionMode(JobArgs.ExecutionMode.NORMAL);
            Job job = service.getJobs().create(searchQuery, jobArgs);

            while (!job.isDone()) {
                Thread.sleep(500);
            }

            Map<String, Object> searchResponse = new HashMap<String, Object>();
            searchResponse.put("job", job);
            searchResponse.put("events", populateEventResponse(job));
            searchCallback.process(searchResponse);
        } catch (InterruptedException e) {
            throw new SplunkConnectorException("Polling for Normal Search results was interrupted", e);
        } catch (Exception e) {
            throw new SplunkConnectorException(e.getMessage(), e);
        }
    }

    /**
     * Run a blocking search, which returns synchronously after waiting for the
     * search to complete (blocking the Splunk Server)
     *
     * @param searchQuery
     *            The query string
     * @param searchArgs
     *            An optional map of search arguments
     * @return The results as a HashMap
     * @throws SplunkConnectorException
     *             when the search cannot execute
     */
    public Map<String, Object> runBlockingSearch(String searchQuery, Map<String, Object> searchArgs) throws SplunkConnectorException {
        try {
            Validate.notEmpty(searchQuery, "Search Query is empty.");
            JobArgs jobArgs = new JobArgs();
            if (searchArgs != null) {
                jobArgs.putAll(searchArgs);
            }
            jobArgs.setExecutionMode(JobArgs.ExecutionMode.BLOCKING);
            Job job = service.getJobs().create(searchQuery, jobArgs);
            Map<String, Object> searchResponse = new HashMap<String, Object>();
            searchResponse.put("job", job);
            searchResponse.put("events", populateEventResponse(job));
            return searchResponse;
        } catch (Exception e) {
            throw new SplunkConnectorException(e.getMessage(), e);
        }
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
     * @throws SplunkConnectorException
     *             when there is an error running the search on Splunk
     */
    public List<Map<String, Object>> runOneShotSearch(String searchQuery, String earliestTime, String latestTime, Map<String, String> args)
            throws SplunkConnectorException {
        try {
            Validate.notEmpty(searchQuery, "Search Query is empty.");
            Validate.notEmpty(earliestTime, "Earliest Time is empty.");
            Validate.notEmpty(latestTime, "Search Latest Time is empty.");
            Args oneshotSearchArgs = new Args();
            oneshotSearchArgs.put("earliest_time", earliestTime);
            oneshotSearchArgs.put("latest_time", latestTime);
            if (args != null) {
                for (Map.Entry<String, String> entry : args.entrySet()) {
                    oneshotSearchArgs.put((String) entry.getKey(), entry.getValue());
                }
            }
            InputStream resultsOneshot = service.oneshotSearch(searchQuery, oneshotSearchArgs);
            return parseEvents(new ResultsReaderXml(resultsOneshot));
        } catch (Exception e) {
            throw new SplunkConnectorException(e.getMessage(), e);
        }
    }

    /**
     * Run a realtime search and process the response returns via a
     * sourcecallback
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
     * @throws SplunkConnectorException
     *             when there is a problem setting up the runtime search
     */
    public void runRealTimeSearch(String searchQuery, String earliestTime, String latestTime, int statusBuckets, int previewCount, final SourceCallback callback)
            throws SplunkConnectorException {
        Job job = null;
        try {
            Validate.notEmpty(searchQuery, "Search Query is empty.");
            JobArgs jobArgs = new JobArgs();
            jobArgs.setExecutionMode(JobArgs.ExecutionMode.NORMAL);
            jobArgs.setSearchMode(JobArgs.SearchMode.REALTIME);
            jobArgs.setEarliestTime(earliestTime);
            jobArgs.setLatestTime(latestTime);
            jobArgs.setStatusBuckets(statusBuckets);

            JobResultsPreviewArgs previewArgs = new JobResultsPreviewArgs();
            previewArgs.setCount(previewCount);
            previewArgs.setOutputMode(JobResultsPreviewArgs.OutputMode.JSON);

            job = service.search(searchQuery, jobArgs);
            while (!job.isReady()) {
                Thread.sleep(500);
            }

            processResults(job, previewArgs, callback);
        } catch (InterruptedException ie) {
            job.finish();
            throw new SplunkConnectorException(ie.getMessage(), ie);
        } catch (Exception e) {
            throw new SplunkConnectorException(e.getMessage(), e);
        }
    }

    private void processResults(Job job, JobResultsPreviewArgs previewArgs, SourceCallback callback) throws SplunkConnectorException {
        while (true) {
            InputStream results = job.getResultsPreview(previewArgs);
            ResultsReaderJson resultsReader;
            try {
                resultsReader = new ResultsReaderJson(results);
                callback.process(parseEvents(resultsReader));
                results.close();
                resultsReader.close();

                Thread.sleep(500);
            } catch (InterruptedException e) {
                job.finish();
                throw new SplunkConnectorException(e.getMessage(), e);
            } catch (Exception e) {
                job.cancel();
                throw new SplunkConnectorException(e.getMessage(), e);
            }
        }
    }

    /**
     * Run an export search
     *
     * @param searchQuery
     *            the search query to run
     * @param earliestTime
     *            the earliest time for the search, defaults for an hour before
     *            now
     * @param latestTime
     *            the latest time, defaults to now
     * @param searchMode
     *            the searchmode, realtime or normal
     * @param outputMode
     *            the output mode, XML or JSON
     * @param callback
     *            The sourcecallback to return results to
     * @throws org.mule.modules.splunk.exception.SplunkConnectorException
     *             when there is an issue running the search
     */
    public void runExportSearch(String searchQuery, String earliestTime, String latestTime, SearchMode searchMode, OutputMode outputMode,
            final SourceCallback callback) throws SplunkConnectorException {
        try {
            Validate.notEmpty(searchQuery, "Search Query is empty.");
            InputStream exportSearch = null;
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
                exportSearch = service.export(searchQuery, newExportArgs);
                processCallback(new IgnoreFieldPropertyMultiResultsReaderJson(exportSearch), callback);
            } else {
                newExportArgs.setOutputMode(JobExportArgs.OutputMode.XML);
                exportSearch = service.export(searchQuery, newExportArgs);
                processCallback(new MultiResultsReaderXml(exportSearch), callback);
            }
        } catch (Exception ex) {
            throw new SplunkConnectorException("Error processing callback", ex);
        }
    }

    private void processCallback(MultiResultsReader<?> multiResultsReader, SourceCallback callback) throws Exception {
        List<SearchResults> searchResultsList = new ArrayList<SearchResults>();
        for (SearchResults searchResults : multiResultsReader) {
            searchResultsList.add(searchResults);
        }
        callback.process(searchResultsList);
        multiResultsReader.close();
    }

    /**
     * Get the Collection of Inputs
     *
     * @return InputCollection of all inputs available to the user
     */
    public List<Map<String, Object>> getInputs() {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        InputCollection inputs = service.getInputs();
        if (inputs != null) {
            for (Input input : inputs.values()) {
                result.add(processSet(input.entrySet()));
            }
        }
        return result;
    }

    /**
     * Convert the java key
     *
     * @param key
     *            - the String key to convert to the java convention (camelCase)
     * @return A string converted from javascript (naming_conventions) to Java
     *         namingConventions
     */
    protected String convertToJavaConvention(String key) {
        String result = key.startsWith("_") ? key.substring(1) : key;
        return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, result);
    }

    /**
     * Creates an Input with a given identifier and kind
     *
     * @param inputIdentifier
     *            The name of the domain controller
     * @param kind
     *            The InputKind
     * @param args
     *            An Optional Key-Value Map of Properties to set
     * @return The input
     * @throws org.mule.modules.splunk.exception.SplunkConnectorException
     *             when there is an issue executing
     */
    public Map<String, Object> createInput(String inputIdentifier, InputKind kind, Map<String, Object> args) throws SplunkConnectorException {
        try {
            InputCollection myInputs = service.getInputs();
            Input input;
            if ((args != null) && (!args.isEmpty())) {
                input = myInputs.create(inputIdentifier, kind, args);
            } else {
                input = myInputs.create(inputIdentifier, kind);
            }
            return processSet(input.entrySet());
        } catch (Exception e) {
            throw new SplunkConnectorException(e.getMessage(), e);
        }
    }

    /**
     * Modifies an input with the properties supplied.
     *
     * @param inputIdentifier
     *            The identifier of the Input to modify
     * @param inputArgs
     *            The map of properties to update
     * @return Returns the modified input.
     * @throws SplunkConnectorException
     *             on invalid parameters
     */
    public Map<String, Object> modifyInput(String inputIdentifier, Map<String, Object> inputArgs) throws SplunkConnectorException {
        try {
            Validate.notEmpty(inputIdentifier, "You must provide a valid input identifier");
            Validate.notEmpty(inputArgs, "You must provide some properties to modify");
            Input input = service.getInputs().get(inputIdentifier);
            input.update(inputArgs);
            return processSet(input.entrySet());
        } catch (NullPointerException e) {
            throw new SplunkConnectorException("Unable to find Input with inputIdentifier=" + inputIdentifier, e);
        } catch (Exception e) {
            throw new SplunkConnectorException(e.getMessage(), e);
        }
    }

    /**
     * Retrieves an Input with the given identifier
     *
     * @param inputIdentifier
     *            The identifier, for example a file path if it is a Monitor
     *            Input
     * @return The Input specified.
     * @throws SplunkConnectorException
     *             on invalid inputIdentifier
     */
    public Map<String, Object> getInput(String inputIdentifier) throws SplunkConnectorException {
        try {
            Validate.notEmpty(inputIdentifier, "You must provide a valid input identifier");
            Input input = service.getInputs().get(inputIdentifier);
            Map<String, Object> mapFromSet = new HashMap<String, Object>();
            if (input != null && input.entrySet() != null) {
                mapFromSet = processSet(input.entrySet());
            }
            return mapFromSet;
        } catch (IllegalArgumentException e) {
            throw new SplunkConnectorException(e.getMessage(), e);
        }
    }

    /**
     * Retrieves a collection of indexes based on the criteria provided
     *
     * @param sortKey
     *            The Key to sort by
     * @param sortDirection
     *            The SortDirection to sort by
     * @param collectionParameters
     *            Optional Map of additional arguments to pass to the call
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
            result.add(processSet(index.entrySet()));
        }
        return result;
    }

    /**
     * Creates an Index with optional arguments
     *
     * @param indexName
     *            The name of the index to create
     * @param args
     *            Optional key-value pairs of arguments to apply on creation
     * @return the new Index
     * @throws SplunkConnectorException
     *             on invalid indexName
     */
    public Map<String, Object> createIndex(String indexName, Map<String, Object> args) throws SplunkConnectorException {
        try {
            Index index;
            if ((args != null) && !args.isEmpty()) {
                index = service.getIndexes().create(indexName, args);
            } else {
                index = service.getIndexes().create(indexName);
            }
            return processSet(index.entrySet());
        } catch (Exception e) {
            throw new SplunkConnectorException(e.getMessage(), e);
        }
    }

    /**
     * Modifies an index with the properties supplied.
     *
     * @param indexName
     *            A Splunk Index to modify.
     * @param indexArgs
     *            The map of arguments to update
     * @return Returns the modified index.
     * @throws SplunkConnectorException
     *             on invalid indexName
     */
    public Map<String, Object> modifyIndex(String indexName, Map<String, Object> indexArgs) throws SplunkConnectorException {
        try {
            Validate.notEmpty(indexName, "You must provide a valid index name");
            Validate.notEmpty(indexArgs, "You must provide some properties to modify");
            Index index = service.getIndexes().get(indexName);
            index.update(indexArgs);
            return processSet(index.entrySet());
        } catch (NullPointerException e) {
            throw new SplunkConnectorException("Unable to find Index with indexName=" + indexName, e);
        } catch (Exception e) {
            throw new SplunkConnectorException(e.getMessage(), e);
        }

    }

    /**
     * Retrieves an Index with the given identifier
     *
     * @param indexIdentifier
     *            The identifier
     * @return The Index specified.
     * @throws SplunkConnectorException
     *             on invalid indexIdentifier
     */
    public Map<String, Object> getIndex(String indexIdentifier) throws SplunkConnectorException {
        try {
            Validate.notEmpty(indexIdentifier, "You must provide a valid index name");
            Index index = service.getIndexes().get(indexIdentifier);
            Map<String, Object> mapFromSet = new HashMap<String, Object>();
            if (index != null && index.entrySet() != null) {
                mapFromSet = processSet(index.entrySet());
            }
            return mapFromSet;
        } catch (Exception e) {
            throw new SplunkConnectorException(e.getMessage(), e);
        }
    }

    /**
     * Clean the index, which removes all events from it
     *
     * @param indexName
     *            The name of the index to clean
     * @param maxSeconds
     *            Optional how long to wait, -1 is forever (not recommended on a
     *            Connector). Default is 180s
     * @return the cleaned index
     * @throws SplunkConnectorException
     *             on invalid indexName
     */
    public Map<String, Object> cleanIndex(String indexName, int maxSeconds) throws SplunkConnectorException {
        try {
            Validate.notEmpty(indexName, "You must provide an index name");
            Index index = service.getIndexes().get(indexName);
            index = index.clean(maxSeconds);
            return processSet(index.entrySet());
        } catch (NullPointerException e) {
            throw new SplunkConnectorException("Unable to find Index with indexName=" + indexName, e);
        } catch (Exception e) {
            throw new SplunkConnectorException(e.getMessage(), e);
        }
    }

    /**
     * Add data to an index without an input, using HTTP to submit a string
     *
     * @param indexName
     *            The name of the index to update
     * @param stringData
     *            The data string to send
     * @param indexArgs
     *            Optional map of arguments to apply to the update
     * @return The index that has been updated
     * @throws SplunkConnectorException
     *             on invalid indexName
     */
    public Map<String, Object> addDataToIndex(String indexName, String stringData, Map<String, Object> indexArgs) throws SplunkConnectorException {
        try {
            Validate.notEmpty(indexName, "You must provide an index name");
            Validate.notEmpty(stringData, "You must provide some string data");
            Index index = service.getIndexes().get(indexName);
            if (indexArgs != null && !indexArgs.isEmpty()) {
                Args eventArgs = new Args();
                eventArgs.putAll(indexArgs);
                index.submit(eventArgs, stringData);
            } else {
                index.submit(stringData);
            }
            return processSet(index.entrySet());
        } catch (NullPointerException e) {
            throw new SplunkConnectorException("Unable to find Index with indexName=" + indexName, e);
        } catch (Exception e) {
            throw new SplunkConnectorException(e.getMessage(), e);
        }
    }

    /**
     * Add data to a tcp input on a given port
     * 
     * @param portNumber
     *            The TCP Port Number to use
     * @param stringData
     *            The data string to add
     * @return Success or Failure
     */
    public boolean addDataToTcpInput(String portNumber, String stringData) {
        try {
            TcpInput input = (TcpInput) service.getInputs().get(portNumber);
            input.submit(stringData);
            return true;
        } catch (Exception e) {
            LOGGER.info("Unable to add data to TCP Port", e);
            return false;
        }
    }

    /**
     * Add data to a udp input on a given port
     *
     * @param portNumber
     *            The UDP Port Number to use
     * @param data
     *            The data string to add
     * @return Success or Failure
     */
    public boolean addDataToUdpInput(String portNumber, String data) {
        try {
            UdpInput input = (UdpInput) service.getInputs().get(portNumber);
            input.submit(data);
            return true;
        } catch (Exception e) {
            LOGGER.info("Unable to submit to that UDP Port", e);
            return false;
        }
    }

    /**
     * Remove an Input
     *
     * @param inputIdentifier
     *            the identifier of the input to remove, for example a port
     *            number or filename
     * @return Success or Failure
     */
    public boolean removeInput(String inputIdentifier) {
        try {
            Input input = service.getInputs().get(inputIdentifier);
            input.remove();
            return true;
        } catch (Exception e) {
            LOGGER.info("Invalid Input Identifier", e);
            return false;
        }
    }

    /**
     * Remove an index
     *
     * @param indexName
     *            The name of the index to remove
     * @return Success or Failure
     */
    public boolean removeIndex(String indexName) {
        try {
            Index index = service.getIndexes().get(indexName);
            index.remove();
            return true;
        } catch (Exception e) {
            LOGGER.info("Invalid Index Name", e);
            return false;
        }
    }

    private Map<String, Object> processSet(Set<Map.Entry<String, Object>> set) {
        Map<String, Object> mapFromSet = new HashMap<String, Object>();
        for (Map.Entry<String, Object> entry : set) {
            mapFromSet.put(entry.getKey(), entry.getValue());
        }
        return mapFromSet;
    }
}
