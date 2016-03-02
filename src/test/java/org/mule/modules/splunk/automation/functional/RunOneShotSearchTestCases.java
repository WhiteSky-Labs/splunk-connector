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
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.splunk.HttpException;

public class RunOneShotSearchTestCases extends SplunkAbstractTestCase {

	@Test
	public void testRunOneShotSearch() {
		try {
			Map<String, String> args = new HashMap<>();
			args.put("description", "Sample Description");
			List<Map<String, Object>> results = getConnector()
					.runOneShotSearch("search * | head 100", "-10d", "now",
							args);
			assertNotNull(results);
			assertTrue(results.size() > 0);
		} catch (Exception e) {
			fail("Exception not expected: " + e.getMessage());
		}
	}

	@Test
	public void testRunOneShotSearchWithInvalidQuery() {
		try {
			getConnector().runOneShotSearch("Invalid search query", "-10d",
					"now", null);
			fail("An invalid search query should throw an exception");
		} catch (HttpException e) {
			assertTrue(e.getMessage().contains("Unknown search command"));
		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception type not expected: " + e.getMessage());
		}
	}

	@Test
	public void testRunOneShotSearchWithInvalidSearchArgs() {
		try {
			Map<String, String> args = new HashMap<>();
			args.put("invalid", "invalid value");
			List<Map<String, Object>> results = getConnector()
					.runOneShotSearch("search * | head 100", "-10d", "now",
							args);
			// invalid search args are ignored for a one-shot search, should
			// return successfully
			assertNotNull(results);
			assertTrue(results.size() > 0);
		} catch (Exception e) {
			fail("Exception not expected: " + e.getMessage());
		}
	}

	@Test
	public void testRunOneShotSearchWithoutSearchArgs() {
		try {
			List<Map<String, Object>> results = getConnector()
					.runOneShotSearch("search * | head 100", "-10d", "now",
							null);
			// missing search args are ignored for a one-shot search, should
			// return successfully
			assertNotNull(results);
			assertTrue(results.size() > 0);
		} catch (Exception e) {
			fail("Exception not expected: " + e.getMessage());
		}
	}

}
