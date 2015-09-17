package com.alliancefoundry.controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
		try {
			eventId = dao.insertEvent(evt);
			log.debug("created event with event id " + eventId);
		} catch (SQLException e) {
			log.debug("Event could not be created.");
		}
		return eventId;
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
		for(Event e : evts){
			try {
				eventIds.add(dao.insertEvent(e));
			} catch (SQLException eSQL) {
				log.debug("Event could not be created.");
			}
		}
		String eventIdStr = "";
		for(String l : eventIds){
			eventIdStr += "\n" + l;
		}
		if(!eventIdStr.equals("")){
			log.debug("created events with event id[s]" + eventIdStr);
		}
		return eventIdStr;
	}
	
	/**
	 * Gets information about an event
	 * 
	 * @param id
	 * @return
	 */
    @RequestMapping(value="/event/{id}", method = RequestMethod.GET)
    public Event getEvent(@PathVariable String id){
    	Event eventFromDb = dao.getEvent(id);
        if(eventFromDb != null) {
        	log.debug("retrieved event with event id " + eventFromDb.getEventId());
        } else {
        	log.debug("No event could be retrieved with the specified id");
        }
        return eventFromDb;
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
    			log.debug("retrieved events with event id[s]" + eventIdStr);
    		} else {
    			log.debug("No events match the specified request parameters");
    		}
    		return eventsFromDb;
    	} catch(IllegalArgumentException e){
    		System.out.println(e.getMessage());
    		return null;
    	}
    }


}
