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

import static org.junit.Assert.assertNotNull;

/**
 * Test {@link org.mule.modules.splunk.OutputMode} internals
 * <p/>
 */
public class OutputModeTest {
    @Test
    public void testOutputMode() {
        assertNotNull(OutputMode.JSON);
        assertNotNull(OutputMode.XML);
    }
}
