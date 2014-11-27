/**
 *
 * (c) 2003-2012 MuleSoft, Inc. This software is protected under international
 * copyright law. All use of this software is subject to MuleSoft's Master
 * Subscription Agreement (or other Terms of Service) separately entered
 * into between you and MuleSoft. If such an agreement is not in
 * place, you may not use the software.
 */

package org.mule.modules.splunk;

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
