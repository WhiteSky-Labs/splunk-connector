/**
 *
 * (c) 2015 WhiteSky Labs, Pty Ltd. This software is protected under international
 * copyright law. All use of this software is subject to WhiteSky Labs' Master
 * Subscription Agreement (or other Terms of Service) separately entered
 * into between you and WhiteSky Labs. If such an agreement is not in
 * place, you may not use the software.
 */


package com.wsl.modules.splunk.automation.testrunners;

import com.wsl.modules.splunk.automation.SmokeTests;
import com.wsl.modules.splunk.automation.testcases.*;
import org.junit.runner.RunWith;

@RunWith(org.junit.experimental.categories.Categories.class)
@org.junit.experimental.categories.Categories.IncludeCategory(SmokeTests.class)
@org.junit.runners.Suite.SuiteClasses({
        CreateIndexTestCases.class,
        CreateInputsTestCases.class,
        CreateSavedSearchTestCases.class,
        DeleteSavedSearchTestCases.class,
        GetIndexTestCases.class,
        GetInputTestCases.class,
        GetSavedSearchesTestCases.class,
        RunSavedSearchTestCases.class,
})
public class SmokeTestSuite {


}