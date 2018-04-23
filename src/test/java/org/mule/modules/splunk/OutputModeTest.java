/**
 * (c) 2018 WhiteSky Labs, Pty Ltd. The software in this package is published under the terms of the CPAL v1.0 license,
 * a copy of which has been included with this distribution in the LICENSE.md file.
 */
package org.mule.modules.splunk;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

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
