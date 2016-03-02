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

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AddDataToIndexTestCases extends SplunkAbstractTestCase {

	private static final String INDEX_NAME = "add_data_to_index_test_index";

	@Before
	public void setup() {
		getConnector().createIndex(INDEX_NAME, null);
	}

	@After
	public void tearDown() {
		getConnector().removeIndex(INDEX_NAME);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
		}
	}

	@Test
	public void testAddDataToIndex() {
		Map<String, Object> result = getConnector().addDataToIndex(INDEX_NAME,
				"addDataToIndexStringData", null);
		assertNotNull(result);
		assertTrue(((String) result.get("homePath")).contains(INDEX_NAME));
	}

	@Test
	public void testAddDataToIndexWithArgs() {
		Map<String, Object> args = new HashMap<>();
		args.put("maxDataSize", "750");
		Map<String, Object> result = getConnector().addDataToIndex(INDEX_NAME,
				"addDataToIndexStringData", args);
		assertNotNull(result);
		assertTrue(((String) result.get("homePath")).contains(INDEX_NAME));
	}

}