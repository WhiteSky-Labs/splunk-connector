/**
 *
 * (c) 2015 WhiteSky Labs, Pty Ltd. This software is protected under international
 * copyright law. All use of this software is subject to WhiteSky Labs' Master
 * Subscription Agreement (or other Terms of Service) separately entered
 * into between you and WhiteSky Labs. If such an agreement is not in
 * place, you may not use the software.
 */

package org.mule.modules.splunk.exception;

import org.junit.Test;
import org.mule.modules.splunk.exception.SplunkConnectorException;

import static org.junit.Assert.assertEquals;

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
