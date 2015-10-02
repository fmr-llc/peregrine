package com.alliancefoundry.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alliancefoundry.dao.IDAO;
import com.alliancefoundry.exceptions.EventNotFoundException;
import com.alliancefoundry.exceptions.PeregrineException;
import com.alliancefoundry.model.Event;
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
	public String setEvent(@RequestBody Event evt){
		String eventId;
		evt.setReceivedTimeStamp(DateTime.now());
		try {
			eventId = dao.insertEvent(evt);
			log.info("created event with event id " + eventId);
			publisher.attemptPublishEvent(evt);
			return eventId;
		} catch (DataIntegrityViolationException e) {
			log.error("Error inserting an event: " + e.getCause().getMessage());
			return null;
		}catch (PeregrineException e) {
			log.error("Error inserting an event: " + e.getCause().getMessage());
			return null;
		}
	}
	
	/**
	 * Creates new events
	 * 
	 * @param evts	the list of events to be created
	 * @return		the list of ids of the created events
	 */
	@RequestMapping(value="/events", method = RequestMethod.POST)
	public List<String> setEvents(@RequestBody List<Event> evts){
		List<String> eventIds = new ArrayList<String>();
		try {
			for(Event e : evts){
				e.setReceivedTimeStamp(DateTime.now());
			}
			eventIds = dao.insertEvents(evts);
			
			publisher.attemptPublishEvents(evts);
				
			if (eventIds.size() > 0){
			    log.info("Created events with event id[s]" + eventIds.stream().collect(Collectors.joining("\n")));
			} else {
				log.info("No events provided to insert");
			}
			return eventIds;
		} catch (DataIntegrityViolationException e) {
			log.error("Error inserting an event: " + e.getCause().getMessage() + ".  None of the events were inserted");
			return null;
		} catch (PeregrineException e) {
			log.error("Error inserting an event: " + e.getCause().getMessage());
			return null;
		}
	}
	
	/**
	 * Gets information about an event
	 * 
	 * @param id	of the event to be retrieved
	 * @return		the event with the corresponding event id
	 */
    @RequestMapping(value="/event/{id}", method = RequestMethod.GET)
    public Event getEvent(@PathVariable String id){
		try {
			Event eventFromDb = dao.getEvent(id);
			if(eventFromDb != null) {
	        	log.info("retrieved event with event id " + eventFromDb.getEventId());
	        } else {
	        	log.info("No event could be retrieved with the specified id");
	        }
	        return eventFromDb;
		} catch (EventNotFoundException e) {
			log.error("Error retrieving event: " + e.getMessage());
			return null;
		}
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
    public List<Event> getEvents(
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
    	if(generations != null && generations < 1){
    		//TODO: Refactor how this condition is handled
    		log.error("Invalid value for generations.  Must be greater than 0");
    		return null;
    	}
    	EventsRequest req = new EventsRequest(createdAfterVal, createdBeforeVal, source, objectId,
    			correlationId, eventName, generations);
    	List<Event> eventsFromDb = new ArrayList<Event>();
    	List<String> eventIds = new ArrayList<String>();
    	try{
    		eventsFromDb = dao.getEvents(req);
    		for(Event e : eventsFromDb){
    			eventIds.add(e.getEventId());
    		}
    		if(eventIds.size() > 0){
    			log.info("retrieved events with event id[s] " + eventIds.stream().collect(Collectors.joining("\n")));
    		} else {
    			log.info("No events match the specified request parameters");
    		}
    		return eventsFromDb;
    	} catch(IllegalArgumentException e){
    		log.error("Incorrect or missing request parameter: " + e.getMessage());
    		return null;
    	} catch(EventNotFoundException e) {
    		log.error("Error retrieving an event: " + e.getMessage());
    		return null;
    	}
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
    public Event getLatestEvent(
    		@RequestParam(value="source", required=false) String source,	
    		@RequestParam(value="objectId", required=false) String objectId,
    		@RequestParam(value="correlationId", required=false) String correlationId,
    		@RequestParam(value="eventName", required=false) String eventName){

    	EventsRequest req = new EventsRequest(null, null, source, objectId,
    			correlationId, eventName, null);
    	Event event;
		try {
			event = dao.getLatestEvent(req);
			if(event != null){
	    		log.info("retrieved event with event id " + event.getEventId());
	    	} else {
	    		log.info("No event matches the specified request parameters");
	    	}
			return event;
		} catch(IllegalArgumentException e){
    		log.error("Incorrect or missing request parameter: " + e.getMessage());
    		return null;
		} catch (EventNotFoundException e) {
			log.error("Error retrieving event: " + e.getMessage());
			return null;
		}
    }
    
    
	/**
	 * Replays a event
	 * 
	 * @param eventId - Id of event to replay
	 * @return whether or not the replay was successful
	 */
	@RequestMapping(value="/replay/{id}", method = RequestMethod.POST)
	public String ReplayEvent(
			@RequestParam(value="eventid", required=false) String eventId){
		String message = String.format("Successfully replayed Event - Event Id: %s", eventId);
		
		try {
			Event eventFromDb = dao.getEvent(eventId);
			publisher.attemptPublishEvent(eventFromDb);
		} catch (PeregrineException e) {
			log.error("Error replaying event");
			log.error("Error Message: " + e.getMessage());
			message = String.format("Event could not be replayed. Event Id: %s", eventId);
		} catch (EventNotFoundException e) {
			log.error("Event cannot be found.");
		}
		return message;
	}

	public PublisherRouter getPublisher() {
		return publisher;
	}

	public void setPublisher(PublisherRouter publisher) {
		this.publisher = publisher;
	}
}
