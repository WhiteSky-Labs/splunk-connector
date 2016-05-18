/**
 *
 * (c) 2016 WhiteSky Labs, Pty Ltd. This software is protected under international
 * copyright law. All use of this software is subject to WhiteSky Labs' Master
 * Subscription Agreement (or other Terms of Service) separately entered
 * into between you and WhiteSky Labs. If such an agreement is not in
 * place, you may not use the software.
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
