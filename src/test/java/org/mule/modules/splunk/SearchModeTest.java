/**
 *
 * (c) 2016 WhiteSky Labs, Pty Ltd. This software is protected under international
 * copyright law. All use of this software is subject to WhiteSky Labs' Master
 * Subscription Agreement (or other Terms of Service) separately entered
 * into between you and WhiteSky Labs. If such an agreement is not in
 * place, you may not use the software.
 */
package org.mule.modules.splunk;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

/**
 * Test {@link org.mule.modules.splunk.SearchModeTest} internals
 * <p/>
 */
public class SearchModeTest {

    @Test
    public void testSearchMode() {
        assertNotNull(SearchMode.NORMAL);
        assertNotNull(SearchMode.REALTIME);
    }
}
