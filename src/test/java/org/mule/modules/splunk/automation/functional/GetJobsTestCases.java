/**
 * (c) 2018 WhiteSky Labs, Pty Ltd. The software in this package is published under the terms of the CPAL v1.0 license,
 * a copy of which has been included with this distribution in the LICENSE.md file.
 */
package org.mule.modules.splunk.automation.functional;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class GetJobsTestCases extends SplunkAbstractTestCases {

    private static String SAVED_SEARCH_NAME = "saved_search_demo"; 
            
    @Before
    public void setup() {        
        try {
            getConnector().createSavedSearch(SAVED_SEARCH_NAME, "search * | head 10", null);
            getConnector().runSavedSearch(SAVED_SEARCH_NAME);
        } catch (Exception e) {
        }
    }
    
    @After
    public void tearDown() {
        try {
            getConnector().deleteSavedSearch(SAVED_SEARCH_NAME);    
        } catch(Exception e) {   
        }
    }
    
    @Test
    public void testGetJobs() {
        List<Map<String, Object>> result = getConnector().getJobs();
        assertNotNull(result);
        assertTrue(result.size() > 0);
    }
}
