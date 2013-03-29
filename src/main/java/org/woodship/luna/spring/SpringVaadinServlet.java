package org.woodship.luna.spring;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.icepush.servlet.MainServlet;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.vaadin.artur.icepush.ICEPush;
import org.vaadin.artur.icepush.JavascriptProvider;

import com.vaadin.server.Constants;
import com.vaadin.server.DeploymentConfiguration;
import com.vaadin.server.ServiceException;
import com.vaadin.server.SessionInitEvent;
import com.vaadin.server.SessionInitListener;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinServletService;

@SuppressWarnings("serial")
public class SpringVaadinServlet extends VaadinServlet {

	private static final String SYSTEM_MESSAGES_BEAN_NAME_PARAMETER = "systemMessagesBeanName";

	private transient ApplicationContext applicationContext;

	private String systemMessagesBeanName = "";

	private MainServlet ICEPushServlet;

	private JavascriptProvider javascriptProvider;

	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
		applicationContext = WebApplicationContextUtils
				.getWebApplicationContext(servletConfig.getServletContext());

		if (servletConfig.getInitParameter(SYSTEM_MESSAGES_BEAN_NAME_PARAMETER) != null) {
			systemMessagesBeanName = servletConfig
					.getInitParameter(SYSTEM_MESSAGES_BEAN_NAME_PARAMETER);
		}
		
		if (SpringApplicationContext.getApplicationContext() == null) {
			SpringApplicationContext.setApplicationContext(applicationContext);
		}

		try {
			super.init(servletConfig);
		} catch (ServletException e) {
			if ("Application not specified in servlet parameters".equals(e
					.getMessage())) {
				throw e;
			}
		}

		ICEPushServlet = new MainServlet(servletConfig.getServletContext());
		try {
			javascriptProvider = new JavascriptProvider(getServletContext()
					.getContextPath());
			ICEPush.setCodeJavascriptLocation(javascriptProvider
					.getCodeLocation());
		} catch (IOException e) {
			throw new ServletException("Error initializing JavascriptProvider",
					e);
		}
	}

	@Override
	protected VaadinServletService createServletService(
			DeploymentConfiguration deploymentConfiguration) {
		final VaadinServletService service = super
				.createServletService(deploymentConfiguration);

		if (systemMessagesBeanName != null
				&& !"".equals(systemMessagesBeanName)) {
			service.setSystemMessagesProvider(new SpringVaadinSystemMessagesProvider(applicationContext, systemMessagesBeanName));
		}

		String uiProviderProperty = service.getDeploymentConfiguration()
				.getApplicationOrSystemProperty(
						Constants.SERVLET_PARAMETER_UI_PROVIDER, null);

		if (uiProviderProperty == null) {
			service.addSessionInitListener(new SessionInitListener() {

				@Override
				public void sessionInit(SessionInitEvent event)
						throws ServiceException {
					event.getSession().addUIProvider(new SpringUIProvider());
				}
			});
		}

		return service;
	}

	@Override
	protected void service(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String pathInfo = request.getPathInfo();
		if (pathInfo != null
				&& pathInfo.equals("/" + javascriptProvider.getCodeName())) {
			serveIcePushCode(request, response);
			return;
		}
		if (request.getRequestURI().endsWith(".icepush")) {
			try {
				ICEPushServlet.service(request, response);
			} catch (ServletException e) {
				throw e;
			} catch (IOException e) {
				throw e;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} else {
			super.service(request, response);
		}
	}

	@Override
	public void destroy() {
		super.destroy();
		ICEPushServlet.shutdown();
	}

	private void serveIcePushCode(HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		String icepushJavscript = javascriptProvider.getJavaScript();

		response.setHeader("Content-Type", "text/javascript");
		response.getOutputStream().write(icepushJavscript.getBytes());
	}
}
