/**
 * 
 */
package com.alliancefoundry.tests;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.alliancefoundry.dao.JDBCDAOimpl;
import com.alliancefoundry.model.DataItem;
import com.alliancefoundry.model.Event;
import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;

/**
 * @author Robert Coords
 *
 */
public class PersistEventTests {
	
	JDBCDAOimpl dao;
	String storedEventId;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		dao = new JDBCDAOimpl();
		storedEventId = "";
	}

	/**
	 * Test to persist an event for a new object within database.
	 * @throws SQLException 
	 */
	@Test
	public void persistEventTest() throws SQLException {
		Event event = new Event(
				null,
				"NewObject",
				"2014041501939907",
				"131565",
				1,
				"newObject",
				"Object",
				"TestSource",
				"TestDestination",
				"TestSubdestination",
				false,
				DateTime.now(),
				DateTime.now(),
				DateTime.now().plusDays(7),
				null,
				null,
				true,
				DateTime.now()
			);
		String eventId = dao.insertEvent(event);
		storedEventId = eventId;
		Event eventFromDb = dao.getEvent(eventId);
		String expected = eventId;
		String actual = eventFromDb.getEventId();
		assertEquals(expected, actual);
	}
	
	/**
	 * Test to persist an event that updates an object within database.
	 * @throws SQLException 
	 */
	@Test
	public void persistSecondEventTest() throws SQLException {
		Event event = new Event(
				storedEventId + "",
				"ObjectUpdate",
				"2014041501939907",
				"131565",
				2,
				"updateObject",
				"Object",
				"TestSource",
				"TestDestination",
				"TestSubdestination",
				false,
				DateTime.now(),
				DateTime.now(),
				DateTime.now().plusDays(7),
				null,
				null,
				true,
				DateTime.now()
			);
		String eventId = dao.insertEvent(event);
		storedEventId = eventId;
		Event eventFromDb = dao.getEvent(eventId);
		String expected = eventId;
		String actual = eventFromDb.getEventId();
		assertEquals(expected, actual);
	}
	
	/**
	 * Test to persist an event that updates an object within database.
	 * @throws SQLException 
	 */
	@Test
	public void persistThirdEventTest() throws SQLException {
		Event event = new Event(
				storedEventId + "",
				"ObjectUpdate",
				"2014041501939907",
				"131565",
				3,
				"updateObject",
				"Object",
				"TestSource",
				"TestDestination",
				"TestSubdestination",
				false,
				DateTime.now(),
				DateTime.now(),
				DateTime.now().plusDays(7),
				null,
				null,
				true,
				DateTime.now()
			);
		String eventId = dao.insertEvent(event);
		Event eventFromDb = dao.getEvent(eventId);
		String expected = eventId;
		String actual = eventFromDb.getEventId();
		assertEquals(expected, actual);
	}
	
	/**
	 * Test to persist an event that tries to update an event which has already been updated.
	 * This event should be persisted to database.
	 * @throws SQLException 
	 */
	@Test
	public void persistEventUpdateToPreviouslyUpdatedEventTest() throws SQLException {
		Event event = new Event(
				"1062",
				"ObjectUpdate",
				"2014041501939907",
				"131565",
				2,
				"updateObject",
				"Object",
				"TestSource",
				"TestDestination",
				"TestSubdestination",
				false,
				DateTime.now(),
				DateTime.now(),
				DateTime.now().plusDays(7),
				"1062",
				null,
				true,
				DateTime.now()
			);
		String eventId = dao.insertEvent(event);
		Event eventFromDb = dao.getEvent(eventId);
		String expected = eventId;
		String actual = eventFromDb.getEventId();
		assertEquals(expected, actual);
	}
	
	/**
	 * Test to persist an event that does not have publishTimeStamp or expirationTimeStamp to database.
	 * Event should be persisted to database.
	 */
	@Test
	public void persistEventWithoutPublishTimeStampOrExpirationTimeStampTest() {
		Event event = new Event(
				null,
				"NewObject",
				"2041501939154893",
				"785213",
				1,
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
		
		event.setPublishTimeStamp(null);
		event.setExpirationTimeStamp(null);
		System.out.println("1: " + event.getPublishTimeStamp() + " - " + event.getExpirationTimeStamp());
		String eventId;
		try {
			eventId = dao.insertEvent(event);
			System.out.println("2: " + eventId);
			Event eventFromDb = dao.getEvent(eventId);
			Event expected = event;
			Event actual = eventFromDb;
			assertEquals(expected.getPublishTimeStamp(), actual.getPublishTimeStamp());
			assertEquals(expected.getExpirationTimeStamp(), actual.getExpirationTimeStamp());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		DateTime expectedPTS = event.getPublishTimeStamp();
//		DateTime actualPTS = eventFromDb.getPublishTimeStamp();
//		DateTime expectedETS = event.getExpirationTimeStamp();
//		DateTime actualETS = eventFromDb.getExpirationTimeStamp();
//		System.out.println("expectedPTS: " + expectedPTS + "\nactualPTS: " + actualPTS + "\nexpectedETS: " + expectedETS + "\nactualETS: " + actualETS);
//		assertEquals(expectedPTS.toString(), actualPTS.toString());
//		assertEquals(expectedETS.toString(), actualETS.toString());
	}
	
	/**
	 * Test to persist an event that does not have any destinations specified.
	 * Event should be stored in database.
	 */
	@Test
	public void persistEventWithoutDestinationsTest() {
		Event event = new Event(
				null,
				"NewObject",
				"2049391548931234",
				"685213",
				1,
				"newObject",
				"Object",
				"TestSource",
				null,
				null,
				false,
				DateTime.now(),
				DateTime.now(),
				DateTime.now().plusDays(7),
				null,
				null,
				true,
				DateTime.now()
			);
		String eventId;
		try {
			eventId = dao.insertEvent(event);
			Event eventFromDb = dao.getEvent(eventId);
			String expectedD = event.getDestination();
			String actualD = eventFromDb.getDestination();
			String expectedS = event.getSubdestination();
			String actualS = event.getSubdestination();
			assertEquals(expectedD, actualD);
			assertEquals(expectedS, actualS);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Test to try to persist an event that does not have receivedTimeStamp or insertTimeStamp specified.
	 * Event should not be stored in database.
	 * @throws SQLException 
	 */
	@Test
	public void persistEventWithoutReceivedTimeStampOrInsertTimeStampTest() throws SQLException {
		Event event = new Event(
				null,
				"NewObject",
				"2049391648931234",
				"685203",
				1,
				"newObject",
				"Object",
				"TestSource",
				"TestDestination",
				"TestSubdestination",
				false,
				DateTime.now(),
				new DateTime(),
				DateTime.now().plusDays(7),
				null,
				null,
				true,
				new DateTime()
			);
		try {
			event.setReceivedTimeStamp(null);
			event.setInsertTimeStamp(null);
			String eventId = dao.insertEvent(event);
			Event eventFromDb = dao.getEvent(eventId);
			Event expected = null;
			Event actual = eventFromDb;
			assertEquals(expected, actual);
		} catch (NullPointerException e) {
			assertTrue(true);
		}
	}
	
	/**
	 * Test to try to persist an event that does not have an objectId specified.
	 * Event should not be stored in database.
	 * @throws SQLException 
	 */
	@Test
	public void persistEventWithoutObjectIdTest() throws SQLException {
		Event event = new Event(
				null,
				"NewObject",
				null,
				"685203",
				1,
				"newObject",
				"Object",
				"TestSource",
				"TestDestination",
				"TestSubdestination",
				false,
				DateTime.now(),
				DateTime.now(),
				DateTime.now().plusDays(7),
				null,
				null,
				true,
				DateTime.now()
			);
		try {
			String eventId = dao.insertEvent(event);
			Event eventFromDb = dao.getEvent(eventId);
			Event expected = null;
			Event actual = eventFromDb;
			assertEquals(expected, actual);
		} catch (SQLException e) {
			assertTrue(true);
		}
	}
	
	/**
	 * Test to try to persist an event that does not have a messageType specified.
	 * Event should not be stored in database.
	 * @throws SQLException 
	 */
	@Test
	public void persistEventWithoutMessageTypeTest() throws SQLException {
		Event event = new Event(
				null,
				"NewObject",
				"2049991648931234",
				"685200",
				1,
				null,
				"Object",
				"TestSource",
				"TestDestination",
				"TestSubdestination",
				false,
				DateTime.now(),
				DateTime.now(),
				DateTime.now().plusDays(7),
				null,
				null,
				true,
				DateTime.now()
			);
		try {
			String eventId = dao.insertEvent(event);
			Event eventFromDb = dao.getEvent(eventId);
			Event expected = null;
			Event actual = eventFromDb;
			assertEquals(expected, actual);
		} catch (SQLException e) {
			assertTrue(true);
		}
	}
	
	/**
	 * Test to try to persist an event that does not have a dataType specified.
	 * Event should be stored in database.
	 * @throws SQLException 
	 */
	@Test
	public void persistEventWithoutDataTypeTest() throws SQLException {
		Event event = new Event(
				null,
				"NewObject",
				"2049991648931234",
				"685200",
				1,
				"newObject",
				null,
				"TestSource",
				"TestDestination",
				"TestSubdestination",
				false,
				DateTime.now(),
				DateTime.now(),
				DateTime.now().plusDays(7),
				null,
				null,
				true,
				DateTime.now()
			);
		String eventId = dao.insertEvent(event);
		Event eventFromDb = dao.getEvent(eventId);
		String expected = null;
		String actual = eventFromDb.getDataType();
		assertEquals(expected, actual);
	}
	
	/**
	 * Test to try to persist an event that does not have CustomeHeaders identified.
	 * Event should be stored in database.
	 * @throws SQLException 
	 */
	@Test
	public void persistEventWithoutHeaders() throws SQLException {
		Event event = new Event(
				null,
				"NewObject",
				"1149991648931234",
				"685201",
				1,
				"newObject",
				null,
				"TestSource",
				"TestDestination",
				"TestSubdestination",
				false,
				DateTime.now(),
				DateTime.now(),
				DateTime.now().plusDays(7),
				null,
				null,
				true,
				DateTime.now()
			);
		event.setCustomHeaders(new HashMap<String, String>());
		String eventId = dao.insertEvent(event);
		Event eventFromDb = dao.getEvent(eventId);
		Map<String, String> expected = new HashMap<String, String>();
		Map<String, String> actual = eventFromDb.getCustomHeaders();
		assertEquals(expected, actual);
	}
	
	/**
	 * Test to try to persist event that does not have a CustomPayload identified.
	 * Event should be stored in database.
	 * @throws SQLException 
	 */
	@Test
	public void persistEventWithoutPayload() throws SQLException {
		Event event = new Event(
				null,
				"NewObject",
				"1248991648931234",
				"685202",
				1,
				"newObject",
				null,
				"TestSource",
				"TestDestination",
				"TestSubdestination",
				false,
				DateTime.now(),
				DateTime.now(),
				DateTime.now().plusDays(7),
				null,
				null,
				true,
				DateTime.now()
			);
		event.setCustomPayload(new HashMap<String, DataItem>());
		String eventId = dao.insertEvent(event);
		Event eventFromDb = dao.getEvent(eventId);
		Map<String, DataItem> expected = new HashMap<String, DataItem>();
		Map<String, DataItem> actual = eventFromDb.getCustomPayload();
		assertEquals(expected, actual);
	}

}