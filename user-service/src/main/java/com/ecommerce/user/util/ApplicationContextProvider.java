package com.ecommerce.user.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class ApplicationContextProvider implements ApplicationContextAware {

	private static ApplicationContext context;

	public static <T> T getBean(Class<T> requiredType) {
		return context.getBean(requiredType);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		context = applicationContext;
	}
}
