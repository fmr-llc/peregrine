package com.alliancefoundry.tests;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.alliancefoundry.dao.JDBCDAOimpl;
import com.alliancefoundry.model.DataItem;
import com.alliancefoundry.model.Event;
import com.alliancefoundry.model.EventsRequest;

public class JDBCDAOtest {
	
	JDBCDAOimpl dao;
	Event event;
	String eventId;
	Event eventFromDb;
	
	Event getEvent1;
	Event getEvent2;
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
		dao = new JDBCDAOimpl();

		createdAfter = new DateTime(0);
		createdBefore = DateTime.now();
		source = "some source";
		objectId = "some object id";
		correlationId = "some correlation id";
		name = "Testing name";
		generations = 2;
		
		parentEventId = UUID.randomUUID().toString();
		child1EventId = UUID.randomUUID().toString();
		child2EventId = UUID.randomUUID().toString();
		
		AbstractApplicationContext ctx;
		ctx = new ClassPathXmlApplicationContext("db-mock-events.xml");

		getEvent1 = ctx.getBean("parentEvent", Event.class);
		getEvent1.setEventId(parentEventId);
		getEvent2 = ctx.getBean("childEvent1", Event.class);
		getEvent2.setEventId(child1EventId);
		getEvent2.setParentId(parentEventId);
		getEvent3 = ctx.getBean("childEvent2", Event.class);
		getEvent3.setEventId(child2EventId);
		getEvent3.setParentId(parentEventId);
		
		ctx.close();
		
		dao.insertEvent(getEvent1);
		dao.insertEvent(getEvent2);
		dao.insertEvent(getEvent3);
	}
	
	/********************
	 *Testing getEvent()*
	 ********************/
	
	@Test
	public void getFromDbTest() {
		eventId = parentEventId;
		
		eventFromDb = dao.getEvent(eventId);
		String expected = eventId;
		String actual = eventFromDb.getEventId();
		assertEquals(expected,actual);
	}
	
	@Test
	public void EventNotFoundInDbTest() {
		//there shouldn't be an event with eventId of ""
		eventId = "";
		
		Event eventFromDb = dao.getEvent(eventId);
		Event expected = null;
		Event actual = eventFromDb;
		assertEquals(expected, actual);
	}
	
	/*********************
	 *Testing getEvents()*
	 *********************/
	
	@Test(expected=IllegalArgumentException.class)
	public void getMultipleEventsFromDbOnlyGenParamAndCreatedAfterParamTest() {
		EventsRequest req = new EventsRequest(createdAfter,null,null,null,null,null,generations);
		dao.getEvents(req);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void getMultipleEventsFromDbNoParamsTest() {
		EventsRequest req = new EventsRequest(null,null,null,null,null,null,null);
		dao.getEvents(req);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void getMultipleEventsFromDbOneParamTest() {
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
	
	@Test
	public void getMultipleEventsFromDbMultipleParamTest() {
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
	public void getMultipleEventsFromDbAllParamTest() {
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
	public void insertToDbTest() {
		event = new Event(
					"a",
					"Random Test",
					"a",
					"a",
					null,
					"a",
					"a",
					"a",
					"a",
					"a",
					true,
					null,
					DateTime.now(),
					DateTime.now(),
					"a",
					"a",
					false,
					DateTime.now()
				);
		Map<String,String> headers = new HashMap<String,String>();
		Map<String,DataItem> payload = new HashMap<String,DataItem>();
		
		headers.put("some key", "some value");
		payload.put("some key", new DataItem("some data type","some value"));
		headers.put("some other key", "some other value");
		payload.put("some other key", new DataItem("some other data type","some other value"));
		
		event.setCustomHeaders(headers);
		event.setCustomPayload(payload);
		
		try {
			eventId = dao.insertEvent(event);
		} catch (SQLException e) {
			System.out.println("There was a SQL issue when inserting the event into the database");
		}
		eventFromDb = dao.getEvent(eventId);
		String expected = eventId;
		String actual = eventFromDb.getEventId();
		assertEquals(expected,actual);	
	}
	
	@Test
	public void insertMultipleEventsToDbTest() {
		event = new Event(
			"c",
			"c",
			"c",
			"c",
			3,
			"c",
			"c",
			"c",
			"c",
			"c",
			true,
			DateTime.now(),
			DateTime.now(),
			DateTime.now(),
			"c",
			"c",
			true,
			DateTime.now()
		);
		Event event2 = new Event(
			"b",
			"b",
			"b",
			"b",
			3,
			"b",
			"b",
			"b",
			"b",
			"b",
			true,
			DateTime.now(),
			DateTime.now(),
			DateTime.now(),
			"b",
			"b",
			true,
			DateTime.now()
		);
		List<String> expected = new ArrayList<String>();
		try {
			expected.add(dao.insertEvent(event));
			expected.add(dao.insertEvent(event2));
		} catch (SQLException e) {
			System.out.println("There was a SQL issue when attempting to insert multiple events to the database");
		}
		List<String> actual = new ArrayList<String>();
		for(String id : expected){
			actual.add((dao.getEvent(id)).getEventId());
		}
		assertEquals(expected, actual);
	}
	
	@Test
	public void insertToAndRetrieveFromDbDateTimeTest() {
		DateTime datetime = DateTime.now();
		event = new Event(
					"a",
					"a",
					"a",
					"a",
					3,
					"a",
					"a",
					"a",
					"a",
					"a",
					true,
					datetime,
					datetime,
					datetime,
					"a",
					"a",
					true,
					datetime
				);
		try {
			eventId = dao.insertEvent(event);
		} catch (SQLException e) {
			System.out.println("There was a SQL issue when attempting to validate datetimes are inserted properly to the database");
		}
		eventFromDb = dao.getEvent(eventId);
		//datetime before insert into one of the DateTime fields
		String expected = datetime.toString();
		//datetime 
		String actual = eventFromDb.getPublishTimeStamp().toString();
		assertEquals(expected, actual);
	}
}
