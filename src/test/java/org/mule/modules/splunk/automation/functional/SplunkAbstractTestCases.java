/**
 *
 * (c) 2016 WhiteSky Labs, Pty Ltd. This software is protected under international
 * copyright law. All use of this software is subject to WhiteSky Labs' Master
 * Subscription Agreement (or other Terms of Service) separately entered
 * into between you and WhiteSky Labs. If such an agreement is not in
 * place, you may not use the software.
 */
package org.mule.modules.splunk.automation.functional;

import org.mule.modules.splunk.SplunkConnector;
import org.mule.tools.devkit.ctf.junit.AbstractTestCase;

public class SplunkAbstractTestCases extends AbstractTestCase<SplunkConnector> {

    public SplunkAbstractTestCases() {
        super(SplunkConnector.class);
    }
}
