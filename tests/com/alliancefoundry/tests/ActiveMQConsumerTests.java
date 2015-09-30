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
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.alliancefoundry.exceptions.PeregrineErrorCodes;
import com.alliancefoundry.exceptions.PeregrineException;
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
	ActiveMQSubscriber subscriber;
	
	@Before
	public void setUp() {
		AbstractApplicationContext ctx;
		ctx = new ClassPathXmlApplicationContext("activemq-mock-events.xml");
		ctx.registerShutdownHook();

		event1 = ctx.getBean("mockEvent1", Event.class);
		event2 = ctx.getBean("mockEvent2", Event.class);
		
		ctx.close();
		
		ctx = new ClassPathXmlApplicationContext("eventservice-servlet.xml");
		ctx.registerShutdownHook();
		// Create subscribers/consumers
		subscriber = ctx.getBean("activemqSubscriber1", ActiveMQSubscriber.class);
		manager = ctx.getBean("eventPublisherservice", EventServicePublisher.class);
		ctx.close();

		subscriber.subscribeTopic("topic1");
		
		listener = new MessageListener() {
			public void onMessage(Message message) {
				PeregrineException exception = null;
				if(message instanceof TextMessage){
					TextMessage txt = (TextMessage)message;
					try {
						String eventAsJson = txt.getText();
						// turn json string back into event object
						ObjectMapper mapper = new ObjectMapper(); 
						eventFromListener = mapper.readValue(eventAsJson, Event.class);
					}  catch (JsonParseException e) {
						exception = new PeregrineException(PeregrineErrorCodes.MSG_FORMAT_ERROR, "Error converting JSON to an Object.", e);
					} catch (JsonMappingException e) {
						exception = new PeregrineException(PeregrineErrorCodes.MSG_FORMAT_ERROR, "Error mapping JSON to Object.", e);
					} catch (IOException e) {
						exception = new PeregrineException(PeregrineErrorCodes.INPUT_SOURCE_ERROR, "Error parsing input source", e);
					} catch (JMSException e) {
						exception = new PeregrineException(PeregrineErrorCodes.JMS_INTERNAL_ERROR, "An internal error occurred", e);
					}
					
					if(exception != null){
						// Cannot throw exception here,
						// Just log it
						System.out.println("An error has occured, replace with a log");
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
