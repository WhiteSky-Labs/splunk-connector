/**
 *
 * (c) 2016 WhiteSky Labs, Pty Ltd. This software is protected under international
 * copyright law. All use of this software is subject to WhiteSky Labs' Master
 * Subscription Agreement (or other Terms of Service) separately entered
 * into between you and WhiteSky Labs. If such an agreement is not in
 * place, you may not use the software.
 */
package org.mule.modules.splunk.automation.runner;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.mule.modules.splunk.SplunkConnector;
import org.mule.modules.splunk.automation.functional.AddDataToIndexTestCases;
import org.mule.modules.splunk.automation.functional.AddDataToTcpInputTestCases;
import org.mule.modules.splunk.automation.functional.AddDataToUdpInputTestCases;
import org.mule.modules.splunk.automation.functional.CleanIndexTestCases;
import org.mule.modules.splunk.automation.functional.CreateIndexTestCases;
import org.mule.modules.splunk.automation.functional.CreateInputTestCases;
import org.mule.modules.splunk.automation.functional.CreateSavedSearchTestCases;
import org.mule.modules.splunk.automation.functional.DeleteSavedSearchTestCases;
import org.mule.modules.splunk.automation.functional.GetApplicationsTestCases;
import org.mule.modules.splunk.automation.functional.GetDataModelTestCases;
import org.mule.modules.splunk.automation.functional.GetDataModelsTestCases;
import org.mule.modules.splunk.automation.functional.GetIndexTestCases;
import org.mule.modules.splunk.automation.functional.GetIndexesTestCases;
import org.mule.modules.splunk.automation.functional.GetInputTestCases;
import org.mule.modules.splunk.automation.functional.GetInputsTestCases;
import org.mule.modules.splunk.automation.functional.GetJobsTestCases;
import org.mule.modules.splunk.automation.functional.GetSavedSearchHistoryTestCases;
import org.mule.modules.splunk.automation.functional.GetSavedSearchesTestCases;
import org.mule.modules.splunk.automation.functional.ModifyIndexTestCases;
import org.mule.modules.splunk.automation.functional.ModifyInputTestCases;
import org.mule.modules.splunk.automation.functional.ModifySavedSearchPropertiesTestCases;
import org.mule.modules.splunk.automation.functional.RemoveIndexTestCases;
import org.mule.modules.splunk.automation.functional.RemoveInputTestCases;
import org.mule.modules.splunk.automation.functional.RunBlockingSearchTestCases;
import org.mule.modules.splunk.automation.functional.RunExportSearchTestCases;
import org.mule.modules.splunk.automation.functional.RunNormalSearchTestCases;
import org.mule.modules.splunk.automation.functional.RunOneShotSearchTestCases;
import org.mule.modules.splunk.automation.functional.RunSavedSearchTestCases;
import org.mule.modules.splunk.automation.functional.RunSavedSearchWithArgumentsTestCases;
import org.mule.modules.splunk.automation.functional.ViewSavedSearchPropertiesTestCases;
import org.mule.tools.devkit.ctf.mockup.ConnectorTestContext;

@RunWith(Suite.class)
@org.junit.runners.Suite.SuiteClasses({
        AddDataToIndexTestCases.class,
        AddDataToTcpInputTestCases.class,
        AddDataToUdpInputTestCases.class,
        CleanIndexTestCases.class,
        CreateIndexTestCases.class,
        CreateInputTestCases.class,
        CreateSavedSearchTestCases.class,
        DeleteSavedSearchTestCases.class,
        GetApplicationsTestCases.class,
        GetDataModelsTestCases.class,
        GetDataModelTestCases.class,
        GetIndexesTestCases.class,
        GetIndexTestCases.class,
        GetInputsTestCases.class,
        GetInputTestCases.class,
        GetJobsTestCases.class,
        GetSavedSearchesTestCases.class,
        GetSavedSearchHistoryTestCases.class,
        ModifyIndexTestCases.class,
        ModifyInputTestCases.class,
        ModifySavedSearchPropertiesTestCases.class,
        RemoveIndexTestCases.class,
        RemoveInputTestCases.class,
        RunBlockingSearchTestCases.class,
        RunOneShotSearchTestCases.class,
        RunExportSearchTestCases.class,
        RunNormalSearchTestCases.class,
        RunOneShotSearchTestCases.class,
        RunSavedSearchTestCases.class,
        RunSavedSearchWithArgumentsTestCases.class,
        ViewSavedSearchPropertiesTestCases.class
})
public class FunctionalTestSuite {

    @BeforeClass
    public static void initialiseSuite() {
        ConnectorTestContext.initialize(SplunkConnector.class);
    }

    @AfterClass
    public static void shutdownSuite() throws Exception {
        ConnectorTestContext.shutDown();
    }
}
