package com.alliancefoundry.dao;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import com.alliancefoundry.exceptions.EventNotFoundException;
import com.alliancefoundry.model.Event;
import com.alliancefoundry.model.EventsRequest;

/**
 * Created by: Bobby Writtenberry
 *
 */
public interface DAO {
	/**
	 * @param events							list of events to be inserted
	 * @return									list of event ids of the events that were inserted
	 * @throws DataIntegrityViolationException	if insertion data is invalid
	 */
	public List<String> insertEvents(List<Event> events) throws DataIntegrityViolationException;
	/**
	 * @param eventId					of the event to be retrieved
	 * @return							the event with the corresponding event id
	 * @throws EventNotFoundException	if the event does not exist
	 */
	public Event getEvent(String eventId) throws EventNotFoundException;
	/**
	 * @param req						has values set to search parameters
	 * @return							list of events matching the search parameters
	 * @throws IllegalArgumentException	if request data is invalid
	 * @throws EventNotFoundException	if no events exist matching the request params
	 */
	public List<Event> getEvents(EventsRequest req) throws IllegalArgumentException, EventNotFoundException;
	/**
	 * @param req						has values set to search parameters
	 * @return							most recent event inserted matching search params
	 * @throws IllegalArgumentException	if request data is invalid
	 * @throws EventNotFoundException	if no events exist matching the request params
	 */
	public Event getLatestEvent(EventsRequest req) throws IllegalArgumentException, EventNotFoundException;
}