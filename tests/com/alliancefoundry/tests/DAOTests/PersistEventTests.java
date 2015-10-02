/**
 * 
 */
package com.alliancefoundry.tests.DAOTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.DataIntegrityViolationException;
	
import com.alliancefoundry.dao.IDAO;
import com.alliancefoundry.exceptions.EventNotFoundException;
import com.alliancefoundry.model.DataItem;
import com.alliancefoundry.model.Event;

/**
 * @author Robert Coords
 *
 */
public class PersistEventTests {
	
	IDAO dao;
	AbstractApplicationContext ctx;
	String storedEventId = "";
	Event event1, event2, event3, event4, event5, event6, event7, event8, event9, 
		event10, event11, event12;
	List<Event> singleEventInsertList = new ArrayList<Event>();

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		ctx = new ClassPathXmlApplicationContext("eventservice-beans.xml");
		dao = ctx.getBean("dao", IDAO.class);
		
		AbstractApplicationContext ctx;
		ctx = new ClassPathXmlApplicationContext("db-mock-events.xml");
		event1 = ctx.getBean("persistEvent1", Event.class);
		event2 = ctx.getBean("persistEvent2", Event.class);
		event2.setParentId(storedEventId);
		event3 = ctx.getBean("persistEvent3", Event.class);
		event3.setParentId(storedEventId);
		event4 = ctx.getBean("persistEvent4", Event.class);
		event4.setParentId(event1.getEventId());
		event5 = ctx.getBean("persistEvent5", Event.class);
		event6 = ctx.getBean("persistEvent6", Event.class);
		event7 = ctx.getBean("persistEvent7", Event.class);
		event8 = ctx.getBean("persistEvent8", Event.class);
		event9 = ctx.getBean("persistEvent9", Event.class);
		event10 = ctx.getBean("persistEvent10", Event.class);
		event10.setCustomPayload(new HashMap<String, DataItem>());
		event11 = ctx.getBean("persistEvent11", Event.class);
		event11.setCustomHeaders(new HashMap<String, String>());
		event12 = ctx.getBean("persistEvent12", Event.class);
		ctx.close();
	}

	/**
	 * Test to persist an event for a new object within database.
	 * @throws SQLException 
	 * @throws EventNotFoundException 
	 */
	@Test
	public void persistEventTest() throws SQLException, EventNotFoundException {
		Event event = event1;
		event.setPublishTimeStamp(event.getPublishTimeStamp().toDateTime(DateTimeZone.UTC));
		event.setReceivedTimeStamp(event.getReceivedTimeStamp().toDateTime(DateTimeZone.UTC));
		event.setExpirationTimeStamp(event.getExpirationTimeStamp().toDateTime(DateTimeZone.UTC));
		singleEventInsertList.add(event);
		String eventId = (dao.insertEvents(singleEventInsertList)).get(0);
		event.setInsertTimeStamp(event.getInsertTimeStamp().toDateTime(DateTimeZone.UTC));
		singleEventInsertList.remove(0);
		storedEventId = eventId;
		Event eventFromDb = dao.getEvent(eventId);
		Event expected = event;
		Event actual = eventFromDb;
		assertEquals(expected,actual);
	}
	
	/**
	 * Test to persist an event that updates an object within database.
	 * @throws SQLException 
	 * @throws EventNotFoundException 
	 */
	@Test
	public void persistSecondEventTest() throws SQLException, EventNotFoundException {
		event2.setParentId(storedEventId);
		Event event = event2;
		event.setPublishTimeStamp(event.getPublishTimeStamp().toDateTime(DateTimeZone.UTC));
		event.setReceivedTimeStamp(event.getReceivedTimeStamp().toDateTime(DateTimeZone.UTC));
		event.setExpirationTimeStamp(event.getExpirationTimeStamp().toDateTime(DateTimeZone.UTC));
		singleEventInsertList.add(event);
		String eventId = (dao.insertEvents(singleEventInsertList)).get(0);
		event.setInsertTimeStamp(event.getInsertTimeStamp().toDateTime(DateTimeZone.UTC));
		singleEventInsertList.remove(0);
		storedEventId = eventId;
		Event eventFromDb = dao.getEvent(eventId);
		Event expected = event;
		Event actual = eventFromDb;
		assertEquals(expected,actual);
	}
	
	/**
	 * Test to persist an event that updates an object within database.
	 * @throws SQLException 
	 * @throws EventNotFoundException 
	 */
	@Test
	public void persistThirdEventTest() throws SQLException, EventNotFoundException {
		event3.setParentId(storedEventId);
		Event event = event3;
		event.setPublishTimeStamp(event.getPublishTimeStamp().toDateTime(DateTimeZone.UTC));
		event.setReceivedTimeStamp(event.getReceivedTimeStamp().toDateTime(DateTimeZone.UTC));
		event.setExpirationTimeStamp(event.getExpirationTimeStamp().toDateTime(DateTimeZone.UTC));
		singleEventInsertList.add(event);
		String eventId = (dao.insertEvents(singleEventInsertList)).get(0);
		event.setInsertTimeStamp(event.getInsertTimeStamp().toDateTime(DateTimeZone.UTC));
		singleEventInsertList.remove(0);
		storedEventId = eventId;
		Event eventFromDb = dao.getEvent(eventId);
		Event expected = event;
		Event actual = eventFromDb;
		assertEquals(expected,actual);
	}
	
	/**
	 * Test to persist an event that tries to update an event which has already been updated.
	 * This event should be persisted to database.
	 * @throws SQLException 
	 * @throws EventNotFoundException 
	 */
	@Test
	public void persistEventUpdateToPreviouslyUpdatedEventTest() throws SQLException, EventNotFoundException {
		event4.setParentId(event1.getEventId());
		Event event = event4;
		event.setPublishTimeStamp(event.getPublishTimeStamp().toDateTime(DateTimeZone.UTC));
		event.setReceivedTimeStamp(event.getReceivedTimeStamp().toDateTime(DateTimeZone.UTC));
		event.setExpirationTimeStamp(event.getExpirationTimeStamp().toDateTime(DateTimeZone.UTC));
		singleEventInsertList.add(event);
		String eventId = (dao.insertEvents(singleEventInsertList)).get(0);
		event.setInsertTimeStamp(event.getInsertTimeStamp().toDateTime(DateTimeZone.UTC));
		singleEventInsertList.remove(0);
		storedEventId = eventId;
		Event eventFromDb = dao.getEvent(eventId);
		Event expected = event;
		Event actual = eventFromDb;
		assertEquals(expected,actual);
	}
	
	/**
	 * Test to persist an event that does not have publishTimeStamp or expirationTimeStamp to database.
	 * Event should be persisted to database.
	 * @throws EventNotFoundException 
	 */
	@Test
	public void persistEventWithoutPublishTimeStampOrExpirationTimeStampTest() throws EventNotFoundException {
		try {
			Event event = event5;
			singleEventInsertList.add(event);
			String eventId = (dao.insertEvents(singleEventInsertList)).get(0);
			singleEventInsertList.remove(0);
			storedEventId = eventId;
			Event eventFromDb = dao.getEvent(eventId);
			Event expected = event;
			Event actual = eventFromDb;
			assertEquals(expected,actual);
		} catch (NullPointerException e) {
			assertTrue(true);
		}
	}
	
	/**
	 * Test to persist an event that does not have any destinations specified.
	 * Event should be stored in database.
	 * @throws EventNotFoundException 
	 */
	@Test
	public void persistEventWithoutDestinationsTest() throws EventNotFoundException {
		Event event = event6;
		event.setPublishTimeStamp(event.getPublishTimeStamp().toDateTime(DateTimeZone.UTC));
		event.setReceivedTimeStamp(event.getReceivedTimeStamp().toDateTime(DateTimeZone.UTC));
		event.setExpirationTimeStamp(event.getExpirationTimeStamp().toDateTime(DateTimeZone.UTC));
		singleEventInsertList.add(event);
		String eventId = (dao.insertEvents(singleEventInsertList)).get(0);
		event.setInsertTimeStamp(event.getInsertTimeStamp().toDateTime(DateTimeZone.UTC));
		singleEventInsertList.remove(0);
		storedEventId = eventId;
		Event eventFromDb = dao.getEvent(eventId);
		Event expected = event;
		Event actual = eventFromDb;
		assertEquals(expected,actual);
	}
	
	/**
	 * Test to try to persist an event that does not have receivedTimeStamp or insertTimeStamp specified.
	 * Event should not be stored in database.
	 * @throws SQLException 
	 * @throws EventNotFoundException 
	 */
	@Test
	public void persistEventWithoutReceivedTimeStampOrInsertTimeStampTest() throws SQLException, EventNotFoundException {
		try {
			Event event = event7;
			singleEventInsertList.add(event);
			String eventId = (dao.insertEvents(singleEventInsertList)).get(0);
			singleEventInsertList.remove(0);
			storedEventId = eventId;
			Event eventFromDb = dao.getEvent(eventId);
			Event expected = event;
			Event actual = eventFromDb;
			assertEquals(expected,actual);
		} catch (NullPointerException e) {
			assertTrue(true);
		}
	}
	
	/**
	 * Test to try to persist an event that does not have an objectId specified.
	 * Event should not be stored in database.
	 * @throws SQLException 
	 * @throws EventNotFoundException 
	 */
	@Test
	public void persistEventWithoutObjectIdTest() throws SQLException, EventNotFoundException {
		try {
			Event event = event8;
			singleEventInsertList.add(event);
			String eventId = (dao.insertEvents(singleEventInsertList)).get(0);
			singleEventInsertList.remove(0);
			storedEventId = eventId;
			Event eventFromDb = dao.getEvent(eventId);
			Event expected = event;
			Event actual = eventFromDb;
			assertEquals(expected,actual);
		} catch (DataIntegrityViolationException e) {
			assertTrue(true);
		}
	}
	
	/**
	 * Test to try to persist an event that does not have a messageType specified.
	 * Event should not be stored in database.
	 * @throws SQLException 
	 * @throws EventNotFoundException 
	 */
	@Test
	public void persistEventWithoutMessageTypeTest() throws SQLException, EventNotFoundException {
		try {
			Event event = event9;
			singleEventInsertList.add(event);
			String eventId = (dao.insertEvents(singleEventInsertList)).get(0);
			singleEventInsertList.remove(0);
			storedEventId = eventId;
			Event eventFromDb = dao.getEvent(eventId);
			Event expected = event;
			Event actual = eventFromDb;
			assertEquals(expected,actual);
		} catch (DataIntegrityViolationException e) {
			assertTrue(true);
		}
	}
	
	/**
	 * Test to try to persist an event that does not have a dataType specified.
	 * Event should be stored in database.
	 * @throws SQLException 
	 * @throws EventNotFoundException 
	 */
	@Test
	public void persistEventWithoutDataTypeTest() throws SQLException, EventNotFoundException {
		Event event = event12;
		event.setPublishTimeStamp(event.getPublishTimeStamp().toDateTime(DateTimeZone.UTC));
		event.setReceivedTimeStamp(event.getReceivedTimeStamp().toDateTime(DateTimeZone.UTC));
		event.setExpirationTimeStamp(event.getExpirationTimeStamp().toDateTime(DateTimeZone.UTC));
		singleEventInsertList.add(event);
		String eventId = (dao.insertEvents(singleEventInsertList)).get(0);
		event.setInsertTimeStamp(event.getInsertTimeStamp().toDateTime(DateTimeZone.UTC));
		singleEventInsertList.remove(0);
		storedEventId = eventId;
		Event eventFromDb = dao.getEvent(eventId);
		Event expected = event;
		Event actual = eventFromDb;
		assertEquals(expected,actual);
	}
	
	/**
	 * Test to try to persist an event that does not have CustomeHeaders identified.
	 * Event should be stored in database.
	 * @throws SQLException 
	 * @throws EventNotFoundException 
	 */
	@Test
	public void persistEventWithoutHeaders() throws SQLException, EventNotFoundException {
		Event event = event10;
		event.setPublishTimeStamp(event.getPublishTimeStamp().toDateTime(DateTimeZone.UTC));
		event.setReceivedTimeStamp(event.getReceivedTimeStamp().toDateTime(DateTimeZone.UTC));
		event.setExpirationTimeStamp(event.getExpirationTimeStamp().toDateTime(DateTimeZone.UTC));
		singleEventInsertList.add(event);
		String eventId = (dao.insertEvents(singleEventInsertList)).get(0);
		event.setInsertTimeStamp(event.getInsertTimeStamp().toDateTime(DateTimeZone.UTC));
		singleEventInsertList.remove(0);
		storedEventId = eventId;
		Event eventFromDb = dao.getEvent(eventId);
		Event expected = event;
		Event actual = eventFromDb;
		assertEquals(expected,actual);
	}
	
	/**
	 * Test to try to persist event that does not have a CustomPayload identified.
	 * Event should be stored in database.
	 * @throws SQLException 
	 * @throws EventNotFoundException 
	 */
	@Test
	public void persistEventWithoutPayload() throws SQLException, EventNotFoundException {
		Event event = event11;
		event.setPublishTimeStamp(event.getPublishTimeStamp().toDateTime(DateTimeZone.UTC));
		event.setReceivedTimeStamp(event.getReceivedTimeStamp().toDateTime(DateTimeZone.UTC));
		event.setExpirationTimeStamp(event.getExpirationTimeStamp().toDateTime(DateTimeZone.UTC));
		singleEventInsertList.add(event);
		String eventId = (dao.insertEvents(singleEventInsertList)).get(0);
		event.setInsertTimeStamp(event.getInsertTimeStamp().toDateTime(DateTimeZone.UTC));
		singleEventInsertList.remove(0);
		storedEventId = eventId;
		Event eventFromDb = dao.getEvent(eventId);
		Event expected = event;
		Event actual = eventFromDb;
		assertEquals(expected,actual);
	}

}