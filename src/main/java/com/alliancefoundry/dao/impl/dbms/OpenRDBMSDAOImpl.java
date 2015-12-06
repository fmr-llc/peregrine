package com.alliancefoundry.dao.impl.dbms;

import com.alliancefoundry.dao.DAOException;
import com.alliancefoundry.dao.EventDAO;
import com.alliancefoundry.model.*;
import org.joda.time.DateTime;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.*;

import static org.jooq.conf.ParamType.INLINED;
import static org.jooq.impl.DSL.*;
import static com.alliancefoundry.dao.impl.dbms.DBConstants.*;

/**
 * This implementation leverages JOOQ to build SQL without using String
 * concatenation or heavy weight ORM frameworks.
 * Subclasses of this class should override getDialect
 * method and return the appropriate dialect required for the open
 * source relational DBMS of your choice.
 * JOOQ support the following open source databases "out of the box"
 *
 * 	CURBRID 8.4
 * 	Derby 10.10
 *  Firebird 2.5
 *  H2 1.3
 *  HSQLDB 2.2
 *  MariaDB 5.2
 *  MySQL 5.5
 *  PostgreSQL 9.3, 9.4, 9.5
 *  SQLite
 *
 *  Additional commercial databases are supported for a nominal fee.
 *  JOOQ can be extended to support whatever database is required
 *  as an alternatives to paid licence options.
 */
public class OpenRDBMSDAOImpl extends DaoProvider implements EventDAO {

	private static final Logger log = LoggerFactory.getLogger(OpenRDBMSDAOImpl.class);

	protected org.jooq.SQLDialect dialect = SQLDialect.DERBY;

	/**
	 * Override this method in subclasses if you wish to use other dialects.
	 * @return
     */
	public SQLDialect getDialect(){
		return dialect;
	}

	public void setDialect(org.jooq.SQLDialect dialect){
		this.dialect = dialect;
	}



	@Override
	@Transactional(rollbackFor = Exception.class)
	public EventResponse insertEvent(Event event) throws DataAccessException {

		log.debug("serialized state of attempted event insert: " + event.toString());

		// Setup Defaults
		Integer pSequenceNumber = event.getSequenceNumber() == null ?
				null : event.getSequenceNumber();
		event.setSequenceNumber(pSequenceNumber);

		long pPublishingTimestamp = event.getPublishTimeStamp() == null ?
				0 : event.getPublishTimeStamp().getMillis();

		long pReceivedTimestamp = event.getReceivedTimeStamp() == null ?
				System.currentTimeMillis() : event.getReceivedTimeStamp().getMillis();

		long pExpirationTimestamp = event.getExpirationTimeStamp() == null ?
				0 : event.getExpirationTimeStamp().getMillis();

		Boolean pPublishable = event.isPublishable() == null ?
				false : event.isPublishable();
		if (pPublishable!=null){
			event.setPublishable(pPublishable);
		}

		long pInsertTimestamp = event.getInsertTimeStamp() == null ?
				0 : event.getInsertTimeStamp().getMillis();


		// process inserts
		DSLContext create = DSL.using(getDialect());

		String sql = create.insertInto(table(EVENT_STORE_TBL),
				field(EVENT_ID),
				field(PARENT_EVENT_ID),
				field(EVENT_NAME),
				field(OBJECT_ID),
				field(CORRELATION_ID),
				field(SEQUENCE_NUMBER),
				field(MESSAGE_TYPE),
				field(DATA_TYPE),
				field(SOURCE),
				field(DESTINATION),
				field(SUBDESTINATION),
				field(REPLAY_INDICATOR),
				field(PUBLISH_TIMESTAMP),
				field(RECEIVED_TIMESTAMP),
				field(EXPIRATION_TIMESTAMP),
				field(PREEVENT_STATE),
				field(POSTEVENT_STATE),
				field(IS_PUBLISHABLE),
				field(INSERT_TIMESTAMP))
				.values(event.getEventId(),
				event.getParentEventId(),
				event.getEventName(),
				event.getObjectId(),
				event.getCorrelationId(),
				cast(pSequenceNumber, Integer.class),
				event.getMessageType(),
				event.getDataType(),
				event.getSource(),
				event.getDestination(),
				event.getSubdestination(),
				event.isReplayIndicator(),
				pPublishingTimestamp == 0 ? null : pPublishingTimestamp,
				pReceivedTimestamp == 0 ? null : pReceivedTimestamp,
				pExpirationTimestamp == 0 ? null : pExpirationTimestamp,
				event.getPreEventState(),
				event.getPostEventState(),
				cast(pPublishable, Boolean.class),
				pInsertTimestamp == 0 ? null : pInsertTimestamp
				)
				.getSQL(INLINED);

		log.debug("insert event using SQL: " + sql);

		jdbcTemplate.update(sql);


		if (event.getCustomHeaders()!=null && event.getCustomHeaders().size()>0){

			DSLContext createHeaderSql = DSL.using(getDialect());
			InsertValuesStep3 headersValuesStep = createHeaderSql.insertInto(table(EVENT_HEADERS_TBL),
					field(EVENT_ID),
					field(NAME),
					field(VALUE));

			for(String key : event.getCustomHeaders().keySet()){
				headersValuesStep.values(
						event.getEventId(),
						key,
						event.getCustomHeaders().get(key));
			}

			String headersSQL = headersValuesStep.getSQL(INLINED);
			log.debug("insert headers using SQL: " + headersSQL);
			jdbcTemplate.update(headersSQL);
		}


		if (event.getCustomPayload()!=null && event.getCustomPayload().size()>0){

			DSLContext createPayloadSql = DSL.using(getDialect());

			InsertValuesStep4 payloadHeadersStep = createPayloadSql.
					insertInto(table(EVENT_PAYLOAD_TBL),
					field(EVENT_ID),
					field(NAME),
					field(VALUE),
					field(DATA_TYPE));

			Iterator<Triplet> payloadEntry = event.getCustomPayload().iterator();

			while(payloadEntry.hasNext()){
				Triplet dataItem = payloadEntry.next();
				payloadHeadersStep.values(
						event.getEventId(),
						dataItem != null ? dataItem.getName() : null,
						dataItem != null ? dataItem.getValue() : null,
						dataItem != null ? dataItem.getType(): null);

			}

			String payLoadSQL = payloadHeadersStep.getSQL(INLINED);
			log.debug("insert payload using SQL: " + payLoadSQL);
			jdbcTemplate.update(payLoadSQL);

		}


		EventResponse er = new EventResponse(event);
		er.setPersistStatus("OK");
		er.setPersistStatusMessage("Event persisted successfully.");

		return er;

	}

	private InsertSetMoreStep addParam(InsertSetStep isms, String name, Object value){
		if (value!=null){
			log.debug("setting field with name: " + name + " = " + value);
			return isms.set(field(name), value);
		}
		return null;
	}

	private InsertSetMoreStep addParam(InsertSetMoreStep isms, String name, Object value){
		if (value!=null){
			log.debug("setting field with name: " + name + " = " + value);
			return isms.set(field(name), value);
		}
		return isms;
	}


	@Override
	public EventsResponse insertEvents(List<Event> events) throws DataAccessException {


		int eventCount = events.size();

		String sql = "INSERT INTO event.event_store(eventId, parentId, eventName, objectId, correlationId, sequenceNumber, " +
				"messageType, dataType, source, destination, subdestination, replayIndicator, publishTimestamp," +
				"receivedTimestamp, expirationTimestamp, preEventState, postEventState, isPublishable, insertTimeStamp) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,? )";


		jdbcTemplate.update(sql, new PreparedStatementSetter() {

			public void setValues(PreparedStatement ps) throws SQLException {

				for (int i = 0; i < eventCount; i++) {

					Event event = events.get(i);
					String eventId = event.getEventId();

					ps.setString(1, eventId);
					ps.setString(2, event.getParentEventId());
					ps.setString(3, event.getEventName());
					ps.setString(4, event.getObjectId());
					ps.setString(5, event.getCorrelationId());

					Integer seqNum = event.getSequenceNumber();
					if (seqNum != null) {
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
					if (replay != null) {
						ps.setBoolean(12, replay);
					} else {
						ps.setBoolean(12, false);
					}

					DateTime pubTime = event.getPublishTimeStamp();
					if (pubTime != null) {
						ps.setLong(13, pubTime.getMillis());
					} else {
						ps.setNull(13, Types.BIGINT);
					}

					ps.setLong(14, event.getReceivedTimeStamp().getMillis());

					DateTime expTime = event.getExpirationTimeStamp();
					if (expTime != null) {
						ps.setLong(15, expTime.getMillis());
					} else {
						ps.setNull(15, Types.BIGINT);
					}

					ps.setString(16, event.getPreEventState());
					ps.setString(17, event.getPostEventState());

					Boolean publish = event.isPublishable();
					if (publish != null) {
						ps.setBoolean(18, publish);
					} else {
						ps.setBoolean(18, false);
					}

					event.setInsertTimeStamp(DateTime.now());
					ps.setLong(19, event.getInsertTimeStamp().getMillis());



				}
			}
		});


		String headersSql = "INSERT INTO event.event_headers VALUES ( ?,?,? )";

		jdbcTemplate.update(headersSql, new PreparedStatementSetter() {

			public void setValues(PreparedStatement headersPs) throws SQLException {
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
			}});



		String payloadSql = "INSERT INTO event.event_payload VALUES ( ?,?,?,? )";

		jdbcTemplate.update(payloadSql, new PreparedStatementSetter() {

			public void setValues(PreparedStatement payloadPs) throws SQLException {
				for (int i=0; i<eventCount; i++) {
					Event event = events.get(i);

					Iterator<Triplet> payloadEntries = event.getCustomPayload().iterator();

					while(payloadEntries.hasNext()){
						Triplet entry = payloadEntries.next();
						payloadPs.setString(1, event.getEventId());
						payloadPs.setString(2, entry.getName());
						payloadPs.setString(3, entry.getValue());
						payloadPs.setString(4, entry.getType());
						payloadPs.addBatch();
					}

				}

			}});


			EventsResponse es = new EventsResponse();
			List<EventResponse> ler = new ArrayList<EventResponse>();

			for (int i=0; i<eventCount; i++){
				ler.add(new EventResponse(events.get(i)));
			}

			return new EventsResponse(ler);

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
	public EventResponse getEvent(String eventId) throws DataAccessException {

		DSLContext create = DSL.using(getDialect());

		Select<?> select  = create.select()
				.from(table(EVENT_STORE_TBL))
				.where(EVENT_ID + " = '" + eventId + "'");
		String sql = select.getSQL();


		Event event = (Event)jdbcTemplate
				.queryForObject(
						sql, new Object[] { },
				new EventRowMapper());

		if (event!=null){

			event.setCustomHeaders(fetchHeaders(event.getEventId()));
			event.setCustomPayload(fetchPayload(eventId));

			EventResponse es = new EventResponse(event);

			return es;

		}


		throw new EmptyResultDataAccessException(1);

	}

	@Override
	public EventsResponse getEvents(List<String> req) throws DataAccessException {


		EventsResponse er = new EventsResponse();
		List<EventResponse> responses = new ArrayList<EventResponse>();

		Iterator<String> strIter = req.iterator();
		boolean errInd = false;
		while(strIter.hasNext()){

			long start = System.currentTimeMillis();

			String eventId = strIter.next();
			try {

				EventResponse res = this.getEvent(eventId);
				long end = System.currentTimeMillis();
				res.setProcessingDuration(end-start);
				responses.add(res);

			} catch (EmptyResultDataAccessException e){

				EventResponse res = new EventResponse();
				res.setStatus("ERROR");
				res.setStatusMessage("Record not Found: " + eventId);
				long end = System.currentTimeMillis();
				res.setProcessingDuration(end-start);
				responses.add(res);
				errInd = true;

			} catch (DataAccessException e){
				throw e;
			}

		}

		er.setEvents(responses);

		if (errInd){
			er.setStatus("ERROR");
			er.setStatusMessage("One or more request for an event failed.");
		} else {
			er.setStatus("OK");
		}

		return er;


	}

	@Override
	public EventsResponse queryEvents(String source, String generations, String name, String objectId, String correlationId, String createdAfter, String createdBefore, String timestamp) throws DAOException {


		throw new DAOException("feature not implemented");
	}

	@Override
	public EventsResponse getEventSources() throws DataAccessException {

		validateDB();

		DSLContext create = DSL.using(getDialect());
		String sql= create.selectDistinct(field(SOURCE))
				.from(table(EVENT_STORE_TBL))
				.getSQL();

		EventsResponse er = new EventsResponse();
		List<String> sources= jdbcTemplate.query(sql, new StringRowMapper());

		if (sources==null || sources.isEmpty()) { throw new EmptyResultDataAccessException(1); };


		sources.removeAll(Collections.singleton(null));
		er.setEventSources(sources);
		er.setEvents(null);
		return er;
	}

	@Override
	public EventsResponse getEventNames(String source) throws DataAccessException {


		DSLContext create = DSL.using(getDialect());
		String sql = create.select(field(EVENT_NAME))
				.from(table(EVENT_STORE_TBL))
				.where("source = '" + source +"'")
				.groupBy(field(EVENT_NAME))
				.getSQL(INLINED);

		List<String> names = jdbcTemplate.query(sql, new StringRowMapper());

		if (names==null || names.isEmpty()) { throw new EmptyResultDataAccessException(1); };

		names.removeAll(Collections.singleton(null));
		EventsResponse er = new EventsResponse();
		er.setEventNames(names);

		return er;
	}


	@Override
	public EventResponse getLatestEvent(String source, String eventName, String objectId, String correlationId) throws DataAccessException {


		log.debug("invoking getLatestEvent method with source = " + source);

		DSLContext create = DSL.using(getDialect());

		String sql = "";
		SelectJoinStep step = create.select(
					field(EVENT_NAME)
					,field(EVENT_ID)
					,field(OBJECT_ID)
					,field(CORRELATION_ID)
					,field(SOURCE)
					,max(field(INSERT_TIMESTAMP)).as(field(INSERT_TIMESTAMP)))
					.from(table(EVENT_STORE_TBL));

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

		if (source!=null && firstConjunction==false) {
			cs = step.where("source = '" + source + "'");
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
			sql = step.groupBy(field(EVENT_NAME)
					,field(OBJECT_ID)
					,field(CORRELATION_ID)
					,field(SOURCE)).getSQL();
		} else {
			sql = cs.groupBy(field(EVENT_NAME)
					,field(OBJECT_ID)
					,field(CORRELATION_ID)
					,field(SOURCE)
					).getSQL(INLINED);
		}

		Event event = new Event();
		final String cSql = sql;

		List<Event> result = jdbcTemplate.query(cSql, new EventRowMapper());

		return this.getEvent(result.get(0).getEventId());
	}


	@Override
	public EventResponse replayEvent(EventRequest request) throws DataAccessException {


		return null;
	}


	private String getEventsSql(List<String> req){


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

	private List<Event> fetchEvents(List<String> req) throws DataAccessException {

		List<Event> ler = new ArrayList();

		Iterator<String> iter = req.iterator();
		while(iter.hasNext()){
			Event evt = this.getEvent(iter.next()).getEvent();
			ler.add(evt);
		}

		return ler;

	}

	private Map<String, String> fetchHeaders(String eventId) throws DataAccessException {

		Map<String,String> customHeaders = new HashMap<String,String>();

		if (eventId == null) { return customHeaders; }

		DSLContext create = DSL.using(getDialect());

		String sql = create.select(field(NAME), field(VALUE))
				.from(table(EVENT_HEADERS_TBL))
				.where(EVENT_ID + " = '" + eventId + "'")
				.getSQL();

		log.debug("fetching headers for event id: " + eventId + " with sql: " + sql);

		List<Triplet> headerResult = jdbcTemplate.query(sql, new HeaderRowMapper());

		Iterator<Triplet> iterHeader = headerResult.iterator();
		while(iterHeader.hasNext()){
			Triplet currentHeader = iterHeader.next();
			customHeaders.put(currentHeader.getName(), currentHeader.getValue());
		}

		return customHeaders;

	}


	private List<Triplet> fetchPayload(String eventId) throws DataAccessException {

		if (eventId == null) { return null; }

		DSLContext create = DSL.using(getDialect());

		String sql = create.select(field(NAME), field(VALUE), field(DATA_TYPE))
				.from(table(EVENT_PAYLOAD_TBL))
				.where(EVENT_ID + " = '" + eventId + "'")
				.getSQL();

		log.debug("fetching payload for event id: " + eventId + " with sql: " + sql);

		List<Triplet> payloadResult = jdbcTemplate.query(sql, new PayloadRowMapper());

		return payloadResult;

	}




}



