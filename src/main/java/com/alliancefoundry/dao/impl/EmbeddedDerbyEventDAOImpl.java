package com.alliancefoundry.dao.impl;

import com.alliancefoundry.dao.DAOException;
import com.alliancefoundry.dao.EventDAO;
import com.alliancefoundry.model.*;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmbeddedDerbyEventDAOImpl implements EventDAO {

	private boolean schemaInit = false;
	private DataSource dataSource;
	private static final Logger log = LoggerFactory.getLogger(EmbeddedDerbyEventDAOImpl.class);

	public EmbeddedDerbyEventDAOImpl(DataSource dataSource){
		this.dataSource = dataSource;
	}


	public boolean initialize() throws DAOException {

		Connection conn = null;

		try {
			conn  = dataSource.getConnection();
			validateDB(conn);

			return true;
		} catch (SQLException e){
			log.error("problem initializing DAO.", e);
		} finally{
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					log.error("attempted to close connection. " + e.getMessage());
				}
			}
		}

		return true;
	}


	/**
	 * Deallocate the DAO resources.
	 *
	 * @return true if resources are releases successfully
	 * @throws DAOException
	 */
	public boolean shutdown() throws DAOException{
		return true;
	}


	private boolean schemaExists(Connection conn){

		String sql = "SELECT * FROM SYS.SYSSCHEMAS";
		try{
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			while(rs.next()){
				log.debug("schema found with name: " + rs.getString(2));
				if (rs.getString(1).equalsIgnoreCase("event")) return true;
			}

			log.debug("no schemas found with correct name.");
			return false;

		} catch (SQLException e){
			log.debug("could not determine if schema exists.");
			return false;
		}

	}


	private boolean createSchema(Connection conn){

		String sql = "CREATE SCHEMA event";

		try {
			// create table
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.execute();

			schemaInit = true;
			return true;

		} catch (SQLException e){
			log.debug("schema already exists. ");
			return true;
		}

	}

	private boolean createTables(Connection conn){

		String tableSql1 = "CREATE TABLE event.event_store (\n" +
				"  eventId varchar(45) NOT NULL,\n" +
				"  parentId varchar(45) DEFAULT NULL,\n" +
				"  eventName varchar(45) DEFAULT NULL,\n" +
				"  objectId varchar(45) NOT NULL,\n" +
				"  correlationId varchar(45) DEFAULT NULL,\n" +
				"  sequenceNumber int DEFAULT NULL,\n" +
				"  messageType varchar(45) NOT NULL,\n" +
				"  dataType varchar(45) DEFAULT NULL,\n" +
				"  source varchar(45) DEFAULT NULL,\n" +
				"  destination varchar(45) DEFAULT NULL,\n" +
				"  subdestination varchar(45) DEFAULT NULL,\n" +
				"  replayIndicator boolean NOT NULL,\n" +
				"  publishTimestamp bigint,\n" +
				"  receivedTimestamp bigint,\n" +
				"  expirationTimestamp bigint,\n" +
				"  preEventState clob DEFAULT NULL,\n" +
				"  postEventState clob DEFAULT NULL,\n" +
				"  isPublishable boolean NOT NULL,\n" +
				"  insertTimeStamp bigint NOT NULL,\n" +
				"  PRIMARY KEY (eventId)\n" +
				")";


				String tableSql2 = "CREATE TABLE event.event_headers (\n" +
				"  eventId varchar(45) NOT NULL,\n" +
				"  name varchar(45) NOT NULL,\n" +
				"  value varchar(45) NOT NULL,\n" +
				"  PRIMARY KEY (eventId,name)\n" +
				//"  CONSTRAINT headersEventId FOREIGN KEY (eventId) REFERENCES event_store (eventId) ON DELETE CASCADE\n" +
				")";


				String tableSql3 = "CREATE TABLE event.event_payload (\n" +
				"  eventId varchar(45) NOT NULL,\n" +
				"  name varchar(45) NOT NULL,\n" +
				"  value varchar(45) NOT NULL,\n" +
				"  dataType varchar(45) DEFAULT NULL,\n" +
				"  PRIMARY KEY (eventId,name)\n" +
				//"  CONSTRAINT payloadEventId FOREIGN KEY (eventId) REFERENCES event_store (eventId) ON DELETE CASCADE\n" +
				")";

				String tableSql4 = "CREATE TABLE event.publication_audit (\n" +
				"  eventId varchar(45) NOT NULL,\n" +
				"  captureTimestamp bigint NOT NULL,\n" +
				"  persistTimestamp bigint NOT NULL,\n" +
				"  publishCount int DEFAULT 0,\n" +
				"  PRIMARY KEY (eventId)\n" +
				//"  CONSTRAINT auditEventId FOREIGN KEY (eventId) REFERENCES event_store (eventId) ON DELETE CASCADE\n" +
				")";

				String tableSql5 = "CREATE TABLE event.publish_timestamp (\n" +
				"  eventId varchar(45) NOT NULL,\n" +
				"  publishTimestamp bigint NOT NULL,\n" +
				"  publishId int DEFAULT 1,\n" +
				"  PRIMARY KEY (eventId,publishId)\n" +
				//"  CONSTRAINT publishEventId FOREIGN KEY (eventId) REFERENCES event_store (eventId) ON DELETE CASCADE\n" +
				")";

		try {
			PreparedStatement ps2 = conn.prepareStatement(tableSql1);
			ps2.execute();
		} catch (SQLException e){
			log.warn("table already exists");
		}

		try {
			PreparedStatement ps2 = conn.prepareStatement(tableSql2);
			ps2.execute();
		} catch (SQLException e){
			log.warn("table already exists");
		}

		try {
			PreparedStatement ps2 = conn.prepareStatement(tableSql3);
			ps2.execute();
		} catch (SQLException e){
			log.warn("table already exists");
		}

		try {
			PreparedStatement ps2 = conn.prepareStatement(tableSql4);
			ps2.execute();
		} catch (SQLException e){
			log.warn("table already exists");
		}

		try {
			PreparedStatement ps2 = conn.prepareStatement(tableSql5);
			ps2.execute();
		} catch (SQLException e){
			log.warn("table already exists");
		}



		return true;

	}

	private void validateDB(Connection conn){
		if (schemaInit==false){
			if (schemaExists(conn)==false){
				createSchema(conn);
				createTables(conn);
			}
		}

	}




	@Override
	public EventResponse insertEvent(Event event) throws DAOException {

		Connection conn = null;


		String sql = "INSERT INTO event.event_store(eventId, parentId, eventName, objectId, correlationId, sequenceNumber, " +
				"messageType, dataType, source, destination, subdestination, replayIndicator, publishTimestamp," +
				"receivedTimestamp, expirationTimestamp, preEventState, postEventState, isPublishable, insertTimeStamp) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,? )";
		String headersSql = "INSERT INTO event.event_headers VALUES ( ?,?,? )";
		String payloadSql = "INSERT INTO event.event_payload VALUES ( ?,?,?,? )";

		try {

			conn = dataSource.getConnection();

			String eventId = event.getEventId();
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, eventId);
			ps.setString(2, event.getParentEventId());
			ps.setString(3, event.getEventName());
			ps.setString(4, event.getObjectId());
			ps.setString(5, event.getCorrelationId());

			Integer seqNum = event.getSequenceNumber();
			if(seqNum != null) {
				ps.setInt(6, seqNum);
			} else {
				ps.setNull(6, Types.INTEGER);
			}

			ps.setString(7, event.getMessageType());
			ps.setString(8, event.getDataType());
			ps.setString(9, event.getSource());
			ps.setString(10, event.getDestination());
			ps.setString(11, event.getSubdestination());

			Boolean replay = event.isReplayIndicator();
			if(replay != null) {
				ps.setBoolean(12, replay);
			} else {
				ps.setBoolean(12, false);
			}

			DateTime pubTime = event.getPublishTimeStamp();
			if(pubTime != null){
				ps.setLong(13, pubTime.getMillis());
			} else {
				ps.setNull(13, Types.BIGINT);
			}

			ps.setLong(14, event.getReceivedTimeStamp().getMillis());

			DateTime expTime = event.getExpirationTimeStamp();
			if(expTime != null){
				ps.setLong(15, expTime.getMillis());
			} else {
				ps.setNull(15, Types.BIGINT);
			}

			ps.setString(16, event.getPreEventState());
			ps.setString(17, event.getPostEventState());

			Boolean publish = event.isPublishable();
			if(publish != null){
				ps.setBoolean(18, publish);
			} else {
				ps.setBoolean(18, false);
			}

			event.setInsertTimeStamp(DateTime.now());
			ps.setLong(19, event.getInsertTimeStamp().getMillis());

			ps.executeUpdate();



			PreparedStatement headersPs = conn.prepareStatement(headersSql);

			//insert header info into its table
			for(String key : event.getCustomHeaders().keySet()){
				headersPs.setString(1, eventId);
				headersPs.setString(2, key);
				headersPs.setString(3, event.getCustomHeaders().get(key));
				headersPs.executeUpdate();
			}

			PreparedStatement payloadPs = conn.prepareStatement(payloadSql);

			//insert payload info into its table
			for(String key : event.getCustomPayload().keySet()){
				payloadPs.setString(1, eventId);
				payloadPs.setString(2, key);
				payloadPs.setString(3, event.getCustomPayload().get(key).getValue());
				payloadPs.setString(4, event.getCustomPayload().get(key).getDataType().name());
				payloadPs.executeUpdate();
			}

			EventResponse er = new EventResponse(event);
			er.setPersistStatus("OK");
			er.setPersistStatusMessage("Event persisted successfully.");

			return er;

		} catch (SQLException e){
			if (e.getErrorCode()==3000){
				EventResponse er = new EventResponse();
				er.setPersistStatus("DUPLICATE_EVENT_ID");
				er.setPersistStatusMessage("An event with the event id specified already exists. Duplicate event id's are not permitted.");
			}
			log.error(e.getMessage() + ": " + e.getErrorCode(), e);
			throw new DAOException(e);
		} finally{
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					log.error("attempted to close connection. " + e.getMessage());
				}
			}

		}
	}




	@Override
	public EventsResponse insertEvents(List<Event> events) throws DAOException {

		Connection conn = null;
		int eventCount = events.size();

		String sql = "INSERT INTO event.event_store(eventId, parentId, eventName, objectId, correlationId, sequenceNumber, " +
				"messageType, dataType, source, destination, subdestination, replayIndicator, publishTimestamp," +
				"receivedTimestamp, expirationTimestamp, preEventState, postEventState, isPublishable, insertTimeStamp) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,? )";
		String headersSql = "INSERT INTO event.event_headers VALUES ( ?,?,? )";
		String payloadSql = "INSERT INTO event.event_payload VALUES ( ?,?,?,? )";

		try {

			conn = dataSource.getConnection();
			conn.setAutoCommit(false);

			PreparedStatement ps = conn.prepareStatement(sql);

			for (int i=0; i<eventCount; i++){

				Event event = events.get(i);
				String eventId = event.getEventId();

				ps.setString(1, eventId);
				ps.setString(2, event.getParentEventId());
				ps.setString(3, event.getEventName());
				ps.setString(4, event.getObjectId());
				ps.setString(5, event.getCorrelationId());

				Integer seqNum = event.getSequenceNumber();
				if(seqNum != null) {
					ps.setInt(6, seqNum);
				} else {
					ps.setNull(6, Types.INTEGER);
				}

				ps.setString(7, event.getMessageType());
				ps.setString(8, event.getDataType());
				ps.setString(9, event.getSource());
				ps.setString(10, event.getDestination());
				ps.setString(11, event.getSubdestination());

				Boolean replay = event.isReplayIndicator();
				if(replay != null) {
					ps.setBoolean(12, replay);
				} else {
					ps.setBoolean(12, false);
				}

				DateTime pubTime = event.getPublishTimeStamp();
				if(pubTime != null){
					ps.setLong(13, pubTime.getMillis());
				} else {
					ps.setNull(13, Types.BIGINT);
				}

				ps.setLong(14, event.getReceivedTimeStamp().getMillis());

				DateTime expTime = event.getExpirationTimeStamp();
				if(expTime != null){
					ps.setLong(15, expTime.getMillis());
				} else {
					ps.setNull(15, Types.BIGINT);
				}

				ps.setString(16, event.getPreEventState());
				ps.setString(17, event.getPostEventState());

				Boolean publish = event.isPublishable();
				if(publish != null){
					ps.setBoolean(18, publish);
				} else {
					ps.setBoolean(18, false);
				}

				event.setInsertTimeStamp(DateTime.now());
				ps.setLong(19, event.getInsertTimeStamp().getMillis());

				ps.addBatch();

			}


			ps.executeBatch();



			PreparedStatement headersPs = conn.prepareStatement(headersSql);

			//insert header info into its table
			for (int i=0; i<eventCount; i++) {
				Event event = events.get(i);
				String eventId = event.getEventId();
				for (String key : event.getCustomHeaders().keySet()) {
					headersPs.setString(1, eventId);
					headersPs.setString(2, key);
					headersPs.setString(3, event.getCustomHeaders().get(key));
					headersPs.addBatch();
				}
			}
			headersPs.executeBatch();

			PreparedStatement payloadPs = conn.prepareStatement(payloadSql);

			//insert payload info into its table
			for (int i=0; i<eventCount; i++) {
				Event event = events.get(i);
				for (String key : event.getCustomPayload().keySet()) {
					String eventId = event.getEventId();
					payloadPs.setString(1, eventId);
					payloadPs.setString(2, key);
					payloadPs.setString(3, event.getCustomPayload().get(key).getValue());
					payloadPs.setString(4, event.getCustomPayload().get(key).getDataType().name());
					payloadPs.addBatch();
				}
			}
			payloadPs.executeBatch();

			conn.commit();


			EventsResponse es = new EventsResponse();
			List<EventResponse> ler = new ArrayList<EventResponse>();

			for (int i=0; i<eventCount; i++){
				ler.add(new EventResponse(events.get(i)));
			}


			return new EventsResponse(ler);

		} catch (SQLException e){
			log.error(e.getMessage(), e);
			throw new DAOException(e);
		} finally{
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					log.error("attempted to close connection. " + e.getMessage());
				}
			}

		}
	}


	/**
	 * Retrieve an event from the database and wrap it in an Event object.
	 * Return null if the requested Event object is not found in the database.
	 *
	 * @param eventId the id of the Event.
	 * @return An event object or null.
	 * @throws DAOException
	 */
	@Override
	public EventResponse getEvent(String eventId) throws DAOException {

		log.debug("Derby DAO attempting to retrieve event with id: " + eventId);
		Connection conn = null;

		String sql = "SELECT * FROM event.event_store WHERE eventId = ?";
		String headersSql = "SELECT name,value FROM event.event_headers WHERE eventId = ?";
		String payloadSql = "SELECT name,value,dataType FROM event.event_payload WHERE eventId = ?";

		Event event = null;

		try {

			conn = dataSource.getConnection();

			PreparedStatement ps = conn.prepareStatement(sql);

			ps.setString(1, eventId);
			
			ResultSet rs = ps.executeQuery();

			if (rs.next()){

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


				// populate headers

				PreparedStatement psHeaders = conn.prepareStatement(headersSql);

				//set the value being checked for equality
				psHeaders.setString(1, eventId);

				ResultSet rsHeaders = psHeaders.executeQuery();
				Map<String,String> customHeaders = new HashMap<String,String>();

				//get header info from its table
				while(rsHeaders.next()){
					customHeaders.put(rsHeaders.getString("name"), rsHeaders.getString("value"));
				}


				// populate payload

				PreparedStatement psPayload = conn.prepareStatement(payloadSql);

				//set the value being checked for equality
				psPayload.setString(1, eventId);

				ResultSet rsPayload = psPayload.executeQuery();
				Map<String,DataItem> customPayload = new HashMap<String,DataItem>();

				//get payload info from its table
				while(rsPayload.next()){
					String payName = rsPayload.getString("name");
					String payType = rsPayload.getString("dataType");
					String payVal = rsPayload.getString("value");


					PrimitiveDatatype v = null;
					if (payType.equalsIgnoreCase("boolean")) { v = PrimitiveDatatype.Boolean; }
					if (payType.equalsIgnoreCase("byte")) { v = PrimitiveDatatype.Byte; }
					if (payType.equalsIgnoreCase("double")) { v = PrimitiveDatatype.Double; }
					if (payType.equalsIgnoreCase("float")) { v = PrimitiveDatatype.Float; }
					if (payType.equalsIgnoreCase("integer")) { v = PrimitiveDatatype.Integer; }
					if (payType.equalsIgnoreCase("long")) { v = PrimitiveDatatype.Long; }
					if (payType.equalsIgnoreCase("short")) { v = PrimitiveDatatype.Short; }
					if (payType.equalsIgnoreCase("string")) { v = PrimitiveDatatype.String; }
					if (v==null) { v = PrimitiveDatatype.String; }

					customPayload.put(payName, new DataItem(v,payVal));
				}

				event.setCustomHeaders(customHeaders);
				event.setCustomPayload(customPayload);

			}

			EventResponse es = null;

			if (event==null){
				es = new EventResponse();
				es.setPersistStatus("NOT_FOUND");
				es.setPersistStatusMessage("Event not found with event id: " + eventId);
			} else {
				es = new EventResponse(event);
			}


			return es;

		} catch (SQLException e) {
			log.error("request for event resulted in: " + e.getMessage());
			throw new DAOException(e);
		} finally{
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					log.error("attempted to close connection failed. " + e.getMessage());
				}
			}
		}
	}


	@Override
	public EventsResponse getEvents(List<String> req) throws DAOException {

		log.debug("Derby DAO attempting to retrieve events");
		Connection conn = null;

		String sql = "SELECT * FROM event.event_store WHERE eventId IN (";
		int listLen = req.size();
		for (int i=0; i<listLen; i++){
			sql = sql + req.get(i);
			if (i!=listLen) {
				sql = sql + ",";
			}
		}
		sql = sql + ")";



		String headerSql = "SELECT * FROM event.event_store WHERE eventId IN (";
		for (int i=0; i<listLen; i++){
			headerSql = headerSql + req.get(i);
			if (i!=listLen) {
				headerSql = headerSql + ",";
			}
		}
		headerSql = headerSql + ")";


		String payloadSql = "SELECT * FROM event.event_store WHERE eventId IN (";
		for (int i=0; i<listLen; i++){
			payloadSql = payloadSql + req.get(i);
			if (i!=listLen) {
				payloadSql = payloadSql + ",";
			}
		}
		payloadSql = payloadSql + ")";








		EventsResponse er = new EventsResponse();
		List<EventResponse> ler = new ArrayList<EventResponse>();

		try {

			conn = dataSource.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();


			while(rs.next()){

				DateTime publish = new DateTime(rs.getLong("publishTimeStamp"));
				if (rs.wasNull()) publish = null;
				DateTime expiration = new DateTime(rs.getLong("expirationTimeStamp"));
				if (rs.wasNull()) expiration = null;

				Event event = new Event(
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

				EventResponse ers = new EventResponse(event);
				ler.add(ers);

			}

			er.setEvents(ler);


			// populate headers

			PreparedStatement psHeaders = conn.prepareStatement(headerSql);

			ResultSet rsHeaders = psHeaders.executeQuery();
			Map<String,String> customHeaders = new HashMap<String,String>();

			//get header info from its table
			while(rsHeaders.next()){
				customHeaders.put(rsHeaders.getString("name"), rsHeaders.getString("value"));
			}

			/*
				// populate payload

				PreparedStatement psPayload = conn.prepareStatement(payloadSql);

				//set the value being checked for equality
				psPayload.setString(1, eventId);

				ResultSet rsPayload = psPayload.executeQuery();
				Map<String,DataItem> customPayload = new HashMap<>();

				//get payload info from its table
				while(rsPayload.next()){
					String payName = rsPayload.getString("name");
					String payType = rsPayload.getString("dataType");
					String payVal = rsPayload.getString("value");
					customPayload.put(payName, new DataItem(payType,payVal));
				}

				event.setCustomHeaders(customHeaders);
				event.setCustomPayload(customPayload);
				*/



		} catch (SQLException e) {
				log.error("request for events resulted in: " + e.getMessage());
				throw new DAOException(e);
		} finally{
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					log.error("attempted to close connection failed. " + e.getMessage());
				}
			}
		}

		return er;
	}
}
