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
package org.mule.modules.splunk.testcases;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.Timeout;
import org.mule.api.ConnectionException;
import org.mule.modules.tests.ConnectorTestCase;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SplunkTestParent extends ConnectorTestCase {

    private static final String[] SPRING_CONFIG_FILES = new String[]{"AutomationSpringBeans.xml"};

    private static ApplicationContext context;

    @Override
    protected String getConfigResources() {
        return "automation-test-flows.xml";
    }

    Map<String, Object> testObjects;

    @Rule
    public Timeout globalTimeout = new Timeout(300000);


    @BeforeClass
    public static void beforeClass() {
        context = new ClassPathXmlApplicationContext(SPRING_CONFIG_FILES);
    }

    @Before
    public void setUp() throws IOException, ConnectionException {
        setObjectMapper(new ObjectMapper());
        testObjects = new HashMap<String, Object>();
        context = new ClassPathXmlApplicationContext(SPRING_CONFIG_FILES);
        String getCurrentDirectory = System.getProperty("user.dir");
    }


    @After
    public void tearDown() throws Exception {
        setObjectMapper(null);
        testObjects = null;
        context = null;
    }

    private ObjectMapper objectMapper;

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

}
