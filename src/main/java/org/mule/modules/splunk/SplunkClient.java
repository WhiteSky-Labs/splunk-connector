/**
 *
 * (c) 2015 WhiteSky Labs, Pty Ltd. This software is protected under international
 * copyright law. All use of this software is subject to WhiteSky Labs' Master
 * Subscription Agreement (or other Terms of Service) separately entered
 * into between you and WhiteSky Labs. If such an agreement is not in
 * place, you may not use the software.
 */

package org.mule.modules.splunk;

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

import com.splunk.CollectionArgs;
import com.splunk.InputKind;
import com.splunk.SSLSecurityProtocol;
import com.splunk.SavedSearchDispatchArgs;
import com.splunk.Service;
import com.splunk.ServiceArgs;

/**
 * The class that provides various Splunk functionality.
 */
public class SplunkClient extends AbstractClient {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(SplunkClient.class);

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
            setService(Service.connect(loginArgs));
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
        return getApplicationService().getApplications();
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
            return getDataModelService().getDataModel(dataModelName);
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
        return getDataModelService().getDataModels();
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
            return getIndexService().addDataToIndex(indexName, stringData, indexArgs);
        } catch (NullPointerException e) {
            throw new SplunkConnectorException("Unable to find Index with indexName=" + indexName, e);
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
            return getIndexService().cleanIndex(indexName, maxSeconds);
        } catch (NullPointerException e) {
            throw new SplunkConnectorException("Unable to find Index with indexName=" + indexName, e);
        } catch (Exception e) {
            throw new SplunkConnectorException(e.getMessage(), e);
        }
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
            return getIndexService().createIndex(indexName, args);
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
            return getIndexService().getIndex(indexIdentifier);
        } catch (Exception e) {
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
        return getIndexService().getIndexes(sortKey, sortDirection, collectionParameters);
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
            return getIndexService().modifyIndex(indexName, indexArgs);
        } catch (NullPointerException e) {
            throw new SplunkConnectorException("Unable to find Index with indexName=" + indexName, e);
        } catch (Exception e) {
            throw new SplunkConnectorException(e.getMessage(), e);
        }
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
        return getSavedSearchService().getSavedSearches(app, owner);
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
            return getSavedSearchService().createSavedSearch(searchName, searchQuery, searchArgs);
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
            return getSavedSearchService().viewSavedSearchProperties(searchName, app, owner);
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
            return getSavedSearchService().modifySavedSearchProperties(searchName, searchProperties);
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
        return getSavedSearchService().getSavedSearchHistory(searchName, app, owner);
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
            Validate.notEmpty(searchName, "You must provide a valid search name");
            return getSavedSearchService().runSavedSearch(searchName);
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
            return getSavedSearchService().runSavedSearchWithArguments(searchName, customArgs, searchDispatchArgsParam);
        } catch (NullPointerException npe) {
            throw new SplunkConnectorException("Unable to find Saved Search with searchName=" + searchName, npe);
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
        return getSavedSearchService().deleteSavedSearch(searchName);
    }

    /**
     * Get the current users Jobs
     *
     * @return A List of the current user's Job objects retrieved from the
     *         Splunk Server
     */
    public List<Map<String, Object>> getJobs() {
        return getJobService().getJobs();
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
            getSearchService().runNormalSearch(searchQuery, searchArgs, searchCallback);
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
            return getSearchService().runBlockingSearch(searchQuery, searchArgs);
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
            Validate.notEmpty(latestTime, "Latest Time is empty.");
            return getSearchService().runOneShotSearch(searchQuery, earliestTime, latestTime, args);
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
        try {
            Validate.notEmpty(searchQuery, "Search Query is empty.");
            getSearchService().runRealTimeSearch(searchQuery, earliestTime, latestTime, statusBuckets, previewCount, callback);
        } catch (Exception e) {
            throw new SplunkConnectorException(e.getMessage(), e);
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
            getSearchService().runExportSearch(searchQuery, earliestTime, latestTime, searchMode, outputMode, callback);
        } catch (IllegalArgumentException e) {
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
        return getInputService().getInputs();
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
            return getInputService().createInput(inputIdentifier, kind, args);
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
            return getInputService().modifyInput(inputIdentifier, inputArgs);
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
            return getInputService().getInput(inputIdentifier);
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
        return getInputService().addDataToTcpInput(portNumber, stringData);
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
        return getInputService().addDataToUdpInput(portNumber, data);
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
        return getInputService().removeInput(inputIdentifier);
    }

    /**
     * Remove an index
     *
     * @param indexName
     *            The name of the index to remove
     * @return Success or Failure
     */
    public boolean removeIndex(String indexName) {
        return getIndexService().removeIndex(indexName);
    }
}
