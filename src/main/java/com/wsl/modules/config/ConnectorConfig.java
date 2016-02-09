package com.wsl.modules.config;

import org.mule.api.annotations.Configurable;
import org.mule.api.annotations.components.Configuration;

@Configuration(friendlyName = "Configuration")
public class ConnectorConfig {

	/**
	 * The Splunk Host
	 */
	@Configurable
     private String host;

	/**
	 * The Splunk Port
	 */
	@Configurable
	private String port;

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
        return isValid() ? Integer.parseInt(getPort()) : 0;
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
	boolean isInt(String str) {
        return str.matches("^-?\\d+$");
	}

	public boolean isValid() {
        return getHost() != null && getPort() != null && !getHost().isEmpty()
				&& getPort().isEmpty();
	}
}
