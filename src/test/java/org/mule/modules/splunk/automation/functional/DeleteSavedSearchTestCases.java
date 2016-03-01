/**
 *
 * (c) 2015 WhiteSky Labs, Pty Ltd. This software is protected under international
 * copyright law. All use of this software is subject to WhiteSky Labs' Master
 * Subscription Agreement (or other Terms of Service) separately entered
 * into between you and WhiteSky Labs. If such an agreement is not in
 * place, you may not use the software.
 */

package org.mule.modules.splunk.automation.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

public class DeleteSavedSearchTestCases extends SplunkAbstractTestCase {

	@Test
	public void testDeleteSavedSearch() {
		getConnector().createSavedSearch("delete_saved_search_test_search",
				"search * | head 100", null);

		boolean result = getConnector().deleteSavedSearch(
				"delete_saved_search_test_search");
		assertTrue(result);
	}

	@Test
	public void testDeleteSavedSearchWithEmptyName() {
		try {
			getConnector().deleteSavedSearch("");
			fail("Exception should be thrown when using an empty name to delete a Saved Search");
		} catch (NullPointerException e) {
			assertEquals(null, e.getMessage());
		} catch (Exception e) {
			fail("Exception not expected: " + e.getMessage());
		}
	}

	@Test
	public void testDeleteSavedSearchWithNullName() {
		try {
			getConnector().deleteSavedSearch("");
			fail("Exception should be thrown when using an null name to delete a Saved Search");
		} catch (NullPointerException e) {
			assertEquals(null, e.getMessage());
		} catch (Exception e) {
			fail("Exception not expected: " + e.getMessage());
		}
	}

}
