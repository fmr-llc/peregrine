package com.alliancefoundry.publisher;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;


import com.alliancefoundry.model.Event;

public class EventServicePublisher {
	
	public static final String TOPIC_KEY = "topic";
	public static final String DESTINATION_KEY = "destination";
	public static final String KAFKA_KEY = "kafka";
	public static final String ACTIVEMQ_KEY = "activemq";
	
	
	@Autowired
	List<PublisherInterface> publishers;

	public void publishEvent(Event event, Map<String, String> eventConfig) {
		
		String destination = eventConfig.get(DESTINATION_KEY);
		
		
		for( PublisherInterface publisher : publishers){
			if(destination.equals(publisher.getDestType())){
				
				publisher.publishEvent(event, eventConfig);
			}
		}
		
	}
	
	public void publishEvent(List<Event> events, Map<String, String> eventConfig) {
		
		String destination = eventConfig.get(DESTINATION_KEY);
		
		
		for( PublisherInterface publisher : publishers){
			if(destination.equals(publisher.getDestType())){
				
				publisher.publishEvent(events, eventConfig);
			}
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



		//kafkaPublshisher = ObjectDispenser.getContext().getBean("kafkaPublisher", KafkaPublisher.class);
		//mqPublisher = ObjectDispenser.getContext().getBean("activemqPublisher", ActiveMQPublisher.class);
		
		//publishers.add(mqPublisher);
		//publishers.add(kafkaPublshisher);


	}

	public List<PublisherInterface> getPublishers() {
		return publishers;
	}

	public void setPublishers(List<PublisherInterface> publishers) {
		this.publishers = publishers;
	}
	
}