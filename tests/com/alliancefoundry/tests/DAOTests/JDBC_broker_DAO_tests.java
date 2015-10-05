package com.alliancefoundry.tests.DAOTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.alliancefoundry.dao.IDAO;
import com.alliancefoundry.exceptions.EventNotFoundException;
import com.alliancefoundry.exceptions.PeregrineErrorCodes;
import com.alliancefoundry.exceptions.PeregrineException;
import com.alliancefoundry.model.DataItem;
import com.alliancefoundry.model.Event;
import com.alliancefoundry.publisher.PublisherRouter;
import com.alliancefoundry.tests.PublisherTests.KafkaTests.KafkaSubscriber;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by: Paul Fahey
 * 
 *
 */

public class JDBC_broker_DAO_tests {
	
	static final Logger log = LoggerFactory.getLogger(JDBC_broker_DAO_tests.class);
	
	IDAO dao;
	Event event;
	String eventId;
	Event eventFromDb;
	
	Event getEvent1, getEvent2, getEvent3, getEvent4;
	
	PublisherRouter publisher;

	@Before
	public void setUp() throws Exception {
		
		AbstractApplicationContext ctx;
		ctx = new ClassPathXmlApplicationContext("kafka-mock-events.xml");

		getEvent1 = ctx.getBean("event8", Event.class);
		getEvent2 = ctx.getBean("event9", Event.class);
		getEvent3 = ctx.getBean("event10", Event.class);
		getEvent4 = ctx.getBean("event11", Event.class);
		
		ctx.close(); 
		
		ctx = new ClassPathXmlApplicationContext("eventservice-beans.xml");
		
		dao = ctx.getBean("dao", IDAO.class);

		ctx.close();
		
		AbstractApplicationContext pubctx;
		pubctx = new ClassPathXmlApplicationContext("eventservice-beans.xml");
		pubctx.registerShutdownHook();

		// setup publiher
		publisher = pubctx.getBean("eventPublisherservice", PublisherRouter.class);
		pubctx.close();
	}
	
	/***********************
	 *Testing insertEvent()
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 * @throws EventNotFoundException 
	 * @throws PeregrineException *
	 ***********************/
	
	@Test
	public void insertToDbAndBrokerTest() throws EventNotFoundException, PeregrineException {
		event = getEvent2;

		Map<String,String> headers = new HashMap<String,String>();
		Map<String,DataItem> payload = new HashMap<String,DataItem>();
		
		headers.put("some key", "some value");
		payload.put("some key", new DataItem("some data type","some value"));
		headers.put("some other key", "some other value");
		payload.put("some other key", new DataItem("some other data type","some other value"));
		
		event.setCustomHeaders(headers);
		event.setCustomPayload(payload);
		
		String eventId = dao.insertEvent(event);
		
		publisher.attemptPublishEvent(event);
	
		eventFromDb = dao.getEvent(eventId);
	
		String expected = eventId;
		String actual = eventFromDb.getEventId();
		
		KafkaSubscriber kafkaSubscriber = new KafkaSubscriber("testJaneDoe347");
		String event_json = kafkaSubscriber.consumeEvent();
		ObjectMapper mapper = new ObjectMapper(); 
		Event actual2;
		try {
			actual2 = mapper.readValue(event_json, Event.class);
		} catch (JsonParseException e) {
			log.debug("Could not parse into JSON object.");
			throw new PeregrineException(PeregrineErrorCodes.JSON_PARSE_ERROR, "Could not parse into JSON object.", e);
		} catch (JsonMappingException e) {
			log.debug("Could not map JSON object into Event object.");
			throw new PeregrineException(PeregrineErrorCodes.JSON_MAPPING_ERROR, "Could not map JSON object into Event object.", e);
		} catch (IOException e) {
			log.debug("No import source found.");
			throw new PeregrineException(PeregrineErrorCodes.INPUT_SOURCE_ERROR, "No import source found.", e);
		}
		
		//db insert check
		assertEquals(expected,actual);	
		
		//broker check
		assertEquals(eventFromDb.getEventId(), actual2.getEventId());	
	}
	
	@Test(expected=PeregrineException.class)
	public void eventPublishFailTest() throws EventNotFoundException, PeregrineException {
		
		event = getEvent4;

		Map<String,String> headers = new HashMap<String,String>();
		Map<String,DataItem> payload = new HashMap<String,DataItem>();
		
		headers.put("some key", "some value");
		payload.put("some key", new DataItem("some data type","some value"));
		headers.put("some other key", "some other value");
		payload.put("some other key", new DataItem("some other data type","some other value"));
		
		event.setCustomHeaders(headers);
		event.setCustomPayload(payload);
		
		String eventId = dao.insertEvent(event);
		
		publisher.attemptPublishEvent(event);
	
		eventFromDb = dao.getEvent(eventId);
	
		String expected = eventId;
		String actual = eventFromDb.getEventId();
		
		KafkaSubscriber kafkaSubscriber = new KafkaSubscriber("testJaneDoe347");
		String event_json = kafkaSubscriber.consumeEvent();
		ObjectMapper mapper = new ObjectMapper(); 
		Event actual2;
		try {
			actual2 = mapper.readValue(event_json, Event.class);
		} catch (JsonParseException e) {
			log.debug("Could not parse into JSON object.");
			throw new PeregrineException(PeregrineErrorCodes.JSON_PARSE_ERROR, "Could not parse into JSON object.", e);
		} catch (JsonMappingException e) {
			log.debug("Could not map JSON object into Event object.");
			throw new PeregrineException(PeregrineErrorCodes.JSON_MAPPING_ERROR, "Could not map JSON object into Event object.", e);
		} catch (IOException e) {
			log.debug("No import source found.");
			throw new PeregrineException(PeregrineErrorCodes.INPUT_SOURCE_ERROR, "No import source found.", e);
		}
		
		
		//db insert check
		assertEquals(expected,actual);	
		
		//broker check
		assertEquals(eventFromDb.getEventId(), actual2.getEventId());	
	}
	
	
	@Test
	public void insertMultipleEventsToDbTest() throws PeregrineException, EventNotFoundException {
		
		event = getEvent1;
		Event event2 = getEvent3;
		List<Event> events = new ArrayList<Event>();
		events.add(event);
		events.add(event2);
		
		List<String> expected = new ArrayList<String>();
		expected = dao.insertEvents(events);
		
		publisher.attemptPublishEvents(events);
	
		List<String> actual = new ArrayList<String>();
		
		//for broker testing
		List<String> actual2 = new ArrayList<String>();
		
		for(String id : expected){
			
			actual.add((dao.getEvent(id)).getEventId());
			
			KafkaSubscriber kafkaSubscriber = new KafkaSubscriber("testJaneDoe346");
			
			String eventasString = kafkaSubscriber.consumeEvent();
			ObjectMapper mapper = new ObjectMapper(); 
			Event event;
			try {
				event = mapper.readValue(eventasString, Event.class);
			} catch (JsonParseException e) {
				log.debug("Could not parse into JSON object.");
				throw new PeregrineException(PeregrineErrorCodes.JSON_PARSE_ERROR, "Could not parse into JSON object.", e);
			} catch (JsonMappingException e) {
				log.debug("Could not map JSON object into Event object.");
				throw new PeregrineException(PeregrineErrorCodes.JSON_MAPPING_ERROR, "Could not map JSON object into Event object.", e);
			} catch (IOException e) {
				log.debug("No import source found.");
				throw new PeregrineException(PeregrineErrorCodes.INPUT_SOURCE_ERROR, "No import source found.", e);
			}
			

			actual2.add(event.getEventId());
		}
		
		//db insert check
		assertEquals(expected,actual);	
		
		//broker check
		for(String evid : expected){
			assertTrue(actual2.contains(evid));
		}

	}
	
	public boolean compareLists(List<String> expected, List<String> actual2) {
		if (expected.size() == actual2.size()) {
			for (int a = 0; a < expected.size(); a++) {
				String ev1 = expected.get(a);
				String ev2 = actual2.get(a);
				if (!ev1.equals(ev2)) {
					return false;
				}
			}
			return true;
		} else {
			return false;
		}
	}
}