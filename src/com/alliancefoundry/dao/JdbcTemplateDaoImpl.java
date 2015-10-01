package com.alliancefoundry.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.alliancefoundry.exceptions.EventNotFoundException;
import com.alliancefoundry.exceptions.PeregrineException;
import com.alliancefoundry.model.DataItem;
import com.alliancefoundry.model.Event;
import com.alliancefoundry.model.EventsRequest;
import com.alliancefoundry.publisher.EventServicePublisher;

import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.jdbc.core.RowMapper;

/**
 * Created by: Bobby Writtenberry
 *
 */
@Component
public class JdbcTemplateDaoImpl implements IDAO {
	
	private JdbcTemplate jdbcTemplate;
	private AbstractApplicationContext ctx;
	private SqlQuery sql;
	EventServicePublisher publisher;
	public JdbcTemplateDaoImpl(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
		ctx = new ClassPathXmlApplicationContext("queries.xml");
		sql = ctx.getBean("sql", SqlQuery.class);
		ctx.close();
	}

	public JdbcTemplateDaoImpl() {
	}

	/**
	 * @param events							list of events to be inserted
	 * @return									list of event ids of the events that were inserted
	 * @throws DataIntegrityViolationException	if insertion data is invalid
	 */
	@Transactional
	public List<String> insertEvents(List<Event> events) throws DataIntegrityViolationException {
		List<String> eventIds = new ArrayList<String>();
		String eventSql = sql.getInsertSingleEvent();
		String headersSql = sql.getInsertHeader();
		String payloadSql = sql.getInsertPayload();
		try{
			for (Event event : events) {
				//insert into event_store
				String eventId = insertIntoEventStoreTable(eventSql, event);

				//insert into event_headers
				insertIntoEventHeadersTable(headersSql, event);

				//insert into event_payload
				insertIntoEventPayloadTable(payloadSql, event);
				
				eventIds.add(eventId);
			}
			for (Event event : events) {
				//try to publish the event
				attemptPublishEvent(event);
			}
				
			return eventIds;
		} catch(DataIntegrityViolationException e) {
			throw e;
		}
	}
	
	/**
	 * @param eventId					of the event to be retrieved
	 * @return							the event with the corresponding event id
	 * @throws EventNotFoundException	if the event does not exist
	 */
	public Event getEvent(String eventId) throws EventNotFoundException {
		String eventSql = sql.getSingleEventById();
		List<Event> eventList = jdbcTemplate.query(eventSql, new PreparedStatementSetter() {
			@Override
			public void setValues(java.sql.PreparedStatement ps) throws SQLException {
				ps.setString(1, eventId);
			}
		}, new EventRowMapper(jdbcTemplate, sql));
		//query limits to one result because event ids are unique so there should only be one event
		if (eventList.size() == 1) {
			return eventList.get(0);
		} else {
			throw new EventNotFoundException("Event not found in database");
		}
	}
	
	/**
	 * @param req						has values set to search parameters
	 * @return							list of events matching the search parameters
	 * @throws IllegalArgumentException	if request data is invalid
	 * @throws EventNotFoundException	if no events exist matching the request params
	 */
	public List<Event> getEvents(EventsRequest req) throws IllegalArgumentException, EventNotFoundException {
		//ensure valid request params
		Integer genNum = verifyRequestParameters(req, false);
		
		//build the SQL query
		String eventSql = sqlStringBuilder(req, false);
		
		//retrieve the list matching request params
		List<Event> eventList = queryWithPs(eventSql, req, false);
		
		//verify some events were retrieved
		if (eventList.size() == 0) {
			throw new EventNotFoundException("No events matching the specified parameters were found in database");
		}
		
		//get the events based off of generation count if one is specified
		if (genNum == null) {
			return eventList;
		} else {
			List<Event> genList = new ArrayList<Event>();
			List<Node> eventForest = new ArrayList<Node>();
			eventForest = generations((ArrayList<Event>) eventList, eventForest);
			putGenerationsInList(eventForest, genList, 0, req.getGenerations());
			eventList = genList;
		}
		return eventList;
	}
	
	/**
	 * Basically the same as getEvents except doesn't care about createdBefore, 
	 * createdAfter, or generations
	 * 
	 * @param req						has values set to search parameters
	 * @return							most recent event inserted matching search params
	 * @throws IllegalArgumentException	if request data is invalid
	 * @throws EventNotFoundException	if no events exist matching the request params
	 */
	public Event getLatestEvent(EventsRequest req) throws IllegalArgumentException, EventNotFoundException {
		//ensure valid request params
		verifyRequestParameters(req, true);
		
		//build the SQL query
		String eventSql = sqlStringBuilder(req, true);

		//retrieve the list matching request params
		List<Event> eventList = queryWithPs(eventSql, req, true);
		
		//verify some events were retrieved
		if (eventList.size() == 0) {
			throw new EventNotFoundException("No events matching the specified parameters were found in database");
		}
		
		//query limits to one result so there should only be one event in the list
		return eventList.get(0);
	}
	
	/**
	 * 
	 * @param eventSql	SQL string to insert event into DB
	 * @param event		to be inserted into DB
	 * @return
	 */
	private String insertIntoEventStoreTable(String eventSql, Event event){
		String eventId = UUID.randomUUID().toString();
		event.setEventId(eventId);
		event.setInsertTimeStamp(DateTime.now());
		
		jdbcTemplate.update(eventSql,
				event.getEventId(),
				event.getParentId(),
				event.getEventName(),
				event.getObjectId(),
				event.getCorrelationId(),
				event.getSequenceNumber(),
				event.getMessageType(),
				event.getDataType(),
				event.getSource(),
				event.getDestination(),
				event.getSubdestination(),
				event.isReplayIndicator(),
				event.getPublishTimeStamp().getMillis(),
				event.getReceivedTimeStamp().getMillis(),
				event.getExpirationTimeStamp().getMillis(),
				event.getPreEventState(),
				event.getPostEventState(),
				event.getIsPublishable(),
				event.getInsertTimeStamp().getMillis());
		
		return eventId;
	}
	
	/**
	 * 
	 * @param headersSql	SQL string to insert headers into DB
	 * @param event			that holds the headers to be inserted
	 */
	private void insertIntoEventHeadersTable(String headersSql, Event event){
		for(String key : event.getCustomHeaders().keySet()){
			jdbcTemplate.update(headersSql,
					event.getEventId(),
					key,
					event.getCustomHeaders().get(key));
		}
	}
	
	/**
	 * 
	 * @param headersSql	SQL string to insert payload into DB
	 * @param event			that holds the payload to be inserted
	 */
	private void insertIntoEventPayloadTable(String payloadSql, Event event){
		for(String key : event.getCustomPayload().keySet()){
			jdbcTemplate.update(payloadSql,
					event.getEventId(),
					key,
					event.getCustomPayload().get(key).getValue(),
					event.getCustomPayload().get(key).getDataType());
		}
	}
	
	/**
	 * 
	 * @param event		to be published
	 */
	private void attemptPublishEvent(Event event){
		if(event.getIsPublishable() == true){
				
				ctx = new ClassPathXmlApplicationContext("eventservice-beans.xml");
				ctx.registerShutdownHook();

				EventServicePublisher publisher = ctx.getBean("eventPublisherservice", EventServicePublisher.class);
				ctx.close();
				
				publisher.connectPublishers();
				try {
					publisher.publishEventByMapper(event);
				} catch (PeregrineException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("Event: " + event.getEventId()+ " was published");
							
			}
			else{
				System.out.println("Event: " + event.getEventId()+ " was not published");
			}
	}
	
	/**
	 * 
	 * @param req		request parameters
	 * @param latest	determines whether using only params related to getLatestEvent
	 * @return			generations count
	 */
	private Integer verifyRequestParameters(EventsRequest req, boolean latest){
		if(!latest){
			if( req.getCreatedAfter() == null){
    			throw new IllegalArgumentException("A createdAfter date must be specified");
    		}
			if(req.getSource() == null && req.getObjectId() == null && req.getCorrelationId() == null){
				throw new IllegalArgumentException("A source, object id, or correlation id must be specified");
			}
			Integer genNum = req.getGenerations();
			if(genNum != null && genNum < 1) {
				throw new IllegalArgumentException("Invalid value for generations.  Must be greater than 0");
			}
			return genNum;
		} else {
			if (req.getSource() == null) {
				throw new IllegalArgumentException("A source must be specified");
			}
			return -1;
		}
	}
	
	/**
	 * 
	 * @param req		request parameters to determine how the SQL string is built
	 * @param latest	determines whether using only params related to getLatestEvent
	 * @return			SQL string built to get Events
	 */
	private String sqlStringBuilder(EventsRequest req, boolean latest){
		String eventSql = sql.getMultipleEventsById();
		if(!latest){
			eventSql += sql.getReqCreatedAfter();
			if(req.getCreatedBefore() != null){
				eventSql += sql.getReqCreatedBefore();
			}
			if(req.getSource() != null){
				eventSql += sql.getReqSource();
			}
			if(req.getObjectId() != null){
				eventSql += sql.getReqObjectId();
			}
			if(req.getCorrelationId() != null){
				eventSql += sql.getReqCorrelationId();
			}
			if(req.getEventName() != null){
				eventSql += sql.getReqEventName();
			}
		} else {
			eventSql += sql.getReqSource();
			if(req.getObjectId() != null){
				eventSql += sql.getReqObjectId();
				}
			if(req.getCorrelationId() != null){
				eventSql += sql.getReqCorrelationId();
			}
			if(req.getEventName() != null){
				eventSql += sql.getReqEventName();
			}
			eventSql += sql.getLatestEvent();
		}
		return eventSql;
	}

	/**
	 * 
	 * @param eventSql	SQL string built to get Events
	 * @param req		request parameters to be used in the prepared statement
	 * @param latest	determines whether using only params related to getLatestEvent
	 * @return			list of events resulting from running the prepared statement
	 */
	private List<Event> queryWithPs(String eventSql, EventsRequest req, boolean latest) {
		List<Event> eventList = jdbcTemplate.query(eventSql, new PreparedStatementSetter() {
			@Override
			public void setValues(java.sql.PreparedStatement ps) throws SQLException {
				int index = 1;
				// assign values for prepared statement
				if (!latest && req.getCreatedAfter() != null) {
					ps.setLong(index, req.getCreatedAfter().getMillis());
					index++;
				}
				if (!latest && req.getCreatedBefore() != null) {
					ps.setLong(index, req.getCreatedBefore().getMillis());
					index++;
				}
				if (req.getSource() != null) {
					ps.setString(index, req.getSource());
					index++;
				}
				if (req.getObjectId() != null) {
					ps.setString(index, req.getObjectId());
					index++;
				}
				if (req.getCorrelationId() != null) {
					ps.setString(index, req.getCorrelationId());
					index++;
				}
				if (req.getEventName() != null) {
					ps.setString(index, req.getEventName());
					index++;
				}
			}
		}, new EventRowMapper(jdbcTemplate, sql));
		return eventList;
	}
	
	/**
	 * @param events		to be added to some tree
	 * @param eventForest	where the trees will be added to
	 * @return				the final forest
	 */
	private List<Node> generations(ArrayList<Event> events, List<Node> eventForest){
		if(events.isEmpty()){
			return eventForest;
		} else if(eventForest.isEmpty()) {
			eventForest.add(new Node(events.get(0)));
			events.remove(events.get(0));
			return generations(events, eventForest);
		} else {
			for(int index = 0; index < eventForest.size(); index++){
				eventForest.get(index).insertNode(events.get(0),eventForest.get(index));
				if(eventForest.get(index).isInserted()) {
					events.remove(events.get(0));
					return generations(events, eventForest);
				}
			}
			eventForest.add(new Node(events.get(0)));
			events.remove(events.get(0));
			return generations(events, eventForest);
		}
	}
	
	/**
	 * @param eventForest	to be parsed through to find events
	 * @param genList		list of events parsed from the forest
	 * @param genCount		index representing the current generation level
	 * @param maxGens		maximum depth to parse a tree
	 */
	private void putGenerationsInList(List<Node> eventForest, List<Event> genList, int genCount, int maxGens){
		for(Node n : eventForest){
			if(genCount < maxGens){
				genList.add(n.getEvent());
				if(n.getChildren() != null){
					putGenerationsInList(n.getChildren(), genList, ++genCount, maxGens);
				}
			}
		}
	}
	
	/**
	 * Class to map the designated rows from the event_store table into event objects
	 */
	private static class EventRowMapper implements RowMapper<Event> {
		private JdbcTemplate jdbcTemplate;
		private SqlQuery sql;
		public EventRowMapper(JdbcTemplate jdbcTemplate, SqlQuery sql) {
			this.jdbcTemplate = jdbcTemplate;
			this.sql = sql;
		}
		
		/**
		 * 
		 * @param rs				that results from running query
		 * @param index				not used, but part of interface declaration, so needed
		 * @throws SQLException		if there is an issue running the query on the DB
		 */
		public Event mapRow(ResultSet rs, int index) throws SQLException {
			Event event = new Event();
			setNullables(rs, event);
			setRestOfEvent(rs, event);
			setHeadersAndPayload(rs, event);
			return event;
		}
		
		/**
		 * 
		 * @param rs				that results from running query
		 * @param event				to be created from the values in the Result Set
		 * @throws SQLException		if there is an issue running the query on the DB
		 */
		private void setNullables(ResultSet rs, Event event) throws SQLException{
			Integer sequenceNumber = rs.getInt("sequenceNumber");
			if (rs.wasNull()) sequenceNumber = null;
			event.setSequenceNumber(sequenceNumber);
			DateTime publishTimeStamp = new DateTime(rs.getLong("publishTimeStamp"),DateTimeZone.UTC);
			if (rs.wasNull()) publishTimeStamp = null;
			event.setPublishTimeStamp(publishTimeStamp);
			DateTime expirationTimeStamp = new DateTime(rs.getLong("expirationTimeStamp"),DateTimeZone.UTC);
			if (rs.wasNull()) expirationTimeStamp = null;
			event.setExpirationTimeStamp(expirationTimeStamp);
		}
		
		/**
		 * 
		 * @param rs				that results from running query
		 * @param event				to be created from the values in the Result Set
		 * @throws SQLException		if there is an issue running the query on the DB
		 */
		private void setRestOfEvent(ResultSet rs, Event event) throws SQLException{
			event.setEventId(rs.getString("eventId"));
			event.setParentId(rs.getString("parentId"));
			event.setEventName(rs.getString("eventName"));
			event.setObjectId(rs.getString("objectId"));
			event.setCorrelationId(rs.getString("correlationId"));
			event.setMessageType(rs.getString("messageType"));
			event.setDataType(rs.getString("dataType"));
			event.setSource(rs.getString("source"));
			event.setDestination(rs.getString("destination"));
			event.setSubdestination(rs.getString("subdestination"));
			event.setReplayIndicator(rs.getBoolean("replayIndicator"));
			event.setReceivedTimeStamp(new DateTime((rs.getLong("receivedTimeStamp")),DateTimeZone.UTC));
			event.setPreEventState(rs.getString("preEventState"));
			event.setPostEventState(rs.getString("postEventState"));
			event.setIsPublishable(rs.getBoolean("isPublishable"));
			event.setInsertTimeStamp(new DateTime(rs.getLong("insertTimeStamp"),DateTimeZone.UTC));
		}
		
		/**
		 * 
		 * @param rs				that results from running query
		 * @param event				to be created from the values in the Result Set
		 */
		private void setHeadersAndPayload(ResultSet rs, Event event){
			String eventId = event.getEventId();
			String headersSql = sql.getHeader();
			List<String[]> headers = jdbcTemplate.query(headersSql,
				new PreparedStatementSetter() {
					@Override
					public void setValues(java.sql.PreparedStatement ps) throws SQLException {
						ps.setString(1, eventId);	
					}
        		}, new HeaderRowMapper() );
			Map<String,String> customHeaders = new HashMap<String,String>();
			for(String [] header : headers) {
				customHeaders.put(header[0], header[1]);
			}
			
			String payloadSql = sql.getPayload();
			List<String[]> payloads = jdbcTemplate.query(payloadSql,
				new PreparedStatementSetter() {
					@Override
					public void setValues(java.sql.PreparedStatement ps) throws SQLException {
						ps.setString(1, eventId);	
					}
				},new PayloadRowMapper() );
			Map<String,DataItem> customPayload = new HashMap<String,DataItem>();
			for(String [] payload : payloads) {
				customPayload.put(payload[0], new DataItem(payload[1],payload[2]));
			}
		}
	}
	
	/**
	 * Class to map the designated rows from the event_headers table into event objects
	 */
	private static class HeaderRowMapper implements RowMapper<String[]> {
		public String[] mapRow(ResultSet rs, int index) throws SQLException {
			String[] header = new String[3];
			header[0] = rs.getString("name");
			header[1] = rs.getString("value");
			return header;
		}
	}
	
	/**
	 * Class to map the designated rows from the event_payload table into event objects
	 */
	private static class PayloadRowMapper implements RowMapper<String[]> {
		public String[] mapRow(ResultSet rs, int index) throws SQLException {
			String[] payload = new String[3];
			payload[0] = rs.getString("name");
			payload[1] = rs.getString("dataType");
			payload[2] = rs.getString("value");
			return payload;
		}
	}
}