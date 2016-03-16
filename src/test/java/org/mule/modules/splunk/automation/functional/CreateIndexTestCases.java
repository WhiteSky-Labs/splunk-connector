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

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Test;
import org.mule.modules.splunk.exception.SplunkConnectorException;

import com.splunk.HttpException;

public class CreateIndexTestCases extends SplunkAbstractTestCase {

    private static final String INDEX_NAME = "create_index_test_index";
    private boolean doTearDown = false;

    @After
    public void tearDown() {
        if (doTearDown) {
            try {
                getConnector().removeIndex(INDEX_NAME);
                Thread.sleep(5000);
            } catch (Exception e) {
                fail("Cleanup failed with exception: " + e.getMessage());
            }
        }
    }

    @Test
    public void testCreateIndex() {
        try {
            Map<String, Object> result = getConnector().createIndex(INDEX_NAME, null);
            doTearDown = true;
            assertNotNull(result);
            assertTrue(((String) result.get("homePath")).contains(INDEX_NAME));
        } catch (SplunkConnectorException e) {
            fail("Exception not expected: " + e.getMessage());
        }
    }

    @Test
    public void testCreateIndexWithArgs() {
        try {
            Map<String, Object> args = new HashMap<>();
            args.put("maxDataSize", "750");
            Map<String, Object> result = getConnector().createIndex(INDEX_NAME, args);
            doTearDown = true;
            assertNotNull(result);
            assertTrue(((String) result.get("homePath")).contains(INDEX_NAME));
        } catch (SplunkConnectorException e) {
            fail("Exception not expected: " + e.getMessage());
        }
    }

    @Test
    public void testCreateIndexWithInvalidArgs() {
        try {
            Map<String, Object> args = new HashMap<>();
            args.put("Invalid", "true");
            getConnector().createIndex(INDEX_NAME, args);
            fail("Error should be thrown with invalid args");
        } catch (HttpException e) {
            assertTrue(e.getMessage().contains("is not supported by this handler"));
        } catch (Exception e) {
            fail("Exception type not expected: " + e.getMessage());
        }
    }

}