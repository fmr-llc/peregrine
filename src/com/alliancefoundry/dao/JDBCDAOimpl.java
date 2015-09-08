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

public class JDBCDAOimpl {
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
			UUID uuid = UUID.randomUUID();
			String eventId = uuid.toString();
			PreparedStatement ps = (PreparedStatement) conn.prepareStatement(sql);
			//set the value of each column for the row being inserted other
			//than eventId
			ps.setString(1, eventId);
			ps.setString(2, event.getParentId());
			ps.setString(3, event.getEventName());
			ps.setString(4, event.getObjectId());
			ps.setString(5, event.getCorrelationId());
			try{
				ps.setInt(6, event.getSequenceNumber());
			} catch(NullPointerException e){
				ps.setNull(6, 0);
			}
			ps.setString(7, event.getMessageType());
			ps.setString(8, event.getDataType());
			ps.setString(9, event.getSource());
			ps.setString(10, event.getDestination());
			ps.setString(11, event.getSubdestination());
			try{
				ps.setBoolean(12, event.isReplayIndicator());
			} catch(NullPointerException e){
				ps.setNull(12, 0);
			}
			try {
				ps.setLong(13, event.getPublishTimeStamp().getMillis());
			} catch(NullPointerException e) {
				ps.setNull(13, 0);
			}
			ps.setLong(14, event.getReceivedTimeStamp().getMillis());
			try{
				ps.setLong(15, event.getExpirationTimeStamp().getMillis());
			} catch(NullPointerException e){
				ps.setNull(15, 0);
			}
			ps.setString(16, event.getPreEventState());
			ps.setString(17, event.getPostEventState());
			try{
				ps.setBoolean(18, event.isPublishable());
			}catch(NullPointerException e){
				ps.setNull(18, 0);
			}
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
			throw e;
		} finally{
			endConnection();
		}
	}

	//Retrieve an event from the database into an Event object
	//that is returned.  Return null if the requested Event
	//object is not found in the database.
	public Event getEvent(String eventId) {
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
			
			DateTime publish = new DateTime(rs.getLong("publishTimeStamp"));
			if (rs.wasNull()) publish = null;
			DateTime expiration = new DateTime(rs.getLong("expirationTimeStamp"));
			if (rs.wasNull()) expiration = null;
			
			event = new Event(
					rs.getString("parentId"),
					rs.getString("eventName"),
					rs.getString("objectId"),
					rs.getString("correlationId"),
					rs.getInt("sequenceNumber"),
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
			return null;
		} finally{
			endConnection();
		}
	}
	
	// Retrieve multiple events from the database based off of an EventsRequest object
	//into a list of Event objects that is returned.
	public List<Event> getEvents(EventsRequest req) {
		List<Event> eventList = new ArrayList<Event>();
		if( req.getCreatedAfter() == null || req.getCreatedAfter().equals("")){
    		throw new IllegalArgumentException("A createdAfter date must be specified.");
    	}
		getConnection();
		String reqCreatedAfter = "AND publishTimeStamp > ? ";
		String reqCreatedBefore = "AND publishTimeStamp < ? ";
		String reqSource = "AND source = ? ";
		String reqObjectId = "AND objectId = ? ";
		String reqCorrelationId = "AND correlationId = ? ";
		String reqEventName = "AND eventName = ? ";
		//TODO: provide for request with generations count
		
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
		if(req.getName()!= null){
			sql += reqEventName;
		}
		
		/*String headersSql = "SELECT name,value FROM event_headers WHERE eventId = ?";
		String payloadSql = "SELECT name,value FROM event_payload WHERE eventId = ?";*/
		
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
			if(req.getName()!= null){
				ps.setString(index, req.getName());
				index++;
			}

			ResultSet rs = ps.executeQuery();

			// get to start of resultSet
			while (rs.next()) {
				String eventId = rs.getString("eventId");
				String parentId = rs.getString("parentId");
				String eventName = rs.getString("eventName");
				String objectId = rs.getString("objectId");
				String correlationId = rs.getString("correlationId");
				int sequenceNumber = rs.getInt("sequenceNumber");
				String messageType = rs.getString("messageType");
				String dataType = rs.getString("dataType");
				String source = rs.getString("source");
				String destination = rs.getString("destination");
				String subdestination = rs.getString("subdestination");
				boolean replayIndicator = rs.getBoolean("replayIndicator");
				DateTime publishTimeStamp = new DateTime(rs.getLong("publishTimeStamp"));
				DateTime receivedTimeStamp = new DateTime(rs.getLong("receivedTimeStamp"));
				DateTime expirationTimeStamp = new DateTime(rs.getLong("expirationTimeStamp"));
				String preEventState = rs.getString("preEventState");
				String postEventState = rs.getString("postEventState");
				boolean isPublishable = rs.getBoolean("isPublishable");
				DateTime insertTimeStamp = new DateTime(rs.getLong("insertTimeStamp"));
				
				/*PreparedStatement psHeaders = (PreparedStatement) conn.prepareStatement(headersSql);
				
				//set the value being checked for equality
				psHeaders.setLong(1, eventId);
				
				ResultSet rsHeaders = psHeaders.executeQuery();
				Map<String,String> customHeaders = new HashMap<String,String>();
				
				//get to start of resultSet
				rsHeaders.next();
				while(rsHeaders.next()){
					customHeaders.put(rsHeaders.getString("name"), rsHeaders.getString("value"));
				}
				
				PreparedStatement psPayload = (PreparedStatement) conn.prepareStatement(payloadSql);
				
				//set the value being checked for equality
				psPayload.setLong(1, eventId);
				
				ResultSet rsPayload = psPayload.executeQuery();
				Map<String,String> customPayload = new HashMap<String,String>();
				
				//get to start of resultSet
				rsPayload.next();
				while(rsPayload.next()){
					customPayload.put(rsPayload.getString("name"), rsPayload.getString("value"));
				}*/
				
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
					/*customHeaders,
					customPayload,*/
					preEventState,
					postEventState,
					isPublishable,
					insertTimeStamp
					);
				e.setEventId(eventId);
				eventList.add(e);
			}
			return eventList;
		} catch (SQLException e) {
			// all events couldn't be retrieved so return ones that could
			return eventList;
		} finally {
			endConnection();
		}
	}
}
