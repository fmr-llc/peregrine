package com.alliancefoundry.publisher;

import java.util.List;

import com.alliancefoundry.model.Event;

public interface PublisherInterface {
	
	void connect() throws PublisherException;
	void publishEvent(Event event, RouterConfig config) throws PublisherException;
	void publishEvent(List<Event> events, RouterConfig config) throws PublisherException;


}
