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
        GetApplicationsTestCases.class,
        GetJobsTestCases.class,
        RunBlockingSearchTestCases.class,
        RunOneShotSearchTestCases.class,
        GetSavedSearchesTestCases.class,
        CreateSavedSearchTestCases.class,
        ViewSavedSearchPropertiesTestCases.class,
        ModifySavedSearchPropertiesTestCases.class,
        GetSavedSearchHistoryTestCases.class,
        RunSavedSearchTestCases.class,
        RunSavedSearchWithArgumentsTestCases.class,
        DeleteSavedSearchTestCases.class,
        GetDataModelTestCases.class,
        RunExportSearchTestCases.class,
        RunRealTimeSearchTestCases.class
})
public class RegressionTestSuite {


}
