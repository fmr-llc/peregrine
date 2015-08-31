package com.alliancefoundry.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

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
	//Returns -1 if insert failed.
	public long insertEvent(Event event) {
		getConnection();
		String sql = "INSERT INTO events VALUES ( NULL,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,? )";
		try {
			PreparedStatement ps = (PreparedStatement) conn.prepareStatement(sql);
			//set the value of each column for the row being inserted other
			//than eventId
			ps.setString(1, event.getParentId());
			ps.setString(2, event.getEventName());
			ps.setString(3, event.getObjectId());
			ps.setString(4, event.getCorrelationId());
			ps.setString(5, event.getSequenceNumber());
			ps.setString(6, event.getMessageType());
			ps.setString(7, event.getDataType());
			ps.setString(8, event.getSource());
			ps.setString(9, event.getDestination());
			ps.setString(10, event.getSubdestination());
			ps.setBoolean(11, event.isReplayIndicator());
			ps.setLong(12, event.getPublishedTimeStamp().getMillis());
			ps.setLong(13, event.getReceivedTimeStamp().getMillis());
			ps.setLong(14, event.getExpirationTimeStamp().getMillis());
			ps.setString(15, event.getPreEventState());
			ps.setString(16, event.getPostEventState());
			ps.setBoolean(17, event.isPublishable());
			ps.setLong(18, event.getInsertTimeStamp().getMillis());
			
			ps.executeUpdate();
			
			String getIdSql = "SELECT LAST_INSERT_ID()";
			PreparedStatement ps2 = (PreparedStatement) conn.prepareStatement(getIdSql);
			ResultSet rs = ps2.executeQuery();
			
			//get to start of resultSet
			rs.next();

			//use '1' as index because there should be only one
			//value in the result set, the eventId of the row
			//that was inserted
			long eventId = rs.getLong(1);
			
			return eventId;
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		} finally{
			endConnection();
		}
	}

	//Retrieve an event from the database into an Event object
	//that is returned.  Return null if the requested Event
	//object is not found in the database.
	public Event getEvent(long eventId) {
		getConnection();
		String sql = "SELECT * FROM events WHERE eventId = ?";
		try {
			PreparedStatement ps = (PreparedStatement) conn.prepareStatement(sql);
			
			//set the value being checked for equality
			ps.setLong(1, eventId);
			
			ResultSet rs = ps.executeQuery();
			Event event = new Event();
			
			//get to start of resultSet
			rs.next();
			
			event = new Event(
					rs.getString("parentId"),
					rs.getString("eventName"),
					rs.getString("objectId"),
					rs.getString("correlationId"),
					rs.getString("sequenceNumber"),
					rs.getString("messageType"),
					rs.getString("dataType"),
					rs.getString("source"),
					rs.getString("destination"),
					rs.getString("subdestination"),
					rs.getBoolean("replayIndicator"),
					new DateTime(rs.getLong("publishedTimeStamp")),
					new DateTime(rs.getLong("receivedTimeStamp")),
					new DateTime(rs.getLong("expirationTimeStamp")),
					rs.getString("preEventState"),
					rs.getString("postEventState"),
					rs.getBoolean("isPublishable"),
					new DateTime(rs.getLong("insertTimeStamp"))
				);
			event.setEventId(rs.getLong("eventId"));
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
		getConnection();
		String sql = "SELECT * FROM events "
				+ "WHERE publishedTimeStamp > ? "
				//+ "AND publishedTimeStamp < ? "
				+ "AND source = ? "
				+ "AND objectId = ? "
				+ "AND correlationId = ? "
				+ "AND eventName = ? ";
		try {
			PreparedStatement ps = (PreparedStatement) conn.prepareStatement(sql);

			// set the value being checked for equality
			ps.setLong(1, 0);
			//ps.setLong(2, );
			ps.setString(2, req.getSource());
			ps.setString(3, req.getObjectId());
			ps.setString(4, req.getCorrelationId());
			ps.setString(5, req.getName());
			

			ResultSet rs = ps.executeQuery();

			// get to start of resultSet
			while (rs.next()) {
				Event e = new Event(rs.getString("parentId"), rs.getString("eventName"), rs.getString("objectId"),
						rs.getString("correlationId"), rs.getString("sequenceNumber"), rs.getString("messageType"),
						rs.getString("dataType"), rs.getString("source"), rs.getString("destination"),
						rs.getString("subdestination"), rs.getBoolean("replayIndicator"),
						new DateTime(rs.getLong("publishedTimeStamp")), new DateTime(rs.getLong("receivedTimeStamp")),
						new DateTime(rs.getLong("expirationTimeStamp")), rs.getString("preEventState"),
						rs.getString("postEventState"), rs.getBoolean("isPublishable"),
						new DateTime(rs.getLong("insertTimeStamp")));
				e.setEventId(rs.getLong("eventId"));
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
