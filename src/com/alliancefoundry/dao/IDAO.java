package com.alliancefoundry.dao;

import java.util.List;

import com.alliancefoundry.exceptions.PeregrineException;
import com.alliancefoundry.model.Event;
import com.alliancefoundry.model.EventsRequest;

/**
 * Created by: Bobby Writtenberry
 *
 */
public interface IDAO {

	/**
	 * @param event								event to be inserted
	 * @return									event id of the event that was inserted
	 * @throws PeregrineException 				if some problem related to an event occurs
	 */
	public String insertEvent(Event event) throws PeregrineException;
	/**
	 * @param events							list of events to be inserted
	 * @return									list of event ids of the events that were inserted
	 * @throws PeregrineException 				if some problem related to an event occurs
	 */
	public List<String> insertEvents(List<Event> events) throws PeregrineException;
	/**
	 * @param eventId					of the event to be retrieved
	 * @return							the event with the corresponding event id
	 * @throws PeregrineException		if some problem related to an event occurs
	 */
	public Event getEvent(String eventId) throws PeregrineException;
	/**
	 * @param req						has values set to search parameters
	 * @return							list of events matching the search parameters
	 * @throws PeregrineException 		if some problem related to an event occurs
	 */
	public List<Event> getEvents(EventsRequest req) throws PeregrineException;
	/**
	 * @param req						has values set to search parameters
	 * @return							most recent event inserted matching search params
	 * @throws PeregrineException 		if some problem related to an event occurs
	 */
	public Event getLatestEvent(EventsRequest req) throws PeregrineException;
}