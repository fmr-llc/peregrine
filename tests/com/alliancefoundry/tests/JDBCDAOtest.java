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
		dao = new JDBCDAOimpl();

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
	 *Testing getLatestEvent()
	 * @throws SQLException *
	 **************************/
	
	@Test
	public void getLatestEventFromDbNoParamsTest() throws SQLException {
		EventsRequest req = new EventsRequest(null,null,null,null,null,null,null);
		long actual = dao.getLatestEvent(req).getInsertTimeStamp().getMillis();
		long expected = getEvent3.getInsertTimeStamp().getMillis();
		assertEquals(expected,actual);
	}
	
	@Test(expected=SQLException.class)
	public void getLatestEventFromDbNoMatchingParamsTest() throws SQLException {
		EventsRequest req = new EventsRequest(null,null,"","","","",null);
		dao.getLatestEvent(req);
	}
	
	@Test
	public void getLatestEventFromDbMultipleMatchingParamsTest() throws SQLException {
		EventsRequest req = new EventsRequest(null,null,
				getEvent2.getSource(),
				getEvent2.getObjectId(),
				getEvent2.getCorrelationId(),
				getEvent2.getEventName(),
				null);
		long actual = dao.getLatestEvent(req).getInsertTimeStamp().getMillis();
		long expected = getEvent2.getInsertTimeStamp().getMillis();
		assertEquals(expected,actual);
	}
	
	/********************
	 *Testing getEvent()
	 * @throws SQLException *
	 ********************/
	
	@Test
	public void getFromDbTest() throws SQLException {
		eventFromDb = dao.getEvent(child1EventId);
		String expected = child1EventId;
		String actual = eventFromDb.getEventId();
		assertEquals(expected,actual);
	}
	
	@Test(expected=SQLException.class)
	public void EventNotFoundInDbTest() throws SQLException {
		//there shouldn't be an event with eventId of ""
		eventId = "";
		dao.getEvent(eventId);
	}
	
	/*********************
	 *Testing getEvents()
	 * @throws SQLException 
	 * @throws IllegalArgumentException *
	 *********************/
	
	@Test(expected=IllegalArgumentException.class)
	public void getMultipleEventsFromDbOnlyGenParamAndCreatedAfterParamTest() throws IllegalArgumentException, SQLException {
		EventsRequest req = new EventsRequest(createdAfter,null,null,null,null,null,generations);
		dao.getEvents(req);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void getMultipleEventsFromDbNoParamsTest() throws IllegalArgumentException, SQLException {
		EventsRequest req = new EventsRequest(null,null,null,null,null,null,null);
		dao.getEvents(req);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void getMultipleEventsFromDbOneParamTest() throws IllegalArgumentException, SQLException {
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
	public void getMultipleEventsFromDbMultipleParamTest() throws IllegalArgumentException, SQLException {
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
	public void getMultipleEventsFromDbAllParamTest() throws IllegalArgumentException, SQLException {
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
	 *Testing insertEvent()
	 * @throws SQLException *
	 ***********************/
	
	@Test
	public void insertToDbTest() throws SQLException {
		event = getEvent2;
		event.setEventId(UUID.randomUUID().toString());
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
	public void insertToAndRetrieveFromDbDateTimeTest() throws SQLException {
		DateTime datetime = DateTime.now();
		event = getEvent2;
		event.setEventId(UUID.randomUUID().toString());
		event.setPublishTimeStamp(datetime);
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
	
	/***********************
	 *Testing insertEvents()
	 * @throws SQLException *
	 ***********************/
	
	@Test
	public void insertMultipleEventsToDbTest() throws SQLException {
		event = getEvent2;
		Event event2 = getEvent3;
		event.setEventId(UUID.randomUUID().toString());
		event2.setEventId(UUID.randomUUID().toString());
		List<Event> events = new ArrayList<Event>();
		events.add(event);
		events.add(event2);
		List<String> expected = new ArrayList<String>();
		try {
			expected = dao.insertEvents(events);
		} catch (SQLException e) {
			System.out.println("There was a SQL issue when attempting to insert multiple events to the database");
		}
		List<String> actual = new ArrayList<String>();
		for(String id : expected){
			actual.add((dao.getEvent(id)).getEventId());
		}
		assertEquals(expected, actual);
	}
	
	@Test(expected=SQLException.class)
	public void insertMultipleEventsToDbInvalidEventTest() throws SQLException{
		event = getEvent2;
		Event event2 = getEvent3;
		event.setEventId("multiple insert test");
		event2.setEventId("multiple insert test (2)");
		event2.setObjectId(null);
		List<Event> events = new ArrayList<Event>();
		events.add(event);
		events.add(event2);
		dao.insertEvents(events);
	}
}