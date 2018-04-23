/**
 * (c) 2018 WhiteSky Labs, Pty Ltd. The software in this package is published under the terms of the CPAL v1.0 license,
 * a copy of which has been included with this distribution in the LICENSE.md file.
 */
package org.mule.modules.splunk.automation.functional;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.splunk.CollectionArgs;
import com.splunk.HttpException;

public class GetIndexesTestCases extends SplunkAbstractTestCases {

    @Test
    public void testGetIndexes() {
        List<Map<String, Object>> result = getConnector().getIndexes(null,
                null, null);
        assertNotNull(result);
        assertTrue(result.size() > 0);
    }

    @Test
    public void testGetIndexesWithParametersTestData() {
        List<Map<String, Object>> result = getConnector().getIndexes(
                "totalEventCount", CollectionArgs.SortDirection.DESC, null);
        assertNotNull(result);
        assertTrue(result.size() > 0);
    }

    @Test
    public void testGetIndexesWithInvalidParametersTestData() {
        try {
            Map<String, Object> args = new HashMap<String, Object>();
            args.put("invalidparameter", "true");
            getConnector().getIndexes("totalEventCount",
                    CollectionArgs.SortDirection.DESC, args);
            fail("Error should be thrown for invalid parameter");
        } catch (HttpException e) {
            assertTrue(e.getMessage()
                    .contains(
                            "is not supported by this handler"));
        } catch (Exception e) {
            fail("Exception type not expected: " + e.getMessage());
        }
    }

}
