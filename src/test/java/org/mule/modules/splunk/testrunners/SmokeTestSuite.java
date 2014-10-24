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
import org.mule.modules.splunk.testcases.DataModelTestCase;

@RunWith(Categories.class)
@IncludeCategory(SmokeTests.class)
@SuiteClasses({
        DataModelTestCase.class
})
public class SmokeTestSuite {

}
