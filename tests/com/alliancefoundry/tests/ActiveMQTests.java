package com.alliancefoundry.tests;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.alliancefoundry.model.Event;
import com.alliancefoundry.publisher.EventServicePublisher;
import com.alliancefoundry.publisher.IPublisher;
import com.alliancefoundry.publisher.activemq.ActiveMQPublisher;
import com.alliancefoundry.publisher.kafka.KafkaPublisher;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import junit.framework.Assert;

public class ActiveMQTests {
	
	ActiveMQSubscriber subscriber1;
	ActiveMQPublisher publisher;
	EventServicePublisher manager;
	Event event;
	
	private boolean eventTestPass1 = false;
	
	List<IPublisher> publishers;

	@Before
	public void setUp() throws Exception {
		AbstractApplicationContext ctx;
		ctx = new ClassPathXmlApplicationContext("activemq-mock-events.xml");
		ctx.registerShutdownHook();

		event = ctx.getBean("mockEvent1", Event.class);

		ctx.close();
		
		ctx = new ClassPathXmlApplicationContext("eventservice-servlet.xml");
		ctx.registerShutdownHook();
		// Create subscribers/consumers
		subscriber1 = ctx.getBean("activemqSubscriber1", ActiveMQSubscriber.class);
		ctx.close();

		subscriber1.subscribeTopic("topic1");
		
		//  Create publisher
		ctx = new ClassPathXmlApplicationContext("eventservice-servlet.xml");
		ctx.registerShutdownHook();

		// setup publiher
		manager = ctx.getBean("eventPublisherservice", EventServicePublisher.class);
		ctx.close();
		
		manager.connectPublishers();
		
	}

	@After
	public void tearDown() throws Exception {
		
		subscriber1.Shutdown();
	}

	@Test
	public void testSendEventJsonToSubscribersViaManager() throws JsonProcessingException {
//		fail("Not yet implemented");
		
		final int customEventId = 44;
		
		// create customer messageListener
		MessageListener listener = new MessageListener() {
			
			public void onMessage(Message message) {
				if(message instanceof TextMessage){
					TextMessage txt = (TextMessage)message;
					try {
						String eventAsJson = txt.getText();
						// turn json string back into event object
						ObjectMapper mapper = new ObjectMapper(); 
						Event event = mapper.readValue(eventAsJson, Event.class);
						eventTestPass1 = customEventId == event.getSequenceNumber();
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
		
		// attach listener to subscriber
		subscriber1.setConsumerListener(listener);


		event.setSequenceNumber(customEventId);

		manager.publishEventByMapper(event);
		
		// need to wait so that we have time to subscribe and publish
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			System.out.println("Error sleep interrupted.");
		}
		
		Assert.assertTrue("Event id should be 44",eventTestPass1);
		
	}	
	
}
