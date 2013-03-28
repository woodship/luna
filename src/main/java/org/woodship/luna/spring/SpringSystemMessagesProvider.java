package org.woodship.luna.spring;

import java.io.Serializable;
import java.util.Locale;

import com.vaadin.server.SystemMessages;

public interface SpringSystemMessagesProvider extends Serializable {

	public SystemMessages getSystemMessages(Locale locale);
}
