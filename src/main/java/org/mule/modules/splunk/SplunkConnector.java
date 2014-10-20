/**
 * (c) 2003-2012 MuleSoft, Inc. This software is protected under international
 * copyright law. All use of this software is subject to MuleSoft's Master
 * Subscription Agreement (or other Terms of Service) separately entered
 * into between you and MuleSoft. If such an agreement is not in
 * place, you may not use the software.
 **/

package org.mule.modules.splunk;

import com.splunk.Application;
import com.splunk.Job;
import com.splunk.JobArgs;
import org.mule.api.annotations.*;
import org.mule.api.annotations.display.Password;
import org.mule.api.annotations.param.ConnectionKey;
import org.mule.api.ConnectionException;
import org.mule.common.metadata.MetaData;
import org.mule.common.metadata.MetaDataKey;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Cloud Connector
 *
 * @author MuleSoft, Inc.
 */
@Connector(name = "splunk", schemaVersion = "1.0.0-SNAPSHOT", friendlyName = "Splunk Connector", metaData = MetaDataSwitch.ON)
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
    public Map<String, Object> runSearch(String searchQuery, JobArgs.ExecutionMode executionMode) {
        return splunkClient.runSearch(searchQuery, executionMode);
    }


    /**
     * Run a basic oneshot search and display results
     * @param searchQuery The search query
     * @param earliestTime The earliest time
     * @param latestTime The latest time
     * @return
     */
    @Processor
    public Map<String, Object> runOneShotSearch(String searchQuery, String earliestTime, String latestTime) {
        return splunkClient.runOneShotSearch(searchQuery, earliestTime, latestTime);
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
}
