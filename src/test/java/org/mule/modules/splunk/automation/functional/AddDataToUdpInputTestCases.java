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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mule.modules.splunk.exception.SplunkConnectorException;

import com.splunk.InputKind;

public class AddDataToUdpInputTestCases extends SplunkAbstractTestCase {

	@Before
	public void setup() throws SplunkConnectorException {
		getConnector().createInput("9988", InputKind.Udp, null);
	}

	@After
	public void tearDown() {
		getConnector().removeInput("9988");
	}

	@Test
	public void testAddDataToUdpInput() {
		boolean result = getConnector().addDataToUdpInput("9988",
				"addDataToUdpInputTestDataString");
		assertTrue(result);
	}
}