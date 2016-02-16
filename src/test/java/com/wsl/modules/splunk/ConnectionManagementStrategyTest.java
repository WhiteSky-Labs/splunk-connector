package com.wsl.modules.splunk;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mule.api.ConnectionException;

import com.splunk.Service;
import com.wsl.modules.strategy.ConnectionManagementStrategy;

public class ConnectionManagementStrategyTest {

	private ConnectionManagementStrategy connectionStrategy;

	@Mock
	SplunkClient client;
	@Mock
	Service service;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		
		this.connectionStrategy = spy(new ConnectionManagementStrategy());
	}

	@Test
	public void testConnect() {
		connectionStrategy.setHost("localhost");
		connectionStrategy.setPort("8080");
		
		try {
			connectionStrategy.connect("admin", "admin");
			fail("Exception should be thrown");
		} catch (ConnectionException e) {
			assertEquals("Connection refused", e.getMessage());
			assertNotNull(connectionStrategy.getClient());
			assertEquals("admin", connectionStrategy.getUsername());
			assertEquals("admin", connectionStrategy.getPassword());
		}
	}
	
	@Test
	public void testDisconnect() {
		connectionStrategy.disconnect();
		assertEquals(null, connectionStrategy.getClient());
	}
	
	@Test
	public void testIsConnected() {
		connectionStrategy.setClient(client);
		when(client.getService()).thenReturn(service);
		when(service.getToken()).thenReturn("token");
		when(client.getService()).thenReturn(service);
		when(service.login()).thenReturn(service);
		
		assertEquals(true, connectionStrategy.isConnected());
	}
	
	@Test
	public void testIsConnectedNullClient() {
		assertEquals(false, connectionStrategy.isConnected());
	}
	
	@Test
	public void testIsConnectedNullToken() {
		connectionStrategy.setClient(client);
		when(client.getService()).thenReturn(service);
		assertEquals(false, connectionStrategy.isConnected());
	}
	
	@Test
	public void testIsConnectedNullService() {
		connectionStrategy.setClient(client);
		when(client.getService()).thenReturn(service);
		when(service.getToken()).thenReturn("token");
		when(client.getService()).thenReturn(service);
		
		assertEquals(false, connectionStrategy.isConnected());
	}
	
	@Test
	public void testGetConnectionIdentifier() {
		connectionStrategy.setClient(client);
		when(client.getService()).thenReturn(service);
		when(service.getToken()).thenReturn("token");
		
		assertEquals("token", connectionStrategy.getConnectionIdentifier());
	}
	
	@Test
	public void testGetConnectionIdentifierDefault() {
		connectionStrategy.setClient(client);
		assertEquals("001", connectionStrategy.getConnectionIdentifier());
	}
	
	@Test
	public void testIsValid() {
		connectionStrategy.setUsername("Test");
		connectionStrategy.setPassword("Test");
		connectionStrategy.setHost("localhost");
		connectionStrategy.setPort("8089");
		
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
		connectionStrategy.setUsername("Test");
		connectionStrategy.setPassword("Test");
		connectionStrategy.setHost("");
		connectionStrategy.setPort("8089");
		
		assertEquals(false, connectionStrategy.isValid());
	}
	
	@Test
	public void testIsValidWithOneNullValue() {
		connectionStrategy.setUsername("Test");
		connectionStrategy.setPassword(null);
		connectionStrategy.setHost("localhost");
		connectionStrategy.setPort("8089");
		
		assertEquals(false, connectionStrategy.isValid());
	}
	
	@Test
	public void testIsValidWithMixedValues() {
		connectionStrategy.setUsername("Test");
		connectionStrategy.setPassword(null);
		connectionStrategy.setHost("localhost");
		connectionStrategy.setPort("");
		
		assertEquals(false, connectionStrategy.isValid());
	}
	
	@Test
	public void testGetIntPort() {
		connectionStrategy.setUsername("Test");
		connectionStrategy.setPassword("Test");
		connectionStrategy.setHost("localhost");
		connectionStrategy.setPort("8089");
		
		assertEquals(8089, connectionStrategy.getIntPort());
	}
	
	@Test
	public void testGetIntPortWithNullPortValue() {
		connectionStrategy.setUsername("Test");
		connectionStrategy.setPassword("Test");
		connectionStrategy.setHost("localhost");
		connectionStrategy.setPort(null);
		
		assertEquals(0, connectionStrategy.getIntPort());
	}
	
	@Test
	public void testGetIntPortWithEmptyPortValue() {
		connectionStrategy.setUsername("Test");
		connectionStrategy.setPassword("Test");
		connectionStrategy.setHost("localhost");
		connectionStrategy.setPort("");
		
		assertEquals(0, connectionStrategy.getIntPort());
	}
	
	@Test
	public void testGetIntPortWithNonIntPortValue() {
		connectionStrategy.setUsername("Test");
		connectionStrategy.setPassword("Test");
		connectionStrategy.setHost("localhost");
		connectionStrategy.setPort("dummy");
		
		assertEquals(0, connectionStrategy.getIntPort());
	}
}
