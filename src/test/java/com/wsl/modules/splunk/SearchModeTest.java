/**
 *
 * (c) 2003-2015 MuleSoft, Inc. This software is protected under international
 * copyright law. All use of this software is subject to MuleSoft's Master
 * Subscription Agreement (or other Terms of Service) separately entered
 * into between you and MuleSoft. If such an agreement is not in
 * place, you may not use the software.
 */

package com.wsl.modules.splunk;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * Test {@link com.wsl.modules.splunk.SearchModeTest} internals
 * <p/>
 */
public class SearchModeTest {
    @Test
    public void testSearchMode() {
        assertNotNull(SearchMode.NORMAL);
        assertNotNull(SearchMode.REALTIME);
    }
}
