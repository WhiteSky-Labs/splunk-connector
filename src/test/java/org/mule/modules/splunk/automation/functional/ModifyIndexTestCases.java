/**
 *
 * (c) 2016 WhiteSky Labs, Pty Ltd. This software is protected under international
 * copyright law. All use of this software is subject to WhiteSky Labs' Master
 * Subscription Agreement (or other Terms of Service) separately entered
 * into between you and WhiteSky Labs. If such an agreement is not in
 * place, you may not use the software.
 */
package org.mule.modules.splunk.automation.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mule.modules.splunk.exception.SplunkConnectorException;

public class ModifyIndexTestCases extends SplunkAbstractTestCase {

    private static final String INDEX_NAME = "modify_index_test_index";
    private Map<String, Object> args;

    @Before
    public void setup() throws SplunkConnectorException {
        args = new HashMap<>();
        getConnector().createIndex(INDEX_NAME, args);
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
    public void testModifyIndex() {
        try {
            args.put("maxDataSize", "750");
            Map<String, Object> result = getConnector().modifyIndex(INDEX_NAME, args);
            assertNotNull(result);
            assertEquals("750", result.get("maxDataSize"));
        } catch (Exception e) {
            fail("Exception not expected: " + e.getMessage());
        }
    }

    @Test
    public void testModifyInputWithInvalidArgs() {
        try {
            args.put("invalid", "not valid");
            getConnector().modifyIndex(INDEX_NAME, args);
            fail("Error should be thrown when using invalid arguments");
        } catch (SplunkConnectorException sce) {
            assertTrue(sce.getMessage()
                    .contains("is not supported by this handler"));
        } catch (Exception e) {
            fail("Exception type not expected: " + e.getMessage());
        }
    }
}
