package com.alliancefoundry.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alliancefoundry.dao.IDAO;
import com.alliancefoundry.exceptions.PeregrineException;
import com.alliancefoundry.model.Event;
import com.alliancefoundry.model.EventResponse;
import com.alliancefoundry.model.EventsRequest;
import com.alliancefoundry.publisher.PublisherRouter;

/**
 * Created by: Paul Bernard, Bobby Writtenberry, Paul Fahey, Robert Coords
 *
 */
@RestController
public class EventServiceController  {
	
	static final Logger log = LoggerFactory.getLogger(EventServiceController.class);

	@Autowired
	IDAO dao;
	public void setDao(IDAO dao) {
		this.dao = dao;
	}
	
	@Autowired
	private PublisherRouter publisher;

	/**
	 * Creates a new event
	 * 
	 * @param evt	the event to be created
	 * @return		the id of the created event
	 */
	@RequestMapping(value="/event", method = RequestMethod.POST)
	public EventResponse setEvent(@RequestBody Event evt){
		EventResponse response = new EventResponse();
		String msg;
		evt.setReceivedTimeStamp(DateTime.now());
		try {
			Event.verifyNonNullables(evt);
			String eventId = dao.insertEvent(evt);
			msg  = String.format("Successfully created event - Event Id: %s", eventId);
			publisher.attemptPublishEvent(evt);
			response.getEventIds().add(eventId);
		}catch (PeregrineException e) {
			response.getEvents().add(evt);
			msg = String.format("Error inserting or publishing an event: %s", e.getMessage());
		}
		response.setMsg(msg);
		log.info(msg);
		return response;
	}
	
	/**
	 * Creates new events
	 * 
	 * @param evts	the list of events to be created
	 * @return		the list of ids of the created events
	 */
	@RequestMapping(value="/events", method = RequestMethod.POST)
	public EventResponse setEvents(@RequestBody List<Event> evts){
		EventResponse response = new EventResponse();
		String msg;
		List<String> eventIds = new ArrayList<String>();
		try {
			for(Event e : evts){
				Event.verifyNonNullables(e);
				e.setReceivedTimeStamp(DateTime.now());
			}
			eventIds = dao.insertEvents(evts);
			msg  = String.format("Successfully created event[s] - Event Id[s]: %s", eventIds.stream().collect(Collectors.joining("\n")));
			publisher.attemptPublishEvents(evts);
			response.setEventIds(eventIds);
		}catch (PeregrineException e) {
			response.setEvents(evts);
			msg = String.format("Error inserting or publishing an event: %s", e.getMessage());
		}
		response.setMsg(msg);
		log.info(msg);
		return response;
	}
	
	/**
	 * Gets information about an event
	 * 
	 * @param id	of the event to be retrieved
	 * @return		the event with the corresponding event id
	 */
    @RequestMapping(value="/event/{id}", method = RequestMethod.GET)
    public EventResponse getEvent(@PathVariable String id){
		EventResponse response = new EventResponse();
		String msg;
    	try {
			Event eventFromDb = dao.getEvent(id);
			msg  = String.format("Successfully retrieved event - Event Id: %s", eventFromDb.getEventId());
	        response.getEvents().add(eventFromDb); 
		} catch (PeregrineException e) {
			msg = String.format("Error retrieving event - Event Id %s: %s", id, e.getMessage());
			response.getEventIds().add(id);
		}
    	response.setMsg(msg);
    	log.info(msg);
    	return response;
    }
    
    /**
     * Gets information about multiple events
     * 
     * @param createdAfter		timestamp after which an event was created
     * @param createdBefore		timestamp before which an event was created
     * @param source			of an event
     * @param objectId			of an event
     * @param correlationId		of an event
     * @param eventName			of an event
     * @param generations		maximum depth in a tree to retrieve events
     * @return					list of events with values matching params
     */
    @RequestMapping(value="/events", method = RequestMethod.GET)
    public EventResponse getEvents(
    		@RequestParam(value="createdAfter", required=false) String createdAfter,
    		@RequestParam(value="createdBefore", required=false) String createdBefore,
    		@RequestParam(value="source", required=false) String source,	
    		@RequestParam(value="objectId", required=false) String objectId,
    		@RequestParam(value="correlationId", required=false) String correlationId,
    		@RequestParam(value="eventName", required=false) String eventName,
			@RequestParam(value="generations", required=false) Integer generations){
    	DateTime createdAfterVal;
    	DateTime createdBeforeVal;
    	if(createdAfter == null){
    		createdAfterVal = null;
    	} else {
    		createdAfterVal = new DateTime(createdAfter);
    	}
    	if(createdBefore == null){
    		createdBeforeVal = null;
    	} else {
    		createdBeforeVal = new DateTime(createdBefore);
    	}
    	EventResponse response = new EventResponse();
    	String msg;
    	EventsRequest req = new EventsRequest(createdAfterVal, createdBeforeVal, source, objectId,
    			correlationId, eventName, generations);
    	List<Event> eventsFromDb = new ArrayList<Event>();
    	List<String> eventIds = new ArrayList<String>();
    	try{
    		EventsRequest.verifyRequestParameters(req, false);
    		eventsFromDb = dao.getEvents(req);
    		for(Event e : eventsFromDb){
    			eventIds.add(e.getEventId());
    		}
    		response.setEvents(eventsFromDb);
    		msg = String.format("Successfully retrieved event[s] - Event Id[s]: %s", eventIds.stream().collect(Collectors.joining("\n")));
    	} catch(PeregrineException e) {
    		msg = String.format("Error retrieving an event: %s", e.getMessage());
    	}
    	response.setMsg(msg);
    	log.info(msg);
    	return response;
    }

    /**
     * Gets information about the latest event with the requested params
     * 
     * @param source			of an event
     * @param objectId			of an event
     * @param correlationId		of an event
     * @param eventName			of an event
     * @return					the event with values matching the params
     */
    @RequestMapping(value="/latest-event", method = RequestMethod.GET)
    public EventResponse getLatestEvent(
    		@RequestParam(value="source", required=false) String source,	
    		@RequestParam(value="objectId", required=false) String objectId,
    		@RequestParam(value="correlationId", required=false) String correlationId,
    		@RequestParam(value="eventName", required=false) String eventName){

    	EventResponse response = new EventResponse();
    	String msg;
    	EventsRequest req = new EventsRequest(null, null, source, objectId,
    			correlationId, eventName, null);
    	Event event;
		try {
			EventsRequest.verifyRequestParameters(req, true);
			event = dao.getLatestEvent(req);
			response.getEvents().add(event);
	    	msg  = String.format("Successfully retrieved event - Event Id: %s", event.getEventId());
		} catch (PeregrineException e) {
			msg = String.format("Error retrieving event: %s", e.getMessage());
		}
		response.setMsg(msg);
		log.info(msg);
		return response;
    }
    
    
	/**
	 * Replays a event
	 * 
	 * @param eventId - Id of event to replay
	 * @return whether or not the replay was successful
	 */
	@RequestMapping(value="/replay/{id}", method = RequestMethod.POST)
	public EventResponse replayEvent(@PathVariable String id){
		EventResponse response = new EventResponse();
		String msg;
		
		try {
			Event eventFromDb = dao.getEvent(id);
			publisher.attemptPublishEvent(eventFromDb);
			msg = String.format("Successfully replayed event - Event Id: %s", id);
		} catch (PeregrineException e) {
			msg = String.format("Error replaying event - Event Id %s: %s", id, e.getMessage());
		}
		response.setMsg(msg);
		log.info(msg);
		return response;
	}

	public PublisherRouter getPublisher() {
		return publisher;
	}

	public void setPublisher(PublisherRouter publisher) {
		this.publisher = publisher;
	}
}