package com.alliancefoundry.dao;

import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;

import com.alliancefoundry.exceptions.PeregrineException;
import com.alliancefoundry.model.Event;
import com.alliancefoundry.model.EventPublicationAudit;
import com.alliancefoundry.model.EventsRequest;

/**
 * Created by: Bobby Writtenberry
 *
 */
public interface IDAO {

	/**
	 * @param event						event to be inserted
	 * @param audits					audits to set persist timestamp for
	 * @return							event id of the event that was inserted
	 * @throws PeregrineException 		if some problem related to an event occurs
	 */
	public String insertEvent(Event event, Map<String,EventPublicationAudit> audits) throws PeregrineException;
	/**
	 * @param events					list of events to be inserted
	 * @param audits					audits to set persist timestamp for
	 * @return							list of event ids of the events that were inserted
	 * @throws PeregrineException 		if some problem related to an event occurs
	 */
	public List<String> insertEvents(List<Event> events, Map<String,EventPublicationAudit> audits) throws PeregrineException;
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
	/**
	 * @param audits					audits to insert into data store
	 * @return							
	 * @throws PeregrineException 	if some problem related to insertion occurs
	 */
	public void insertPublicationAudit(Map<String,EventPublicationAudit> audits) throws PeregrineException;
	/**
	 * @param audit					audit of the event being inserted
	 * @param timestamp				timestamp of publishing time
	 * @param publishId				unique id to identify publish instances for a particular event id
	 * @return							
	 * @throws PeregrineException 	if some problem related to insertion occurs
	 */
	public void insertPublishTimestamp(EventPublicationAudit audit, DateTime timestamp, int publishId) throws PeregrineException;
	/**
	 * @param eventId				of the event to be retrieved
	 * @return						the event publication audit with the corresponding event id
	 * @throws PeregrineException 	if some problem related to retrieving an audit occurs
	 */
	public EventPublicationAudit getPublicationAudit(String eventId) throws PeregrineException;
}