/**
 * 
 */
package com.alliancefoundry.tests;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.junit.*;

import com.alliancefoundry.model.Event;
import com.alliancefoundry.publisher.EventServicePublisher;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Robert Coords
 *
 */
public class KafkaConsumeTests {
	
	Event event1, event2, event3, event4, event5, event6, event7, simpleEvent;
	Map<String, String> configs;
	
	@Before
	public void setUp() {
		event1 = new Event(null, "8675309", "464865", "464865", 1, "Insert", "Object", "Publisher", 
				"Kafka", "test2", false, new DateTime("2015-09-08T13:40:47.285-0400"), 
				new DateTime("2015-09-15T13:40:47.285-0400"), new DateTime("2015-09-15T13:40:47.285-0400"), 
				"pre", "post", true, new DateTime("2015-09-08T13:40:47.285-0400"));
		event1.setEventId("18");
		
		event2 = new Event("18", "8675310", "464865", "464865", 2, "Update", "Object", "Publisher", 
				"Kafka", "test2", false, DateTime.now(), DateTime.now(), DateTime.now().plusDays(7), 
				"pre", "post", true, DateTime.now());
		event2.setEventId("200");
		
		event3 = new Event(null, "841df123", "p8352", "p8352", 1, "Insert", "Object", "Publisher", "Kafka", 
				"test2", false, new DateTime("2015-09-09T10:07:18.285-0400"), 
				new DateTime("2015-09-09T10:07:18.285-0400"), new DateTime("2015-09-16T10:07:18.285-0400"), 
				"preState1", "postState1", true, new DateTime("2015-09-09T10:07:18.285-0400"));
		event3.setEventId("201");
		
		event4 = new Event("201", "841df124", "p8352", "p8352", 2, "Update", "Object", "Publisher", "Kafka", 
				"test2", false, new DateTime("2015-09-09T10:07:18.285-0400"), 
				new DateTime("2015-09-09T10:07:18.285-0400"), new DateTime("2015-09-16T10:07:18.285-0400"),
				"preEventState2", "postEventState2", true, new DateTime("2015-09-09T10:07:18.285-0400"));
		event4.setEventId("202");
		
		event5 = new Event(null, "841gh124", "p8352", "p8352", 1, "Insert", "Object", "EventService", 
				"destination", "topic", false, new DateTime("2015-09-09T10:07:18.285-0400"), 
				new DateTime("2015-09-09T10:07:18.285-0400"), new DateTime("2015-09-16T10:07:18.285-0400"), 
				"preState", "postState", true, new DateTime("2015-09-09T10:07:18.285-0400"));
		event5.setEventId("5");
		
		event6 = event2;
		event6.setEventId("206");
		event7 = event4;
		event7.setEventId("207");
		
		simpleEvent = new Event();
//		simpleEvent = new Event(null, null, "asher456", null, null, "EventMessage", null, null, null, null, 
//				null, new DateTime(), new DateTime("2015-09-09T10:07:18:48.285-0400"), new DateTime(), null, 
//				null, null, new DateTime("2015-09-09T10:07:18:48.285-0400"));
		simpleEvent.setPublishTimeStamp(null);
		simpleEvent.setExpirationTimeStamp(null);
		simpleEvent.setEventId("simple");
		
		configs = new HashMap<String, String>();
	}
	
	// Publish and Consume 1 event.
	@Test
	public void consumeTest1() throws JsonParseException, JsonMappingException, IOException {
		Event expected = event1;
		
		configs.put(EventServicePublisher.TOPIC_KEY, "test2");
		configs.put(EventServicePublisher.DESTINATION_KEY, EventServicePublisher.KAFKA_KEY);
		
		EventServicePublisher manager = new EventServicePublisher();
		manager.setupPublishersViaAppContext();
		manager.connectPublishers();
		manager.publishEvent(event1, configs);
		
		KafkaSubscriber kafkaSubscriber = new KafkaSubscriber();
		String event = kafkaSubscriber.consumeEvent();
		ObjectMapper mapper = new ObjectMapper(); 
		Event actual = mapper.readValue(event, Event.class);
		
		assertTrue(expected.equals(actual));
	}
	
	// Publish and Consume a second event
	@Test
	public void consumeTest2() throws JsonParseException, JsonMappingException, IOException {
		Event expected = event2;
		
		configs.put(EventServicePublisher.TOPIC_KEY, "test2");
		configs.put(EventServicePublisher.DESTINATION_KEY, EventServicePublisher.KAFKA_KEY);
		
		EventServicePublisher manager = new EventServicePublisher();
		manager.setupPublishersViaAppContext();
		manager.connectPublishers();
		manager.publishEvent(event2, configs);
		
		KafkaSubscriber kafkaSubscriber = new KafkaSubscriber();
		String event = kafkaSubscriber.consumeEvent();
		ObjectMapper mapper = new ObjectMapper(); 
		Event actual = mapper.readValue(event, Event.class);
		
		assertEquals(expected, actual);
	}
	
	// Publish and consume 2 events.
	@Test
	public void consumeTwoEventsTest() throws JsonParseException, JsonMappingException, IOException {
		List<Event> expected = new ArrayList<Event>();
		expected.add(event3); expected.add(event4);
		
		configs.put(EventServicePublisher.TOPIC_KEY, "test2");
		configs.put(EventServicePublisher.DESTINATION_KEY, EventServicePublisher.KAFKA_KEY);
		
		EventServicePublisher manager = new EventServicePublisher();
		manager.setupPublishersViaAppContext();
		manager.connectPublishers();
		manager.publishEvent(event3, configs);
		EventServicePublisher manager2 = new EventServicePublisher();
		manager2.setupPublishersViaAppContext();
		manager2.connectPublishers();
		manager2.publishEvent(event3, configs);
		manager2.publishEvent(event4, configs);
		
		KafkaSubscriber kafkaSubscriber = new KafkaSubscriber();
		String event = kafkaSubscriber.consumeEvent();
		ObjectMapper mapper = new ObjectMapper(); 
		Event[] actual = mapper.readValue(event, Event[].class);
		
		assertTrue(expected.equals(actual));
	}
	
	// Publish and consume 3 events.
	@Test
	public void consumeMultipleEventsTest() throws JsonParseException, JsonMappingException, IOException {
		List<Event> expected = new ArrayList<Event>();
		expected.add(event5); expected.add(event6); expected.add(event7);
		
		configs.put(EventServicePublisher.TOPIC_KEY, "test2");
		configs.put(EventServicePublisher.DESTINATION_KEY, EventServicePublisher.KAFKA_KEY);
		
		EventServicePublisher manager = new EventServicePublisher();
		manager.setupPublishersViaAppContext();
		manager.connectPublishers();
		manager.publishEvent(event5, configs);
		manager.publishEvent(event6, configs);
		manager.publishEvent(event7, configs);
		
		KafkaSubscriber kafkaSubscriber = new KafkaSubscriber();
		String event = kafkaSubscriber.consumeEvent();
		ObjectMapper mapper = new ObjectMapper(); 
		Event[] actual = mapper.readValue(event, Event[].class);
		
		assertEquals(expected, actual);
	}
	
	// Publish and Consume an event with only mandatory fields filled.
	@Test
	public void consumeEventWithNullsTest() throws JsonParseException, JsonMappingException, IOException {
		Event expected = simpleEvent;
		
		configs.put(EventServicePublisher.TOPIC_KEY, "test2");
		configs.put(EventServicePublisher.DESTINATION_KEY, EventServicePublisher.KAFKA_KEY);
		
		EventServicePublisher manager = new EventServicePublisher();
		manager.setupPublishersViaAppContext();
		manager.connectPublishers();
		manager.publishEvent(simpleEvent, configs);
		
		KafkaSubscriber kafkaSubscriber = new KafkaSubscriber();
		String event = kafkaSubscriber.consumeEvent();
		ObjectMapper mapper = new ObjectMapper(); 
		Event actual = mapper.readValue(event, Event.class);
		
		assertEquals(expected, actual);
	}
}
