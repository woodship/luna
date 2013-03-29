package org.woodship.luna.spring;

import java.io.Serializable;
import java.util.Locale;

import javax.annotation.Resource;

import org.springframework.context.MessageSource;

import com.vaadin.server.VaadinSession;

@SuppressWarnings("serial")
public class VaadinMessageSource implements Serializable {
	
	@Resource private transient MessageSource messageSource;
	
	public String getMessage(String code) {
		Locale locale = VaadinSession.getCurrent().getLocale();
		return messageSource.getMessage(code, null, locale);
	}
	
	public String getMessage(String code, String defaultMessage) {
		Locale locale = VaadinSession.getCurrent().getLocale();
		return messageSource.getMessage(code, null, defaultMessage, locale);
	}
	
	public String getMessage(String code, Object[] args) {
		Locale locale = VaadinSession.getCurrent().getLocale();
		return messageSource.getMessage(code, args, locale);
	}
}
