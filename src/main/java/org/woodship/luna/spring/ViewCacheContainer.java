package org.woodship.luna.spring;

import com.vaadin.navigator.View;

public interface ViewCacheContainer {

	public View getView(String name, String beanName, boolean cached);
}
