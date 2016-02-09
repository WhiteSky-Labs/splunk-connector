package org.mule.modules.splunk.automation.functional;

import org.mule.tools.devkit.ctf.junit.AbstractTestCase;

import com.wsl.modules.splunk.SplunkConnector;

public class SplunkAbstractTestCase extends AbstractTestCase<SplunkConnector>{
	
	public SplunkAbstractTestCase() {
		super(SplunkConnector.class);
	}
	
	
}
