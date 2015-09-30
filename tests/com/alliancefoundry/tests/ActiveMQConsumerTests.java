/**
 * 
 */
package com.alliancefoundry.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.alliancefoundry.model.Event;
import com.alliancefoundry.publisher.EventServicePublisher;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Robert Coords
 *
 */
public class ActiveMQConsumerTests {
	
	Event event1, event2, event3, nullEvent, nullParentIdEvent, nullEventNameEvent, 
		nullCorrelationIdEvent, nullSequenceNumberEvent, nullDataTypeEvent, 
		nullSourceEvent, nullDestinationEvent, nullSubdestinationEvent, 
		nullPreEventStateEvent, nullPostEventStateEvent;
	Map<String, String> configs;
	EventServicePublisher manager;
	MessageListener listener;
	Event eventFromListener, secondEvent;
	ActiveMQSubscriber subscriber, secondSubscriber, thirdSubscriber;
	
	@Before
	public void setUp() {
		AbstractApplicationContext ctx;
		ctx = new ClassPathXmlApplicationContext("activemq-mock-events.xml");
		ctx.registerShutdownHook();

		event1 = ctx.getBean("mockEvent1", Event.class);
		event2 = ctx.getBean("mockEvent2", Event.class);
		event3 = ctx.getBean("mockEvent3", Event.class);
		nullEvent = ctx.getBean("nullMockEvent", Event.class);
		nullParentIdEvent = ctx.getBean("nullParentIdEvent", Event.class);
		nullEventNameEvent = ctx.getBean("nullEventNameEvent", Event.class);
		nullCorrelationIdEvent = ctx.getBean("nullCorrelationIdEvent", Event.class);
		nullSequenceNumberEvent = ctx.getBean("nullSequenceNumberEvent", Event.class);
		nullDataTypeEvent = ctx.getBean("nullDataTypeEvent", Event.class);
		nullSourceEvent = ctx.getBean("nullSourceEvent", Event.class);
		nullDestinationEvent = ctx.getBean("nullDestinationEvent", Event.class);
		nullSubdestinationEvent = ctx.getBean("nullSubdestinationEvent", Event.class);
		nullPreEventStateEvent = ctx.getBean("nullPreEventStateEvent", Event.class);
		nullPostEventStateEvent = ctx.getBean("nullPostEventStateEvent", Event.class);
		
		ctx.close();
		
		ctx = new ClassPathXmlApplicationContext("eventservice-beans.xml");
		ctx.registerShutdownHook();
		// Create subscribers/consumers
		subscriber = ctx.getBean("activemqSubscriber1", ActiveMQSubscriber.class);
		secondSubscriber = ctx.getBean("activemqSubscriber2", ActiveMQSubscriber.class);
		thirdSubscriber = ctx.getBean("activemqSubscriber3", ActiveMQSubscriber.class);
		manager = ctx.getBean("eventPublisherservice", EventServicePublisher.class);
		ctx.close();

		subscriber.subscribeTopic("topic1");
		secondSubscriber.subscribeTopic("topic2");
		thirdSubscriber.subscribeTopic("topic1");
		
		listener = new MessageListener() {
			public void onMessage(Message message) {
				if(message instanceof TextMessage){
					TextMessage txt = (TextMessage)message;
					try {
						String eventAsJson = txt.getText();
						// turn json string back into event object
						ObjectMapper mapper = new ObjectMapper(); 
						eventFromListener = mapper.readValue(eventAsJson, Event.class);
					} catch (JsonParseException e) {
						System.out.println("Error converting JSON to an Object.");
					} catch (JsonMappingException e) {
						System.out.println("Error mapping JSON to Object.");
					} catch (IOException e) {
						System.out.println("Error parsing input source.");
					} catch (JMSException e) {
						System.out.println("An internal error occurred, preventing "
								+ "the JMS provider from retrieving the text.");
					}
				}				
			}
		};
		
		configs = new HashMap<String, String>();
		
		manager.connectPublishers();
	}
	
	// Base Test 1
	@Test
	public void baseTest1() {
		subscriber.setConsumerListener(listener);
				
		manager.publishEventByMapper(event1);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			System.out.println("Error sleep interrupted.");
		}
		
		Event expected = event1;
		Event actual = eventFromListener;
		
		assertEquals(expected.toString(), actual.toString());
	}
	
	// Base Test 2
	@Test
	public void baseTest2() {
		subscriber.setConsumerListener(listener);
		
		manager.publishEventByMapper(event2);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			System.out.println("Error sleep interrupted.");
		}
		
		Event expected = event2;
		Event actual = eventFromListener;
		
		assertEquals(expected.toString(), actual.toString());
	}
	
	// Publish and consume 2 events from 1 topic
	@Test
	public void consumeTwoEventsTest() {
		List<Event> actual = new ArrayList<Event>();
//		final Event tempEvent;
		MessageListener listener2;
		listener2 = new MessageListener() {
			public void onMessage(Message message) {
				if(message instanceof TextMessage){
					TextMessage txt = (TextMessage)message;
					try {
						String eventAsJson = txt.getText();
						// turn json string back into event object
						ObjectMapper mapper = new ObjectMapper(); 
						Event tempEvent = mapper.readValue(eventAsJson, Event.class);
						actual.add(tempEvent);
					} catch (JsonParseException e) {
						System.out.println("Error converting JSON to an Object.");
					} catch (JsonMappingException e) {
						System.out.println("Error mapping JSON to Object.");
					} catch (IOException e) {
						System.out.println("Error parsing input source.");
					} catch (JMSException e) {
						System.out.println("An internal error occurred, preventing "
								+ "the JMS provider from retrieving the text.");
					}
				}				
			}
		};
		
		List<Event> expected = new ArrayList<Event>();
		expected.add(event1); expected.add(event2);
		
		subscriber.setConsumerListener(listener2);
		
		manager.publishEventByMapper(event1);
		manager.publishEventByMapper(event2);
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			System.out.println("Error sleep interrupted.");
		}
		
		assertTrue(compareLists(expected, actual));
	}
	
	// Publish and consume multiple events from 1 topic
	@Test
	public void consumeMultipleEventsTest() {
		List<Event> actual = new ArrayList<Event>();
//		Event tempEvent1;
		MessageListener listener2;
		listener2 = new MessageListener() {
			public void onMessage(Message message) {
				if(message instanceof TextMessage){
					TextMessage txt = (TextMessage)message;
					try {
						String eventAsJson = txt.getText();
						// turn json string back into event object
						ObjectMapper mapper = new ObjectMapper(); 
						Event tempEvent = mapper.readValue(eventAsJson, Event.class);
						actual.add(tempEvent);
					} catch (JsonParseException e) {
						System.out.println("Error converting JSON to an Object.");
					} catch (JsonMappingException e) {
						System.out.println("Error mapping JSON to Object.");
					} catch (IOException e) {
						System.out.println("Error parsing input source.");
					} catch (JMSException e) {
						System.out.println("An internal error occurred, preventing "
								+ "the JMS provider from retrieving the text.");
					}
				}				
			}
		};
		
		List<Event> expected = new ArrayList<Event>();
		expected.add(event1); expected.add(event2); expected.add(event3);
		
		subscriber.setConsumerListener(listener2);
		
		manager.publishEventByMapper(event1);
		manager.publishEventByMapper(event2);
		manager.publishEventByMapper(event3);
		try {
			Thread.sleep(6000);
		} catch (InterruptedException e) {
			System.out.println("Error sleep interrupted.");
		}
		
		assertTrue(compareLists(expected, actual));
	}
	
	// Multiple subscribers consume the same event
	@Test
	public void consumeEventMultipleSubscribersTest() {
		MessageListener listener2;
		listener2 = new MessageListener() {
			public void onMessage(Message message) {
				if(message instanceof TextMessage){
					TextMessage txt = (TextMessage)message;
					try {
						String eventAsJson = txt.getText();
						// turn json string back into event object
						ObjectMapper mapper = new ObjectMapper(); 
						secondEvent = mapper.readValue(eventAsJson, Event.class);
					} catch (JsonParseException e) {
						System.out.println("Error converting JSON to an Object.");
					} catch (JsonMappingException e) {
						System.out.println("Error mapping JSON to Object.");
					} catch (IOException e) {
						System.out.println("Error parsing input source.");
					} catch (JMSException e) {
						System.out.println("An internal error occurred, preventing "
								+ "the JMS provider from retrieving the text.");
					}
				}				
			}
		};
		
		subscriber.setConsumerListener(listener);
		thirdSubscriber.setConsumerListener(listener2);
		
		manager.publishEventByMapper(event3);
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			System.out.println("Error sleep interrupted.");
		}
		
		Event expected = eventFromListener;
		Event actual = secondEvent;
		assertEquals(expected.toString(), actual.toString());
	}
	
	// Consume event with Nulls test
	@Test
	public void nullEventTest() {
		subscriber.setConsumerListener(listener);
		
		manager.publishEventByMapper(event2);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			System.out.println("Error sleep interrupted.");
		}
		
		Event expected = event2;
		Event actual = eventFromListener;
		
		assertEquals(expected.toString(), actual.toString());
	}
	
	// Consume an event with a null parent id
	@Test
	public void nullParentIdEventTest() {
		subscriber.setConsumerListener(listener);
		
		manager.publishEventByMapper(nullParentIdEvent);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			System.out.println("Error sleep interrupted.");
		}
		
		Event expected = nullParentIdEvent;
		Event actual = eventFromListener;
		
		assertEquals(expected.toString(), actual.toString());
	}
	
	// Consume an event with a null event name
	@Test
	public void nullEventNameEventTest() {
		subscriber.setConsumerListener(listener);
		
		manager.publishEventByMapper(nullEventNameEvent);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			System.out.println("Error sleep interrupted.");
		}
		
		Event expected = nullEventNameEvent;
		Event actual = eventFromListener;
		
		assertEquals(expected.toString(), actual.toString());
	}
	
	// Consume an event with a null correlation id
	@Test
	public void nullCorrelationIdEventTest() {
		subscriber.setConsumerListener(listener);
		
		manager.publishEventByMapper(nullCorrelationIdEvent);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			System.out.println("Error sleep interrupted.");
		}
		
		Event expected = nullCorrelationIdEvent;
		Event actual = eventFromListener;
		
		assertEquals(expected.toString(), actual.toString());
	}
	
	// Consume an event with a null sequence number
	@Test
	public void nullSequenceNumberEventTest() {
		subscriber.setConsumerListener(listener);
		
		manager.publishEventByMapper(nullSequenceNumberEvent);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			System.out.println("Error sleep interrupted.");
		}
		
		Event expected = nullSequenceNumberEvent;
		Event actual = eventFromListener;
		
		assertEquals(expected.toString(), actual.toString());
	}
	
	// Consume an event with a null data type
	@Test
	public void nullDataTypeEventTest() {
		subscriber.setConsumerListener(listener);
		
		manager.publishEventByMapper(nullDataTypeEvent);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			System.out.println("Error sleep interrupted.");
		}
		
		Event expected = nullDataTypeEvent;
		Event actual = eventFromListener;
		
		assertEquals(expected.toString(), actual.toString());
	}
	
	// Consume an event with a null source
	@Test
	public void nullSourceEventTest() {
		subscriber.setConsumerListener(listener);
		
		manager.publishEventByMapper(nullSourceEvent);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			System.out.println("Error sleep interrupted.");
		}
		
		Event expected = nullSourceEvent;
		Event actual = eventFromListener;
		
		assertEquals(expected.toString(), actual.toString());
	}
	
	// Consume an event with a null destination
	@Test
	public void nullDestinationEventTest() {
		subscriber.setConsumerListener(listener);
		
		manager.publishEventByMapper(nullDestinationEvent);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			System.out.println("Error sleep interrupted.");
		}
		
		Event expected = nullDestinationEvent;
		Event actual = eventFromListener;
		
		assertEquals(expected.toString(), actual.toString());
	}
	
	// Consume an event with a null subdestination
	@Test
	public void nullSubdestinationEventTest() {
		subscriber.setConsumerListener(listener);
		
		manager.publishEventByMapper(nullSubdestinationEvent);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			System.out.println("Error sleep interrupted.");
		}
		
		Event expected = nullSubdestinationEvent;
		Event actual = eventFromListener;
		
		assertEquals(expected.toString(), actual.toString());
	}
	
	// Consume an event with a null preEventState
	@Test
	public void nullPreEventStateEventTest() {
		subscriber.setConsumerListener(listener);
		
		manager.publishEventByMapper(nullPreEventStateEvent);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			System.out.println("Error sleep interrupted.");
		}
		
		Event expected = nullPreEventStateEvent;
		Event actual = eventFromListener;
		
		assertEquals(expected.toString(), actual.toString());
	}
	
	// Consume an event with a null postEventState
	@Test
	public void nullPostEventStateEventTest() {
		subscriber.setConsumerListener(listener);
		
		manager.publishEventByMapper(nullPostEventStateEvent);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			System.out.println("Error sleep interrupted.");
		}
		
		Event expected = nullPostEventStateEvent;
		Event actual = eventFromListener;
		
		assertEquals(expected.toString(), actual.toString());
	}
	
	// Publish event to multiple topics???
	
	
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
