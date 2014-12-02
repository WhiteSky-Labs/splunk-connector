/**
 * (c) 2003-2012 MuleSoft, Inc. This software is protected under international
 * copyright law. All use of this software is subject to MuleSoft's Master
 * Subscription Agreement (or other Terms of Service) separately entered
 * into between you and MuleSoft. If such an agreement is not in
 * place, you may not use the software.
 **/
package org.mule.modules.splunk;

import com.splunk.*;
import org.mule.api.ConnectionException;
import org.mule.api.annotations.*;
import org.mule.api.annotations.display.Password;
import org.mule.api.annotations.display.Placement;
import org.mule.api.annotations.param.ConnectionKey;
import org.mule.api.annotations.param.Default;
import org.mule.api.annotations.param.Optional;
import org.mule.api.callback.SourceCallback;
import org.mule.modules.splunk.exception.SplunkConnectorException;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Splunk Mule Connector
 *
 * @author WhiteSky Labs
 */
@Connector(name = "splunk", schemaVersion = "1.0.0-SNAPSHOT", friendlyName = "Splunk")
//@RequiresEnterpriseLicense
public class SplunkConnector {

    /**
     * The Splunk Host
     */
    @Configurable
    private String host;

    /**
     * The Splunk Host
     */
    @Configurable
    private int port;

    private SplunkClient splunkClient;

    /**
     * Public Constructor to support Unit Tests
     */
    public SplunkConnector() {

    }

    /**
     * Instantiate a Splunk Connector using a pre-created client
     *
     * @param client The Splunk Client to use
     */
    public SplunkConnector(SplunkClient client) {
        this.splunkClient = client;
    }

    /**
     * Connect to a splunk instance
     *
     * @param username A username
     * @param password A password
     * @throws ConnectionException
     */
    @Connect
    public void connect(@ConnectionKey String username, @Password String password)
            throws ConnectionException {
        // Create a Service instance and log in with the argument map
        splunkClient = new SplunkClient(this);
        splunkClient.connect(username, password, this.getHost(), this.getPort());
    }

    /**
     * Disconnect the connector
     */
    @Disconnect
    public void disconnect() {
        if (splunkClient != null) {
            if (splunkClient.getService() != null) {
                splunkClient.setService(null);
            }
            splunkClient = null;
        }
    }

    /**
     * Validate the connection
     *
     * @return true/false if the Connection is valid
     */
    @ValidateConnection
    public boolean isConnected() {

        return splunkClient != null && splunkClient.getService().getToken() != null;
    }

    /**
     * Get the Connection Identifier (the token)
     *
     * @return the token, or 001 if there is no token
     */
    @ConnectionIdentifier
    public String getConnectionIdentifier() {
        if (splunkClient.getService() != null) {
            return splunkClient.getService().getToken();
        }
        return "001";
    }

    /**
     * List Applications
     * <p/>
     * {@sample.xml ../../../doc/splunk-connector.xml.sample splunk:get-applications}
     *
     * @return Applications
     */
    @Processor
    public List<Application> getApplications() {
        return splunkClient.getApplications();
    }

    /**
     * Get Jobs
     * <p/>
     * {@sample.xml ../../../doc/splunk-connector.xml.sample splunk:get-jobs}
     *
     * @return List of jobs of the current user
     */
    @Processor
    public List<Job> getJobs() {
        return splunkClient.getJobs();
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
        splunkClient.runNormalSearch(searchQuery, searchArgs, searchCallback);
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
        return splunkClient.runBlockingSearch(searchQuery, searchArgs);
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
        return splunkClient.runOneShotSearch(searchQuery, earliestTime, latestTime, args);
    }

    /**
     * Retrieve and list the saved searches in the saved search collection.
     * <p/>
     * {@sample.xml ../../../doc/splunk-connector.xml.sample splunk:get-saved-searches}
     *
     * @param app   Optional the name of the App to restrict the namespace of the list of saved searches to
     * @param owner Optional the owner of the namespace to restrict the list of saved searches by
     * @return SavedSearchCollection
     */
    @Processor
    public List<SavedSearch> getSavedSearches(@Optional String app, @Optional String owner) {
        return splunkClient.getSavedSearches(app, owner);
    }

    /**
     * Create a saved search.
     * <p/>
     * {@sample.xml ../../../doc/splunk-connector.xml.sample splunk:create-saved-search}
     *
     * @param searchName  The name of the search
     * @param searchQuery The query
     * @param searchArgs  A Map of Key-Value Arguments for the saved search. See the Splunk documentation for details.
     * @return SavedSearch the SavedSearch object that can then be executed
     */
    @Processor
    public SavedSearch createSavedSearch(String searchName, String searchQuery, @Optional Map<String, Object> searchArgs) {
        return splunkClient.createSavedSearch(searchName, searchQuery, searchArgs);
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
        return splunkClient.viewSavedSearchProperties(searchName, app, owner);
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
    public SavedSearch modifySavedSearchProperties(String searchName, Map<String, Object> searchProperties) throws SplunkConnectorException {
        return splunkClient.modifySavedSearchProperties(searchName, searchProperties);
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
    public List<Job> getSavedSearchHistory(@Optional String searchName, @Optional String app, @Optional String owner) {
        return splunkClient.getSavedSearchHistory(searchName, app, owner);
    }

    /**
     * Run the saved Search
     * <p/>
     * {@sample.xml ../../../doc/splunk-connector.xml.sample splunk:run-saved-search}
     *
     * @param searchName The name of the search
     * @return The search results as parsed XML, in the form of a list of HashMaps
     * @throws org.mule.modules.splunk.exception.SplunkConnectorException when there is a problem running the saved search
     */
    @Processor
    public List<Map<String, Object>> runSavedSearch(String searchName) throws SplunkConnectorException {
        return splunkClient.runSavedSearch(searchName);
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
        return splunkClient.runSavedSearchWithArguments(searchName, customArgs, searchDispatchArgs);
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
        return splunkClient.deleteSavedSearch(searchName);
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
    public DataModel getDataModel(String dataModelName) {
        return splunkClient.getDataModel(dataModelName);
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
        splunkClient.runRealTimeSearch(searchQuery, earliestTime, latestTime, statusBuckets, previewCount, callback);
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
     * @throws org.mule.modules.splunk.exception.SplunkConnectorException when there is an issue running the search
     */
    @Source
    public void runExportSearch(String searchQuery, @Default("-1h") String earliestTime, @Default("now") String latestTime, final SourceCallback callback) throws SplunkConnectorException {
        splunkClient.runExportSearch(searchQuery, earliestTime, latestTime, SearchMode.NORMAL, OutputMode.JSON, null, callback);
    }

    /**
     * Get the Hostname
     *
     * @return the Splunk Server hostname
     */
    public String getHost() {
        return this.host;
    }

    /**
     * Set splunk hostname
     *
     * @param host The splunk host
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * Get the splunk Port
     *
     * @return the Port of the Splunk Server
     */
    public int getPort() {
        return port;
    }

    /**
     * Set the Splunk Port
     *
     * @param port The port of the Splunk Server to set
     */
    public void setPort(int port) {
        this.port = port;
    }

}
