package org.woodship.luna.spring;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vaadin.navigator.NavigationStateManager;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.SingleComponentContainer;
import com.vaadin.ui.UI;

@SuppressWarnings("serial")
public class DiscoveryNavigator extends Navigator implements ViewCacheContainer {

	protected class ViewCache implements Serializable {
		private final String name;
		private final String beanName;
		private final Class<? extends View> clazz;
		private final boolean cached;

		ViewCache(String name, String beanName, Class<? extends View> clazz,
				boolean cached) {
			this.name = name;
			this.beanName = beanName;
			this.clazz = clazz;
			this.cached = cached;
		}

		public String getName() {
			return name;
		}

		public String getBeanName() {
			return beanName;
		}

		public Class<? extends View> getClazz() {
			return clazz;
		}

		public boolean isCached() {
			return cached;
		}
	}

	protected static final List<ViewCache> views = Collections
			.synchronizedList(new ArrayList<ViewCache>());
	private final Map<String, View> viewScoped = Collections
			.synchronizedMap(new HashMap<String, View>());

	public DiscoveryNavigator(UI ui, ComponentContainer container) {
		super(ui, container);
		initViews();
	}

	public DiscoveryNavigator(UI ui, SingleComponentContainer container) {
		super(ui, container);
		initViews();
	}

	public DiscoveryNavigator(UI ui, ViewDisplay display) {
		super(ui, display);
		initViews();
	}

	public DiscoveryNavigator(UI ui, NavigationStateManager stateManager,
			ViewDisplay display) {
		super(ui, stateManager, display);
		initViews();
	}

	@SuppressWarnings("unchecked")
	protected void initViews() {
		if (views.isEmpty()) {
			String[] beansName = SpringApplicationContext
					.getApplicationContext().getBeanDefinitionNames();
			for (String beanName : beansName) {
				Class<? extends View> beanClass = (Class<? extends View>) SpringApplicationContext
						.getApplicationContext().getType(beanName);

				if (beanClass != null
						&& beanClass.isAnnotationPresent(VaadinView.class)
						&& View.class.isAssignableFrom(beanClass)) {
					VaadinView vaadinView = (VaadinView) beanClass
							.getAnnotation(VaadinView.class);
					String viewName = vaadinView.value();
					boolean viewCached = vaadinView.cached();

					ViewCache viewCache = new ViewCache(viewName, beanName,
							beanClass, viewCached);
					views.add(viewCache);
				}
			}
		}

		addCachedBeans();
	}

	public void addBeanView(String viewName, Class<? extends View> viewClass) {
		addBeanView(viewName, viewClass, false);
	}

	public void addBeanView(String viewName, Class<? extends View> viewClass,
			boolean cached) {
		if (viewName == null || viewClass == null) {
			throw new IllegalArgumentException(
					"view and viewClass must be non-null");
		}

		String[] beanNames = SpringApplicationContext.getApplicationContext()
				.getBeanNamesForType(viewClass);
		if (beanNames.length != 1) {
			throw new IllegalArgumentException(
					"cant't resolve bean name for class :"
							+ viewClass.getName());
		}

		removeView(viewName);
		addBeanView(viewName, beanNames[0], viewClass, cached);
	}

	protected void addCachedBeans() {
		for (ViewCache view : views) {
			addBeanView(view.name, view.beanName, view.clazz, view.cached);
		}
	}

	protected void addBeanView(String viewName, String beanName,
			Class<? extends View> viewClass, boolean cached) {
		addProvider(new SpringViewProvider(viewName, beanName, viewClass,
				cached, this));
	}

	@Override
	public void navigateTo(String navigationState) {
		if (navigationState == null) {
			navigationState = "";
		}

		if (navigationState.startsWith("!")) {
			super.navigateTo(navigationState.substring(1));
		} else {
			super.navigateTo(navigationState);
		}
	}

	@Override
	public View getView(String name, String beanName, boolean cached) {
		if (cached) {
			if (viewScoped.containsKey(name)) {
				return viewScoped.get(name);
			}

			View view = (View) SpringApplicationContext.getApplicationContext()
					.getBean(beanName);
			viewScoped.put(name, view);

			return view;
		}

		return (View) SpringApplicationContext.getApplicationContext().getBean(
				beanName);
	}
}
