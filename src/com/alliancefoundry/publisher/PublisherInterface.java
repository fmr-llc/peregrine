package com.alliancefoundry.publisher;

import java.util.List;
import java.util.Map;

import com.alliancefoundry.model.Event;

public interface PublisherInterface {
	
	public void connect();
	public void publishEvent(Event event, Map<String,String> config);
	public void publishEvent(List<Event> events, Map<String,String> config);
	public String getDestType();


}
