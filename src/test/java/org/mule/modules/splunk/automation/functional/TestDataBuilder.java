package org.mule.modules.splunk.automation.functional;

import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestDataBuilder {

	private static ApplicationContext context;

	public TestDataBuilder() {
		TestDataBuilder.context = new ClassPathXmlApplicationContext(
				"automationSpringBeans.xml");
	}

	@SuppressWarnings("unchecked")
	public Map<String, String> addDataToIndexTestData() {
		return (Map<String, String>) getBean("addDataToIndexTestData");
	}

	public void shutDownDataBuilder() {
		((ConfigurableApplicationContext) context).close();
	}

	private Object getBean(String name) {
		return context.getBean(name);
	}
	
}