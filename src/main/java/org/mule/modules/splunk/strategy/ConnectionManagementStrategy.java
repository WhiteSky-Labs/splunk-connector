/**
 *
 * (c) 2015 WhiteSky Labs, Pty Ltd. This software is protected under international
 * copyright law. All use of this software is subject to WhiteSky Labs' Master
 * Subscription Agreement (or other Terms of Service) separately entered
 * into between you and WhiteSky Labs. If such an agreement is not in
 * place, you may not use the software.
 */

package org.mule.modules.splunk.strategy;

import org.mule.api.ConnectionException;
import org.mule.api.annotations.Configurable;
import org.mule.api.annotations.Connect;
import org.mule.api.annotations.ConnectionIdentifier;
import org.mule.api.annotations.Disconnect;
import org.mule.api.annotations.TestConnectivity;
import org.mule.api.annotations.ValidateConnection;
import org.mule.api.annotations.components.ConnectionManagement;
import org.mule.api.annotations.display.Password;
import org.mule.api.annotations.display.Placement;
import org.mule.api.annotations.param.ConnectionKey;
import org.mule.modules.splunk.SplunkClient;

@ConnectionManagement(friendlyName = "Connection Management", configElementName = "config-type")
public class ConnectionManagementStrategy {

    private SplunkClient client;

    /**
     * The Splunk Username
     */
    @Placement(order = 1, group = "Connection")
    private String username;

    /**
     * The Splunk Password
     */
    @Password
    @Placement(order = 2, group = "Connection")
    private String password;

    /**
     * The Splunk Host
     */
    @Configurable
    @Placement(order = 1, group = "General")
    private String host;

    /**
     * The Splunk Port
     */
    @Configurable
    @Placement(order = 2, group = "General")
    private String port;

    /**
     * Connect to a Splunk instance
     *
     * @param username
     *            A username
     * @param password
     *            A password
     * @throws ConnectionException
     *             on invalid credentials
     */
    @Connect
    @TestConnectivity
    public void connect(@ConnectionKey String username, @Password String password) throws ConnectionException {
        setClient(new SplunkClient());
        setUsername(username);
        setPassword(password);
        client.connect(this);
    }

    /**
     * Disconnect the connector
     */
    @Disconnect
    public void disconnect() {
        client = null;
    }

    /**
     * Validate the connection
     *
     * @return true/false if the Connection is valid
     */
    @ValidateConnection
    public boolean isConnected() {
        return client != null && client.getService()
                .getToken() != null && client.getService()
                .login() != null;
    }

    /**
     * Get the Connection Identifier (the token)
     *
     * @return the token, or 001 if there is no token
     */
    @ConnectionIdentifier
    public String getConnectionIdentifier() {
        if (client.getService() != null) {
            return client.getService()
                    .getToken();
        } else {
            return "001";
        }
    }

    /**
     * Get the SplunkClient instance being used to connect
     * 
     * @return the SplunkClient instance being used
     */
    public SplunkClient getClient() {
        return this.client;
    }

    /**
     * Set the SplunkClient instance to the one provided
     * 
     * @param client
     *            the SplunkClient to set
     */
    public void setClient(SplunkClient client) {
        this.client = client;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public int getIntPort() {
        return isValid() && isInt(getPort()) ? Integer.parseInt(getPort()) : 0;
    }

    public void setPort(String port) {
        this.port = port;
    }

    /**
     * Check if a String value is an integer
     *
     * @param str
     *            A string to test if it represents an integer
     * @return true/false
     */
    private boolean isInt(String str) {
        return str.matches("^-?\\d+$");
    }

    /**
     * Checks if the connection credentials are valid or not
     * 
     * @return True when credentials are valid, otherwise False
     */
    public boolean isValid() {
        return !isEmpty(getUsername()) && !isEmpty(getPassword()) && !isEmpty(getHost()) && !isEmpty(getPort());
    }

    private boolean isEmpty(String value) {
        return value == null || value.isEmpty() ? true : false;
    }

}
