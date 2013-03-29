package org.woodship.luna.spring;

import org.woodship.luna.LunaUI;

import com.vaadin.server.UIClassSelectionEvent;
import com.vaadin.server.UICreateEvent;
import com.vaadin.server.UIProvider;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;

@SuppressWarnings("serial")
public class SpringUIProvider extends UIProvider {
	
	protected static final String BEAN_NAME_PARAMETER = "UIBean";
	
	@Override
	public UI createInstance(UICreateEvent event) {
		return (UI) SpringApplicationContext.getApplicationContext().getBean(getUIBeanName(event.getRequest()));
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Class<? extends UI> getUIClass(UIClassSelectionEvent event) {
		if (event.getRequest().getParameter("mobile") != null && 
				event.getRequest().getParameter("mobile").equals("false")) {
			return (Class<? extends UI>) SpringApplicationContext.getApplicationContext().getType(getUIBeanName(event.getRequest()));
        }

        if (event.getRequest().getHeader("user-agent").toLowerCase().contains("mobile") && 
        		!event.getRequest().getHeader("user-agent").toLowerCase().contains("ipad")) {
            return MobileCheckUI.class;
        }

		return  LunaUI.class;
	}
	
	@Override
	public boolean isPreservedOnRefresh(UICreateEvent event) {
		if (!SpringApplicationContext.getApplicationContext().isPrototype(getUIBeanName(event.getRequest()))) {
			return true;
		}
		
		return super.isPreservedOnRefresh(event);
	}
	
	protected String getUIBeanName(VaadinRequest request) {
		String vaadinBeanName = "ui";
		
		Object uiBeanName = request.getService().getDeploymentConfiguration().getApplicationOrSystemProperty(BEAN_NAME_PARAMETER, null);
		if (uiBeanName != null && uiBeanName instanceof String) {
			vaadinBeanName = uiBeanName.toString();
		}
		return vaadinBeanName;
	}
}
