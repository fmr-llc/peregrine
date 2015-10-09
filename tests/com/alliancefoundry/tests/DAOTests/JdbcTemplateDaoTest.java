package com.alliancefoundry.tests.DAOTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import com.alliancefoundry.dao.IDAO;
import com.alliancefoundry.exceptions.PeregrineException;
import com.alliancefoundry.model.DataItem;
import com.alliancefoundry.model.Event;
import com.alliancefoundry.model.EventsRequest;

/**
 * Created by: Bobby Writtenberry
 *
 */
public class JdbcTemplateDaoTest {
	
	IDAO dao;
	AbstractApplicationContext ctx;
	Event event;
	String eventId;
	Event eventFromDb;
	
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
		parentEventId = dao.insertEvent(getEvent1);
		getEvent2 = ctx.getBean("childEvent1", Event.class);
		getEvent2.setParentId(parentEventId);
		child1EventId = dao.insertEvent(getEvent2);
		getEvent3 = ctx.getBean("childEvent2", Event.class);
		getEvent3.setParentId(parentEventId);
		child2EventId = dao.insertEvent(getEvent3);
		
		ctx.close();
	}
	
	/**************************
	 *Testing getLatestEvent()*
	 **************************/
	
	@Test(expected=PeregrineException.class)
	public void getLatestEventFromDbNoParamsTest() throws PeregrineException {
		EventsRequest req = new EventsRequest(null,null,null,null,null,null,null);
		dao.getLatestEvent(req);
	}
	
	@Test(expected=PeregrineException.class)
	public void getLatestEventFromDbNoMatchingParamsTest() throws PeregrineException {
		EventsRequest req = new EventsRequest(null,null,"","","","",null);
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
		long actual = dao.getLatestEvent(req).getInsertTimeStamp().getMillis();
		long expected = getEvent2.getInsertTimeStamp().getMillis();
		assertEquals(expected,actual);
	}
	
	/********************
	 *Testing getEvent()*
	 ********************/
	
	@Test
	public void getFromDbTest() throws PeregrineException {
		eventFromDb = dao.getEvent(child1EventId);
		String expected = child1EventId;
		String actual = eventFromDb.getEventId();
		assertEquals(expected,actual);
	}
	
	@Test(expected=PeregrineException.class)
	public void EventNotFoundInDbTest() throws PeregrineException {
		//there shouldn't be an event with eventId of ""
		eventId = "";
		dao.getEvent(eventId);
	}
	
	/*********************
	 *Testing getEvents()*
	 *********************/
	
	@Test(expected=PeregrineException.class)
	public void getMultipleEventsFromDbOnlyGenParamAndCreatedAfterParamTest() throws PeregrineException  {
		EventsRequest req = new EventsRequest(createdAfter,null,null,null,null,null,generations);
		dao.getEvents(req);
	}
	
	@Test(expected=PeregrineException.class)
	public void getMultipleEventsFromDbNoParamsTest() throws PeregrineException {
		EventsRequest req = new EventsRequest(null,null,null,null,null,null,null);
		dao.getEvents(req);
	}
	
	@Test(expected=PeregrineException.class)
	public void getMultipleEventsFromDbOneParamTest() throws PeregrineException {
		EventsRequest req = new EventsRequest(createdAfter,null,null,null,null,null,null);
		List<Event> eventList = new ArrayList<Event>();
		eventList = dao.getEvents(req);
		if(eventList.size() == 0){
			fail();
		} else {
			for(Event e : eventList){
				if(!(e.getPublishTimeStamp().getMillis() > createdAfter.getMillis())) fail();
			}
			//all the events have been compared and the comparisons did not fail
			assert(true);
		}
	}
	
	@Test(expected=PeregrineException.class)
	public void getMultipleEventsFromDbInvalidGenCountTest() throws PeregrineException  {
		generations = 0;
		EventsRequest req = new EventsRequest(createdAfter,null,null,null,null,null,generations);
		dao.getEvents(req);
	}
	
	@Test(expected=PeregrineException.class)
	public void getMultipleEventsFromDbNoMatchingParamsTest() throws PeregrineException  {
		//there shouldn't be any events with params matching ""
		EventsRequest req = new EventsRequest(createdAfter,null,"","","","",generations);
		dao.getEvents(req);
	}

	@Test
	public void getMultipleEventsFromDbMultipleParamTest() throws PeregrineException {
		EventsRequest req = new EventsRequest(createdAfter,null,null,objectId,null,name,null);
		List<Event> eventList = new ArrayList<Event>();
		eventList = dao.getEvents(req);
		if(eventList.size() == 0){
			fail();
		} else {
			for(Event e : eventList){
				if(!(e.getPublishTimeStamp().getMillis() > createdAfter.getMillis())) fail();
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
		List<Event> eventList = new ArrayList<Event>();
		eventList = dao.getEvents(req);
		if(eventList.size() == 0){
			fail();
		} else {
			for(Event e : eventList){
				if(!(e.getPublishTimeStamp().getMillis() > createdAfter.getMillis())) fail();
				if(!(e.getPublishTimeStamp().getMillis() < createdBefore.getMillis())) fail();
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
		
		eventId = dao.insertEvent(event);
		eventFromDb = dao.getEvent(eventId);
		String expected = eventId;
		String actual = eventFromDb.getEventId();
		assertEquals(expected,actual);	
	}
	
	@Test
	public void insertToAndRetrieveFromDbDateTimeTest() throws PeregrineException {
		DateTime datetime = DateTime.now();
		event = getEvent2;
		event.setPublishTimeStamp(datetime);
		eventId = dao.insertEvent(event);
		eventFromDb = dao.getEvent(eventId);
		//datetime before insert into one of the DateTime fields
		String expected = datetime.toString();
		//datetime 
		String actual = eventFromDb.getPublishTimeStamp().toString();
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
		
		event2.setCustomHeaders(headers);
		event2.setCustomPayload(payload);
		
		eventId = dao.insertEvent(event2);	
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
		List<String> expected = new ArrayList<String>();
		expected = dao.insertEvents(events);
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
		dao.insertEvents(events);
	}
}