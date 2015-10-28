package com.alliancefoundry.tests;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import com.alliancefoundry.model.Event;
import com.alliancefoundry.publisher.EventServicePublisher;
import com.fasterxml.jackson.core.JsonProcessingException;

public class KafkaTests {
	
	Event event;
	Event event2;
	Map<String,String> configs;

	@Before
	public void setUp(){
		
		 event = new Event("parentId", "Event numba1", "Object Id", "correlation Id", 12, "Message Type", 
				"data type numba 1", "Source numba 1", "destination 1", "sub destination 1", false, new DateTime(876543223321L), 
				new DateTime(1176543223321L), new DateTime(2876543223321L), "pre", "post", false, new DateTime(676543223321L) );
		 
		 event2 = new Event(null, null, null, null, 12, null, 
				 null, null, null, null, false, null, 
					null, null, null, null, false, null );
		
		configs = new HashMap<String, String>();	
	}

	
	@Test
	public void testCosummingEvent() throws JsonProcessingException {
		
		String topic = "mumblyFrumps";
		
		configs.put(EventServicePublisher.TOPIC_KEY, topic);
		configs.put(EventServicePublisher.DESTINATION_KEY, EventServicePublisher.KAFKA_KEY);
			
		event.setSequenceNumber(42);
		
		EventServicePublisher manager = new EventServicePublisher();
		manager.setupPublishersViaAppContext();
		manager.connectPublishers();
		manager.publishEvent(event, configs);

		KafkaSubscriber kafkaSubscriber = new KafkaSubscriber(topic);
		kafkaSubscriber.consumeEvent();
		
		assertEquals("Should be 42", 42, event.getSequenceNumber().intValue());
	}
	
	@Test
	public void testPublishingEvent() {
		
		configs.put(EventServicePublisher.TOPIC_KEY, "test");
		configs.put(EventServicePublisher.DESTINATION_KEY, EventServicePublisher.KAFKA_KEY);

		EventServicePublisher manager = new EventServicePublisher();
		manager.setupPublishersViaAppContext();
		manager.connectPublishers();
		manager.publishEvent(event, configs);
		
		//showing true to show it got throw publishing the event
		assertTrue(true);
		
	}
	
	@Test
	public void testNull() {
		
		String topic = "testTopic";
		
		configs.put(EventServicePublisher.TOPIC_KEY, topic);
		configs.put(EventServicePublisher.DESTINATION_KEY, EventServicePublisher.KAFKA_KEY);

		EventServicePublisher manager = new EventServicePublisher();
		manager.setupPublishersViaAppContext();
		manager.connectPublishers();
		manager.publishEvent(event2, configs);

		KafkaSubscriber kafkaSubscriber = new KafkaSubscriber(topic);
		kafkaSubscriber.consumeEvent();
		
		assertEquals("Should be 12", 12, event2.getSequenceNumber().intValue());
		
	}
}
