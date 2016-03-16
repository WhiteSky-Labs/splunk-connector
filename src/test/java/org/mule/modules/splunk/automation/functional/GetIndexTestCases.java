/**
 *
 * (c) 2015 WhiteSky Labs, Pty Ltd. This software is protected under international
 * copyright law. All use of this software is subject to WhiteSky Labs' Master
 * Subscription Agreement (or other Terms of Service) separately entered
 * into between you and WhiteSky Labs. If such an agreement is not in
 * place, you may not use the software.
 */

package org.mule.modules.splunk.automation.functional;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Map;
//import org.mule.modules.tests.ConnectorTestUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mule.modules.splunk.exception.SplunkConnectorException;

public class GetIndexTestCases extends SplunkAbstractTestCase {

    private static final String INDEX_NAME = "get_index_test_index";

    @Before
    public void setup() throws SplunkConnectorException {
        getConnector().createIndex(INDEX_NAME, null);
    }

    @After
    public void tearDown() {
        getConnector().removeIndex(INDEX_NAME);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
        }
    }

    @Test
    public void testGetIndex() {
        try {
            Map<String, Object> result = getConnector().getIndex(INDEX_NAME);
            assertNotNull(result);
            assertTrue(((String) result.get("homePath")).contains(INDEX_NAME));
        } catch (Exception e) {
            fail("Exception not expected: " + e.getMessage());
        }
    }

    @Test
    public void testGetInvalidIndex() {
        try {
            getConnector().getIndex("Not a real index");
            fail("Should throw exception for invalid index name");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("You must provide a valid index name"));
        } catch (Exception e) {
            fail("Exception type not expected: " + e.getMessage());
        }
    }
}
