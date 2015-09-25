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
import com.alliancefoundry.model.DataItem;
import com.alliancefoundry.model.Event;
import com.alliancefoundry.model.EventsRequest;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.jdbc.core.RowMapper;

@Component
public class JdbcTemplateDaoImpl implements DAO {
	
	private JdbcTemplate jdbcTemplate;
	private AbstractApplicationContext ctx;
	private SqlQuery sql;
	public JdbcTemplateDaoImpl(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
		ctx = new ClassPathXmlApplicationContext("queries.xml");
		sql = ctx.getBean("sql", SqlQuery.class);
		ctx.close();
	}

	public JdbcTemplateDaoImpl() {
	}

	/**
	 * @param event								the event to be inserted
	 * @return									the eventId of the inserted event
	 * @throws DataIntegrityViolationException	if insertion data is invalid
	 */
	public String insertEvent(Event event) throws DataIntegrityViolationException {
		String eventSql = sql.getInsertSingleEvent();
		String headersSql = sql.getInsertHeader();
		String payloadSql = sql.getInsertPayload();
		String eventId = UUID.randomUUID().toString();
		event.setEventId(eventId);
		event.setInsertTimeStamp(DateTime.now());
		try{
			//insert into event_store
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
			
			//insert into event_headers table
			for(String key : event.getCustomHeaders().keySet()){
				jdbcTemplate.update(headersSql,
						event.getEventId(),
						key,
						event.getCustomHeaders().get(key));
			}
		
			//insert into event_payload table
			for(String key : event.getCustomPayload().keySet()){
				jdbcTemplate.update(payloadSql,
						event.getEventId(),
						key,
						event.getCustomPayload().get(key).getValue(),
						event.getCustomPayload().get(key).getDataType());
			}
			return eventId;
		} catch(DataIntegrityViolationException e) {
			throw e;
		}
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

				//insert into event_headers
				for (String key : event.getCustomHeaders().keySet()) {
					jdbcTemplate.update(headersSql, 
							event.getEventId(), 
							key, 
							event.getCustomHeaders().get(key));
				}

				//insert into event_payload
				for (String key : event.getCustomPayload().keySet()) {
					jdbcTemplate.update(payloadSql, 
							event.getEventId(), 
							key, 
							event.getCustomPayload().get(key).getValue(),
							event.getCustomPayload().get(key).getDataType());
				}
				eventIds.add(eventId);
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
	public List<Event> getEvents(EventsRequest req) throws IllegalArgumentException, EventNotFoundException{
		if( req.getCreatedAfter() == null){
    		throw new IllegalArgumentException("A createdAfter date must be specified");
    	}
		if(req.getSource() == null && req.getObjectId() == null && req.getCorrelationId() == null){
			throw new IllegalArgumentException("A source, object id, or correlation id must be specified");
		}
		
		//build the SQL query
		String eventSql = sql.getMultipleEventsById();
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
		
		List<Event> eventList = jdbcTemplate.query(eventSql, new PreparedStatementSetter() {
			@Override
			public void setValues(java.sql.PreparedStatement ps) throws SQLException {
				int index = 1;
				//assign values for prepared statement
				if (req.getCreatedAfter() != null) {
					ps.setLong(index, req.getCreatedAfter().getMillis());
					index++;
				}
				if (req.getCreatedBefore() != null) {
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
		if (eventList.size() == 0) {
			throw new EventNotFoundException("No events matching the specified parameters were found in database");
		}
		//get the events based off of generation count if one is specified
		Integer genNum = req.getGenerations();
		if (genNum != null && genNum > 0) {
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
		if (req.getSource() == null) {
			throw new IllegalArgumentException("A source must be specified");
		}
		//build the SQL query
		String eventSql = sql.getMultipleEventsById();
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

		List<Event> eventList = jdbcTemplate.query(eventSql, new PreparedStatementSetter() {
			@Override
			public void setValues(java.sql.PreparedStatement ps) throws SQLException {
				int index = 1;
				//assign values for the prepared statement
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
		if (eventList.size() == 0) {
			throw new EventNotFoundException("No events matching the specified parameters were found in database");
		}
		//query limits to one result so there should only be one event in the list
		return eventList.get(0);
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
				if(eventForest.get(index).insertNode(events.get(0))) {
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
					putGenerationsInList(n.getChildren(), genList, genCount + 1, maxGens);
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
		public Event mapRow(ResultSet rs, int index) throws SQLException {
			Integer sequenceNumber = rs.getInt("sequenceNumber");
			if (rs.wasNull()) sequenceNumber = null;
			DateTime publishTimeStamp = new DateTime(rs.getLong("publishTimeStamp"),DateTimeZone.UTC);
			if (rs.wasNull()) publishTimeStamp = null;
			DateTime expirationTimeStamp = new DateTime(rs.getLong("expirationTimeStamp"),DateTimeZone.UTC);
			if (rs.wasNull()) expirationTimeStamp = null;
			
			String eventId = rs.getString("eventId");
			String parentId = rs.getString("parentId");
			String eventName = rs.getString("eventName");
			String objectId = rs.getString("objectId");
			String correlationId = rs.getString("correlationId");
			String messageType = rs.getString("messageType");
			String dataType = rs.getString("dataType");
			String source = rs.getString("source");
			String destination = rs.getString("destination");
			String subdestination = rs.getString("subdestination");
			boolean replayIndicator = rs.getBoolean("replayIndicator");
			DateTime receivedTimeStamp = new DateTime((rs.getLong("receivedTimeStamp")),DateTimeZone.UTC);
			String preEventState = rs.getString("preEventState");
			String postEventState = rs.getString("postEventState");
			boolean isPublishable = rs.getBoolean("isPublishable");
			DateTime insertTimeStamp = new DateTime(rs.getLong("insertTimeStamp"),DateTimeZone.UTC);
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
			
			Event e = new Event(
				parentId,
				eventName,
				objectId,
				correlationId,
				sequenceNumber,
				messageType,
				dataType,
				source,
				destination,
				subdestination,
				replayIndicator,
				publishTimeStamp,
				receivedTimeStamp,
				expirationTimeStamp,
				preEventState,
				postEventState,
				isPublishable,
				insertTimeStamp
				);
			e.setEventId(eventId);
			e.setCustomHeaders(customHeaders);
			e.setCustomPayload(customPayload);
			return e;
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