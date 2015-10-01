package com.alliancefoundry.publisher;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.alliancefoundry.model.Event;

public class EventServicePublisher {

	@Autowired
	private Map<String, IPublisher> publishers;
	private String configFile;
	private IBrokerConfig mapper = new BrokerConfigImpl();
	
	public void setEventMapper(IBrokerConfig mapper){
		this.mapper = mapper;
	}
	
	public void publishEventByMapper(Event event){
		
		Map<String, String> eventConfig = mapper.getConfigForEvent(event, this.getConfigFile());
		if(eventConfig == null){
			throw new RuntimeException("Event's topic and destination could not be determined");
		}
		
		
		String destination = eventConfig.get(IBrokerConfig.DESTINATION_KEY);
		String topic = eventConfig.get(IBrokerConfig.TOPIC_KEY);
		
		IPublisher publisher = publishers.get(destination);
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
	
	public Map<String, IPublisher> getPublishers() {
		return publishers;
	}

	public void setPublishers(Map<String, IPublisher> publishers) {
		this.publishers = publishers;
	}

	public String getConfigFile() {
		return configFile;
	}

	public void setConfigFile(String configFile) {
		this.configFile = configFile;
	}
	
}