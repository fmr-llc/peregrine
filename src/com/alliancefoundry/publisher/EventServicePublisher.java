package com.alliancefoundry.publisher;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.alliancefoundry.model.Event;

public class EventServicePublisher {

	@Autowired
	private Map<String, PublisherInterface> publishers;
	private BrokerConfig mapper = new BrokerConfigImpl();
	
	public void setEventMapper(BrokerConfig mapper){
		this.mapper = mapper;
	}
	
	public void publishEventByMapper(Event event){
		
		Map<String, String> eventConfig = mapper.getConfigForEvent(event);
		if(eventConfig == null){
			throw new RuntimeException("Event's topic and destination could not be determined");
		}
		
		
		String destination = eventConfig.get(BrokerConfig.DESTINATION_KEY);
		String topic = eventConfig.get(BrokerConfig.TOPIC_KEY);
		
		PublisherInterface publisher = publishers.get(destination);
		if(publisher != null){
			publisher.publishEvent(event, topic);
		}

	}
	
	public void publishEventByMapper(List<Event> events) {
		
		for(Event event : events){
			publishEventByMapper(event);
		}
			
	}
	
	public void connectPublishers(){
		Set<String> keys = publishers.keySet();
		for( String key : keys){
			publishers.get(key).connect();
		}
		
		
	}
	
	public Map<String, PublisherInterface> getPublishers() {
		return publishers;
	}

	public void setPublishers(Map<String, PublisherInterface> publishers) {
		this.publishers = publishers;
	}
	
}