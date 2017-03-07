/**
 * 
 */
package com.xk.msa.framework.registry;

import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * @author yanhaixun 2017年1月8日
 */
@Component
public class WebListener implements ServletContextListener {
	@Value("${server.address}")
	private String serverAddress;
	@Value("${server.port}")
	private Integer serverPort;
	@Autowired
	private ServiceRegistry serviceRegistry;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.
	 * ServletContextEvent)
	 */
	public void contextDestroyed(ServletContextEvent event) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.ServletContextListener#contextInitialized(javax.servlet.
	 * ServletContextEvent)
	 */
	public void contextInitialized(ServletContextEvent event) {
//		System.err.println("WebListener....");
		ServletContext servletContext = event.getServletContext();
		ApplicationContext applicationContext = WebApplicationContextUtils
				.getRequiredWebApplicationContext(servletContext);
		RequestMappingHandlerMapping mapping = applicationContext.getBean(RequestMappingHandlerMapping.class);
		Map<RequestMappingInfo, HandlerMethod> infoMap = mapping.getHandlerMethods();
		for (RequestMappingInfo info : infoMap.keySet()) {
			String serviceName = info.getName();
			if (serviceName != null) {
				serviceRegistry.register(serviceName, String.format("%s:%d", serverAddress, serverPort));
			}
		}

	}

}
