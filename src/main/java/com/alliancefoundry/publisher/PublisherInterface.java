package com.alliancefoundry.publisher;

import java.util.List;

import com.alliancefoundry.model.Event;

public interface PublisherInterface {
	
	void init() throws PublisherException;
	boolean publishEvent(Event event, RouterConfig config) throws PublisherException;
	boolean publishEvents(List<Event> events, RouterConfig config) throws PublisherException;
	void setBrokerUrl(String url) throws PublisherException;

}
