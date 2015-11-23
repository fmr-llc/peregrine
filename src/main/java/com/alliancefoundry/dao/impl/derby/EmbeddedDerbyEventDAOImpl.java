package com.alliancefoundry.dao.impl.derby;

import com.alliancefoundry.dao.DAOException;
import com.alliancefoundry.dao.EventDAO;
import com.alliancefoundry.model.*;
import org.joda.time.DateTime;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.SelectConditionStep;
import org.jooq.SelectJoinStep;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

import static org.jooq.impl.DSL.*;

public class EmbeddedDerbyEventDAOImpl extends SchemaInit implements EventDAO {

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
					if (conn!=null){
						conn.close();
					}
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

		EventsResponse er = new EventsResponse();

		List<Event> loe = fetchEvents(req);

		if (loe!=null){
			Iterator<Event> el = loe.iterator();
			List<EventResponse> ers = new ArrayList<EventResponse>();
			while(el.hasNext()){
				Event evt = el.next();
				EventResponse etr = new EventResponse();
				etr.setEvent(evt);
				ers.add(etr);
			}

			er.setEvents(ers);

			return er;
		} else {
			er = new EventsResponse();
			er.setStatus("NOT_FOUND");
			er.setStatusMessage("Event with id/id's specified were not found: " + req.toString());
			return er;
		}

	}

	@Override
	public EventsResponse queryEvents(String source, String generations, String name, String objectId, String correlationId, String createdAfter, String createdBefore, String timestamp) throws DAOException {
		throw new DAOException("feature not implmented");
	}

	@Override
	public EventsResponse getEventSources() throws DAOException {

		DSLContext create = DSL.using(SQLDialect.DERBY);
		String sql= create.selectDistinct(field("source"))
				.from(table("event.event_store"))
				.getSQL();

		Connection conn = null;
		EventsResponse er = new EventsResponse();
		List<String> sources = new ArrayList<String>();

		try {
			conn = dataSource.getConnection();
			log.debug("making request to database with sql: " + sql);
			PreparedStatement ps = conn.prepareStatement(sql);

			ResultSet rs = ps.executeQuery();

			while(rs.next()){
				String source = rs.getString(1);
				sources.add(source);
			}
			er.setEventSources(sources);

		} catch (SQLException e) {
				log.error("request for unique sources resulted in: " + e.getMessage() + " with sql = " + sql);
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

	@Override
	public EventsResponse getEventNames(String source) throws DAOException {

		DSLContext create = DSL.using(SQLDialect.DERBY);

		String sql = create.select(field("eventName"))
				.from(table("event.event_store"))
				.where("source = '" + source +"'")
				.groupBy(field("eventName"))
				.getSQL();


		Connection conn = null;
		EventsResponse er = new EventsResponse();
		List<String> names = new ArrayList<String>();

		try {
			conn = dataSource.getConnection();
			log.debug("making request to database with sql: " + sql);
			PreparedStatement ps = conn.prepareStatement(sql);

			ResultSet rs = ps.executeQuery();

			while(rs.next()){
				String name = rs.getString(1);
				names.add(name);
				log.debug("adding name : " + name);
			}
			er.setEventNames(names);

		} catch (SQLException e) {
			log.error("request for unique event names from source specified resulted in: " + e.getMessage() + " with sql = " + sql);
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


	@Override
	public EventResponse getLatestEvent(String source, String eventName, String objectId, String correlationId) throws DAOException {


		log.debug("invoking getLatestEvent method with source = " + source);

		DSLContext create = DSL.using(SQLDialect.DERBY);

		String sql = "";
		SelectJoinStep step = create.select(field("eventName")
					,field("objectId")
					,field("correlationId")
					,field("source")
					,max(field("insertTimestamp")).as(field("insertTimestamp")))
					.from(table("event.event_store"));

		boolean firstConjunction = true;
		SelectConditionStep cs = null;

		if (source!=null) {
			cs = step.where("source = '" + source + "'");
			firstConjunction = false;
		}

		if (eventName!=null && firstConjunction==true) {
			cs = step.where("eventName = '" + eventName + "'");
			firstConjunction = false;
		}

		if (objectId!=null && firstConjunction==true) {
			cs = step.where("objectId = '" + objectId + "'");
			firstConjunction = false;
		}

		if (correlationId!=null && firstConjunction==true) {
			cs = step.where("correlationId = '" + correlationId + "'");
			firstConjunction = false;
		}

		if (eventName!=null && firstConjunction==false) {
			cs = cs.and("eventName = '" + eventName + "'");
		}

		if (objectId!=null && firstConjunction==false) {
			cs = cs.and("objectId = '" + objectId + "'");
		}

		if (correlationId!=null && firstConjunction==false) {
			cs = cs.and("correlationId = '" + correlationId + "'");
		}


		if (cs==null){
			sql = step.groupBy(field("eventName")
					,field("objectId")
					,field("correlationId")
					,field("source")).getSQL();
		} else {
			sql = cs.groupBy(field("eventName")
					,field("objectId")
					,field("correlationId")
					,field("source")
					).getSQL();
		}



		Connection conn = null;
		EventResponse er = new EventResponse();

		Event event = new Event();

		try {
			conn = dataSource.getConnection();
			log.debug("making request to database with sql: " + sql);
			PreparedStatement ps = conn.prepareStatement(sql);



			ResultSet rs = ps.executeQuery();

			while(rs.next()){

				String id = rs.getString("objectId");
				er = this.getEvent(id);
			}


		} catch (SQLException e) {
			log.error("request for latest: " + e.getMessage() + " with sql = " + sql);
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


	@Override
	public EventResponse replayEvent(EventRequest request) throws DAOException {
		return null;
	}


	private String getEventsSql(List<String> req){

		// events


		if (req.size()==1){
			String sql = "SELECT * FROM event.event_store WHERE eventId = ?";
			return sql;
		} else {
			String sql = "SELECT * FROM event.event_store WHERE eventId IN (";
			int listLen = req.size();

			for (int i=0; i<listLen; i++){
				sql = sql + "'" +req.get(i) + "'";
				if (i!=listLen-1) {
					sql = sql + ",";
				}
			}
			sql = sql + ")";

			return sql;
		}

	}



	private List<Event> fetchEvents(List<String> req) throws DAOException {

		List<Event> ler = new ArrayList();
		String eventSQL = getEventsSql(req);
		Connection conn = null;


		try {
			conn = dataSource.getConnection();

			PreparedStatement ps = conn.prepareStatement(eventSQL);

			log.debug("request size = " + req.size());

			if (req.size()==1){
				String eventId = req.get(0);
				ps.setString(1, eventId);
			}

			log.debug("making request to database with sql: " + eventSQL);

			ResultSet rs = ps.executeQuery();



			while(rs.next()){

				log.debug("row found in result set");

				DateTime timestamp = new DateTime(rs.getLong("insertTimeStamp"));
				if (rs.wasNull()) timestamp = null;

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
						null,
						null,
						null,
						rs.getString("preEventState"),
						rs.getString("postEventState"),
						rs.getBoolean("isPublishable"),
						timestamp

				);
				String eventId = rs.getString("eventId");
				event.setEventId(eventId);

				event.setCustomHeaders(fetchHeaders(eventId));
				event.setCustomPayload(fetchPayload(eventId));

				ler.add(event);
			}

			if (ler.size()==0) {log.debug("no results in resultset"); }



		} catch (SQLException e) {
			log.error("request for events resulted in: " + e.getMessage() + " with sql = " + eventSQL);
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

		return ler;
	}



	private Map<String, String> fetchHeaders(String eventId) throws DAOException {

		String headerSQL = "SELECT name,value FROM event.event_headers WHERE eventId = ?";

		Connection conn = null;
		Map<String,String> customHeaders = new HashMap<String,String>();

		try {
			conn = dataSource.getConnection();
			PreparedStatement ps = conn.prepareStatement(headerSQL);

			ps.setString(1, eventId);

			ResultSet rs = ps.executeQuery();


			while(rs.next()){
				customHeaders.put(rs.getString("name"), rs.getString("value"));
			}


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

		return customHeaders;

	}


	private Map<String, DataItem> fetchPayload(String eventId) throws DAOException {

		String payloadSql = "SELECT name,value,dataType FROM event.event_payload WHERE eventId = ?";

		Connection conn = null;
		Map<String,DataItem> customPayload = new HashMap<String,DataItem>();

		try {
			conn = dataSource.getConnection();
			PreparedStatement ps = conn.prepareStatement(payloadSql );

			ps.setString(1, eventId);

			ResultSet rsPayload = ps.executeQuery();



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

		return customPayload;

	}



}
