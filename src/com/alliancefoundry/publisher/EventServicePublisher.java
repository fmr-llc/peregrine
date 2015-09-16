package com.alliancefoundry.publisher;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.alliancefoundry.model.Event;

public class EventServicePublisher {
	
	public static final String TOPIC_KEY = "topic";
	public static final String DESTINATION_KEY = "destination";
	public static final String KAFKA_KEY = "kafka";
	public static final String ACTIVEMQ_KEY = "activemq";
	
	private Map<String, PublisherInterface> pubs;
	private IMapEvents mapper;
	
	@Autowired
	List<PublisherInterface> publishers;

	public void publishEvent(Event event, Map<String, String> eventConfig) {
		
		String destination = eventConfig.get(DESTINATION_KEY);
		
		PublisherInterface pub = pubs.get(destination);
		if(pub != null){
			pub.publishEvent(event, eventConfig);
		}
		/*
		for( PublisherInterface publisher : publishers){
			if(destination.equals(publisher.getDestType())){
				
				publisher.publishEvent(event, eventConfig);
			}
		}
		*/
		
	}
	
	public void publishEvent(List<Event> events, Map<String, String> eventConfig) {
		
		String destination = eventConfig.get(DESTINATION_KEY);
		
		PublisherInterface pub = pubs.get(destination);
		if(pub != null){
			pub.publishEvent(events, eventConfig);
		}
		
		/*

		for( PublisherInterface publisher : publishers){
			if(destination.equals(publisher.getDestType())){
				
				publisher.publishEvent(events, eventConfig);
			}
		}
		*/
		
	}
	
	public void setEventMapper(IMapEvents mapper){
		this.mapper = mapper;
	}
	
	public void publishEventByMapper(Event event){
		
		Map<String, String> eventConfig = mapper.getConfigFromEvent(event);
		if(eventConfig == null){
			System.out.println("Event's topic and destination could not be determined");
			return; // do nothing 
		}
		String destination = eventConfig.get(IMapEvents.DESTINATION_KEY);
		String topic = eventConfig.get(IMapEvents.TOPIC_KEY);
		
		PublisherInterface publisher = pubs.get(destination);
		if(publisher != null){
			publisher.publishEvent(event, topic);
		}

	}
	
	public void connectPublishers(){
		for( PublisherInterface publisher : publishers){
			publisher.connect();
		}
	}
	
	public void setupPublishersViaAppContext(){
		publishers = new ArrayList<>();
		
		KafkaPublisher kafkaPublshisher;
		ActiveMQPublisher mqPublisher;

		AbstractApplicationContext ctx;
		ctx = new ClassPathXmlApplicationContext("eventservice-servlet.xml");
		ctx.registerShutdownHook();

		kafkaPublshisher = ctx.getBean("kafkaPublisher", KafkaPublisher.class);
		mqPublisher = ctx.getBean("activemqPublisher", ActiveMQPublisher.class);
		
		publishers.add(mqPublisher);
		publishers.add(kafkaPublshisher);
		
		pubs.put(ACTIVEMQ_KEY, mqPublisher);
		pubs.put(KAFKA_KEY, kafkaPublshisher);
		
		ctx.close();

	}

	public List<PublisherInterface> getPublishers() {
		return publishers;
	}

	public void setPublishers(List<PublisherInterface> publishers) {
		this.publishers = publishers;
	}
	
}