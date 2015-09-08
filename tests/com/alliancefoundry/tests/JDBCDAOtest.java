package com.alliancefoundry.tests;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import com.alliancefoundry.dao.JDBCDAOimpl;
import com.alliancefoundry.model.DataItem;
import com.alliancefoundry.model.Event;
import com.alliancefoundry.model.EventsRequest;

public class JDBCDAOtest {
	
	JDBCDAOimpl dao;
	Event event;
	Event eventFromDb;
	String eventId;
	
	@Before
	public void setUp() throws Exception {
		dao = new JDBCDAOimpl();
	} 

	@Test
	public void getFromDbTest() {
		//run insert first, in order to find out a valid eventId
		eventId = "4413df38-53a2-44df-bb89-10ad79f01f2e";
		
		eventFromDb = dao.getEvent(eventId);
		String expected = eventId;
		String actual = eventFromDb.getEventId();
		assertEquals(expected,actual);
	}
	
	/*@Test
	public void getMultipleEventsFromDbNoParamsTest() {
		EventsRequest req = new EventsRequest();
		List<Event> eventList = new ArrayList<Event>();
		eventList = dao.getEvents(req);
		if(eventList.size() == 0){
			fail();
		} else {
			int count = 1;
			for(Event e : eventList){
				if(e.getEventId() != count) fail();
				count++;
			}
			assert(true);
		}
	}*/
	
	/*@Test
	public void getMultipleEventsFromDbOneParamTest() {
		EventsRequest req = new EventsRequest();
		req.setSource("a");
		List<Event> eventList = new ArrayList<Event>();
		eventList = dao.getEvents(req);
		if(eventList.size() == 0){
			fail();
		} else {
			for(Event e : eventList){
				if(!e.getSource().equals("a")) fail();
			}
			assert(true);
		}
	}
	
	@Test
	public void getMultipleEventsFromDbMultipleParamTest() {
		EventsRequest req = new EventsRequest();
		req.setObjectId("a");
		req.setName("a");
		List<Event> eventList = new ArrayList<Event>();
		eventList = dao.getEvents(req);
		if(eventList.size() == 0){
			fail();
		} else {
			for(Event e : eventList){
				if(!e.getObjectId().equals("a")) fail();
				if(!e.getEventName().equals("a")) fail();
			}
			assert(true);
		}
	}
	
	@Test
	public void getMultipleEventsFromDbAllParamTest() {
		EventsRequest req = new EventsRequest();
		DateTime currentTime = DateTime.now();
		req.setCreatedAfter(new DateTime(0));
		req.setCreatedBefore(currentTime);
		req.setSource("a");
		req.setObjectId("a");
		req.setCorrelationId("a");
		req.setName("a");
		req.setGenerations(0);
		List<Event> eventList = new ArrayList<Event>();
		eventList = dao.getEvents(req);
		if(eventList.size() == 0){
			fail();
		} else {
			for(Event e : eventList){
				if(!(e.getPublishTimeStamp().getMillis() > 0)) fail();
				if(!(e.getPublishTimeStamp().getMillis() < currentTime.getMillis())) fail();
				if(!e.getSource().equals("a")) fail();
				if(!e.getObjectId().equals("a")) fail();
				if(!e.getCorrelationId().equals("a")) fail();
				if(!e.getEventName().equals("a")) fail();
			}
			assert(true);
		}
	}*/
	
	@Test
	public void EventNotFoundInDbTest() {
		//there shouldn't be an event with eventId of ""
		eventId = "";
		
		Event eventFromDb = dao.getEvent(eventId);
		Event expected = null;
		Event actual = eventFromDb;
		assertEquals(expected, actual);
	}
	
	@Test
	public void insertToDbTest() {
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
					DateTime.now(),
					DateTime.now(),
					DateTime.now(),
					"a",
					"a",
					true,
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
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			expected.add(dao.insertEvent(event2));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		eventFromDb = dao.getEvent(eventId);
		//datetime before insert into one of the DateTime fields
		String expected = datetime.toString();
		//datetime 
		String actual = eventFromDb.getPublishTimeStamp().toString();
		assertEquals(expected, actual);
	}
}
