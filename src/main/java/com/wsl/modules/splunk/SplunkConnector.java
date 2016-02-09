/**
 *
 * (c) 2015 WhiteSky Labs, Pty Ltd. This software is protected under international
 * copyright law. All use of this software is subject to WhiteSky Labs' Master
 * Subscription Agreement (or other Terms of Service) separately entered
 * into between you and WhiteSky Labs. If such an agreement is not in
 * place, you may not use the software.
 */

package com.wsl.modules.splunk;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mule.api.annotations.Config;
import org.mule.api.annotations.Connector;
import org.mule.api.annotations.Processor;
import org.mule.api.annotations.Source;
import org.mule.api.annotations.display.Placement;
import org.mule.api.annotations.licensing.RequiresEnterpriseLicense;
import org.mule.api.annotations.param.Default;
import org.mule.api.annotations.param.Optional;
import org.mule.api.callback.SourceCallback;

import com.splunk.CollectionArgs;
import com.splunk.InputKind;
import com.splunk.SavedSearchDispatchArgs;
import com.wsl.modules.config.ConnectorConfig;
import com.wsl.modules.splunk.exception.SplunkConnectorException;

/**
 * Splunk Mule Connector
 *
 * @author WhiteSky Labs
 */
@Connector(name = "splunk", schemaVersion = "1.0.0-SNAPSHOT", friendlyName = "Splunk")
@RequiresEnterpriseLicense
public class SplunkConnector {

    private SplunkConnectionManagementStrategy strategy;

    @Config
    private ConnectorConfig config;

    private SplunkClient client;

    /**
     * Get the SplunkClient instance being used to connect
     * @return the SplunkClient instance being used
     */
    public SplunkClient getClient(){
        return this.client;
    }

    /**
     * Set the SplunkClient instance to the one provided
     * @param client the SplunkClient to set
     */
    public void setClient(SplunkClient client){
        this.client = client;
    }

    /**
     * Public Constructor to support Unit Tests
     */
    public SplunkConnector() {
        setClient(new SplunkClient(this));
    }

    /**
     * List Applications
     * <p/>
     * {@sample.xml ../../../doc/splunk-connector.xml.sample splunk:get-applications}
     *
     * @return A List of Applications as a Map
     */
    @Processor
    public List<Map<String, Object>> getApplications() {
        return client.getApplications();
    }

    /**
     * Get Jobs
     * <p/>
     * {@sample.xml ../../../doc/splunk-connector.xml.sample splunk:get-jobs}
     *
     * @return List of jobs for the current user as Maps
     */
    @Processor
    public List<Map<String, Object>> getJobs() {
        return client.getJobs();
    }


    /**
     * Run a normal search, which polls for completion. Results will be streamed via a SourceCallback.
     * <p/>
     * {@sample.xml ../../../doc/splunk-connector.xml.sample splunk:run-normal-search}
     *
     * @param searchQuery    The search Query
     * @param searchArgs     The search properties
     * @param searchCallback the {@link SourceCallback} used to dispatch messages when a response is received
     * @throws SplunkConnectorException when the search cannot execute
     */
    @Source
    public void runNormalSearch(String searchQuery, @Optional Map<String, Object> searchArgs, final SourceCallback searchCallback) throws SplunkConnectorException {
        client.runNormalSearch(searchQuery, searchArgs, searchCallback);
    }

    /**
     * Runs a blocking search, which will wait for completion on the splunk server and return synchronously.
     * <p/>
     * {@sample.xml ../../../doc/splunk-connector.xml.sample splunk:run-blocking-search}
     *
     * @param searchQuery The search query
     * @param searchArgs  The map of search arguments, described in the splunk documentation.
     * @return the results as a list of hashmaps
     * @throws SplunkConnectorException when the search cannot execute
     */
    @Processor
    public Map<String, Object> runBlockingSearch(String searchQuery, @Optional Map<String, Object> searchArgs) throws SplunkConnectorException {
        return client.runBlockingSearch(searchQuery, searchArgs);
    }


    /**
     * Run a basic oneshot search and display results
     * NOTE: XML output appears to be the only supported type right now.
     * <p/>
     * {@sample.xml ../../../doc/splunk-connector.xml.sample splunk:run-one-shot-search}
     *
     * @param searchQuery  The search query
     * @param earliestTime The earliest time
     * @param latestTime   The latest time
     * @param args         Optional map of arguments to pass to the search.
     * @return The XML results of the search, parsed into a List of Hashmaps
     * @throws SplunkConnectorException when an error occurs communicating with Splunk
     */
    @Processor
    public List<Map<String, Object>> runOneShotSearch(String searchQuery, String earliestTime, String latestTime, @Optional Map<String, String> args) throws SplunkConnectorException {
        return client.runOneShotSearch(searchQuery, earliestTime, latestTime, args);
    }

    /**
     * Retrieve and list the saved searches in the saved search collection.
     * <p/>
     * {@sample.xml ../../../doc/splunk-connector.xml.sample splunk:get-saved-searches}
     *
     * @param app   Optional the name of the App to restrict the namespace of the list of saved searches to
     * @param owner Optional the owner of the namespace to restrict the list of saved searches by
     * @return A List of Maps that represent the saved searches
     */
    @Processor
    public List<Map<String, Object>> getSavedSearches(@Optional String app, @Optional String owner) {
        return client.getSavedSearches(app, owner);
    }

    /**
     * Create a saved search.
     * <p/>
     * {@sample.xml ../../../doc/splunk-connector.xml.sample splunk:create-saved-search}
     *
     * @param searchName  The name of the search
     * @param searchQuery The query
     * @param searchArgs  A Map of Key-Value Arguments for the saved search. See the Splunk documentation for details.
     * @return a map of the SavedSearch object that can then be executed
     */
    @Processor
    public Map<String, Object> createSavedSearch(String searchName, String searchQuery, @Optional Map<String, Object> searchArgs) {
        return client.createSavedSearch(searchName, searchQuery, searchArgs);
    }

    /**
     * View the properties of the  saved search
     * <p/>
     * {@sample.xml ../../../doc/splunk-connector.xml.sample splunk:view-saved-search-properties}
     *
     * @param searchName The name of the search
     * @param app        The Optional App to restrict the namespace
     * @param owner      The Optional Owner to restrict the namespace
     * @return EntrySet of the properties
     */
    @Processor
    public Set<Map.Entry<String, Object>> viewSavedSearchProperties(String searchName, @Optional String app, @Optional String owner) {
        return client.viewSavedSearchProperties(searchName, app, owner);
    }

    /**
     * Modify the properties of the  saved search
     * <p/>
     * {@sample.xml ../../../doc/splunk-connector.xml.sample splunk:modify-saved-search-properties}
     *
     * @param searchName       The name of the search
     * @param searchProperties The map of search properties to apply
     * @return The updated SavedSearch
     * @throws SplunkConnectorException when the search properties are invalid
     */
    @Processor
    public Map<String, Object> modifySavedSearchProperties(String searchName, Map<String, Object> searchProperties) throws SplunkConnectorException {
        return client.modifySavedSearchProperties(searchName, searchProperties);
    }

    /**
     * Modify the Saved Search Properties
     * <p/>
     * {@sample.xml ../../../doc/splunk-connector.xml.sample splunk:get-saved-search-history}
     *
     * @param searchName (Optional) The name of the search
     * @param app        (Optional) The application namespace for the saved search
     * @param owner      (Optional) The owner of the namespace for the saved search
     * @return List of Job History
     */
    @Processor
    public List<Map<String, Object>> getSavedSearchHistory(@Optional String searchName, @Optional String app, @Optional String owner) {
        return client.getSavedSearchHistory(searchName, app, owner);
    }

    /**
     * Run the saved Search
     * <p/>
     * {@sample.xml ../../../doc/splunk-connector.xml.sample splunk:run-saved-search}
     *
     * @param searchName The name of the search
     * @return The search results as parsed XML, in the form of a list of HashMaps
     * @throws com.wsl.modules.splunk.exception.SplunkConnectorException when there is a problem running the saved search
     */
    @Processor
    public List<Map<String, Object>> runSavedSearch(String searchName) throws SplunkConnectorException {
        return client.runSavedSearch(searchName);
    }

    /**
     * Run a saved search with run-time arguments
     * <p/>
     * {@sample.xml ../../../doc/splunk-connector.xml.sample splunk:run-saved-search-with-arguments}
     *
     * @param searchName         The name of the search
     * @param customArgs         Custom Arguments, Optional list of custom arguments to supply
     * @param searchDispatchArgs Optional list of search dispatch arguments
     * @return The results as a List of Hashmaps
     * @throws SplunkConnectorException when there is an issue running the saved search
     */
    @Processor
    public List<Map<String, Object>> runSavedSearchWithArguments(String searchName,
                                                                 @Optional Map<String, Object> customArgs,
                                                                 @Optional SavedSearchDispatchArgs searchDispatchArgs)
            throws SplunkConnectorException {
        return client.runSavedSearchWithArguments(searchName, customArgs, searchDispatchArgs);
    }

    /**
     * Delete the Specified saved Search
     * <p/>
     * {@sample.xml ../../../doc/splunk-connector.xml.sample splunk:delete-saved-search}
     *
     * @param searchName The name of the search
     * @return if the delete operation was successful (true/false)
     */
    @Processor
    public boolean deleteSavedSearch(String searchName) {
        return client.deleteSavedSearch(searchName);
    }

    /**
     * Get an individual data model
     * <p/>
     * {@sample.xml ../../../doc/splunk-connector.xml.sample splunk:get-data-model}
     *
     * @param dataModelName the name of the data model to get
     * @return the DataModel requested
     */
    @Processor
    public Map<String, Object> getDataModel(String dataModelName) {
        return client.getDataModel(dataModelName);
    }

    /**
     * Get all data models
     * <p/>
     * {@sample.xml ../../../doc/splunk-connector.xml.sample splunk:get-data-models}
     *
     * @return All DataModels available to the user
     */
    @Processor
    public List<Map<String, Object>> getDataModels() {
        return client.getDataModels();
    }


    /**
     * Run a realtime search and process the response
     * returns via a sourcecallback
     * <p/>
     * {@sample.xml ../../../doc/splunk-connector.xml.sample splunk:run-real-time-search}
     *
     * @param searchQuery   The query to run in realtime
     * @param earliestTime  The start time for the realtime search
     * @param latestTime    The latest time for the realtime search
     * @param statusBuckets the status buckets to use - defaults to 300
     * @param previewCount  the number of previews to retrieve - defaults to 100
     * @param callback      the SourceCallback to capture the response
     * @throws SplunkConnectorException when there is a problem setting up the runtime search
     */
    @Source
    public void runRealTimeSearch(String searchQuery, @Default("rt") String earliestTime, @Default("rt") String latestTime,
                                  @Placement(group = "Job Properties") @Default("300") int statusBuckets,
                                  @Placement(group = "Preview Properties") @Default("100") int previewCount,
                                  final SourceCallback callback) throws SplunkConnectorException {
        client.runRealTimeSearch(searchQuery, earliestTime, latestTime, statusBuckets, previewCount, callback);
    }

    /**
     * Run an export search, which streams results
     * <p/>
     * {@sample.xml ../../../doc/splunk-connector.xml.sample splunk:run-export-search}
     *
     * @param searchQuery  the search query to run
     * @param earliestTime The earliest time to search from, default is -1h
     * @param latestTime   The latest time to search to, default is now
     * @param callback     The SourceCallback to stream results to
     * @throws com.wsl.modules.splunk.exception.SplunkConnectorException when there is an issue running the search
     */
    @Source
    public void runExportSearch(String searchQuery, @Default("-1h") String earliestTime, @Default("now") String latestTime, final SourceCallback callback) throws SplunkConnectorException {
        client.runExportSearch(searchQuery, earliestTime, latestTime, SearchMode.NORMAL, OutputMode.JSON, callback);
    }

    /**
     * Get Inputs returns an InputCollection of possible inputs to use with Splunk
     * <p/>
     * {@sample.xml ../../../doc/splunk-connector.xml.sample splunk:get-inputs}
     *
     * @return List of Inputs available to the user
     */
    @Processor
    public List<Map<String, Object>> getInputs() {
        return client.getInputs();
    }

    /**
     * Creates an Input with a given identifier and kind
     * <p/>
     * {@sample.xml ../../../doc/splunk-connector.xml.sample splunk:create-input}
     *
     * @param inputIdentifier The name of the domain controller
     * @param kind The InputKind
     * @param args           An Optional Key-Value Map of Properties to set
     * @return An Input of that Kind
     */
    @Processor
    public Map<String, Object> createInput(String inputIdentifier, InputKind kind, @Optional Map<String, Object> args) {
        return client.createInput(inputIdentifier, kind, args);
    }

    /**
     * Modifies an input with the properties supplied.
     * <p/>
     * {@sample.xml ../../../doc/splunk-connector.xml.sample splunk:modify-input}
     *
     * @param inputIdentifier      A Splunk Input to modify.
     * @param inputArgs The map of properties to update
     * @return Returns the modified input.
     */
    @Processor
    public Map<String, Object> modifyInput(String inputIdentifier, Map<String, Object> inputArgs) {
        return client.modifyInput(inputIdentifier, inputArgs);
    }

    /**
     * Retrieves an Input with the given identifier
     * <p/>
     * {@sample.xml ../../../doc/splunk-connector.xml.sample splunk:get-input}
     *
     * @param inputIdentifier The identifier, for example a file path if it is a Monitor Input
     * @return The Input specified.
     */
    @Processor
    public Map<String, Object> getInput(String inputIdentifier) {
        return client.getInput(inputIdentifier);
    }

    /**
     * Retrieves a collection of indexes based on the criteria provided
     * <p/>
     * {@sample.xml ../../../doc/splunk-connector.xml.sample splunk:get-indexes}
     *
     * @param sortKey              The Key to sort by
     * @param sortDirection        The SortDirection to sort by
     * @param collectionParameters Optional Map of additional arguments to pass to the call
     * @return Collection of indexes
     */
    @Processor
    public List<Map<String, Object>> getIndexes(@Optional String sortKey, @Optional CollectionArgs.SortDirection sortDirection, @Optional Map<String, Object> collectionParameters) {
        return client.getIndexes(sortKey, sortDirection, collectionParameters);
    }

    /**
     * Creates an Index with optional arguments
     * <p/>
     * {@sample.xml ../../../doc/splunk-connector.xml.sample splunk:create-index}
     *
     * @param indexName The name of the index to create
     * @param args      Optional key-value pairs of arguments to apply on creation
     * @return the new Index
     */
    @Processor
    public Map<String, Object> createIndex(String indexName, @Optional Map<String, Object> args) {
        return client.createIndex(indexName, args);
    }

    /**
     * Modifies an index with the properties supplied.
     * <p/>
     * {@sample.xml ../../../doc/splunk-connector.xml.sample splunk:modify-index}
     *
     * @param indexName      A Splunk Index to modify.
     * @param indexArgs The map of properties to update
     * @return Returns the modified index.
     */
    @Processor
    public Map<String, Object> modifyIndex(String indexName, Map<String, Object> indexArgs) {
        return client.modifyIndex(indexName, indexArgs);
    }

    /**
     * Retrieves an Index with the given identifier
     * <p/>
     * {@sample.xml ../../../doc/splunk-connector.xml.sample splunk:get-index}
     *
     * @param indexIdentifier The identifier of the index
     * @return The Index specified.
     */
    @Processor
    public Map<String, Object> getIndex(String indexIdentifier) {
        return client.getIndex(indexIdentifier);
    }

    /**
     * Clean the index, which removes all events from it
     * <p/>
     * {@sample.xml ../../../doc/splunk-connector.xml.sample splunk:clean-index}
     *
     * @param indexName  The name of the index to clean
     * @param maxSeconds Optional how long to wait, -1 is forever (not recommended on a Connector). Default is 180s
     * @return the cleaned index
     */
    @Processor
    public Map<String, Object> cleanIndex(String indexName, @Default("180") int maxSeconds) {
        return client.cleanIndex(indexName, maxSeconds);
    }

    /**
     * Add data to an index without an input, using HTTP to submit a string
     * <p/>
     * {@sample.xml ../../../doc/splunk-connector.xml.sample splunk:add-data-to-index}
     *
     * @param indexName  The name of the index to update
     * @param stringData The data string to send
     * @param indexArgs Optional map of arguments to apply to the update
     * @return The index that has been updated
     */
    @Processor
    public Map<String, Object> addDataToIndex(String indexName, String stringData, @Optional Map<String, Object> indexArgs) {
        return client.addDataToIndex(indexName, stringData, indexArgs);
    }

    /**
     * Add data to a Tcp Input
     * <p/>
     * {@sample.xml ../../../doc/splunk-connector.xml.sample splunk:add-data-to-tcp-input}
     *
     * @param portNumber The port number to send data on
     * @param stringData The data to send
     * @return Success or Failure
     * @throws SplunkConnectorException when cannot connect to the port
     */
    @Processor
    public Boolean addDataToTcpInput(String portNumber, String stringData) throws SplunkConnectorException {
        return client.addDataToTcpInput(portNumber, stringData);
    }

    /**
     * Add data to a Udp Input
     * <p/>
     * {@sample.xml ../../../doc/splunk-connector.xml.sample splunk:add-data-to-udp-input}
     *
     * @param portNumber The port number to send data on
     * @param stringData The data to send
     * @return Success or Failure
     * @throws SplunkConnectorException when cannot connect to the port
     */
    @Processor
    public Boolean addDataToUdpInput(String portNumber, String stringData) throws SplunkConnectorException {
        return client.addDataToUdpInput(portNumber, stringData);
    }

    /**
     * Remove an input
     * <p/>
     * {@sample.xml ../../../doc/splunk-connector.xml.sample splunk:remove-input}
     *
     * @param inputIdentifier the identifier, for example the port number
     * @return Success or Failure
     */
    @Processor
    public Boolean removeInput(String inputIdentifier) {
        return client.removeInput(inputIdentifier);
    }

    /**
     * Remove an index
     * <p/>
     * {@sample.xml ../../../doc/splunk-connector.xml.sample splunk:remove-index}
     *
     * @param indexName The name of the index to remove
     * @return Success or Failure
     */
    @Processor
    public Boolean removeIndex(String indexName) {
        return client.removeIndex(indexName);
    }

    public SplunkConnectionManagementStrategy getStrategy()
    {
        return this.strategy;
    }

    public ConnectorConfig getConfig() {
        return config;
	}

	public void setConfig(ConnectorConfig config) {
        this.config = config;
	}

	public void setStrategy(SplunkConnectionManagementStrategy strategy) {
        this.strategy = strategy;
	}

}
