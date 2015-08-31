package com.alliancefoundry.tests;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import com.alliancefoundry.dao.JDBCDAOimpl;
import com.alliancefoundry.model.Event;
import com.alliancefoundry.model.EventsRequest;

public class JDBCDAOtest {
	
	JDBCDAOimpl dao;
	Event event;
	Event eventFromDb;
	long eventId;
	
	@Before
	public void setUp() throws Exception {
		dao = new JDBCDAOimpl();
	} 

	@Test
	public void getFromDbTest() {
		eventId = 1738;
		eventFromDb = dao.getEvent(eventId);
		long expected = eventId;
		long actual = eventFromDb.getEventId();
		assertEquals(expected, actual);
	}
	
	@Test
	public void getMultipleEventsFromDbTest() {
		EventsRequest req = new EventsRequest();
		req.setCreatedAfter(new DateTime(0));
		req.setCreatedBefore(DateTime.now());
		req.setSource("c");
		req.setObjectId("c");
		req.setCorrelationId("c");
		req.setName("c");
		req.setGenerations(0);
		List<Event> eventList = new ArrayList<Event>();
		eventList = dao.getEvents(req);
		//eventId = 1738;
		//eventFromDb = dao.getEvent(eventId);
		/*long expected = eventId;
		long actual = eventFromDb.getEventId();
		assertEquals(expected, actual);*/
		boolean exists = true;
		if(eventList.size() == 0){
			assert(false);
		} else {
			for(Event e : eventList){
				if(e.getSource() != "c") exists = false;
			}
			assert(exists);
		}
	}
	
	@Test
	public void EventNotFoundInDbTest() {
		//there shouldn't be an event with eventId of -1
		eventId = -1;
		
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
					"a",
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
		eventId = dao.insertEvent(event);
		eventFromDb = dao.getEvent(eventId);
		long expected = eventId;
		long actual = eventFromDb.getEventId();
		assertEquals(expected, actual);
	}
	
	@Test
	public void insertMultipleEventsToDbTest() {
		event = new Event(
			"c",
			"c",
			"c",
			"c",
			"c",
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
			"b",
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
		List<Long> expected = new ArrayList<Long>();
		expected.add(dao.insertEvent(event));
		expected.add(dao.insertEvent(event2));
		List<Long> actual = new ArrayList<Long>();
		for(long id : expected){
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
					"a",
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
		eventId = dao.insertEvent(event);
		eventFromDb = dao.getEvent(eventId);
		//datetime before insert into one of the DateTime fields
		String expected = datetime.toString();
		//datetime 
		String actual = eventFromDb.getPublishedTimeStamp().toString();
		assertEquals(expected, actual);
	}
	
	@Test
	public void persistEventWithoutPublishTimeStampOrExpirationTimeStampTest() {
		Event event = new Event(
				null,
				"NewObject",
				"2041501939154893",
				"785213",
				"1",
				"newObject",
				"Object",
				"TestSource",
				"TestDestination",
				"TestSubdestination",
				false,
				new DateTime(),
				DateTime.now(),
				new DateTime(),
				null,
				null,
				true,
				DateTime.now()
			);
		long eventId = dao.insertEvent(event);
		Event eventFromDb = dao.getEvent(eventId);
		DateTime expectedPTS = event.getPublishedTimeStamp();
		DateTime actualPTS = eventFromDb.getPublishedTimeStamp();
		DateTime expectedETS = event.getExpirationTimeStamp();
		DateTime actualETS = eventFromDb.getExpirationTimeStamp();
		System.out.println("expectedPTS: " + expectedPTS + "\nactualPTS: " + actualPTS + "\nexpectedETS: " + expectedETS + "\nactualETS: " + actualETS);
		assertEquals(expectedPTS.toString(), actualPTS.toString());
		assertEquals(expectedETS.toString(), actualETS.toString());
	}
	
}
