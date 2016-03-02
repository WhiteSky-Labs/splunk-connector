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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Map;

import org.junit.Test;

import com.splunk.InputKind;

public class GetInputTestCases extends SplunkAbstractTestCase {

	@Test
	public void testGetInput() {
		//setup
		getConnector().createInput("9906", InputKind.Tcp, null);
		
		Map<String, Object> result = getConnector().getInput("9906");
		assertNotNull(result);
		assertEquals("default", result.get("index"));
		
		//teardown
		getConnector().removeInput("9906");
	}

	@Test
	public void testGetInputWithInvalidIdentifier() {
		try {
			getConnector().getInput("An Invalid Identifier");
			fail("Exception should be thrown for an invalid input identifier");
		} catch (IllegalArgumentException e) {
			assertEquals("You must provide a valid input identifier",
					e.getMessage());
		} catch (Exception e) {
			fail("Exception type not expected: " + e.getMessage());
		}
	}

}