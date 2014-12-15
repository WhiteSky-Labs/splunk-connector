/**
 *
 * (c) 2003-2015 MuleSoft, Inc. This software is protected under international
 * copyright law. All use of this software is subject to MuleSoft's Master
 * Subscription Agreement (or other Terms of Service) separately entered
 * into between you and MuleSoft. If such an agreement is not in
 * place, you may not use the software.
 */


package com.wsl.modules.splunk.automation.testrunners;

import com.wsl.modules.splunk.automation.SmokeTests;
import com.wsl.modules.splunk.automation.testcases.*;
import org.junit.runner.RunWith;

@RunWith(org.junit.experimental.categories.Categories.class)
@org.junit.experimental.categories.Categories.IncludeCategory(SmokeTests.class)
@org.junit.runners.Suite.SuiteClasses({
        AddDataToIndexTestCases.class,
        AddDataToTcpInputTestCases.class,
        AddDataToUdpInputTestCases.class,
        CleanIndexesTestCases.class,
        CreateIndexTestCases.class,
        CreateInputsTestCases.class,
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
        RunBlockingSearchTestCases.class,
        RunExportSearchTestCases.class,
        RunNormalSearchTestCases.class,
        RunOneShotSearchTestCases.class,
        RunRealTimeSearchTestCases.class,
        RunSavedSearchTestCases.class,
        RunSavedSearchWithArgumentsTestCases.class,
        ViewSavedSearchPropertiesTestCases.class
})
public class SmokeTestSuite {


}
