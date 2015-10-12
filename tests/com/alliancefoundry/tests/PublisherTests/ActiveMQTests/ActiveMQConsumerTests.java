/**
 * 
 */
package com.alliancefoundry.tests.PublisherTests.ActiveMQTests;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.alliancefoundry.exceptions.PeregrineErrorCodes;
import com.alliancefoundry.exceptions.PeregrineException;
import com.alliancefoundry.model.Event;
import com.alliancefoundry.publisher.PublisherRouter;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Robert Coords, Paul Fahey
 *
 */
public class ActiveMQConsumerTests {
	
	static final Logger log = LoggerFactory.getLogger(ActiveMQConsumerTests.class);
	
	Event event1, event2, event3, event4, event5, event6, event7, event8, nullEvent, nullParentIdEvent, nullEventNameEvent, 
		nullCorrelationIdEvent, nullSequenceNumberEvent, nullDataTypeEvent, 
		nullSourceEvent, nullDestinationEvent, nullSubdestinationEvent, 
		nullPreEventStateEvent, nullPostEventStateEvent;
	Map<String, String> configs;
	PublisherRouter manager;
	MessageListener listener;
	Event eventFromListener, secondEvent;
	ActiveMQSubscriber subscriber, secondSubscriber, thirdSubscriber, s4, s5, s6, s7, s8, s9, s10, s11, s12, 
	s13, s14, s15, s16, s17;
	
	PeregrineException exception = null;
	
	@Before
	public void setUp() {
		AbstractApplicationContext ctx;
		ctx = new ClassPathXmlApplicationContext("activemq-mock-events.xml");
		ctx.registerShutdownHook();

		event1 = ctx.getBean("mockEvent1", Event.class);
		event2 = ctx.getBean("mockEvent2", Event.class);
		event3 = ctx.getBean("mockEvent3", Event.class);
		event4 = ctx.getBean("mockEvent7", Event.class);
		event5 = ctx.getBean("mockEvent8", Event.class);
		event6 = ctx.getBean("mockEvent9", Event.class);
		event7 = ctx.getBean("mockEvent10", Event.class);
		event8 = ctx.getBean("mockEvent11", Event.class);
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
		
		manager = ctx.getBean("eventPublisherservice", PublisherRouter.class);
		
		ctx.close();
		
		ctx = new ClassPathXmlApplicationContext("activemq-subscribers.xml");
		ctx.registerShutdownHook();
		
		// Create subscribers/consumers
		subscriber = ctx.getBean("activemqSubscriber1", ActiveMQSubscriber.class);
		secondSubscriber = ctx.getBean("activemqSubscriber2", ActiveMQSubscriber.class);
		thirdSubscriber = ctx.getBean("activemqSubscriber3", ActiveMQSubscriber.class);
		s4 = ctx.getBean("activemqSubscriber4", ActiveMQSubscriber.class);
		s5 = ctx.getBean("activemqSubscriber5", ActiveMQSubscriber.class);
		s6 = ctx.getBean("activemqSubscriber6", ActiveMQSubscriber.class);
		s7 = ctx.getBean("activemqSubscriber7", ActiveMQSubscriber.class);
		s8 = ctx.getBean("activemqSubscriber8", ActiveMQSubscriber.class);
		s9 = ctx.getBean("activemqSubscriber9", ActiveMQSubscriber.class);
		s10 = ctx.getBean("activemqSubscriber10", ActiveMQSubscriber.class);
		s11 = ctx.getBean("activemqSubscriber11", ActiveMQSubscriber.class);
		s12 = ctx.getBean("activemqSubscriber12", ActiveMQSubscriber.class);
		s13 = ctx.getBean("activemqSubscriber13", ActiveMQSubscriber.class);
		s14 = ctx.getBean("activemqSubscriber14", ActiveMQSubscriber.class);
		s15 = ctx.getBean("activemqSubscriber15", ActiveMQSubscriber.class);
		s16 = ctx.getBean("activemqSubscriber16", ActiveMQSubscriber.class);
		s17 = ctx.getBean("activemqSubscriber17", ActiveMQSubscriber.class);

		ctx.close();
		
		subscriber.subscribeTopic("topic1");
		secondSubscriber.subscribeTopic("topic2");
		thirdSubscriber.subscribeTopic("topic3");
		s4.subscribeTopic("topic4");
		s5.subscribeTopic("topic5");
		s6.subscribeTopic("topic5");
		s7.subscribeTopic("topic6");
		s8.subscribeTopic("topic1a");
		s9.subscribeTopic("topic2a");
		s10.subscribeTopic("topic7");
		s11.subscribeTopic("topic8");
		s12.subscribeTopic("topic9");
		s13.subscribeTopic("topic10");
		s14.subscribeTopic("topic11");
		s15.subscribeTopic("topic12");
		s16.subscribeTopic("topic13");
		s17.subscribeTopic("topic14");
		
		listener = new MessageListener() {
			public void onMessage(Message message) {
				if(message instanceof TextMessage){
					TextMessage txt = (TextMessage)message;
					try {
						String eventAsJson = txt.getText();
						// turn json string back into event object
						ObjectMapper mapper = new ObjectMapper(); 
						eventFromListener = mapper.readValue(eventAsJson, Event.class);
					}  catch (JsonParseException e) {
						log.debug("Could not parse into JSON object.");
						exception = new PeregrineException(PeregrineErrorCodes.MSG_FORMAT_ERROR, "Error converting JSON to an Object.", e);
					} catch (JsonMappingException e) {
						log.debug("Could not map JSON object into Event object.");
						exception = new PeregrineException(PeregrineErrorCodes.MSG_FORMAT_ERROR, "Error mapping JSON to Object.", e);
					} catch (IOException e) {
						log.debug("No import source found.");
						exception = new PeregrineException(PeregrineErrorCodes.INPUT_SOURCE_ERROR, "Error parsing input source", e);
					} catch (JMSException e) {
						log.debug("An internal error occurred, preventing the operation from occuring: " + e.getMessage());
						exception = new PeregrineException(PeregrineErrorCodes.JMS_INTERNAL_ERROR, "An internal error occurred", e);
					}
					
					if(exception != null){
						// Cannot throw exception here,
						// Just log it
						log.debug("An internal error occurred, preventing the operation from occuring.");
					}

				}				
			}
		};
		
		configs = new HashMap<String, String>();
		
		manager.connectPublishers();
	}
	
	// Base Test 1
	@Test
	public void baseTest1() throws PeregrineException {
		subscriber.setConsumerListener(listener);
				
		manager.publishEventByMapper(event1);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			log.debug("Error sleep interrupted: " + e.getMessage());
		}
		
		Event expected = event1;
		Event actual = eventFromListener;
		
		assertEquals(expected.toString(), actual.toString());
	}
	
	// Base Test 2
	@Test
	public void baseTest2() throws PeregrineException {
		secondSubscriber.setConsumerListener(listener);
		
		manager.publishEventByMapper(event2);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			log.debug("Error sleep interrupted: " + e.getMessage());
		}
		
		Event expected = event2;
		Event actual = eventFromListener;
		
		assertEquals(expected.toString(), actual.toString());
	}
	
	// Publish and consume 2 events from 1 topic
	@Test
	public void consumeTwoEventsTest() throws PeregrineException {
		List<Event> actual = new ArrayList<Event>();
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
						log.debug("Could not parse into JSON object.");
						exception = new PeregrineException(PeregrineErrorCodes.MSG_FORMAT_ERROR, "Error converting JSON to an Object.", e);
					} catch (JsonMappingException e) {
						log.debug("Could not map JSON object into Event object.");
						exception = new PeregrineException(PeregrineErrorCodes.MSG_FORMAT_ERROR, "Error mapping JSON to Object.", e);
					} catch (IOException e) {
						log.debug("No import source found.");
						exception = new PeregrineException(PeregrineErrorCodes.INPUT_SOURCE_ERROR, "Error parsing input source", e);
					} catch (JMSException e) {
						log.debug("An internal error occurred, preventing the operation from occuring: " + e.getMessage());
						exception = new PeregrineException(PeregrineErrorCodes.JMS_INTERNAL_ERROR, "An internal error occurred", e);
					}
				}				
			}
		};
		
		List<Event> expected = new ArrayList<Event>();
		expected.add(event3); expected.add(event4);
		
		thirdSubscriber.setConsumerListener(listener2);
		
		manager.publishEventByMapper(event3);
		manager.publishEventByMapper(event4);
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			log.debug("Error sleep interrupted: " + e.getMessage());
		}
		
		assertTrue(compareLists(expected, actual));
	}
	
	// Publish and consume multiple events from 1 topic
	@Test
	public void consumeMultipleEventsTest() throws PeregrineException {
		List<Event> actual = new ArrayList<Event>();
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
					}  catch (JsonParseException e) {
						log.debug("Could not parse into JSON object.");
						exception = new PeregrineException(PeregrineErrorCodes.MSG_FORMAT_ERROR, "Error converting JSON to an Object.", e);
					} catch (JsonMappingException e) {
						log.debug("Could not map JSON object into Event object.");
						exception = new PeregrineException(PeregrineErrorCodes.MSG_FORMAT_ERROR, "Error mapping JSON to Object.", e);
					} catch (IOException e) {
						log.debug("No import source found.");
						exception = new PeregrineException(PeregrineErrorCodes.INPUT_SOURCE_ERROR, "Error parsing input source", e);
					} catch (JMSException e) {
						log.debug("An internal error occurred, preventing the operation from occuring: " + e.getMessage());
						exception = new PeregrineException(PeregrineErrorCodes.JMS_INTERNAL_ERROR, "An internal error occurred", e);
					}
				}				
			}
		};
		
		List<Event> expected = new ArrayList<Event>();
		expected.add(event5); expected.add(event6); expected.add(event7);
		
		s4.setConsumerListener(listener2);
		
		manager.publishEventByMapper(event5);
		manager.publishEventByMapper(event6);
		manager.publishEventByMapper(event7);
		try {
			Thread.sleep(6000);
		} catch (InterruptedException e) {
			log.debug("Error sleep interrupted: " + e.getMessage());
		}
		
		assertTrue(compareLists(expected, actual));
	}
	
	// Multiple subscribers consume the same event
	@Test
	public void consumeEventMultipleSubscribersTest() throws PeregrineException {
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
					}  catch (JsonParseException e) {
						log.debug("Could not parse into JSON object.");
						exception = new PeregrineException(PeregrineErrorCodes.MSG_FORMAT_ERROR, "Error converting JSON to an Object.", e);
					} catch (JsonMappingException e) {
						log.debug("Could not map JSON object into Event object.");
						exception = new PeregrineException(PeregrineErrorCodes.MSG_FORMAT_ERROR, "Error mapping JSON to Object.", e);
					} catch (IOException e) {
						log.debug("No import source found.");
						exception = new PeregrineException(PeregrineErrorCodes.INPUT_SOURCE_ERROR, "Error parsing input source", e);
					} catch (JMSException e) {
						log.debug("An internal error occurred, preventing the operation from occuring: " + e.getMessage());
						exception = new PeregrineException(PeregrineErrorCodes.JMS_INTERNAL_ERROR, "An internal error occurred", e);
					}
				}				
			}
		};
		
		s5.setConsumerListener(listener);
		s6.setConsumerListener(listener2);
		
		manager.publishEventByMapper(event8);
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			log.debug("Error sleep interrupted: " + e.getMessage());
		}
		
		Event expected = eventFromListener;
		Event actual = secondEvent;
		assertEquals(expected.toString(), actual.toString());
	}
	
	// Consume event with Nulls test
	@Test
	public void nullEventTest() throws PeregrineException {
		s7.setConsumerListener(listener);
		
		manager.publishEventByMapper(nullEvent);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			log.debug("Error sleep interrupted: " + e.getMessage());
		}
		
		Event expected = nullEvent;
		Event actual = eventFromListener;
		
		assertEquals(expected.toString(), actual.toString());
	}
	
	// Consume an event with a null parent id
	@Test
	public void nullParentIdEventTest() throws PeregrineException {
		s8.setConsumerListener(listener);
		
		manager.publishEventByMapper(nullParentIdEvent);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			log.debug("Error sleep interrupted: " + e.getMessage());
		}
		
		Event expected = nullParentIdEvent;
		Event actual = eventFromListener;
		
		assertEquals(expected.toString(), actual.toString());
	}
	
	// Consume an event with a null event name
	@Test
	public void nullEventNameEventTest() throws PeregrineException {
		s9.setConsumerListener(listener);
		
		manager.publishEventByMapper(nullEventNameEvent);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			log.debug("Error sleep interrupted: " + e.getMessage());
		}
		
		Event expected = nullEventNameEvent;
		Event actual = eventFromListener;
		
		assertEquals(expected.toString(), actual.toString());
	}
	
	// Consume an event with a null correlation id
	@Test
	public void nullCorrelationIdEventTest() throws PeregrineException {
		s10.setConsumerListener(listener);
		
		manager.publishEventByMapper(nullCorrelationIdEvent);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			log.debug("Error sleep interrupted: " + e.getMessage());
		}
		
		Event expected = nullCorrelationIdEvent;
		Event actual = eventFromListener;
		
		assertEquals(expected.toString(), actual.toString());
	}
	
	// Consume an event with a null sequence number
	@Test
	public void nullSequenceNumberEventTest() throws PeregrineException {
		s11.setConsumerListener(listener);
		
		manager.publishEventByMapper(nullSequenceNumberEvent);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			log.debug("Error sleep interrupted: " + e.getMessage());
		}
		
		Event expected = nullSequenceNumberEvent;
		Event actual = eventFromListener;
		
		assertEquals(expected.toString(), actual.toString());
	}
	
	// Consume an event with a null data type
	@Test
	public void nullDataTypeEventTest() throws PeregrineException {
		s12.setConsumerListener(listener);
		
		manager.publishEventByMapper(nullDataTypeEvent);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			log.debug("Error sleep interrupted: " + e.getMessage());
		}
		
		Event expected = nullDataTypeEvent;
		Event actual = eventFromListener;
		
		assertEquals(expected.toString(), actual.toString());
	}
	
	// Consume an event with a null source
	@Test
	public void nullSourceEventTest() throws PeregrineException {
		s13.setConsumerListener(listener);
		
		manager.publishEventByMapper(nullSourceEvent);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			log.debug("Error sleep interrupted: " + e.getMessage());
		}
		
		Event expected = nullSourceEvent;
		Event actual = eventFromListener;
		
		assertEquals(expected.toString(), actual.toString());
	}
	
	// Consume an event with a null destination
	@Test
	public void nullDestinationEventTest() throws PeregrineException {
		s14.setConsumerListener(listener);
		
		manager.publishEventByMapper(nullDestinationEvent);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			log.debug("Error sleep interrupted: " + e.getMessage());
		}
		
		Event expected = nullDestinationEvent;
		Event actual = eventFromListener;
		
		assertEquals(expected.toString(), actual.toString());
	}
	
	// Consume an event with a null subdestination
	@Test
	public void nullSubdestinationEventTest() throws PeregrineException {
		s15.setConsumerListener(listener);
		
		manager.publishEventByMapper(nullSubdestinationEvent);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			log.debug("Error sleep interrupted: " + e.getMessage());
		}
		
		Event expected = nullSubdestinationEvent;
		Event actual = eventFromListener;
		
		assertEquals(expected.toString(), actual.toString());
	}
	
	// Consume an event with a null preEventState
	@Test
	public void nullPreEventStateEventTest() throws PeregrineException {
		s16.setConsumerListener(listener);
		
		manager.publishEventByMapper(nullPreEventStateEvent);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			log.debug("Error sleep interrupted: " + e.getMessage());
		}
		
		Event expected = nullPreEventStateEvent;
		Event actual = eventFromListener;
		
		assertEquals(expected.toString(), actual.toString());
	}
	
	// Consume an event with a null postEventState
	@Test
	public void nullPostEventStateEventTest() throws PeregrineException {
		s17.setConsumerListener(listener);
		
		manager.publishEventByMapper(nullPostEventStateEvent);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			log.debug("Error sleep interrupted: " + e.getMessage());
		}
		
		Event expected = nullPostEventStateEvent;
		Event actual = eventFromListener;
		
		assertEquals(expected.toString(), actual.toString());
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
