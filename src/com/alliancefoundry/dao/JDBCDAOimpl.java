package com.alliancefoundry.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.joda.time.DateTime;

import com.alliancefoundry.model.DataItem;
import com.alliancefoundry.model.Event;
import com.alliancefoundry.model.EventsRequest;
import com.mysql.jdbc.PreparedStatement;

public class JDBCDAOimpl implements DAO {
	static Connection conn;
	
	private static void getConnection(){
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(
					"jdbc:mysql://127.0.0.1/eventdb",
					"root", "root");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static void endConnection(){
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Connection was never open!");
		}
	}

	//Insert an Event object into the database using a prepared statement
	//and return the event id of the Event object that was inserted.
	//Returns null if insert failed.
	public String insertEvent(Event event) throws SQLException {
		getConnection();
		String sql = "INSERT INTO event_store VALUES ( ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,? )";
		String headersSql = "INSERT INTO event_headers VALUES ( ?,?,? )";
		String payloadSql = "INSERT INTO event_payload VALUES ( ?,?,?,? )";
		try {
			String eventId = UUID.randomUUID().toString();
			PreparedStatement ps = (PreparedStatement) conn.prepareStatement(sql);
			//set the value of each column for the row being inserted other
			//than eventId
			ps.setString(1, eventId);
			ps.setString(2, event.getParentId());
			ps.setString(3, event.getEventName());
			ps.setString(4, event.getObjectId());
			ps.setString(5, event.getCorrelationId());
			//in order to set sequence number as null, you
			//have to declare a variable for it first
			Integer seqNum = event.getSequenceNumber();
			if(seqNum != null) {
				ps.setInt(6, seqNum);
			} else {
				ps.setNull(6, 0);
			}
			
			ps.setString(7, event.getMessageType());
			ps.setString(8, event.getDataType());
			ps.setString(9, event.getSource());
			ps.setString(10, event.getDestination());
			ps.setString(11, event.getSubdestination());
			ps.setBoolean(12, event.isReplayIndicator());
			//in order to set a time stamp as null, you
			//have to declare a variable for it first
			DateTime pubTime = event.getPublishTimeStamp();
			if(pubTime != null){
				ps.setLong(13, pubTime.getMillis());
			} else {
				ps.setNull(13, 0);
			}
			
			ps.setLong(14, event.getReceivedTimeStamp().getMillis());
			
			DateTime expTime = event.getExpirationTimeStamp();
			if(expTime != null){
				ps.setLong(15, expTime.getMillis());
			} else {
				ps.setNull(15, 0);
			}
			
			ps.setString(16, event.getPreEventState());
			ps.setString(17, event.getPostEventState());
			ps.setBoolean(18, event.getIsPublishable());
			event.setInsertTimeStamp(DateTime.now());
			ps.setLong(19, event.getInsertTimeStamp().getMillis());
			
			ps.executeUpdate();
			
			PreparedStatement headersPs = (PreparedStatement) conn.prepareStatement(headersSql);
			
			//insert header info into its table
			for(String key : event.getCustomHeaders().keySet()){
				headersPs.setString(1, eventId);
				headersPs.setString(2, key);
				headersPs.setString(3, event.getCustomHeaders().get(key));
				headersPs.executeUpdate();
			}
			
			PreparedStatement payloadPs = (PreparedStatement) conn.prepareStatement(payloadSql);
			
			//insert payload info into its table
			for(String key : event.getCustomPayload().keySet()){
				payloadPs.setString(1, eventId);
				payloadPs.setString(2, key);
				payloadPs.setString(3, event.getCustomPayload().get(key).getValue());
				payloadPs.setString(4, event.getCustomPayload().get(key).getDataType());
				payloadPs.executeUpdate();
			}
			
			return eventId;
		} catch (SQLException e) {
			//event could not be inserted
			throw e;
		} finally{
			endConnection();
		}
	}

	// Insert an Event object into the database using a prepared statement
	// and return the event id of the Event object that was inserted.
	// Returns null if insert failed.
	public List<String> insertEvents(List<Event> events) throws SQLException {
		List<String> eventIds = new ArrayList<String>();
		getConnection();
		conn.setAutoCommit(false);
		try {
			for (Event event : events) {
				String sql = "INSERT INTO event_store VALUES ( ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,? )";
				String headersSql = "INSERT INTO event_headers VALUES ( ?,?,? )";
				String payloadSql = "INSERT INTO event_payload VALUES ( ?,?,?,? )";
				String eventId = UUID.randomUUID().toString();
				PreparedStatement ps = (PreparedStatement) conn.prepareStatement(sql);
				// set the value of each column for the row being inserted other
				// than eventId
				ps.setString(1, eventId);
				ps.setString(2, event.getParentId());
				ps.setString(3, event.getEventName());
				ps.setString(4, event.getObjectId());
				ps.setString(5, event.getCorrelationId());
				// in order to set sequence number as null, you
				// have to declare a variable for it first
				Integer seqNum = event.getSequenceNumber();
				if (seqNum != null) {
					ps.setInt(6, seqNum);
				} else {
					ps.setNull(6, 0);
				}

				ps.setString(7, event.getMessageType());
				ps.setString(8, event.getDataType());
				ps.setString(9, event.getSource());
				ps.setString(10, event.getDestination());
				ps.setString(11, event.getSubdestination());
				ps.setBoolean(12, event.isReplayIndicator());
				// in order to set a time stamp as null, you
				// have to declare a variable for it first
				DateTime pubTime = event.getPublishTimeStamp();
				if (pubTime != null) {
					ps.setLong(13, pubTime.getMillis());
				} else {
					ps.setNull(13, 0);
				}

				ps.setLong(14, event.getReceivedTimeStamp().getMillis());

				DateTime expTime = event.getExpirationTimeStamp();
				if (expTime != null) {
					ps.setLong(15, expTime.getMillis());
				} else {
					ps.setNull(15, 0);
				}

				ps.setString(16, event.getPreEventState());
				ps.setString(17, event.getPostEventState());
				ps.setBoolean(18, event.getIsPublishable());
				event.setInsertTimeStamp(DateTime.now());
				ps.setLong(19, event.getInsertTimeStamp().getMillis());

				ps.executeUpdate();

				PreparedStatement headersPs = (PreparedStatement) conn.prepareStatement(headersSql);

				// insert header info into its table
				for (String key : event.getCustomHeaders().keySet()) {
					headersPs.setString(1, eventId);
					headersPs.setString(2, key);
					headersPs.setString(3, event.getCustomHeaders().get(key));
					headersPs.executeUpdate();
				}

				PreparedStatement payloadPs = (PreparedStatement) conn.prepareStatement(payloadSql);

				// insert payload info into its table
				for (String key : event.getCustomPayload().keySet()) {
					payloadPs.setString(1, eventId);
					payloadPs.setString(2, key);
					payloadPs.setString(3, event.getCustomPayload().get(key).getValue());
					payloadPs.setString(4, event.getCustomPayload().get(key).getDataType());
					payloadPs.executeUpdate();
				}
				eventIds.add(eventId);
			}
			conn.commit();
			return eventIds;
		} catch (SQLException e) {
			//one of the events could not be inserted, so rollback any previous commits
			conn.rollback();
			throw e;
		} finally {
			conn.setAutoCommit(true);
			endConnection();
		}
	}
	
	//Retrieve an event from the database into an Event object
	//that is returned.  Return null if the requested Event
	//object is not found in the database.
	public Event getEvent(String eventId) throws SQLException {
		getConnection();
		String sql = "SELECT * FROM event_store WHERE eventId = ?";
		String headersSql = "SELECT name,value FROM event_headers WHERE eventId = ?";
		String payloadSql = "SELECT name,value,dataType FROM event_payload WHERE eventId = ?";
		try {
			PreparedStatement ps = (PreparedStatement) conn.prepareStatement(sql);
			
			//set the value of eventId being checked for equality
			ps.setString(1, eventId);
			
			ResultSet rs = ps.executeQuery();
			Event event;
			
			//get to start of resultSet
			rs.next();
			
			Integer seq = rs.getInt("sequenceNumber");
			if (rs.wasNull()) seq = null;
			DateTime publish = new DateTime(rs.getLong("publishTimeStamp"));
			if (rs.wasNull()) publish = null;
			DateTime expiration = new DateTime(rs.getLong("expirationTimeStamp"));
			if (rs.wasNull()) expiration = null;
			
			event = new Event(
					rs.getString("parentId"),
					rs.getString("eventName"),
					rs.getString("objectId"),
					rs.getString("correlationId"),
					seq,
					rs.getString("messageType"),
					rs.getString("dataType"),
					rs.getString("source"),
					rs.getString("destination"),
					rs.getString("subdestination"),
					rs.getBoolean("replayIndicator"),
					publish,
					new DateTime(rs.getLong("receivedTimeStamp")),
					expiration,
					rs.getString("preEventState"),
					rs.getString("postEventState"),
					rs.getBoolean("isPublishable"),
					new DateTime(rs.getLong("insertTimeStamp"))
				);
			event.setEventId(rs.getString("eventId"));
			
			PreparedStatement psHeaders = (PreparedStatement) conn.prepareStatement(headersSql);
			
			//set the value being checked for equality
			psHeaders.setString(1, eventId);
			
			ResultSet rsHeaders = psHeaders.executeQuery();
			Map<String,String> customHeaders = new HashMap<String,String>();
			
			//get header info from its table
			while(rsHeaders.next()){
				customHeaders.put(rsHeaders.getString("name"), rsHeaders.getString("value"));
			}
			
			PreparedStatement psPayload = (PreparedStatement) conn.prepareStatement(payloadSql);
			
			//set the value being checked for equality
			psPayload.setString(1, eventId);
			
			ResultSet rsPayload = psPayload.executeQuery();
			Map<String,DataItem> customPayload = new HashMap<String,DataItem>();
			
			//get payload info from its table
			while(rsPayload.next()){
				String payName = rsPayload.getString("name");
				String payType = rsPayload.getString("dataType");
				String payVal = rsPayload.getString("value");
				customPayload.put(payName, new DataItem(payType,payVal));
			}
			
			event.setCustomHeaders(customHeaders);
			event.setCustomPayload(customPayload);
			return event;
		} catch (SQLException e) {
			//event couldn't be retrieved
			throw e;
		} finally{
			endConnection();
		}
	}
	
	//Retrieve multiple events from the database based off of an EventsRequest object
	//into a list of Event objects that is returned.
	public List<Event> getEvents(EventsRequest req) throws IllegalArgumentException, SQLException{
		List<Event> eventList = new ArrayList<Event>();
		if( req.getCreatedAfter() == null){
    		throw new IllegalArgumentException("A createdAfter date must be specified");
    	}
		if(req.getSource() == null && req.getObjectId() == null && req.getCorrelationId() == null){
			throw new IllegalArgumentException("A source, object id, or correlation id must be specified");
		}
		getConnection();
		String reqCreatedAfter = "AND insertTimeStamp > ? ";
		String reqCreatedBefore = "AND insertTimeStamp < ? ";
		String reqSource = "AND source = ? ";
		String reqObjectId = "AND objectId = ? ";
		String reqCorrelationId = "AND correlationId = ? ";
		String reqEventName = "AND eventName = ? ";
		
		String sql = "SELECT * FROM event_store WHERE TRUE ";
		
		if(req.getCreatedAfter() != null){
			sql += reqCreatedAfter;
		}
		if(req.getCreatedBefore() != null){
			sql += reqCreatedBefore;
		}
		if(req.getSource() != null){
			sql += reqSource;
		}
		if(req.getObjectId() != null){
			sql += reqObjectId;
		}
		if(req.getCorrelationId() != null){
			sql += reqCorrelationId;
		}
		if(req.getName() != null){
			sql += reqEventName;
		}
		
		String headersSql = "SELECT name,value FROM event_headers WHERE eventId = ?";
		String payloadSql = "SELECT name,value,dataType FROM event_payload WHERE eventId = ?";
		
		try {
			PreparedStatement ps = (PreparedStatement) conn.prepareStatement(sql);
			int index = 1;
			if(req.getCreatedAfter() != null){
				ps.setLong(index, req.getCreatedAfter().getMillis());
				index++;
			}
			if(req.getCreatedBefore() != null){
				ps.setLong(index, req.getCreatedBefore().getMillis());
				index++;
			}
			if(req.getSource() != null){
				ps.setString(index, req.getSource());
				index++;
			}
			if(req.getObjectId() != null){
				ps.setString(index, req.getObjectId());
				index++;
			}
			if(req.getCorrelationId() != null){
				ps.setString(index, req.getCorrelationId());
				index++;
			}
			if(req.getName() != null){
				ps.setString(index, req.getName());
				index++;
			}
			ResultSet rs = ps.executeQuery();

			// get to start of resultSet
			while (rs.next()) {
				
				Integer sequenceNumber = rs.getInt("sequenceNumber");
				if (rs.wasNull()) sequenceNumber = null;
				DateTime publishTimeStamp = new DateTime(rs.getLong("publishTimeStamp"));
				if (rs.wasNull()) publishTimeStamp = null;
				DateTime expirationTimeStamp = new DateTime(rs.getLong("expirationTimeStamp"));
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
				DateTime receivedTimeStamp = new DateTime(rs.getLong("receivedTimeStamp"));
				String preEventState = rs.getString("preEventState");
				String postEventState = rs.getString("postEventState");
				boolean isPublishable = rs.getBoolean("isPublishable");
				DateTime insertTimeStamp = new DateTime(rs.getLong("insertTimeStamp"));
				
				PreparedStatement psHeaders = (PreparedStatement) conn.prepareStatement(headersSql);
				
				//set the value being checked for equality
				psHeaders.setString(1, eventId);
				
				ResultSet rsHeaders = psHeaders.executeQuery();
				Map<String,String> customHeaders = new HashMap<String,String>();
				
				//get to start of resultSet
				rsHeaders.next();
				while(rsHeaders.next()){
					customHeaders.put(rsHeaders.getString("name"), rsHeaders.getString("value"));
				}
				
				PreparedStatement psPayload = (PreparedStatement) conn.prepareStatement(payloadSql);
				
				//set the value being checked for equality
				psPayload.setString(1, eventId);
				
				ResultSet rsPayload = psPayload.executeQuery();
				Map<String,DataItem> customPayload = new HashMap<String,DataItem>();
				
				//get payload info from its table
				while(rsPayload.next()){
					String payName = rsPayload.getString("name");
					String payType = rsPayload.getString("dataType");
					String payVal = rsPayload.getString("value");
					customPayload.put(payName, new DataItem(payType,payVal));
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
				eventList.add(e);
			}
			Integer genNum = req.getGenerations();
			if(genNum != null && genNum > 0){
				List<Event> genList = new ArrayList<Event>();
				List<Node> eventForest = new ArrayList<Node>();
				eventForest = generations((ArrayList<Event>) eventList, eventForest);
				putGenerationsInList(eventForest,genList,0,req.getGenerations());
				eventList = genList;
			}
			return eventList;
		} catch (SQLException e) {
			// all events couldn't be retrieved
			throw e;
		} finally {
			endConnection();
		}
	}
	
	//Put a list of Events into a tree
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
	
	//Parse through a tree of Nodes and put the events within them into a list of Events
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
	
	//Retrieve the most recent event inserted into the database based 
	//off of an EventsRequest object.  Basically the same as getEvents
	//except doesn't care about createdBefore, createdAfter, or generations
	public Event getLatestEvent(EventsRequest req) throws SQLException {
		Event event;
		getConnection();
		String reqSource = "AND source = ? ";
		String reqObjectId = "AND objectId = ? ";
		String reqCorrelationId = "AND correlationId = ? ";
		String reqEventName = "AND eventName = ? ";
		
		String sql = "SELECT * FROM event_store WHERE TRUE ";
		
		if(req.getSource() != null){
			sql += reqSource;
		}
		if(req.getObjectId() != null){
			sql += reqObjectId;
		}
		if(req.getCorrelationId() != null){
			sql += reqCorrelationId;
		}
		if(req.getName() != null){
			sql += reqEventName;
		}
		
		sql += "ORDER BY insertTimeStamp DESC LIMIT 1";
		
		String headersSql = "SELECT name,value FROM event_headers WHERE eventId = ?";
		String payloadSql = "SELECT name,value,dataType FROM event_payload WHERE eventId = ?";
		
		try {
			PreparedStatement ps = (PreparedStatement) conn.prepareStatement(sql);
			int index = 1;
			if(req.getSource() != null){
				ps.setString(index, req.getSource());
				index++;
			}
			if(req.getObjectId() != null){
				ps.setString(index, req.getObjectId());
				index++;
			}
			if(req.getCorrelationId() != null){
				ps.setString(index, req.getCorrelationId());
				index++;
			}
			if(req.getName() != null){
				ps.setString(index, req.getName());
				index++;
			}
			ResultSet rs = ps.executeQuery();

			// get to start of resultSet
			rs.next();

			Integer sequenceNumber = rs.getInt("sequenceNumber");
			if (rs.wasNull())
				sequenceNumber = null;
			DateTime publishTimeStamp = new DateTime(rs.getLong("publishTimeStamp"));
			if (rs.wasNull())
				publishTimeStamp = null;
			DateTime expirationTimeStamp = new DateTime(rs.getLong("expirationTimeStamp"));
			if (rs.wasNull())
				expirationTimeStamp = null;

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
			DateTime receivedTimeStamp = new DateTime(rs.getLong("receivedTimeStamp"));
			String preEventState = rs.getString("preEventState");
			String postEventState = rs.getString("postEventState");
			boolean isPublishable = rs.getBoolean("isPublishable");
			DateTime insertTimeStamp = new DateTime(rs.getLong("insertTimeStamp"));

			PreparedStatement psHeaders = (PreparedStatement) conn.prepareStatement(headersSql);

			// set the value being checked for equality
			psHeaders.setString(1, eventId);

			ResultSet rsHeaders = psHeaders.executeQuery();
			Map<String, String> customHeaders = new HashMap<String, String>();

			// get to start of resultSet
			rsHeaders.next();
			while (rsHeaders.next()) {
				customHeaders.put(rsHeaders.getString("name"), rsHeaders.getString("value"));
			}

			PreparedStatement psPayload = (PreparedStatement) conn.prepareStatement(payloadSql);

			// set the value being checked for equality
			psPayload.setString(1, eventId);

			ResultSet rsPayload = psPayload.executeQuery();
			Map<String, DataItem> customPayload = new HashMap<String, DataItem>();

			// get payload info from its table
			while (rsPayload.next()) {
				String payName = rsPayload.getString("name");
				String payType = rsPayload.getString("dataType");
				String payVal = rsPayload.getString("value");
				customPayload.put(payName, new DataItem(payType, payVal));
			}

			event = new Event(
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
			event.setEventId(eventId);
			event.setCustomHeaders(customHeaders);
			event.setCustomPayload(customPayload);

			return event;
		} catch (SQLException e) {
			// no event could be retrieved with the specified parameters
			throw e;
		} finally {
			endConnection();
		}
	}
	
}