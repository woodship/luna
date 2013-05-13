package org.woodship.luna;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.woodship.luna.base.AndroidRequestHandler;

import ru.xpoft.vaadin.SpringApplicationContext;
import ru.xpoft.vaadin.SpringUIProvider;
import ru.xpoft.vaadin.SpringVaadinSystemMessagesProvider;

import com.vaadin.server.Constants;
import com.vaadin.server.DeploymentConfiguration;
import com.vaadin.server.RequestHandler;
import com.vaadin.server.ServiceException;
import com.vaadin.server.SessionInitEvent;
import com.vaadin.server.SessionInitListener;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinServletService;

/**
 * @author xpoft
 */
public class SpringVaadinServlet extends VaadinServlet
{
    private static Logger logger = LoggerFactory.getLogger(VaadinServlet.class);
    /**
     * Servlet parameter name for system message bean
     */
    private static final String SYSTEM_MESSAGES_BEAN_NAME_PARAMETER = "systemMessagesBeanName";
    /**
     * Spring Application Context
     */
    private transient ApplicationContext applicationContext;
    /**
     * system message bean name
     */
    private String systemMessagesBeanName = "";

    @Override
    public void init(ServletConfig config) throws ServletException
    {
        applicationContext = WebApplicationContextUtils.getWebApplicationContext(config.getServletContext());

        if (config.getInitParameter(SYSTEM_MESSAGES_BEAN_NAME_PARAMETER) != null)
        {
            systemMessagesBeanName = config.getInitParameter(SYSTEM_MESSAGES_BEAN_NAME_PARAMETER);
            logger.debug("found SYSTEM_MESSAGES_BEAN_NAME_PARAMETER: {}", systemMessagesBeanName);
        }

        if (SpringApplicationContext.getApplicationContext() == null)
        {
            SpringApplicationContext.setApplicationContext(applicationContext);
        }

        super.init(config);
    }

    @Override
    protected VaadinServletService createServletService(DeploymentConfiguration deploymentConfiguration)
    {
        final VaadinServletService service = super.createServletService(deploymentConfiguration);

        // Spring system messages provider
        if (systemMessagesBeanName != null && systemMessagesBeanName != "")
        {
            SpringVaadinSystemMessagesProvider messagesProvider = new SpringVaadinSystemMessagesProvider(applicationContext, systemMessagesBeanName);
            logger.debug("set SpringVaadinSystemMessagesProvider");
            service.setSystemMessagesProvider(messagesProvider);
        }

        String uiProviderProperty = service.getDeploymentConfiguration().getApplicationOrSystemProperty(Constants.SERVLET_PARAMETER_UI_PROVIDER, null);

        // Add SpringUIProvider if custom provider doesn't defined.
        if (uiProviderProperty == null)
        {
            service.addSessionInitListener(new SessionInitListener()
            {
                @Override
                public void sessionInit(SessionInitEvent event) throws ServiceException
                {
                    event.getSession().addUIProvider(new SpringUIProvider());
                }
            });
        }

        //add by laocui
        service.addSessionInitListener(new SessionInitListener()
        {
            @Override
            public void sessionInit(SessionInitEvent event) throws ServiceException
            {
            	//处理其它客户端请求
            	RequestHandler rh = (RequestHandler)applicationContext.getBean("androidRequestHandler");
            	event.getSession().addRequestHandler(rh);
            }
        });
        return service;
    }
}
