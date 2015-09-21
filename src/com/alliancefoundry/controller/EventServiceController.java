package com.alliancefoundry.controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliancefoundry.dao.JDBCDAOimpl;
import com.alliancefoundry.model.Event;
import com.alliancefoundry.model.EventsRequest;



/**
 * Created by: Paul Bernard
 * 
 *
 */
@RestController
public class EventServiceController  {
	
	static final Logger log = LoggerFactory.getLogger(EventServiceController.class);
	JDBCDAOimpl dao = new JDBCDAOimpl();

	/**
	 * Creates a new event
	 * 
	 * @param evt
	 * @return
	 */
	@RequestMapping(value="/event", method = RequestMethod.POST)
	public String setEvent(@RequestBody Event evt){
		String eventId = "";
		evt.setReceivedTimeStamp(DateTime.now());
		try {
			eventId = dao.insertEvent(evt);
			log.debug("created event with event id " + eventId);
			return eventId;
		} catch (SQLException e) {
			log.debug("Error inserting an event: " + e.getMessage());
			return null;
		}
	}
	
	/**
	 * Creates new events
	 * 
	 * @param evts
	 * @return
	 */
	@RequestMapping(value="/events", method = RequestMethod.POST)
	public List<String> setEvents(@RequestBody List<Event> evts){
		List<String> eventIds = new ArrayList<String>();
		try {
			for(Event e : evts){
				e.setReceivedTimeStamp(DateTime.now());
			}
			eventIds = dao.insertEvents(evts);
			if (eventIds.size() > 0){
			    log.debug("Created events with event id[s]" + eventIds.stream().collect(Collectors.joining("\n")));
			} else {
				log.debug("No events provided to insert");
			}
			return eventIds;
		} catch (SQLException e) {
			log.debug("Error inserting an event: " + e.getMessage());
			return null;
		}
	}
	
	/**
	 * Gets information about an event
	 * 
	 * @param id
	 * @return
	 */
    @RequestMapping(value="/event/{id}", method = RequestMethod.GET)
    public Event getEvent(@PathVariable String id){
		try {
			Event eventFromDb = dao.getEvent(id);
			if(eventFromDb != null) {
	        	log.debug("retrieved event with event id " + eventFromDb.getEventId());
	        } else {
	        	log.debug("No event could be retrieved with the specified id");
	        }
	        return eventFromDb;
		} catch (SQLException e) {
			log.debug("Error retrieving event: " + e.getMessage());
			return null;
		}
    }
    
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
    			log.debug("retrieved events with event id[s] " + eventIds.stream().collect(Collectors.joining("\n")));
    		} else {
    			log.debug("No events match the specified request parameters");
    		}
    		return eventsFromDb;
    	} catch(IllegalArgumentException e){
    		log.debug("Incorrect or missing request parameter: " + e.getMessage());
    		return null;
    	} catch(SQLException e) {
    		log.debug("Error retrieving an event: " + e.getMessage());
    		return null;
    	}
    }

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
	    		log.debug("retrieved event with event id " + event.getEventId());
	    	} else {
	    		log.debug("No event matches the specified request parameters");
	    	}
			return event;
		} catch(IllegalArgumentException e){
    		log.debug("Incorrect or missing request parameter: " + e.getMessage());
    		return null;
		} catch (SQLException e) {
			log.debug("Error retrieving event: " + e.getMessage());
			return null;
		}
    }

}
