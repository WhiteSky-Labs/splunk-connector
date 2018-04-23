/**
 * (c) 2018 WhiteSky Labs, Pty Ltd. The software in this package is published under the terms of the CPAL v1.0 license,
 * a copy of which has been included with this distribution in the LICENSE.md file.
 */
package org.mule.modules.splunk.automation.functional;

import org.mule.modules.splunk.SplunkConnector;
import org.mule.tools.devkit.ctf.junit.AbstractTestCase;

public class SplunkAbstractTestCases extends AbstractTestCase<SplunkConnector> {

    public SplunkAbstractTestCases() {
        super(SplunkConnector.class);
    }
}
