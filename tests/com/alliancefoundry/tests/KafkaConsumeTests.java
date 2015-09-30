package com.alliancefoundry.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.alliancefoundry.model.Event;
import com.alliancefoundry.publisher.EventServicePublisher;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class KafkaConsumeTests {
	
	Event event1, event2, event3, event4, event5, event6, event7, simpleEvent, 
		nullParentEvent, nullEventNameEvent, nullCorrelationIdEvent, nullSequenceNumEvent, 
		nullDataTypeEvent, nullSourceEvent, nullDestEvent, nullSubdestEvent, 
		nullPreStateEvent, nullPostStateEvent;
	EventServicePublisher publisher, publisher2;

	@Before
	public void setUp() throws Exception {
		
		AbstractApplicationContext ctx;
		ctx = new ClassPathXmlApplicationContext("kafka-mock-events.xml");

		event1 = ctx.getBean("event1", Event.class);
		event1.setEventId("18");
		event2 = ctx.getBean("event2", Event.class);
		event2.setEventId("200");
		event3 = ctx.getBean("event3", Event.class);
		event3.setEventId("201");
		event4 = ctx.getBean("event4", Event.class);
		event4.setEventId("202");
		event5 = ctx.getBean("event5", Event.class);
		event5.setEventId("5");
		
		event6 = ctx.getBean("event6", Event.class);
		event6.setEventId("206");
		event7 = ctx.getBean("event7", Event.class);
		event7.setEventId("207");
	
		simpleEvent = ctx.getBean("simpleEvent", Event.class);

		nullParentEvent = ctx.getBean("NullParentEvent", Event.class);
		nullEventNameEvent = ctx.getBean("NullEventNameEvent", Event.class);
		nullCorrelationIdEvent = ctx.getBean("NullCorrelationIdEvent", Event.class);
		nullSequenceNumEvent = ctx.getBean("NullSequenceNumEvent", Event.class);
		nullDataTypeEvent = ctx.getBean("NullDataTypeEvent", Event.class);
		nullSourceEvent = ctx.getBean("NullSourceEvent", Event.class);
		nullDestEvent = ctx.getBean("NullDestinationEvent", Event.class);
		nullSubdestEvent = ctx.getBean("NullSubdestinationEvent", Event.class);
		nullPreStateEvent = ctx.getBean("NullPreEventStateEvent", Event.class);
		nullPostStateEvent = ctx.getBean("NullPostEventStateEvent", Event.class);
				
		ctx.close();
		
		AbstractApplicationContext pubctx;
		pubctx = new ClassPathXmlApplicationContext("eventservice-beans.xml");
		pubctx.registerShutdownHook();

		// setup publiher
		publisher = pubctx.getBean("eventPublisherservice", EventServicePublisher.class);
		pubctx.close();
		
	}

	// Publish and Consume 1 event.
	@Test
	public void consumeTest1() throws JsonParseException, JsonMappingException, IOException {
		
		Event expected = event1;
		publisher.connectPublishers();
		publisher.publishEventByMapper(expected);
		
		KafkaSubscriber kafkaSubscriber = new KafkaSubscriber("testJaneDoe344");
		String event = kafkaSubscriber.consumeEvent();
		ObjectMapper mapper = new ObjectMapper(); 
		Event actual = mapper.readValue(event, Event.class);
		
		assertEquals(expected, actual);
	}
	
	// Publish and Consume a second event
		@Test
		public void consumeTest2() throws JsonParseException, JsonMappingException, IOException {
			
			Event expected = event2;
			
			publisher.connectPublishers();
			publisher.publishEventByMapper(expected);
			
			KafkaSubscriber kafkaSubscriber = new KafkaSubscriber("testJaneDoe");
			String event = kafkaSubscriber.consumeEvent();
			ObjectMapper mapper = new ObjectMapper(); 
			Event actual = mapper.readValue(event, Event.class);
			
			assertEquals(expected, actual);
		}
		
		// Publish and consume 2 events.
		@Test
		public void consumeTwoEventsTest() throws JsonParseException, JsonMappingException, IOException, IllegalAccessException, InvocationTargetException {
			
			List<Event> expected = new ArrayList<Event>();
			expected.add(event3); expected.add(event4);
	
			publisher.connectPublishers();
			publisher.publishEventByMapper(expected);
			
			List<Event> actual = new ArrayList<>();
			
			for(int i = 0; i < expected.size(); i++){
				
				KafkaSubscriber kafkaSubscriber = new KafkaSubscriber("janeCamels");

				String eventasString = kafkaSubscriber.consumeEvent();
				ObjectMapper mapper = new ObjectMapper(); 
				Event event = mapper.readValue(eventasString, Event.class);

				actual.add(event);

			}
			
			assertTrue(compareLists(expected, actual));
		}
		
		// Publish and consume 3 events.
		@Test
		public void consumeMultipleEventsTest() throws JsonParseException, JsonMappingException, IOException {
			
			List<Event> expected = new ArrayList<Event>();
			expected.add(event5); expected.add(event6); expected.add(event7);
			
			publisher.connectPublishers();
			publisher.publishEventByMapper(expected);
			
			List<Event> actual = new ArrayList<>();
			
			for(int i = 0; i < expected.size(); i++){
				
				KafkaSubscriber kafkaSubscriber = new KafkaSubscriber("JoeSchmoe");

				String eventasString = kafkaSubscriber.consumeEvent();
				ObjectMapper mapper = new ObjectMapper(); 
				Event event = mapper.readValue(eventasString, Event.class);

				actual.add(event);

			}
				
			for(Event ev : expected){
				assertTrue(actual.contains(ev));
				
			}

		}
		
		// Publish and Consume an event with only mandatory fields filled.
		@Test
		public void consumeEventWithNullsTest() throws JsonParseException, JsonMappingException, IOException {
			
			Event expected = simpleEvent;
			
			System.out.println("Event before publish: " + expected);
			
			publisher.connectPublishers();
			publisher.publishEventByMapper(expected);
			
			KafkaSubscriber kafkaSubscriber = new KafkaSubscriber("testJohnDoe89088i9");
			String event = kafkaSubscriber.consumeEvent();
			ObjectMapper mapper = new ObjectMapper(); 
			Event actual = mapper.readValue(event, Event.class);
			System.out.println("Event After Consume: " + actual);
			
			assertEquals(expected, actual);
		}
		
		// Test to see if, when an Event is published to a Topic, 2 different subscribers 
		// get the same event from the topic.
		@Test
		public void consumeEventMultipleSubscribersTest() throws JsonParseException, JsonMappingException, IOException {

			publisher.connectPublishers();
			publisher.publishEventByMapper(event1);
			
			AbstractApplicationContext pubctx2;
			pubctx2 = new ClassPathXmlApplicationContext("eventservice-beans.xml");
			pubctx2.registerShutdownHook();

			publisher2 = pubctx2.getBean("eventPublisherservice", EventServicePublisher.class);
			pubctx2.close();
			
			publisher2.connectPublishers();
			publisher2.publishEventByMapper(event1);
			
			KafkaSubscriber kafkaSubscriber1 = new KafkaSubscriber("testJaneDoe344");
			String event1 = kafkaSubscriber1.consumeEvent();
			ObjectMapper mapper1 = new ObjectMapper(); 
			Event expected = mapper1.readValue(event1, Event.class);
			
			KafkaSubscriber kafkaSubscriber2 = new KafkaSubscriber("testJaneDoe344");
			String event2 = kafkaSubscriber2.consumeEvent();
			ObjectMapper mapper2 = new ObjectMapper(); 
			Event actual = mapper2.readValue(event2, Event.class);
				
			assertEquals(expected, actual);
		}
		
		// Publish and Consume an event with a null parentId.
		@Test
		public void consumeEventWithNullParentIdTest() throws JsonParseException, JsonMappingException, IOException {
			
			Event expected = nullParentEvent;
			
			publisher.connectPublishers();
			publisher.publishEventByMapper(expected);
			
			KafkaSubscriber kafkaSubscriber = new KafkaSubscriber("testTopic100");
			String event = kafkaSubscriber.consumeEvent();
			ObjectMapper mapper = new ObjectMapper(); 
			Event actual = mapper.readValue(event, Event.class);
			
			assertEquals(expected, actual);
		}
		
		// Publish and Consume an event with a null eventName.
		@Test
		public void consumeEventWithNullEventNameTest() throws JsonParseException, JsonMappingException, IOException {
			
			Event expected = nullEventNameEvent;
			
			publisher.connectPublishers();
			publisher.publishEventByMapper(expected);
			
			KafkaSubscriber kafkaSubscriber = new KafkaSubscriber("testTopic2");
			String event = kafkaSubscriber.consumeEvent();
			ObjectMapper mapper = new ObjectMapper(); 
			Event actual = mapper.readValue(event, Event.class);
			
			assertEquals(expected, actual);
		}
		
		// Publish and Consume an event with a null correlationId.
		@Test
		public void consumeEventWithNullCorrelationIdTest() throws JsonParseException, JsonMappingException, IOException {
			
			Event expected = nullCorrelationIdEvent;
			
			publisher.connectPublishers();
			publisher.publishEventByMapper(expected);
			
			KafkaSubscriber kafkaSubscriber = new KafkaSubscriber("testTopic3");
			String event = kafkaSubscriber.consumeEvent();
			ObjectMapper mapper = new ObjectMapper(); 
			Event actual = mapper.readValue(event, Event.class);
			
			assertEquals(expected, actual);
		}
		
		// Publish and Consume an event with a null sequenceNumber.
		@Test
		public void consumeEventWithNullSequenceNumberTest() throws JsonParseException, JsonMappingException, IOException {
			
			Event expected = nullSequenceNumEvent;
			
			publisher.connectPublishers();
			publisher.publishEventByMapper(expected);
			
			KafkaSubscriber kafkaSubscriber = new KafkaSubscriber("testTopic400");
			String event = kafkaSubscriber.consumeEvent();
			ObjectMapper mapper = new ObjectMapper(); 
			Event actual = mapper.readValue(event, Event.class);
			
			assertEquals(expected, actual);
		}
		
		// Publish and Consume an event with a null dataType.
		@Test
		public void consumeEventWithNullDataTypeTest() throws JsonParseException, JsonMappingException, IOException {
			
			Event expected = nullDataTypeEvent;
			
			publisher.connectPublishers();
			publisher.publishEventByMapper(expected);
			
			KafkaSubscriber kafkaSubscriber = new KafkaSubscriber("testTopic5");
			String event = kafkaSubscriber.consumeEvent();
			ObjectMapper mapper = new ObjectMapper(); 
			Event actual = mapper.readValue(event, Event.class);
			
			assertEquals(expected, actual);
		}
		
		// Publish and Consume an event with a null source.
		@Test
		public void consumeEventWithNullSourceTest() throws JsonParseException, JsonMappingException, IOException {
			
			Event expected = nullSourceEvent;
			
			publisher.connectPublishers();
			publisher.publishEventByMapper(expected);
			
			KafkaSubscriber kafkaSubscriber = new KafkaSubscriber("testTopic6");
			String event = kafkaSubscriber.consumeEvent();
			ObjectMapper mapper = new ObjectMapper(); 
			Event actual = mapper.readValue(event, Event.class);
			
			assertEquals(expected, actual);
		}
		
		// Publish and Consume an event with a null destination.
		@Test
		public void consumeEventWithNullDestinationTest() throws JsonParseException, JsonMappingException, IOException {
			
			Event expected = nullDestEvent;
			
			publisher.connectPublishers();
			publisher.publishEventByMapper(expected);
			
			KafkaSubscriber kafkaSubscriber = new KafkaSubscriber("testTopic7");
			String event = kafkaSubscriber.consumeEvent();
			ObjectMapper mapper = new ObjectMapper(); 
			Event actual = mapper.readValue(event, Event.class);
			
			assertEquals(expected, actual);
		}
		
		// Publish and Consume an event with a null subdestination.
		@Test
		public void consumeEventWithNullSubdestinationTest() throws JsonParseException, JsonMappingException, IOException {
			
			Event expected = nullSubdestEvent;
			
			publisher.connectPublishers();
			publisher.publishEventByMapper(expected);
			
			KafkaSubscriber kafkaSubscriber = new KafkaSubscriber("testTopic8");
			String event = kafkaSubscriber.consumeEvent();
			ObjectMapper mapper = new ObjectMapper(); 
			Event actual = mapper.readValue(event, Event.class);
			
			assertEquals(expected, actual);
		}
		
		// Publish and Consume an event with a null preEventState.
		@Test
		public void consumeEventWithNullPreEventStateTest() throws JsonParseException, JsonMappingException, IOException {
			
			Event expected = nullPreStateEvent;
			
			publisher.connectPublishers();
			publisher.publishEventByMapper(expected);
			
			KafkaSubscriber kafkaSubscriber = new KafkaSubscriber("testTopic9");
			String event = kafkaSubscriber.consumeEvent();
			ObjectMapper mapper = new ObjectMapper(); 
			Event actual = mapper.readValue(event, Event.class);
			
			assertEquals(expected, actual);
		}
		
		// Publish and Consume an event with a null postEventState.
		@Test
		public void consumeEventWithNullPostEventStateTest() throws JsonParseException, JsonMappingException, IOException {
			
			Event expected = nullPostStateEvent;
			
			publisher.connectPublishers();
			publisher.publishEventByMapper(expected);
			
			KafkaSubscriber kafkaSubscriber = new KafkaSubscriber("testTopic10");
			String event = kafkaSubscriber.consumeEvent();
			ObjectMapper mapper = new ObjectMapper(); 
			Event actual = mapper.readValue(event, Event.class);
			
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
