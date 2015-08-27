package com.alliancefoundry.tests;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import com.alliancefoundry.dao.JDBCDAOimpl;
import com.alliancefoundry.model.Event;

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
		eventId = 1037;
		eventFromDb = dao.getEvent(eventId);
		long expected = eventId;
		long actual = eventFromDb.getEventId();
		assertEquals(expected, actual);
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
	
}
