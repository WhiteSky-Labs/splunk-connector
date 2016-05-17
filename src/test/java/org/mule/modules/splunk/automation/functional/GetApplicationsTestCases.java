/**
 *
 * (c) 2016 WhiteSky Labs, Pty Ltd. This software is protected under international
 * copyright law. All use of this software is subject to WhiteSky Labs' Master
 * Subscription Agreement (or other Terms of Service) separately entered
 * into between you and WhiteSky Labs. If such an agreement is not in
 * place, you may not use the software.
 */
package org.mule.modules.splunk.automation.functional;

import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.Test;

public class GetApplicationsTestCases extends SplunkAbstractTestCases {

    @Test
    public void testGetApplications() {
        List<Map<String, Object>> result = getConnector().getApplications();
        assertTrue(result.size() > 0);
    }

}
