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
import com.alliancefoundry.publisher.ActiveMQPublisher;
import com.alliancefoundry.publisher.EventServicePublisher;
import com.alliancefoundry.publisher.KafkaPublisher;
import com.alliancefoundry.publisher.PublisherInterface;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import junit.framework.Assert;

public class ActiveMQTests {
	
	ActiveMQSubscriber subscriber1;
	ActiveMQSubscriber subscriber2;
	ActiveMQPublisher publisher;
	EventServicePublisher manager;
	Event event = new Event("parentId", "Event numba1", "Object Id", "correlation Id", 12, "Message Type", 
			"data type numba 1", "Source numba 1", "destination 1", "sub destination 1", false, new DateTime(876543223321L), 
			new DateTime(1176543223321L), new DateTime(2876543223321L), "pre", "post", false, new DateTime(676543223321L) );
	
	private boolean eventTestPass1 = false;
	
	List<PublisherInterface> publishers;

	@Before
	public void setUp() throws Exception {
		
		// Create subscribers/consumers
		subscriber1 = new ActiveMQSubscriber("tcp://localhost:61616", "Sam");
		subscriber1.subscribeTopic("Topic Numba 1");
		subscriber2 = new ActiveMQSubscriber("tcp://localhost:61616", "Tim");
		subscriber2.subscribeTopic("topic1");
		
		//  Create publisher
		publisher = new ActiveMQPublisher();
		manager = new EventServicePublisher();
		manager.setupPublishersViaAppContext();
		
	}

	@After
	public void tearDown() throws Exception {
		
		subscriber1.Shutdown();
		subscriber2.Shutdown();
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
		subscriber2.setConsumerListener(listener);


		event.setSequenceNumber(customEventId);
		
		Map<String, String> config = new HashMap<String, String>();
		config.put(EventServicePublisher.TOPIC_KEY, "topic1");
		config.put(EventServicePublisher.DESTINATION_KEY, EventServicePublisher.ACTIVEMQ_KEY);
		
		manager.publishEvent(event, config);
		
		// need to wait so that we have time to subscribe and publish
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			System.out.println("Error sleep interrupted.");
		}
		
		Assert.assertTrue("Event id should be 44",eventTestPass1);
		
	}
	
	public void setupPublishersViaAppContext(){
		
		
		KafkaPublisher kafkaPublshisher;
		ActiveMQPublisher mqPublisher;

		AbstractApplicationContext ctx;
		ctx = new ClassPathXmlApplicationContext("eventservice-servlet.xml");
		ctx.registerShutdownHook();

		kafkaPublshisher = ctx.getBean("kafkaPublisher", KafkaPublisher.class);
		mqPublisher = ctx.getBean("activemqPublisher", ActiveMQPublisher.class);
		
		mqPublisher.connect();
		
		publishers.add(mqPublisher);
		publishers.add(kafkaPublshisher);
		
		ctx.close();

}
	
	
}
