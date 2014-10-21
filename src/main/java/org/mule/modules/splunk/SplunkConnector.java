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
import org.mule.api.MuleContext;
import org.mule.api.MuleEvent;
import org.mule.api.MuleException;
import org.mule.api.annotations.*;
import org.mule.api.annotations.display.Password;
import org.mule.api.annotations.display.Placement;
import org.mule.api.annotations.param.ConnectionKey;
import org.mule.api.ConnectionException;
import org.mule.api.annotations.param.Default;
import org.mule.api.callback.SourceCallback;
import org.mule.api.context.MuleContextAware;
import org.mule.common.metadata.MetaData;
import org.mule.common.metadata.MetaDataKey;
import org.mule.modules.splunk.exception.SplunkConnectorException;

import java.io.IOException;
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
     * Connect
     *
     * @param username A username
     * @param password A password
     * @throws ConnectionException
     */
    @Connect
    public void connect(@ConnectionKey String username, @Password String password)
            throws ConnectionException {
        splunkClient = new SplunkClient(this);
        splunkClient.connect(username, password);
    }

    /**
     * Disconnect
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
     * Are we connected
     */
    @ValidateConnection
    public boolean isConnected() {

        if (splunkClient != null) {
            return splunkClient.getService().getToken() != null;
        }
        return false;
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
     * Run the saved search
     * <p/>
     * {@sample.xml ../../../doc/splunk-connector.xml.sample splunk:run-search}
     *
     * @param searchQuery   The search Query
     * @param executionMode The execution Mode
     * @return The job ans result
     */
    @Processor
    public Map<String, Object> runSearch(String searchQuery, JobArgs.ExecutionMode executionMode) throws SplunkConnectorException {
        return splunkClient.runSearch(searchQuery, executionMode);
    }


    /**
     * Run a basic oneshot search and display results
     *
     * @param searchQuery  The search query
     * @param earliestTime The earliest time
     * @param latestTime   The latest time
     * @return
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
     * {@sample.xml ../../../doc/splunk-connector.xml.sample splunk:create-saved-searches}
     *
     * @param searchName  The name of the search
     * @param searchQuery The query
     * @return SavedSearch
     */
    @Processor
    public SavedSearch createSavedSearch(String searchName, String searchQuery) throws Exception {
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
     *
     * @param searchName The name of the search
     * @return Job
     * @throws InterruptedException
     */
    @Processor
    public List<Map<String, Object>> runSavedSearch(String searchName) throws SplunkConnectorException {
        return splunkClient.runSavedSearch(searchName);
    }

    /**
     * Run a saved search with run-time arguments
     *
     * @param searchName         The name of the search
     * @param searchQuery        The query
     * @param customArgs         Custom Arguments
     * @param searchDispatchArgs SearchDispatch Args
     * @return
     * @throws SplunkConnectorException
     */
    @Processor
    public List<Map<String, Object>> runSavedSearchWithArguments(String searchName, String searchQuery,
                                                                 Map<String, Object> customArgs,
                                                                 SavedSearchDispatchArgs searchDispatchArgs)
            throws SplunkConnectorException {
        return splunkClient.runSavedSearchWithArguments(searchName, searchQuery, customArgs, searchDispatchArgs);
    }

    /**
     * Delete the Specified saved Search
     *
     * @param searchName The name of the search
     * @return true
     */
    @Processor
    public boolean deleteSavedSearch(String searchName) {
        return splunkClient.deleteSavedSearch(searchName);
    }

    /**
     * Run realtime search and process the response
     *
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    @Source
    public void runRealTimeSearch(String searchQuery,
                                  @Placement(group = "Job Properties") JobArgs.ExecutionMode executionMode,
                                  @Placement(group = "Job Properties") JobArgs.SearchMode searchMode,
                                  @Placement(group = "Job Properties") String earliestTime,
                                  @Placement(group = "Job Properties") String latestTime,
                                  @Placement(group = "Job Properties") @Default("300") int statusBuckets,
                                  @Placement(group = "Preview Properties") @Default("300") int previewCount,
                                  final SourceCallback callback_) throws SplunkConnectorException {
        final SoftCallback callback = new SoftCallback(callback_);
        splunkClient.runRealtimeSearch(searchQuery, executionMode, searchMode,
                earliestTime, latestTime, statusBuckets, previewCount, callback);
    }

    /**
     * Run export search
     *
     * @param searchQuery The query
     * @param exportArgs  The export arguments
     * @return
     * @throws IOException
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
     * @return
     */
    public int getPort() {
        return port;
    }

    /**
     * Set the Splunk Port
     *
     * @param port
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
