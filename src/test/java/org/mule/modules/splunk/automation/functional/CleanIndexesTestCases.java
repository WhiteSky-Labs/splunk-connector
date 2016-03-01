/**
 *
 * (c) 2015 WhiteSky Labs, Pty Ltd. This software is protected under international
 * copyright law. All use of this software is subject to WhiteSky Labs' Master
 * Subscription Agreement (or other Terms of Service) separately entered
 * into between you and WhiteSky Labs. If such an agreement is not in
 * place, you may not use the software.
 */

package org.mule.modules.splunk.automation.functional;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CleanIndexesTestCases extends SplunkAbstractTestCase {

	private static final String INDEX_NAME = "clean_index_test_index";

	@Before
	public void setup() {
		getConnector().createIndex(INDEX_NAME, null);
	}

	@After
	public void tearDown() {
		getConnector().removeIndex(INDEX_NAME);
	}

	@Test
	public void testCleanIndex() {
		Map<String, Object> index = getConnector().cleanIndex(INDEX_NAME, 180);
		assertTrue(((String) index.get("homePath")).contains(INDEX_NAME));
	}

	@Test
	public void testCleanInvalidIndex() {
		try {
			getConnector().cleanIndex("Not a real index", 180);
			fail("Error should be thrown cleaning an invalid index");
		} catch (NullPointerException e) {
			assertTrue(e instanceof NullPointerException);
		} catch (Exception e) {
			fail("Exception not expected: " + e.getMessage());
		}
	}
}
