package org.mule.modules.splunk.automation.functional;

import org.mule.modules.splunk.SplunkConnector;
import org.mule.tools.devkit.ctf.junit.AbstractTestCase;

public class SplunkAbstractTestCase extends AbstractTestCase<SplunkConnector> {

    public SplunkAbstractTestCase() {
        super(SplunkConnector.class);
    }
}
