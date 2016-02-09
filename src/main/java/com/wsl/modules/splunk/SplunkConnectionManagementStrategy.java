package com.wsl.modules.splunk;

import org.mule.api.ConnectionException;
import org.mule.api.annotations.Connect;
import org.mule.api.annotations.ConnectionIdentifier;
import org.mule.api.annotations.Disconnect;
import org.mule.api.annotations.TestConnectivity;
import org.mule.api.annotations.ValidateConnection;
import org.mule.api.annotations.components.ConnectionManagement;
import org.mule.api.annotations.display.Password;
import org.mule.api.annotations.param.ConnectionKey;

@ConnectionManagement(friendlyName="ConnectionManagement", configElementName="config-name")
public class SplunkConnectionManagementStrategy {

    private SplunkClient client;
	
    public SplunkConnectionManagementStrategy() {
		// TODO Auto-generated constructor stub
	}
    /**
     * Connect to a splunk instance
     *
     * @param username A username
     * @param password A password
     * @throws ConnectionException
     */
    @Connect
    @TestConnectivity
    public void connect(@ConnectionKey String username, @Password String password)
            throws ConnectionException {
        // Create a Service instance and log in with the argument map
        client.connect(username, password);
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
        return client != null && client.getService().getToken() != null && client.getService().login() != null;
    }

    /**
     * Get the Connection Identifier (the token)
     *
     * @return the token, or 001 if there is no token
     */
    @ConnectionIdentifier
    public String getConnectionIdentifier() {
        if (client.getService() != null) {
            return client.getService().getToken();
        } else {
            return "001";
        }
    }
}
