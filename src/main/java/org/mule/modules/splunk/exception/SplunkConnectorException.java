/**
 * (c) 2003-2014 MuleSoft, Inc. The software in this package is published under the terms of the CPAL v1.0 license,
 * a copy of which has been included with this distribution in the LICENSE.md file.
 **/
package org.mule.modules.splunk.exception;

/**
 * Exception thrown by the connector when we cannot resolve it to {@link org.mule.modules.splunk.exception.SplunkConnectorException}
 */
public class SplunkConnectorException extends Exception {

    public SplunkConnectorException(String message) {
        super(message);
    }

    public SplunkConnectorException(String message, Throwable cause) {
        super(message, cause);
    }
}
