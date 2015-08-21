package com.alliancefoundry.servlet.utils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.LoggerContext;

/**
 * The registration of an JMXConfigurator instance creates a reference from 
 * the system class loader into your application which will prevent it from being 
 * garbage collected when it is stopped or re-deployed, resulting in a severe 
 * memory leak. This context listen will clear that issue.
 *  
 * @author Paul Bernard
 *
 */
public class LoggingContextListener implements ServletContextListener {

	public void contextDestroyed(ServletContextEvent sce) {
	    LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory(); 
	    lc.stop(); // lc.reset();
	    
	  }

	  public void contextInitialized(ServletContextEvent sce) {
	  }
	
}
