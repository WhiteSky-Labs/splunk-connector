/**
 * (c) 2018 WhiteSky Labs, Pty Ltd. The software in this package is published under the terms of the CPAL v1.0 license,
 * a copy of which has been included with this distribution in the LICENSE.md file.
 */
package org.mule.modules.splunk.exception;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Test {@link org.mule.modules.splunk.exception.SplunkConnectorException} internals
 * <p/>
 */

public class SplunkConnectorExceptionTest {

    @Test
    public void testConstructor() {
        Exception dummyException = new Exception();
        SplunkConnectorException ex = new SplunkConnectorException("Testing Exception", dummyException);
        assertEquals("Testing Exception", ex.getMessage());
        assertEquals(dummyException, ex.getCause());
    }

}
