package com.alliancefoundry.dao;

import com.alliancefoundry.model.Event;
import com.alliancefoundry.model.EventRequest;
import com.alliancefoundry.model.EventResponse;
import com.alliancefoundry.model.EventsResponse;
import org.springframework.dao.DataAccessException;

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
	EventResponse insertEvent(Event event) throws DataAccessException;


	/**
	 * Insert a list of events into the a persistent store
	 * and return an EventsResponse object that contains the
	 * status of the invocation.
	 *
	 * @param events the list of events to insert
	 * @return an EventsResponse that contains the status of each invocation.
	 * @throws DAOException
	 */
	EventsResponse insertEvents(List<Event> events) throws DataAccessException;


	/**
	 * Retrieves and event from the datasource and wraps the
	 * event in an EventResponse or wraps the reason it could not be returned.
	 *
	 * @param eventId
	 * @return an event if found or null if not.
	 * @throws DAOException
	 */
	EventResponse getEvent(String eventId) throws DataAccessException;


	/**
	 * Retrieve multiple events from the database based given a list of eventid's
	 * Result is wrapped in an EventsResponse
	 *
	 * @param eventIds
	 * @return
	 * @throws DAOException
	 */
	EventsResponse getEvents(List<String> eventIds) throws DataAccessException;


	/**
	 * Retrieves a set of events matching the criteria specified.
	 * Result is wrapped in an EventsResponse
	 *
	 * @param source
	 * @param generations
	 * @param name
	 * @param objectId
	 * @param correlationId
	 * @param createdAfter
	 * @param createdBefore
     * @param timestamp
     * @return
     */
	EventsResponse queryEvents(String source,
							   String generations,
							   String name,
							   String objectId,
							   String correlationId,
							   String createdAfter,
							   String createdBefore,
							   String timestamp) throws DAOException;

	/**
	 * Returns the list of all event sources in the event store
	 *
	 * @return
     */
	EventsResponse getEventSources() throws DataAccessException;


	/**
	 * Returns a list of event names associated with a given source;
	 *
	 * @param source
	 * @return
	 * @throws DAOException
     */
	EventsResponse getEventNames(String source) throws DataAccessException;


	/**
	 * Returns the lastest event given the criteria specified
	 *
	 * @param source
	 * @param name
	 * @param objectId
	 * @param correlationId
	 * @return
     * @throws DAOException
     */
	EventResponse getLatestEvent(String source,
								   String name,
								   String objectId,
								   String correlationId) throws DataAccessException;

	/**
	 * Given an EventRequest which specifies a
	 * @param request
	 * @return
	 * @throws DAOException
     */
	EventResponse replayEvent(EventRequest request) throws DataAccessException;


}
