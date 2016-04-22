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
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.splunk.CollectionArgs;
import com.splunk.HttpException;

public class GetIndexesTestCases extends SplunkAbstractTestCase {

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
            Map<String, Object> args = new HashMap<>();
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
