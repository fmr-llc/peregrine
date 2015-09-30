package com.alliancefoundry.publisher;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.alliancefoundry.exceptions.PeregrineErrorCodes;
import com.alliancefoundry.exceptions.PeregrineException;
import com.alliancefoundry.model.Event;

public class EventServicePublisher {

	@Autowired
	private Map<String, PublisherInterface> publishers;
	private IMapEvents mapper = new MapEventsImpl();
	
	public void setEventMapper(IMapEvents mapper){
		this.mapper = mapper;
	}
	
	public void publishEventByMapper(Event event) throws PeregrineException{
		
		Map<String, String> eventConfig = mapper.getConfigFromEvent(event);
		if(eventConfig == null){
			System.out.println("Event's topic and destination could not be determined");
			throw new PeregrineException(PeregrineErrorCodes.INVALID_DESTINATION_OR_TOPIC, "Event's topic and destination could not be determined");
		}
		String destination = eventConfig.get(IMapEvents.DESTINATION_KEY);
		String topic = eventConfig.get(IMapEvents.TOPIC_KEY);
		
		PublisherInterface publisher = publishers.get(destination);
		if(publisher != null){
			publisher.publishEvent(event, topic);
		}

	}
	
	public void publishEventByMapper(List<Event> events) throws PeregrineException {
		
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