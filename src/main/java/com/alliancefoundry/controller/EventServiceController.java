package com.alliancefoundry.controller;

import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;

import com.alliancefoundry.publisher.EventServicePublisher;
import com.alliancefoundry.publisher.PublisherException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.ServletConfigAware;
import org.springframework.web.context.ServletContextAware;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliancefoundry.dao.DAOException;
import com.alliancefoundry.model.EventResponse;
import com.alliancefoundry.model.EventsResponse;
import com.alliancefoundry.model.Event;
import com.alliancefoundry.dao.DAOFactory;


/**
 * Created by: Paul Bernard
 * 
 *
 */
@RestController
public class EventServiceController implements ApplicationContextAware, ServletContextAware, ServletConfigAware {
	
	private static final Logger log = LoggerFactory.getLogger(EventServiceController.class);
	private DAOFactory daoFactory;
	private ApplicationContext ctx;

	public DAOFactory getFactory(){
		return daoFactory;
	}
	public void setDAOFactory(DAOFactory factory){
		daoFactory = factory;
	}
	public EventServicePublisher esp = null;

	@Autowired
	private ServletContext context;
	@Autowired
	private ServletConfig config;


	/**
	 * Creates a new event for storage and publication.
	 * 
	 * @param evt the event to be persistent and or published.
	 * @return an EventResponse containing the status of the request
	 */
	@RequestMapping(value="/event", method = RequestMethod.POST)
	@Consumes("application/json")
	public EventResponse setEvent(@RequestBody Event evt){

		log.debug("Attempting to persist an event with id: " + evt.toString());

		EventResponse er;

		try {
			er = daoFactory.geDAO().insertEvent(evt);

			if (er.getPersistStatus().endsWith("OK")){
				if (esp.publishEvent(evt)==false){
					er.setPublishStatus("ERROR");
					er.setPersistStatusMessage("Event was not published per destination.");
					return er;
				}
				er.setPublishStatus("OK");
				return er;
			}

		} catch (DAOException e) {

			log.error(e.getMessage(), e);

			er = new EventResponse();
			er.setPersistStatus("SYSTEM_ERROR");
			er.setPersistStatusMessage("request could not be processed: " + e.getCause());

		} catch (PublisherException e){

			log.error(e.getMessage(), e);

			er = new EventResponse();
			er.setPersistStatus("PUBLISH_ERROR");
			er.setPersistStatusMessage("request could not be processed: " + e.getCause());
		}

		return er;

	}

	/**
	 * Gets information about an event
	 *
	 * @param id event id to be retrieved.
	 * @return an EventResponse containing the event or the reason it could not be retrieved.
	 */
	@RequestMapping(value="/event/{id}", method = RequestMethod.GET)
	public EventResponse getEvent(@PathVariable String id){

		log.debug("Attempting to retrieving an event with id: " + id);

		EventResponse er;

		try {
			er = daoFactory.geDAO().getEvent(id);

		} catch (DAOException e) {

			log.error(e.getMessage(), e);

			er = new EventResponse();
			er.setPersistStatus("SYSTEM_ERROR");
			er.setPersistStatusMessage("request could not be processed: " + e.getCause());
		}

		return er;

	}


	
	/**
	 * Creates new events for storage and for publishing.
	 * 
	 * @param evts
	 * @return
	 */
	@RequestMapping(value="/events", method = RequestMethod.POST)
	public EventsResponse setEvents(@RequestBody List<Event> evts){

		log.debug("Attempting to persist and optionally publish event.");

		try {

			EventsResponse er =  daoFactory.geDAO().insertEvents(evts);
			return er;

		} catch (DAOException e){

			log.error(e.getMessage(), e);

			EventsResponse er = new EventsResponse();
			er.setStatus("INSERT_ERROR");
			er.setStatusMessage("request could not be processed: " + e.getCause());
			return er;
		}

	}


    
    @RequestMapping(value="/events", method = RequestMethod.GET)
    public List<Event> getEvents(List<String> req){
    	log.debug("getEvents request received");
    	return null;
    }


	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		ctx = applicationContext;
	}

	@Override
	public void setServletConfig(final ServletConfig servletConfig) {
		this.config = servletConfig;

	}

	@Override
	public void setServletContext(final ServletContext servletContext) {
		this.context = servletContext;

	}

	public EventServicePublisher getpublisher(){
		return esp;
	}

	public void setPublisher(EventServicePublisher esp){
		this.esp = esp;
	}
}
