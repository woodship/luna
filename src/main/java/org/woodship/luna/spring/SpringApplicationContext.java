package org.woodship.luna.spring;

import java.io.Serializable;

import org.springframework.context.ApplicationContext;

@SuppressWarnings("serial")
public class SpringApplicationContext implements Serializable {

	private static transient ApplicationContext applicationContext;

	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public static void setApplicationContext(ApplicationContext applicationContext) {
		SpringApplicationContext.applicationContext = applicationContext;
	}
}
