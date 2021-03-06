/**
 * (c) 2018 WhiteSky Labs, Pty Ltd. The software in this package is published under the terms of the CPAL v1.0 license,
 * a copy of which has been included with this distribution in the LICENSE.md file.
 */
/**
 * (c) 2003-2014 MuleSoft, Inc. The software in this package is published under the terms of the CPAL v1.0 license,
 * a copy of which has been included with this distribution in the LICENSE.md file.
 **/
package org.mule.modules.splunk.exception;

/**
 * Exception thrown by the connector when we cannot resolve it to {@link org.mule.modules.splunk.exception.SplunkConnectorException}
 */
public class SplunkConnectorException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 1269156746029212077L;

    /**
     * Create a SplunkConnectorException
     *
     * @param message
     *            The human-readable message
     * @param cause
     *            The underlying exception
     */
    public SplunkConnectorException(String message, Throwable cause) {
        super(message, cause);
    }
}
