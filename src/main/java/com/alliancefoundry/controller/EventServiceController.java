package com.alliancefoundry.controller;


import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import com.alliancefoundry.model.*;
import com.alliancefoundry.publisher.EventServicePublisher;
import com.alliancefoundry.publisher.PublisherException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.ServletConfigAware;
import org.springframework.web.context.ServletContextAware;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliancefoundry.dao.DAOException;
import com.alliancefoundry.dao.DAOFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;


@RestController
@RequestMapping(value="/eventservice")
@Api(value="/eventservice", description="Endpoint for interacting with an event store and the publishing of events.")
public class EventServiceController implements ApplicationContextAware, ServletContextAware, ServletConfigAware {
	
	private static final Logger log = LoggerFactory.getLogger(EventServiceController.class);
	@Autowired
	private DAOFactory daoFactory;
	private ApplicationContext ctx;

	public DAOFactory getFactory(){
		return daoFactory;
	}

	public void setDAOFactory(DAOFactory factory){
		daoFactory = factory;
	}
	@Autowired
	public EventServicePublisher esp = null;

	@Autowired
	private ServletContext context;
	@Autowired
	private ServletConfig config;

	public static final String PERSISTENT_STATUS_OK = "OK";
	public static final String PERSISTENT_STATUS_ERROR = "ERROR";

	public static final String PUBLISH_STATUS_OK = "OK";
	public static final String PUBLISH_STATUS_ERROR = "ERROR";
	public static final String PUBLISH_STATUS_NO_ATTEMPT = "NO_ATTEMPT_MADE";

	public static final String REQUEST_STATUS_OK = "OK";
	public static final String REQUEST_STATUS_ERROR = "ERROR";


	public EventServiceController(){
		// default constructor
	}



	/**
	 * Creates a new event for storage and publication.
	 * 
	 * @param request the request containing an event to be persistent and or published.
	 * @return an EventResponse containing the status of the request
	 */
	@RequestMapping(value="/event", method = RequestMethod.POST,
			consumes={MediaType.APPLICATION_JSON_VALUE,
					MediaType.APPLICATION_XML_VALUE},
			produces={MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE})
	@ApiModelProperty(value="Submits an event to the Event Service")
	public EventResponse setEvent(@RequestBody EventRequest request){

		long start = System.currentTimeMillis();

		log.debug("Attempting to persist an event with id: " + request.toString());

		EventResponse er;

		try {
			er = daoFactory.getDAO().insertEvent(request.getEvent());

			if (er==null) {

				er = new EventResponse();
				er.setPersistStatus(PERSISTENT_STATUS_ERROR);
				er.setPersistStatusMessage("Request to persist event did not return status");
				er.setPublishStatus(PUBLISH_STATUS_NO_ATTEMPT);
				er.setPublishStatusMessage("Attempt to persist failed. Publish attempt was not made, even if requested.");
				er.setStatus(REQUEST_STATUS_ERROR);
				er.setStatusMessage("The request was not successful.");

				long duration = System.currentTimeMillis() - start;
				er.setProcessingDuration(duration);

				return er;

			} else if (request.getEvent().isPublishable()==null || request.getEvent().isPublishable().booleanValue()==false){

				er.setPublishStatus(PUBLISH_STATUS_NO_ATTEMPT);
				er.setPublishStatusMessage("Publishing indicator not specified or set to false.");
				er.setStatus(REQUEST_STATUS_OK);
				er.setStatusMessage("The request was successful.");

				long duration = System.currentTimeMillis() - start;
				er.setProcessingDuration(duration);

				return er;

			} else if (er.getPersistStatus().endsWith("OK")){


				if (esp!=null){

					boolean reqPubStatus = esp.publishEvent(request.getEvent());

					if (reqPubStatus==false){

						er.setPublishStatus(PUBLISH_STATUS_ERROR);
						er.setPublishStatusMessage("Event was not published. Message broker could not be reached.");
						er.setStatus(REQUEST_STATUS_ERROR);
						er.setStatusMessage("The request was partially sucessful.");

					} else {

						er.setPublishStatus(PUBLISH_STATUS_OK);
						er.setPublishStatusMessage("Event was published sucessfully.");
						er.setStatus(REQUEST_STATUS_OK);
						er.setStatusMessage("The request was successful.");

					}

				} else {

					er.setPublishStatus(PUBLISH_STATUS_ERROR);
					er.setPublishStatusMessage("Attempt to persist failed. Publish attempt Failed.");
					er.setStatus(REQUEST_STATUS_ERROR);
					er.setStatusMessage("The request was only partially successful.");

				}

			}

		} catch (DAOException e) {

			log.error(e.getMessage(), e);

			er = new EventResponse();
			er.setPersistStatus(PERSISTENT_STATUS_ERROR);
			er.setPersistStatusMessage("request could not be processed: " + e.getCause());


		} catch (PublisherException e){

			log.error(e.getMessage(), e);

			er = new EventResponse();
			er.setPublishStatus("PUBLISH_ERROR");
			er.setPublishStatusMessage("request could not be processed: " + e.getCause());
		}

		long duration = System.currentTimeMillis() - start;
		er.setProcessingDuration(duration);

		return er;

	}

	/**
	 * Retrieves information about a specific event
	 *
	 * Validation is present to limit the risk of sql injection
	 * that limits the composition of event id's to alphanumeric characters.
	 *
	 * @param id event id to be retrieved.
	 * @return an EventResponse containing the event or the reason it could not be retrieved.
	 */
	@RequestMapping(
			value="/event/{id}",
			method = RequestMethod.GET,
			produces={MediaType.APPLICATION_JSON_VALUE,
					MediaType.APPLICATION_XML_VALUE})
	public EventResponse getEvent(@PathVariable String id){

		long start = System.currentTimeMillis();

		EventResponse er;

		if (id.matches("^[a-zA-Z0-9]*$")) {

			log.debug("Attempting to retrieving an event with id: " + id);

			try {

				er = daoFactory.getDAO().getEvent(id);

			} catch (DAOException e) {

				log.error(e.getMessage(), e);
				er = new EventResponse();
				er.setPersistStatus("SYSTEM_ERROR");
				er.setPersistStatusMessage("request could not be processed: " + e.getCause());
			}

		} else {

			er = new EventResponse();
			er.setPersistStatus("SYSTEM_ERROR");
			er.setPersistStatusMessage("request could not be processed: " + "invalid characters present in parameter");

		}

		long duration = System.currentTimeMillis() - start;
		er.setProcessingDuration(duration);

		return er;

	}


	
	/**
	 * Creates new events for storage and for publishing.
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/events", method = RequestMethod.POST,
			consumes={MediaType.APPLICATION_JSON_VALUE,
					MediaType.APPLICATION_XML_VALUE},
			produces={MediaType.APPLICATION_JSON_VALUE,
					MediaType.APPLICATION_XML_VALUE})
	public EventsResponse setEvents(@RequestBody EventsRequest request){

		log.debug("Attempting to persist and optionally publish event.");

		try {

			EventsResponse er =  daoFactory.getDAO().insertEvents(request.getEvents());
			return er;

		} catch (DAOException e){

			log.error(e.getMessage(), e);

			EventsResponse er = new EventsResponse();
			er.setStatus("INSERT_ERROR");
			er.setStatusMessage("request could not be processed: " + e.getCause());
			return er;
		}

	}

	@RequestMapping(value = "/events/{objectIds}", method = RequestMethod.GET,
			produces={MediaType.APPLICATION_JSON_VALUE,
					MediaType.APPLICATION_XML_VALUE})
	public EventsResponse getEvents(@PathVariable String objectIds){

		log.debug("Attempting to retrieve a list of events with id's =" + objectIds);

		// parse and clean up the delimited string containing a list of id's
		StringTokenizer st = new StringTokenizer(objectIds, ",");

		List<String> ls = new ArrayList<String>();
		int c = 0;
		while(st.hasMoreTokens()){
			String token = st.nextToken().trim();
			ls.add(token);
		}

		try {

			EventsResponse er = daoFactory.getDAO().getEvents(ls);
			return er;

		} catch (DAOException e){

			log.error(e.getMessage(), e);
			EventsResponse er = new EventsResponse();
			er.setStatus("QUERY_ERROR");
			er.setStatusMessage("request could not be processed: " + e.getCause());
			return er;

		}

	}



	@RequestMapping(value = "/query-events", method = RequestMethod.GET,
			produces={MediaType.APPLICATION_JSON_VALUE,
					MediaType.APPLICATION_XML_VALUE})
	public EventsResponse queryEvents(@RequestParam String source,
									  @RequestParam String generations,
									  @RequestParam String name,
									  @RequestParam String objectId,
									  @RequestParam String correlationId,
									  @RequestParam String createdAfter,
									  @RequestParam String createdBefore,
									  @RequestParam String timestamp){ return null;}

    


	@RequestMapping(value = "/event-sources", method = RequestMethod.GET,
			produces={MediaType.APPLICATION_JSON_VALUE,
					MediaType.APPLICATION_XML_VALUE})
	public EventsResponse getEventSources()  {

		try {
			EventsResponse er = daoFactory.getDAO().getEventSources();
			return er;
		} catch (DAOException e) {

			log.error(e.getMessage(), e);
			EventsResponse er = new EventsResponse();
			er.setStatus("QUERY_ERROR");
			er.setStatusMessage("request could not be processed: " + e.getCause());
			return er;
		}


	}


	@RequestMapping(value = "/event-names/{source}", method = RequestMethod.GET,
			produces={MediaType.APPLICATION_JSON_VALUE,
					MediaType.APPLICATION_XML_VALUE})
	public EventsResponse getEventNames(@PathVariable String source) {

		log.debug("Attempting to retrieve a list of event names with source = " + source);

		try {
			EventsResponse er = daoFactory.getDAO().getEventNames(source);

			return er;
		} catch (DAOException e) {

			log.error(e.getMessage(), e);
			EventsResponse er = new EventsResponse();
			er.setStatus("QUERY_ERROR");
			er.setStatusMessage("request could not be processed: " + e.getCause());
			return er;
		}

	}


	@RequestMapping(value = "/latest-event", method = RequestMethod.GET,
			produces={MediaType.APPLICATION_JSON_VALUE,
					MediaType.APPLICATION_XML_VALUE})
	public EventResponse getLatestEvent(@RequestParam(value="source", required=false) String source,
										@RequestParam(value="name", required=false) String name,
										@RequestParam(value="objectId", required=false) String objectId,
										@RequestParam(value="correlationId", required=false) String correlationId) {


		log.debug("Attempting to retrieve the latest event matching the criteria provided.");

		try {

			EventResponse er = daoFactory.getDAO().getLatestEvent(source, name, objectId, correlationId);

			return er;

		} catch (DAOException e) {

			log.error(e.getMessage(), e);
			EventResponse er = new EventResponse();
			er.setStatus("QUERY_ERROR");
			er.setStatusMessage("request could not be processed: " + e.getCause());
			return er;
		}

	}

	@RequestMapping(value = "/replay", method = RequestMethod.POST,
			consumes={MediaType.APPLICATION_JSON_VALUE,
					MediaType.APPLICATION_XML_VALUE},
			produces={MediaType.APPLICATION_JSON_VALUE,
					MediaType.APPLICATION_XML_VALUE})
	public EventResponse replayEvent(@RequestBody EventRequest request) {


		long start = System.currentTimeMillis();

		String eventId = request.getEvent().getEventId();
		log.debug("Attempting to replay event with eventid: " + eventId);
		EventResponse eventRes = this.getEvent(eventId);
		Event evt = eventRes.getEvent();

		EventResponse er = new EventResponse();
		er.setEvent(evt);

		try {


			if (evt.isPublishable()==null || evt.isPublishable().booleanValue()==false){

				er.setPublishStatus(PUBLISH_STATUS_NO_ATTEMPT);
				er.setPublishStatusMessage("Publishing indicator not specified or set to false.");
				er.setStatus(REQUEST_STATUS_OK);
				er.setStatusMessage("The request was successful.");

				long duration = System.currentTimeMillis() - start;
				er.setProcessingDuration(duration);

				return er;

			} else {


				if (esp!=null){

					boolean reqPubStatus = esp.publishEvent(evt);

					if (reqPubStatus==false){

						er.setPublishStatus(PUBLISH_STATUS_ERROR);
						er.setPublishStatusMessage("Event was not published. Message broker could not be reached.");
						er.setStatus(REQUEST_STATUS_ERROR);
						er.setStatusMessage("The request was partially sucessful.");

					} else {

						er.setPublishStatus(PUBLISH_STATUS_OK);
						er.setPublishStatusMessage("Event was published sucessfully.");
						er.setStatus(REQUEST_STATUS_OK);
						er.setStatusMessage("The request was successful.");

					}

				} else {

					er.setPublishStatus(PUBLISH_STATUS_ERROR);
					er.setPublishStatusMessage("Attempt to persist failed. Publish attempt Failed.");
					er.setStatus(REQUEST_STATUS_ERROR);
					er.setStatusMessage("The request was only partially successful.");

				}

			}

		} catch (PublisherException e){

			log.error(e.getMessage(), e);

			er = new EventResponse();
			er.setPublishStatus("PUBLISH_ERROR");
			er.setPublishStatusMessage("request could not be processed: " + e.getCause());
		}

		long duration = System.currentTimeMillis() - start;
		er.setProcessingDuration(duration);

		return er;




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
