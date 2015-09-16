package com.alliancefoundry.tests;

import static org.junit.Assert.*;

import java.io.IOException;
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
	public void getFromDbTest() throws IOException {
		//run insert first, in order to find out a valid eventId
		eventId = "c0b3568f-0333-4bce-8d2b-eb84c354fabb";
		
		eventFromDb = dao.getEvent(eventId);
		String expected = eventId;
		String actual = eventFromDb.getEventId();
		System.out.println(expected + "     " + actual);
		assertEquals(expected,actual);
	}
	
	@Test
	public void EventNotFoundInDbTest() throws IOException {
		//there shouldn't be an event with eventId of ""
		eventId = "";
		
		Event eventFromDb = dao.getEvent(eventId);
		Event expected = null;
		Event actual = eventFromDb;
		assertEquals(expected, actual);
	}
	
	@Test
	public void insertToDbTest() throws IOException {
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
	public void insertMultipleEventsToDbTest() throws IOException {
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
	public void insertToAndRetrieveFromDbDateTimeTest() throws IOException {
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
