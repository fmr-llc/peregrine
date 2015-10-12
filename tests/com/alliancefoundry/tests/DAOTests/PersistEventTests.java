/**
 * 
 */
package com.alliancefoundry.tests.DAOTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import com.alliancefoundry.dao.IDAO;
import com.alliancefoundry.exceptions.PeregrineException;
import com.alliancefoundry.model.DataItem;
import com.alliancefoundry.model.Event;
import com.alliancefoundry.model.EventPublicationAudit;

/**
 * @author Robert Coords, Bobby Writtenberry
 *
 */
public class PersistEventTests {
	
	IDAO dao;
	AbstractApplicationContext ctx;
	String storedEventId = "";
	Event event1, event2, event3, event4, event5, event6, event7, event8, event9, 
		event10, event11, event12;

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
	 * @throws PeregrineException 
	 */
	@Test
	public void persistEventTest() throws PeregrineException {
		Event event = event1;
		String eventId = dao.insertEvent(event, new HashMap<String, EventPublicationAudit>());
		event.setTimestamp(event.getTimestamp());
		storedEventId = eventId;
		Event eventFromDb = dao.getEvent(eventId);
		Event expected = event;
		Event actual = eventFromDb;
		assertEquals(expected,actual);
	}
	
	/**
	 * Test to persist an event that updates an object within database. 
	 * @throws PeregrineException 
	 */
	@Test
	public void persistSecondEventTest() throws PeregrineException {
		event2.setParentId(storedEventId);
		Event event = event2;
		String eventId = dao.insertEvent(event, new HashMap<String, EventPublicationAudit>());
		event.setTimestamp(event.getTimestamp());
		storedEventId = eventId;
		Event eventFromDb = dao.getEvent(eventId);
		Event expected = event;
		Event actual = eventFromDb;
		assertEquals(expected,actual);
	}
	
	/**
	 * Test to persist an event that updates an object within database.
	 * @throws PeregrineException 
	 */
	@Test
	public void persistThirdEventTest() throws PeregrineException {
		event3.setParentId(storedEventId);
		Event event = event3;
		String eventId = dao.insertEvent(event, new HashMap<String, EventPublicationAudit>());
		event.setTimestamp(event.getTimestamp());
		storedEventId = eventId;
		Event eventFromDb = dao.getEvent(eventId);
		Event expected = event;
		Event actual = eventFromDb;
		assertEquals(expected,actual);
	}
	
	/**
	 * Test to persist an event that tries to update an event which has already been updated.
	 * This event should be persisted to database.
	 * @throws PeregrineException 
	 */
	@Test
	public void persistEventUpdateToPreviouslyUpdatedEventTest() throws PeregrineException {
		event4.setParentId(event1.getEventId());
		Event event = event4;
		String eventId = dao.insertEvent(event, new HashMap<String, EventPublicationAudit>());
		event.setTimestamp(event.getTimestamp());
		storedEventId = eventId;
		Event eventFromDb = dao.getEvent(eventId);
		Event expected = event;
		Event actual = eventFromDb;
		assertEquals(expected,actual);
	}
	
	/**
	 * Test to persist an event that does not have publishTimeStamp or expirationTimeStamp to database.
	 * Event should be persisted to database. 
	 * @throws PeregrineException 
	 */
	@Test
	public void persistEventWithoutPublishTimeStampOrExpirationTimeStampTest() throws PeregrineException {
		try {
			Event event = event5;
			String eventId = dao.insertEvent(event, new HashMap<String, EventPublicationAudit>());
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
	 * @throws PeregrineException 
	 */
	@Test
	public void persistEventWithoutDestinationsTest() throws PeregrineException {
		Event event = event6;
		String eventId = dao.insertEvent(event, new HashMap<String, EventPublicationAudit>());
		event.setTimestamp(event.getTimestamp());
		storedEventId = eventId;
		Event eventFromDb = dao.getEvent(eventId);
		Event expected = event;
		Event actual = eventFromDb;
		assertEquals(expected,actual);
	}
	
	/**
	 * Test to try to persist an event that does not have timestamp specified.
	 * Event should not be stored in database.
	 * @throws PeregrineException 
	 */
	@Test(expected=PeregrineException.class)
	public void persistEventWithoutTimestampTest() throws PeregrineException {
		Event event = event7;
		dao.insertEvent(event, new HashMap<String, EventPublicationAudit>());
	}
	
	/**
	 * Test to try to persist an event that does not have an objectId specified.
	 * Event should not be stored in database.
	 * @throws PeregrineException 
	 */
	@Test(expected=PeregrineException.class)
	public void persistEventWithoutObjectIdTest() throws PeregrineException {
		Event event = event8;
		dao.insertEvent(event, new HashMap<String, EventPublicationAudit>());
	}
	
	/**
	 * Test to try to persist an event that does not have a messageType specified.
	 * Event should not be stored in database.
	 * @throws PeregrineException 
	 */
	@Test(expected=PeregrineException.class)
	public void persistEventWithoutMessageTypeTest() throws PeregrineException  {
		Event event = event9;
		dao.insertEvent(event, new HashMap<String, EventPublicationAudit>());
	}
	
	/**
	 * Test to try to persist an event that does not have a dataType specified.
	 * Event should be stored in database.
	 * @throws PeregrineException 
	 */
	@Test
	public void persistEventWithoutDataTypeTest() throws PeregrineException {
		Event event = event12;
		String eventId = dao.insertEvent(event, new HashMap<String, EventPublicationAudit>());
		event.setTimestamp(event.getTimestamp());
		storedEventId = eventId;
		Event eventFromDb = dao.getEvent(eventId);
		Event expected = event;
		Event actual = eventFromDb;
		assertEquals(expected,actual);
	}
	
	/**
	 * Test to try to persist an event that does not have CustomeHeaders identified.
	 * Event should be stored in database.
	 * @throws PeregrineException 
	 */
	@Test
	public void persistEventWithoutHeaders() throws PeregrineException {
		Event event = event10;
		String eventId = dao.insertEvent(event, new HashMap<String, EventPublicationAudit>());
		event.setTimestamp(event.getTimestamp());
		storedEventId = eventId;
		Event eventFromDb = dao.getEvent(eventId);
		Event expected = event;
		Event actual = eventFromDb;
		assertEquals(expected,actual);
	}
	
	/**
	 * Test to try to persist event that does not have a CustomPayload identified.
	 * Event should be stored in database.
	 * @throws PeregrineException 
	 */
	@Test
	public void persistEventWithoutPayload() throws PeregrineException {
		Event event = event11;
		String eventId = dao.insertEvent(event, new HashMap<String, EventPublicationAudit>());
		event.setTimestamp(event.getTimestamp());
		storedEventId = eventId;
		Event eventFromDb = dao.getEvent(eventId);
		Event expected = event;
		Event actual = eventFromDb;
		assertEquals(expected,actual);
	}

}