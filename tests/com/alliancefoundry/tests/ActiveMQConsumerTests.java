/**
 * 
 */
package com.alliancefoundry.tests;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.*;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

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
public class ActiveMQConsumerTests {
	
	Event event1, event2;
	Map<String, String> configs;
	EventServicePublisher manager;
	MessageListener listener;
	Event eventFromListener;
	
	@Before
	public void setUp() {
		event1 = new Event(null, "Event Message", "17", "17", 1, "Insert Message", 
				"String", "Event Messenger", "ActiveMQ", "test", false, 
				new DateTime("2015-09-10T15:42:43.285-0400"), 
				new DateTime("2015-09-10T15:42:43.285-0400"), 
				new DateTime("2015-09-17T15:42:43.285-0400"), 
				"preState", "postState", true, new DateTime("2015-09-10T15:42:43.285-0400"));
		event1.setEventId("42");
		
		event2 = new Event("42", "Event Update", "18", "18", 1, "Update Message", 
				"String", "Event Messenger", "ActiveMQ", "test", false, 
				new DateTime("2015-09-18T15:42:43.285-0400"), 
				new DateTime("2015-09-18T15:42:43.285-0400"), 
				new DateTime("2015-09-25T15:42:43.285-0400"), 
				"preStateFor43", "postStateFor43", true, new DateTime("2015-09-18T15:42:43.285-0400"));
		event2.setEventId("43");
		
		
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
		
		manager = new EventServicePublisher();
		manager.setupPublishersViaAppContext();
	}
	
	// Base Test 1
	@Test
	public void baseTest1() {
		ActiveMQSubscriber subscriber = new ActiveMQSubscriber("tcp://localhost:61616", "Test Subscriber");
		
		subscriber.setConsumerListener(listener);
		
		configs.put(EventServicePublisher.TOPIC_KEY, "topic1");
		configs.put(EventServicePublisher.DESTINATION_KEY, EventServicePublisher.ACTIVEMQ_KEY);
		
		manager.publishEvent(event1, configs);
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
		
	}
	
	
}
