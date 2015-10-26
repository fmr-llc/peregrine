package com.alliancefoundry.dao;

import com.alliancefoundry.model.Event;
import com.alliancefoundry.model.EventResponse;
import com.alliancefoundry.model.EventsResponse;

import java.util.List;

/**
 * Created by Paul Bernard on 10/24/15.
 */
public interface EventDAO {


	/**
	 * Insert an Event object into a persistent store
	 * and return an EventResponse object that contains
	 * the status of the invocation.
	 *
	 * @param event the event to insert
	 * @return an EventResponse that contains the status of the invocation
	 * @throws DAOException
	 */
	EventResponse insertEvent(Event event) throws DAOException;


	/**
	 * Insert a list of events into the a persistent store
	 * and return an EventsResponse object that contains the
	 * status of the invocation.
	 *
	 * @param events the list of events to insert
	 * @return an EventsResponse that contains the status of each invocation.
	 * @throws DAOException
	 */
	EventsResponse insertEvents(List<Event> events) throws DAOException;


	/**
	 * Retrieves and event from the datasource and wraps the
	 * event in an EventResponse or wraps the reason it could not be returned.
	 *
	 * @param eventId
	 * @return an event if found or null if not.
	 * @throws DAOException
	 */
	EventResponse getEvent(String eventId) throws DAOException;


	/**
	 * Retrieve multiple events from the database based given a list of eventid's
	 * Result is wrapped in an EventsResponse
	 *
	 * @param eventIds
	 * @return
	 * @throws DAOException
	 */
	EventsResponse getEvents(List<String> eventIds) throws DAOException;


	/**
	 * Initializes the DAO resources
	 *
	 * @return true if initialization is successful otherwise false
	 * @throws DAOException
	 */
	boolean initialize() throws DAOException;


	/**
	 * Deallocate the DAO resources.
	 *
	 * @return true if resources are releases successfully
	 * @throws DAOException
	 */
	boolean shutdown() throws DAOException;
}
