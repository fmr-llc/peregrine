/**
 * 
 */
package com.alliancefoundry.tests;

import static org.junit.Assert.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

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
		
//		simpleEvent = new Event();
		simpleEvent = new Event(null, null, "asher456", null, null, "EventMessage", null, null, null, null, 
				false, new DateTime(), new DateTime("2015-09-09T10:07:18.285-0400"), new DateTime(), null, 
				null, false, new DateTime("2015-09-09T10:07:18.285-0400"));
		simpleEvent.setEventId("simple");
		
		configs = new HashMap<String, String>();
	}
	
	// Publish and Consume 1 event.
	@Test
	public void consumeTest1() throws JsonParseException, JsonMappingException, IOException {
		String topic = "testJaneDoe344";
		Event expected = event1;
		
		configs.put(EventServicePublisher.TOPIC_KEY, topic);
		configs.put(EventServicePublisher.DESTINATION_KEY, EventServicePublisher.KAFKA_KEY);
		
		EventServicePublisher manager = new EventServicePublisher();
		manager.setupPublishersViaAppContext();
		manager.connectPublishers();
		manager.publishEvent(expected, configs);
		
		KafkaSubscriber kafkaSubscriber = new KafkaSubscriber(topic);
		String event = kafkaSubscriber.consumeEvent();
		ObjectMapper mapper = new ObjectMapper(); 
		Event actual = mapper.readValue(event, Event.class);
		
		assertEquals(expected, actual);
	}
	
	// Publish and Consume a second event
	@Test
	public void consumeTest2() throws JsonParseException, JsonMappingException, IOException {
		String topic = "testJaneDoe";
		Event expected = event1;
		
		configs.put(EventServicePublisher.TOPIC_KEY, topic);
		configs.put(EventServicePublisher.DESTINATION_KEY, EventServicePublisher.KAFKA_KEY);
		
		EventServicePublisher manager = new EventServicePublisher();
		manager.setupPublishersViaAppContext();
		manager.connectPublishers();
		manager.publishEvent(expected, configs);
		
		KafkaSubscriber kafkaSubscriber = new KafkaSubscriber(topic);
		String event = kafkaSubscriber.consumeEvent();
		ObjectMapper mapper = new ObjectMapper(); 
		Event actual = mapper.readValue(event, Event.class);
		
		assertEquals(expected, actual);
	}
	
	// Publish and consume 2 events.
	@Test
	public void consumeTwoEventsTest() throws JsonParseException, JsonMappingException, IOException, IllegalAccessException, InvocationTargetException {
		String topic = "janeCamels";
		List<Event> expected = new ArrayList<Event>();
		expected.add(event3); expected.add(event4);
		
		configs.put(EventServicePublisher.TOPIC_KEY, topic);
		configs.put(EventServicePublisher.DESTINATION_KEY, EventServicePublisher.KAFKA_KEY);
		
		EventServicePublisher manager = new EventServicePublisher();
		manager.setupPublishersViaAppContext();
		manager.connectPublishers();
		manager.publishEvent(expected, configs);
		
		KafkaSubscriber kafkaSubscriber = new KafkaSubscriber(topic);
		String event = kafkaSubscriber.consumeEvent();
		ObjectMapper mapper = new ObjectMapper(); 
		List<Map> eventsAsMaps = mapper.readValue(event, List.class);
		
		List<Event> actual = new ArrayList<>();
		
		for(Map map : eventsAsMaps){
			actual.add(new Event(map));
		}
		
		assertTrue(compareLists(expected, actual));
	}
	
	// Publish and consume 3 events.
	@Test
	public void consumeMultipleEventsTest() throws JsonParseException, JsonMappingException, IOException {
		
		String topic = "JoeSchmoe";
		List<Event> expected = new ArrayList<Event>();
		expected.add(event5); expected.add(event6); expected.add(event7);
		
		configs.put(EventServicePublisher.TOPIC_KEY, topic);
		configs.put(EventServicePublisher.DESTINATION_KEY, EventServicePublisher.KAFKA_KEY);
		
		EventServicePublisher manager = new EventServicePublisher();
		manager.setupPublishersViaAppContext();
		manager.connectPublishers();
		manager.publishEvent(expected, configs);
	
		KafkaSubscriber kafkaSubscriber = new KafkaSubscriber(topic);
		String event = kafkaSubscriber.consumeEvent();
		ObjectMapper mapper = new ObjectMapper(); 
		List<Map> eventsAsMaps= mapper.readValue(event, List.class);
		
		List<Event> actual = new ArrayList<>();
		
		for(Map map : eventsAsMaps){
			actual.add(new Event(map));
		}
		
		assertTrue(compareLists(expected, actual));
	}
	
	
	// Publish and Consume an event with only mandatory fields filled.
	@Test
	public void consumeEventWithNullsTest() throws JsonParseException, JsonMappingException, IOException {
		
		String topic = "testJohnDoe89088i9";
		Event expected = simpleEvent;
		
		configs.put(EventServicePublisher.TOPIC_KEY, topic);
		configs.put(EventServicePublisher.DESTINATION_KEY, EventServicePublisher.KAFKA_KEY);
		
		EventServicePublisher manager = new EventServicePublisher();
		manager.setupPublishersViaAppContext();
		manager.connectPublishers();
		manager.publishEvent(expected, configs);
		
		KafkaSubscriber kafkaSubscriber = new KafkaSubscriber(topic);
		String event = kafkaSubscriber.consumeEvent();
		ObjectMapper mapper = new ObjectMapper(); 
		Event actual = mapper.readValue(event, Event.class);
		
		System.out.println(expected);
		System.out.println(actual);
		
		assertEquals(expected, actual);
	}
	
	// Test to see if, when an Event is published to a Topic, 2 different subscribers 
	// get the same event from the topic.
	@Test
	public void consumeEventMultipleSubscribersTest() throws JsonParseException, JsonMappingException, IOException {
		String topic = "testJohnDoe8675309";
		
		configs.put(EventServicePublisher.TOPIC_KEY, topic);
		configs.put(EventServicePublisher.DESTINATION_KEY, EventServicePublisher.KAFKA_KEY);
		
		EventServicePublisher manager1 = new EventServicePublisher();
		manager1.setupPublishersViaAppContext();
		manager1.connectPublishers();
		manager1.publishEvent(event1, configs);
		
		EventServicePublisher manager2 = new EventServicePublisher();
		manager2.setupPublishersViaAppContext();
		manager2.connectPublishers();
		manager2.publishEvent(event1, configs);
		
		KafkaSubscriber kafkaSubscriber1 = new KafkaSubscriber(topic);
		String event1 = kafkaSubscriber1.consumeEvent();
		ObjectMapper mapper1 = new ObjectMapper(); 
		Event expected = mapper1.readValue(event1, Event.class);
		
		KafkaSubscriber kafkaSubscriber2 = new KafkaSubscriber(topic);
		String event2 = kafkaSubscriber2.consumeEvent();
		ObjectMapper mapper2 = new ObjectMapper(); 
		Event actual = mapper2.readValue(event2, Event.class);
		
		System.out.println(expected);
		System.out.println(actual);
		
		assertEquals(expected, actual);
	}
	
	public boolean compareLists(List<Event> list1, List<Event> list2) {
		if (list1.size() == list2.size()) {
			for (int a = 0; a < list1.size(); a++) {
				Event ev1 = list1.get(a);
				Event ev2 = list2.get(a);
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
