package com.alliancefoundry.tests.DAOTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import com.alliancefoundry.dao.IDAO;
import com.alliancefoundry.exceptions.PeregrineException;
import com.alliancefoundry.model.DataItem;
import com.alliancefoundry.model.Event;
import com.alliancefoundry.model.EventPublicationAudit;
import com.alliancefoundry.model.EventsRequest;

/**
 * Created by: Bobby Writtenberry
 *
 */
public class JdbcTemplateDaoTest {
	
	static final Logger log = LoggerFactory.getLogger(JdbcTemplateDaoTest.class);
	
	IDAO dao;
	AbstractApplicationContext ctx;
	Event event;
	String eventId;
	Event eventFromDb;
	EventPublicationAudit audit;
	
	Event getEvent1;
	Event getEvent2;
	//this will also be the most recent inserted
	Event getEvent3;
	String parentEventId;
	String child1EventId;
	String child2EventId;
	
	//variables for an EventsRequest object
	DateTime createdAfter;
	DateTime createdBefore;
	String source;
	String objectId;
	String correlationId;
	String name;
	Integer generations;
	
	@Before
	public void setUp() throws Exception {
		ctx = new ClassPathXmlApplicationContext("eventservice-beans.xml");
		dao = ctx.getBean("dao", IDAO.class);

		createdAfter = new DateTime(0);
		createdBefore = DateTime.now();
		source = "some source";
		objectId = "some object id";
		correlationId = "some correlation id";
		name = "Testing name";
		generations = 2;
		
		AbstractApplicationContext ctx;
		ctx = new ClassPathXmlApplicationContext("db-mock-events.xml");

		getEvent1 = ctx.getBean("parentEvent", Event.class);
		parentEventId = dao.insertEvent(getEvent1, new HashMap<String,EventPublicationAudit>());
		getEvent2 = ctx.getBean("childEvent1", Event.class);
		getEvent2.setParentId(parentEventId);
		child1EventId = dao.insertEvent(getEvent2, new HashMap<String,EventPublicationAudit>());
		getEvent3 = ctx.getBean("childEvent2", Event.class);
		getEvent3.setParentId(parentEventId);
		child2EventId = dao.insertEvent(getEvent3, new HashMap<String,EventPublicationAudit>());
		
		ctx.close();
	}
	
	/***********************************
	 *Testing insertPublicationAudit() *
	 *				&				   *
	 *		   getPublicationAudit()   *
	 ***********************************/
	
	@Test
	public void insertAndGetPublicationAuditTest() throws PeregrineException {
		String auditId = parentEventId;
		audit = new EventPublicationAudit();
		audit.setEventId(auditId);
		audit.setCaptureTimestamp(DateTime.now());
		audit.setPersistTimestamp(DateTime.now());
		audit.addPublishTimestamp(DateTime.now());
		log.debug("insertAndGetPublicationAuditTest(): EventPublicationAudit object is " + audit);
		log.debug("first publish time is " + audit.getPublishTimestamps().get(0));
		Map<String,EventPublicationAudit> audits = new HashMap<String,EventPublicationAudit>();
		audits.put(auditId, audit);
		dao.insertPublicationAudit(audits);
		for(int i = 0; i < 3; i++){
			audit.addPublishTimestamp(DateTime.now());
			log.debug("next publish time is " + audit.getPublishTimestamps().get(i + 1));
			dao.insertPublishTimestamp(audit, audit.getPublishTimestamps().get(audit.getPublishTimestamps().size() - 1), audit.getPublishTimestamps().size());
		}
		EventPublicationAudit expected = audit;
		EventPublicationAudit actual = dao.getPublicationAudit(auditId);
		assertEquals(expected, actual);
	}
	
	@Test(expected=PeregrineException.class)
	public void insertWithNullTimestampsAuditTest() throws PeregrineException {
		String auditId = parentEventId;
		audit = new EventPublicationAudit();
		log.debug("insertWithNullTimestampsAuditTest(): EventPublicationAudit object is " + audit);
		Map<String,EventPublicationAudit> audits = new HashMap<String,EventPublicationAudit>();
		audits.put(auditId, audit);
		dao.insertPublicationAudit(audits);
	}
	
	@Test(expected=PeregrineException.class)
	public void getAuditWithInvalidEventIdTest() throws PeregrineException {
		eventId = "";
		log.debug("getAuditWithInvalidEventIdTest(): Event id of the audit to be retrieved is " + eventId);
		dao.getPublicationAudit(eventId);
	}
	
	/**************************
	 *Testing getLatestEvent()*
	 **************************/
	
	@Test(expected=PeregrineException.class)
	public void getLatestEventFromDbNoParamsTest() throws PeregrineException {
		EventsRequest req = new EventsRequest(null,null,null,null,null,null,null);
		log.debug("getLatestEventFromDbNoParamsTest(): EventsRequest object is " + req);
		dao.getLatestEvent(req);
	}
	
	@Test(expected=PeregrineException.class)
	public void getLatestEventFromDbNoMatchingParamsTest() throws PeregrineException {
		EventsRequest req = new EventsRequest(null,null,"","","","",null);
		log.debug("getLatestEventFromDbNoMatchingParamsTest(): EventsRequest object is " + req);
		dao.getLatestEvent(req);
	}
	
	@Test
	public void getLatestEventFromDbMultipleMatchingParamsTest() throws PeregrineException {
		EventsRequest req = new EventsRequest(null,null,
				getEvent2.getSource(),
				getEvent2.getObjectId(),
				null,
				getEvent2.getEventName(),
				null);
		log.debug("getLatestEventFromDbMultipleMatchingParamsTest(): EventsRequest object is " + req);
		Event actual = dao.getLatestEvent(req);
		Event expected = getEvent2;
		assertEquals(expected,actual);
	}
	
	/********************
	 *Testing getEvent()*
	 ********************/
	
	@Test
	public void getFromDbTest() throws PeregrineException {
		eventFromDb = dao.getEvent(child1EventId);
		log.debug("getFromDbTest(): Event Id is " + child1EventId);
		String expected = child1EventId;
		String actual = eventFromDb.getEventId();
		assertEquals(expected,actual);
	}
	
	@Test(expected=PeregrineException.class)
	public void eventNotFoundInDbTest() throws PeregrineException {
		//there shouldn't be an event with eventId of ""
		eventId = "";
		log.debug("eventNotFoundInDbTest(): Event Id is " + eventId);
		dao.getEvent(eventId);
	}
	
	/*********************
	 *Testing getEvents()*
	 *********************/
	
	@Test(expected=PeregrineException.class)
	public void getMultipleEventsFromDbOnlyGenParamAndCreatedAfterParamTest() throws PeregrineException  {
		EventsRequest req = new EventsRequest(createdAfter,null,null,null,null,null,generations);
		log.debug("getMultipleEventsFromDbOnlyGenParamAndCreatedAfterParamTest(): EventsRequest object is " + req);
		dao.getEvents(req);
	}
	
	@Test(expected=PeregrineException.class)
	public void getMultipleEventsFromDbNoParamsTest() throws PeregrineException {
		EventsRequest req = new EventsRequest(null,null,null,null,null,null,null);
		log.debug("getMultipleEventsFromDbNoParamsTest(): EventsRequest object is " + req);
		dao.getEvents(req);
	}
	
	@Test(expected=PeregrineException.class)
	public void getMultipleEventsFromDbOneParamTest() throws PeregrineException {
		EventsRequest req = new EventsRequest(createdAfter,null,null,null,null,null,null);
		log.debug("getMultipleEventsFromDbOneParamTest(): EventsRequest object is " + req);
		List<Event> eventList = new ArrayList<Event>();
		eventList = dao.getEvents(req);
		if(eventList.size() == 0){
			fail();
		} else {
			for(Event e : eventList){
				if(!(e.getTimestamp().getMillis() > createdAfter.getMillis())) fail();
			}
			//all the events have been compared and the comparisons did not fail
			assert(true);
		}
	}
	
	@Test(expected=PeregrineException.class)
	public void getMultipleEventsFromDbInvalidGenCountTest() throws PeregrineException  {
		generations = 0;
		EventsRequest req = new EventsRequest(createdAfter,null,null,null,null,null,generations);
		log.debug("getMultipleEventsFromDbInvalidGenCountTest(): EventsRequest object is " + req);
		dao.getEvents(req);
	}
	
	@Test(expected=PeregrineException.class)
	public void getMultipleEventsFromDbNoMatchingParamsTest() throws PeregrineException  {
		//there shouldn't be any events with params matching ""
		EventsRequest req = new EventsRequest(createdAfter,null,"","","","",generations);
		log.debug("getMultipleEventsFromDbNoMatchingParamsTest(): EventsRequest object is " + req);
		dao.getEvents(req);
	}

	@Test
	public void getMultipleEventsFromDbMultipleParamTest() throws PeregrineException {
		EventsRequest req = new EventsRequest(createdAfter,null,null,objectId,null,name,null);
		log.debug("getMultipleEventsFromDbMultipleParamTest(): EventsRequest object is " + req);
		List<Event> eventList = new ArrayList<Event>();
		eventList = dao.getEvents(req);
		if(eventList.size() == 0){
			fail();
		} else {
			for(Event e : eventList){
				if(!(e.getTimestamp().getMillis() > createdAfter.getMillis())) fail();
				if(!e.getObjectId().equals(objectId)) fail();
				if(!e.getEventName().equals(name)) fail();
			}
			//all the events have been compared and the comparisons did not fail
			assert(true);
		}
	}
	
	@Test
	public void getMultipleEventsFromDbAllParamTest() throws PeregrineException {
		EventsRequest req = new EventsRequest(createdAfter,createdBefore,source,objectId,correlationId,name,generations);
		log.debug("getMultipleEventsFromDbAllParamTest(): EventsRequest object is " + req);
		List<Event> eventList = new ArrayList<Event>();
		eventList = dao.getEvents(req);
		if(eventList.size() == 0){
			fail();
		} else {
			for(Event e : eventList){
				if(!(e.getTimestamp().getMillis() > createdAfter.getMillis())) fail();
				if(!(e.getTimestamp().getMillis() < createdBefore.getMillis())) fail();
				if(!e.getSource().equals(source)) fail();
				if(!e.getObjectId().equals(objectId)) fail();
				if(!e.getCorrelationId().equals(correlationId)) fail();
				if(!e.getEventName().equals(name)) fail();
			}
			//all the events have been compared and the comparisons did not fail
			assert(true);
		}
	}
	
	/***********************
	 *Testing insertEvent()*
	 ***********************/
	
	@Test
	public void insertToDbTest() throws PeregrineException {
		event = getEvent2;
		Map<String,String> headers = new HashMap<String,String>();
		Map<String,DataItem> payload = new HashMap<String,DataItem>();
		
		headers.put("some key", "some value");
		payload.put("some key", new DataItem("some data type","some value"));
		headers.put("some other key", "some other value");
		payload.put("some other key", new DataItem("some other data type","some other value"));
		
		event.setCustomHeaders(headers);
		event.setCustomPayload(payload);
		
		log.debug("insertToDbTest(): Event object is " + event);
		eventId = dao.insertEvent(event, new HashMap<String,EventPublicationAudit>());
		eventFromDb = dao.getEvent(eventId);
		String expected = eventId;
		String actual = eventFromDb.getEventId();
		assertEquals(expected,actual);	
	}
	
	@Test
	public void insertToAndRetrieveFromDbDateTimeTest() throws PeregrineException {
		event = getEvent2;
		event.setTimestamp(DateTime.now());
		
		log.debug("insertToAndRetrieveFromDbDateTimeTest(): Event object is " + event);
		eventId = dao.insertEvent(event, new HashMap<String,EventPublicationAudit>());
		eventFromDb = dao.getEvent(eventId);
		//datetime before insert into one of the DateTime fields
		String expected = event.getTimestamp().toString();
		//datetime 
		String actual = eventFromDb.getTimestamp().toString();
		assertEquals(expected, actual);
	}
	
	@Test(expected=PeregrineException.class)
	public void insertToDbObjectIdNullTest() throws PeregrineException {
		Event event2 = getEvent2;
		event2.setObjectId(null);
		Map<String,String> headers = new HashMap<String,String>();
		Map<String,DataItem> payload = new HashMap<String,DataItem>();
		
		headers.put("some key", "some value");
		payload.put("some key", new DataItem("some data type","some value"));
		headers.put("some other key", "some other value");
		payload.put("some other key", new DataItem("some other data type","some other value"));
		
		log.debug("insertToDbObjectIdNullTest(): Event object is " + event2);
		event2.setCustomHeaders(headers);
		event2.setCustomPayload(payload);
		
		eventId = dao.insertEvent(event2, new HashMap<String,EventPublicationAudit>());	
	}
	
	/************************
	 *Testing insertEvents()*
	 ************************/
	
	@Test
	public void insertMultipleEventsToDbTest() throws PeregrineException {
		event = getEvent2;
		Event event2 = getEvent3;
		List<Event> events = new ArrayList<Event>();
		events.add(event);
		events.add(event2);
		log.debug("insertMultipleEventsToDbTest(): List of Event objects is " + events);
		List<String> expected = new ArrayList<String>();
		expected = dao.insertEvents(events, new HashMap<String,EventPublicationAudit>());
		List<String> actual = new ArrayList<String>();
		for(String id : expected){
			actual.add((dao.getEvent(id)).getEventId());
		}
		assertEquals(expected, actual);
	}
	
	@Test(expected=PeregrineException.class)
	public void insertMultipleEventsToDbInvalidEventTest() throws PeregrineException {
		event = getEvent2;
		Event event2 = getEvent3;
		event2.setObjectId(null);
		List<Event> events = new ArrayList<Event>();
		events.add(event);
		events.add(event2);
		log.debug("insertMultipleEventsToDbInvalidEventTest(): List of Event objects is " + events);
		dao.insertEvents(events, new HashMap<String,EventPublicationAudit>());
	}
}