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

import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mule.modules.splunk.exception.SplunkConnectorException;

//import org.mule.modules.tests.ConnectorTestUtils;

public class RunSavedSearchTestCases extends SplunkAbstractTestCase {

	private static final String SEARCH_NAME = "run_saved_search_test_search";

	@Before
	public void setup() throws Exception {
		getConnector().createSavedSearch(SEARCH_NAME, "search * | head 100",
				null);
	}

	@After
	public void tearDown() throws Exception {
		getConnector().deleteSavedSearch(SEARCH_NAME);
	}

	@Test
	public void testRunSavedSearch() {
		try {
			List<Map<String, Object>> result = getConnector().runSavedSearch(
					SEARCH_NAME);
			assertNotNull(result);
		} catch (Exception e) {
			fail("Exception not expected: " + e.getMessage());
		}
	}

	@Test
	public void testRunMissingSavedSearch() {
		try {
			getConnector().runSavedSearch("Not a valid search name");
			fail("Running a saved search that doesn't exist should throw an error");
		} catch (Exception e) {
			assertTrue(e instanceof SplunkConnectorException);
		}
	}
}
