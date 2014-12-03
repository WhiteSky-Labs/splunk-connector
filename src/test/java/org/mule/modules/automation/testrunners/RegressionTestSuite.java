/**
 *
 * (c) 2003-2012 MuleSoft, Inc. This software is protected under international
 * copyright law. All use of this software is subject to MuleSoft's Master
 * Subscription Agreement (or other Terms of Service) separately entered
 * into between you and MuleSoft. If such an agreement is not in
 * place, you may not use the software.
 */


package org.mule.modules.automation.testrunners;

import org.junit.runner.RunWith;
import org.mule.modules.automation.RegressionTests;
import org.mule.modules.automation.testcases.*;

@RunWith(org.junit.experimental.categories.Categories.class)
@org.junit.experimental.categories.Categories.IncludeCategory(RegressionTests.class)
@org.junit.runners.Suite.SuiteClasses({
        CreateInputsTestCases.class,
        CreateSavedSearchTestCases.class,
        DeleteSavedSearchTestCases.class,
        GetApplicationsTestCases.class,
        GetDataModelsTestCases.class,
        GetDataModelTestCases.class,
        GetInputsTestCases.class,
        GetJobsTestCases.class,
        GetSavedSearchesTestCases.class,
        GetSavedSearchHistoryTestCases.class,
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
public class RegressionTestSuite {


}
