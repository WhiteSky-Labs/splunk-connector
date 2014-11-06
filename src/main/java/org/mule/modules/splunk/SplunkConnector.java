/**
 * (c) 2003-2012 MuleSoft, Inc. This software is protected under international
 * copyright law. All use of this software is subject to MuleSoft's Master
 * Subscription Agreement (or other Terms of Service) separately entered
 * into between you and MuleSoft. If such an agreement is not in
 * place, you may not use the software.
 **/
package org.mule.modules.splunk;

import com.splunk.*;
import org.apache.commons.lang.UnhandledException;
import org.mule.api.ConnectionException;
import org.mule.api.MuleContext;
import org.mule.api.MuleEvent;
import org.mule.api.MuleException;
import org.mule.api.annotations.*;
import org.mule.api.annotations.display.Password;
import org.mule.api.annotations.display.Placement;
import org.mule.api.annotations.param.ConnectionKey;
import org.mule.api.annotations.param.Default;
import org.mule.api.annotations.param.Optional;
import org.mule.api.callback.SourceCallback;
import org.mule.api.context.MuleContextAware;
import org.mule.common.metadata.MetaData;
import org.mule.common.metadata.MetaDataKey;
import org.mule.modules.splunk.exception.SplunkConnectorException;

import java.util.List;
import java.util.Map;

/**
 * Cloud Connector
 *
 * @author MuleSoft, Inc.
 */
@Connector(name = "splunk", schemaVersion = "1.0.0-SNAPSHOT", friendlyName = "Splunk Connector", metaData = MetaDataSwitch.ON)
public class SplunkConnector implements MuleContextAware {

    private MuleContext muleContext;

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

    @MetaDataKeyRetriever
    public List<MetaDataKey> getMetadataKeys() {
        return splunkClient.getMetadata();
    }

    @MetaDataRetriever
    public MetaData getMetadata(MetaDataKey key) throws ClassNotFoundException {
        return splunkClient.getMetaDataKey(key);
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
     * Disconnect
     */
    @Disconnect
    public void disconnect() {
        if (splunkClient != null) {
            if (splunkClient.getService() != null) {
                splunkClient.setService();
            }
            splunkClient = null;
        }
    }

    /**
     * Are we connected
     */
    @ValidateConnection
    public boolean isConnected() {

        return splunkClient != null && splunkClient.getService().getToken() != null;
    }

    /**
     * Connection identifier
     */
    @ConnectionIdentifier
    public String connectionId() {
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
     * <<<<<<< HEAD
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
     * Run a normal search, which polls for completion. Needs a SourceCallback to return the results.
     * <p/>
     * {@sample.xml ../../../doc/splunk-connector.xml.sample splunk:run-normal-search}
     *
     * @param searchQuery   The search Query
     * @throws SplunkConnectorException when the search cannot execute
     * @return SourceCallback of the results
     */
    @Processor
    public Map<String, Object> runNormalSearch(String searchQuery) throws SplunkConnectorException {
        return splunkClient.runNormalSearch(searchQuery);
    }


    /**
     * Run a basic oneshot search and display results
     * <p/>
     * {@sample.xml ../../../doc/splunk-connector.xml.sample splunk:run-one-shot-search}
     *
     * @param searchQuery  The search query
     * @param earliestTime The earliest time
     * @param latestTime   The latest time
     * @throws SplunkConnectorException when an error occurs communicating with Splunk
     * @return The XML results of the search, parsed into a List of Hashmaps
     */
    @Processor
    public List<Map<String, Object>> runOneShotSearch(String searchQuery, String earliestTime, String latestTime) throws SplunkConnectorException {
        return splunkClient.runOneShotSearch(searchQuery, earliestTime, latestTime);
    }

    /**
     * Retrieve and list the saved searches in the saved search collection.
     * <p/>
     * {@sample.xml ../../../doc/splunk-connector.xml.sample splunk:get-saved-searches}
     *
     * @return SavedSearchCollection
     */
    @Processor
    public List<SavedSearch> getSavedSearches() {
        return splunkClient.getSavedSearches();
    }

    /**
     * Create a saved search.
     * <p/>
     * {@sample.xml ../../../doc/splunk-connector.xml.sample splunk:create-saved-search}
     *
     * @param searchName  The name of the search
     * @param searchQuery The query
     * @throws com.splunk.HttpException when communications are interrupted
     * @return SavedSearch the SavedSearch object that can then be executed
     */
    @Processor
    public SavedSearch createSavedSearch(String searchName, String searchQuery) throws HttpException {
        return splunkClient.createSavedSearch(searchName, searchQuery);
    }

    /**
     * View the properties of the  saved search
     * <p/>
     * {@sample.xml ../../../doc/splunk-connector.xml.sample splunk:view-saved-search}
     *
     * @param searchName The name of the search
     * @return SavedSearch
     */
    @Processor
    public SavedSearch viewSavedSearch(String searchName) {
        return splunkClient.getSavedSearch(searchName);
    }

    /**
     * View the properties of the  saved search
     * <p/>
     * {@sample.xml ../../../doc/splunk-connector.xml.sample splunk:view-saved-search}
     *
     * @param searchName     The name of the search
     * @param description    The desired description
     * @param isSetScheduled Set the Schedule to true
     * @param cronSchedule   The Cron Expression
     * @return SavedSearch
     */
    @Processor
    public SavedSearch modifySavedSearch(String searchName, String description,
                                         boolean isSetScheduled, @Default("15 4 * * 6") String cronSchedule) {
        return splunkClient.modifySavedSearch(searchName, description, isSetScheduled, cronSchedule);
    }

    /**
     * List the saved search history
     * <p/>
     * {@sample.xml ../../../doc/splunk-connector.xml.sample splunk:get-saved-search-history}
     *
     * @param searchName The name of the search
     * @return List of Job History
     */
    @Processor
    public List<Job> getSavedSearchHistory(String searchName) {
        return splunkClient.getSavedSearchHistory(searchName);
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
     * @param searchQuery        The query
     * @param customArgs         Custom Arguments, Optional list of custom arguments to supply
     * @param searchDispatchArgs Optional list of search dispatch arguments
     * @return The results as a List of Hashmaps
     * @throws SplunkConnectorException when there is an issue running the saved search
     */
    @Processor
    public List<Map<String, Object>> runSavedSearchWithArguments(String searchName, String searchQuery,
                                                                 @Optional Map<String, Object> customArgs,
                                                                 @Optional SavedSearchDispatchArgs searchDispatchArgs)
            throws SplunkConnectorException {
        return splunkClient.runSavedSearchWithArguments(searchName, searchQuery, customArgs, searchDispatchArgs);
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

    /** Run a realtime search and process the response
     * returns via a sourcecallback
     * <p/>
     * {@sample.xml ../../../doc/splunk-connector.xml.sample splunk:run-real-time-search}
     *
     * @param searchQuery The query to run in realtime
     * @param statusBuckets the status buckets to use - defaults to 300
     * @param previewCount the number of previews to retrieve - defaults to 100
     * @param callback_ the SourceCallback to capture the response
     * @throws SplunkConnectorException when there is a problem setting up the runtime search
     *
     */
    @Source
    public void runRealTimeSearch(String searchQuery,
                                  @Placement(group = "Job Properties") @Optional @Default("300") int statusBuckets,
                                  @Placement(group = "Preview Properties") @Optional @Default("100") int previewCount,
                                  final SourceCallback callback_) throws SplunkConnectorException {
        final SoftCallback callback = new SoftCallback(callback_);
        splunkClient.runRealTimeSearch(searchQuery, statusBuckets, previewCount, callback);
    }

    /**
     * Run an export search
     * <p/>
     * {@sample.xml ../../../doc/splunk-connector.xml.sample splunk:run-export-search}
     *
     * @param searchQuery the search query to run
     * @param exportArgs The arguments to the search
     * @return A list of the Search Results found from the export search.
     * @throws org.mule.modules.splunk.exception.SplunkConnectorException when there is an issue running the search
     */
    @Processor
    public List<SearchResults> runExportSearch(String searchQuery, JobExportArgs exportArgs) throws SplunkConnectorException {
        return splunkClient.runExportSearch(searchQuery, exportArgs);
    }

    /**
     * Get the host value
     */

    public String getHost() {
        return this.host;
    }

    /**
     * Set splunk host
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

    @Override
    public void setMuleContext(MuleContext context) {
        this.muleContext = context;
    }

    static final class SoftCallback implements SourceCallback {
        private final SourceCallback callback;

        public SoftCallback(SourceCallback callback) {
            this.callback = callback;
        }

        @Override
        public Object process() throws Exception {
            try {
                return callback.process();
            } catch (Exception e) {
                throw new UnhandledException(e);
            }
        }

        @Override
        public Object process(Object payload) {
            try {
                return callback.process(payload);
            } catch (Exception e) {
                throw new UnhandledException(e);
            }
        }

        @Override
        public Object process(Object payload, Map<String, Object> properties) throws Exception {
            try {
                return callback.process(payload);
            } catch (Exception e) {
                throw new UnhandledException(e);
            }
        }

        @Override
        public MuleEvent processEvent(MuleEvent event) throws MuleException {
            try {
                return callback.processEvent(event);
            } catch (Exception e) {
                throw new UnhandledException(e);
            }
        }
    }

    public MuleContext getMuleContext() {
        return muleContext;
    }
}
