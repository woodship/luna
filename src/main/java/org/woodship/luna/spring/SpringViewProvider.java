package org.woodship.luna.spring;

import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;

@SuppressWarnings("serial")
public class SpringViewProvider extends Navigator.ClassBasedViewProvider {
	
	private final String beanName;
	
	private final boolean cached;
	
	private final ViewCacheContainer cacheContainer;
	
	public SpringViewProvider(String viewName, String beanName, Class<? extends View> viewClass, boolean cached, ViewCacheContainer cacheContainer) {
		super(viewName, viewClass);
		this.beanName = beanName;
		this.cached = cached;
		this.cacheContainer = cacheContainer;
	}
	
	public View getView(String viewName) {
		if (getViewName().equals(viewName)) {
			return cacheContainer.getView(viewName, beanName, cached);
		}
		return null;
	}
}
