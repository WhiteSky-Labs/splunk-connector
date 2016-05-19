/**
 *
 * (c) 2016 WhiteSky Labs, Pty Ltd. This software is protected under international
 * copyright law. All use of this software is subject to WhiteSky Labs' Master
 * Subscription Agreement (or other Terms of Service) separately entered
 * into between you and WhiteSky Labs. If such an agreement is not in
 * place, you may not use the software.
 */
package org.mule.modules.splunk.automation.system;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.mule.api.ConnectionException;
import org.mule.modules.splunk.strategy.ConnectionManagementStrategy;
import org.mule.tools.devkit.ctf.configuration.util.ConfigurationUtils;
import org.mule.tools.devkit.ctf.exceptions.ConfigurationLoadingFailedException;

public class ConnectionManagementStrategyTestCase {

    private Properties credentials;
    private ConnectionManagementStrategy connectionStrategy;
    private String host;
    private String port;
    private String username;
    private String password;

    @Before
    public void setup() throws ConfigurationLoadingFailedException {
        this.credentials = ConfigurationUtils.getAutomationCredentialsProperties();
        this.connectionStrategy = new ConnectionManagementStrategy();
        this.host = credentials.getProperty("config-type.host");
        this.port = credentials.getProperty("config-type.port");
        this.username = credentials.getProperty("config-type.username");
        this.password = credentials.getProperty("config-type.password");
    }

    @Test
    public void testConnect() {
        connectionStrategy.setHost(host);
        connectionStrategy.setPort(port);

        try {
            connectionStrategy.connect(username, password);
        } catch (ConnectionException e) {
            fail("Exception should not be thrown");
        }
    }

    @Test
    public void testDisconnect() {
        connectionStrategy.disconnect();
        assertEquals(null, connectionStrategy.getClient());
    }

    @Test
    public void testIsConnected() {
        connectionStrategy.setHost(host);
        connectionStrategy.setPort(port);

        try {
            connectionStrategy.connect(username, password);
        } catch (ConnectionException e) {
        }
        assertEquals(true, connectionStrategy.isConnected());
    }

    @Test
    public void testIsConnectedNullClient() {
        connectionStrategy.setClient(null);
        assertEquals(false, connectionStrategy.isConnected());
    }

    @Test
    public void testGetConnectionIdentifier() {
        connectionStrategy.setHost(host);
        connectionStrategy.setPort(port);

        try {
            connectionStrategy.connect(username, password);
        } catch (ConnectionException e) {
        }
        assertFalse("001".equals(connectionStrategy.getConnectionIdentifier()));
    }

    @Test
    public void testIsValid() {
        connectionStrategy.setUsername(username);
        connectionStrategy.setPassword(password);
        connectionStrategy.setHost(host);
        connectionStrategy.setPort(port);

        assertEquals(true, connectionStrategy.isValid());
    }

    @Test
    public void testIsValidWithNullValues() {
        connectionStrategy.setUsername(null);
        connectionStrategy.setPassword(null);
        connectionStrategy.setHost(null);
        connectionStrategy.setPort(null);

        assertEquals(false, connectionStrategy.isValid());
    }

    @Test
    public void testIsValidWithEmptyValues() {
        connectionStrategy.setUsername("");
        connectionStrategy.setPassword("");
        connectionStrategy.setHost("");
        connectionStrategy.setPort("");

        assertEquals(false, connectionStrategy.isValid());
    }

    @Test
    public void testIsValidWithOneEmptyValue() {
        connectionStrategy.setUsername(username);
        connectionStrategy.setPassword(password);
        connectionStrategy.setHost("");
        connectionStrategy.setPort(port);

        assertEquals(false, connectionStrategy.isValid());
    }

    @Test
    public void testIsValidWithOneNullValue() {
        connectionStrategy.setUsername(username);
        connectionStrategy.setPassword(null);
        connectionStrategy.setHost(host);
        connectionStrategy.setPort(port);

        assertEquals(false, connectionStrategy.isValid());
    }

    @Test
    public void testIsValidWithMixedValues() {
        connectionStrategy.setUsername(username);
        connectionStrategy.setPassword(null);
        connectionStrategy.setHost(host);
        connectionStrategy.setPort("");

        assertEquals(false, connectionStrategy.isValid());
    }

    @Test
    public void testGetIntPort() {
        connectionStrategy.setUsername(username);
        connectionStrategy.setPassword(password);
        connectionStrategy.setHost(host);
        connectionStrategy.setPort(port);

        assertEquals(8089, connectionStrategy.getIntPort());
    }

    @Test
    public void testGetIntPortWithNullPortValue() {
        connectionStrategy.setUsername(username);
        connectionStrategy.setPassword(password);
        connectionStrategy.setHost(host);
        connectionStrategy.setPort(null);

        assertEquals(0, connectionStrategy.getIntPort());
    }

    @Test
    public void testGetIntPortWithEmptyPortValue() {
        connectionStrategy.setUsername(username);
        connectionStrategy.setPassword(password);
        connectionStrategy.setHost(host);
        connectionStrategy.setPort("");

        assertEquals(0, connectionStrategy.getIntPort());
    }

    @Test
    public void testGetIntPortWithNonIntPortValue() {
        connectionStrategy.setUsername(username);
        connectionStrategy.setPassword(password);
        connectionStrategy.setHost(host);
        connectionStrategy.setPort("dummy");

        assertEquals(0, connectionStrategy.getIntPort());
    }
}
