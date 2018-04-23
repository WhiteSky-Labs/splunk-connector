/**
 * (c) 2018 WhiteSky Labs, Pty Ltd. The software in this package is published under the terms of the CPAL v1.0 license,
 * a copy of which has been included with this distribution in the LICENSE.md file.
 */
package org.mule.modules.splunk.automation.functional;

import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.Test;

public class GetDataModelsTestCases extends SplunkAbstractTestCases {

    @Test
    public void testGetDataModels() {
        List<Map<String, Object>> result = getConnector().getDataModels();
        assertTrue(result.size() > 0);
    }
}
