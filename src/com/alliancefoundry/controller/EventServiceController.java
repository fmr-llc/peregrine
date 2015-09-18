package com.alliancefoundry.controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.Consumes;

import org.springframework.web.bind.annotation.ModelAttribute;
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
	@Consumes("application/json")
	public String setEvent(@RequestBody Event evt){
		String eventId = "";
		evt.setEventId(UUID.randomUUID().toString());
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
	public String setEvents(@RequestBody List<Event> evts){
		List<String> eventIds = new ArrayList<String>();
		try {
			eventIds = dao.insertEvents(evts);
			String eventIdStr = "";
			for(String l : eventIds){
				eventIdStr += "\n" + l;
			}
			if(!eventIdStr.equals("")){
				log.debug("created events with event id[s] " + eventIdStr);
			}
			return eventIdStr;
		} catch (SQLException eSQL) {
			log.debug("Error inserting an event: " + eSQL.getMessage());
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
    		@RequestParam(value="name", required=false) String name,
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
    			correlationId, name, generations);
    	List<Event> eventsFromDb = new ArrayList<Event>();
    	try{
    		eventsFromDb = dao.getEvents(req);
    		String eventIdStr = "";
    		for(Event e : eventsFromDb){
    			eventIdStr += "\n" + e.getEventId();
    		}
    		if(!eventIdStr.equals("")){
    			log.debug("retrieved events with event id[s] " + eventIdStr);
    		} else {
    			log.debug("No events match the specified request parameters");
    		}
    		return eventsFromDb;
    	} catch(IllegalArgumentException e){
    		log.debug("Incorrect or missing request parameter");
    		return null;
    	} catch(SQLException eSQL) {
    		log.debug("Error retrieving an event: " + eSQL.getMessage());
    		return null;
    	}
    }

    @RequestMapping(value="/latest-event", method = RequestMethod.GET)
    public Event getLatestEvent(
    		@RequestParam(value="source", required=false) String source,	
    		@RequestParam(value="objectId", required=false) String objectId,
    		@RequestParam(value="correlationId", required=false) String correlationId,
    		@RequestParam(value="name", required=false) String name){

    	EventsRequest req = new EventsRequest(null, null, source, objectId,
    			correlationId, name, null);
    	Event event;
		try {
			event = dao.getLatestEvent(req);
			if(event != null){
	    		log.debug("retrieved event with event id " + event.getEventId());
	    	} else {
	    		log.debug("No event matches the specified request parameters");
	    	}
			return event;
		} catch (SQLException e) {
			log.debug("Error retrieving event: " + e.getMessage());
			return null;
		}
    }

}
