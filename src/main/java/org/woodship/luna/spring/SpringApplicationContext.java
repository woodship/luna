package org.woodship.luna.spring;

import java.io.Serializable;

import org.springframework.context.ApplicationContext;
import org.woodship.luna.db.ContainerUtils;

@SuppressWarnings("serial")
public class SpringApplicationContext implements Serializable {

	private static transient ApplicationContext applicationContext;

	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public static void setApplicationContext(ApplicationContext applicationContext) {
		SpringApplicationContext.applicationContext = applicationContext;
	}
	
	public static ContainerUtils getContainerUtils(){
		return applicationContext.getBean(ContainerUtils.class);
	}
}
