/**
 * 
 */
package com.alliancefoundry.tests;

import java.util.HashMap;
import java.util.Map;


import org.joda.time.DateTime;
import org.junit.*;

import com.alliancefoundry.model.Event;
import com.alliancefoundry.publisher.EventServicePublisher;

/**
 * @author Robert Coords
 *
 */
public class KafkaTestFixture extends fit.Fixture {
	Event event1, event2;
	
	Map<String, String> configs;
	
	@Before
	public void createEvents() {
		event1 = new Event(null, "8675309", "464865", "464865", 1, "Insert", "Object", "Publisher", 
				"Kafka", "test2", false, DateTime.now(), DateTime.now(), DateTime.now().plusDays(7), 
				"pre", "post", true, DateTime.now());
		event1.setEventId("18");
		
		event2 = new Event(null, "8675309", "464865", "464865", 1, "Insert", "Object", "Publisher", 
				"Kafka", "test2", false, DateTime.now(), DateTime.now(), DateTime.now().plusDays(7), 
				"pre", "post", true, DateTime.now());
		
		// Initialize configs
		configs = new HashMap<String, String>();
	}
	
	
	public String consumeEvent1() {
		configs.put(EventServicePublisher.TOPIC_KEY, "test2");
		configs.put(EventServicePublisher.DESTINATION_KEY, EventServicePublisher.KAFKA_KEY);
		
		EventServicePublisher manager = new EventServicePublisher();
		manager.setupPublishersViaAppContext();
		manager.connectPublishers();
		manager.publishEvent(event1, configs);
		
		KafkaSubscriber kafkaSubscriber = new KafkaSubscriber();
		String str = kafkaSubscriber.consumeEvent();
		
		System.out.println(str);
		return str;
	}
	
//	public static void main() {
//		createEvents();
//		System.out.println(consumeEvent(event1));
//	}
}
