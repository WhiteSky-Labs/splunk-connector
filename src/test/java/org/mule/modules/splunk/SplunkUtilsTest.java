package org.mule.modules.splunk;


import com.splunk.JobArgs;
import com.splunk.SavedSearch;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mule.modules.splunk.exception.SplunkConnectorException;
import org.mule.modules.splunk.util.SplunkUtils;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Test {@link org.mule.modules.splunk.util.SplunkUtils} internals
 * <p/>
 */
public class SplunkUtilsTest {

    @Mock
    SavedSearch savedSearch;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSetSearchProperties() throws Exception {
        //create the test data
        Map<String, Object> searchProperties = new HashMap<String, Object>();
        searchProperties.put("description", "Testing Strings");
        searchProperties.put("is_scheduled", "true"); // testing strings as booleans
        searchProperties.put("action.rss.track_alert", true); // testing actual booleans
        searchProperties.put("action.rss.maxresults", "15"); // testing strings as integers
        searchProperties.put("dispatch.max_count", 60); //testing actual integers
        SavedSearch result = SplunkUtils.setSearchProperties(searchProperties, savedSearch);
        assertNotNull(result);
    }


    @Test
    public void testSetJobProperties() throws Exception {
        JobArgs args = new JobArgs();
        Map<String, Object> jobProperties = new HashMap<String, Object>();
        jobProperties.put("namespace", "test"); // testing strings
        jobProperties.put("enable_lookups", "true"); //testing string as boolean
        jobProperties.put("force_bundle_replication", false); //testing boolean
        jobProperties.put("auto_pause", "50"); // testing string as integer
        jobProperties.put("auto_cancel", 15);// testing integer
        args = SplunkUtils.setJobArgs(jobProperties);
        assertEquals(jobProperties.get("namespace"), args.get("namespace"));
        assertEquals(Boolean.parseBoolean((String) jobProperties.get("enable_lookups")), args.get("enable_lookups"));
        assertEquals(jobProperties.get("force_bundle_replication"), args.get("force_bundle_replication"));
        assertEquals(Integer.parseInt((String) jobProperties.get("auto_pause")), args.get("auto_pause"));
        assertEquals(jobProperties.get("auto_cancel"), args.get("auto_cancel"));
    }

    @Test
    public void testSetInvalidSearchProperty() throws Exception {
        Map<String, Object> searchProperties = new HashMap<String, Object>();
        searchProperties.put("invalid", "invalid");
        try {
            SavedSearch result = SplunkUtils.setSearchProperties(searchProperties, savedSearch);
            fail("Exception not thrown");
        } catch (SplunkConnectorException e) {
            assertEquals("No matching method, check your properties are correct", e.getMessage());
        }
    }

    @Test
    public void testSetInvalidJobProperty() throws Exception {
        JobArgs args = new JobArgs();
        Map<String, Object> jobProperties = new HashMap<String, Object>();
        jobProperties.put("invalid", "invalid"); // testing strings
        try {
            args = SplunkUtils.setJobArgs(jobProperties);
            fail("Exception not thrown");
        } catch (SplunkConnectorException e) {
            assertEquals("No matching method, check your properties are correct", e.getMessage());
        }
    }


}
