/**
 *
 * (c) 2003-2012 MuleSoft, Inc. This software is protected under international
 * copyright law. All use of this software is subject to MuleSoft's Master
 * Subscription Agreement (or other Terms of Service) separately entered
 * into between you and MuleSoft. If such an agreement is not in
 * place, you may not use the software.
 */

/**
 * (c) 2003-2014 MuleSoft, Inc. The software in this package is published under the terms of the CPAL v1.0 license,
 * a copy of which has been included with this distribution in the LICENSE.md file.
 **/
package org.mule.modules.splunk.testrunners;


import org.junit.experimental.categories.Categories;
import org.junit.experimental.categories.Categories.IncludeCategory;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;
import org.mule.modules.splunk.SmokeTests;
import org.mule.modules.splunk.testcases.ApplicationsTestCase;
import org.mule.modules.splunk.testcases.DataModelTestCase;
import org.mule.modules.splunk.testcases.JobsTestCase;
import org.mule.modules.splunk.testcases.SavedSearchesTestCase;

@RunWith(Categories.class)
@IncludeCategory(SmokeTests.class)
@SuiteClasses({
        DataModelTestCase.class,
        JobsTestCase.class,
        ApplicationsTestCase.class,
        SavedSearchesTestCase.class

})
public class SmokeTestSuite {

}
