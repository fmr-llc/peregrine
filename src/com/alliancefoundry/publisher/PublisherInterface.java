package com.alliancefoundry.publisher;

import java.util.Map;

import com.alliancefoundry.model.Event;

public interface PublisherInterface {
	
	public void connect();
	public void publishEvent(Event event, Map<String,String> config);
	public String getDestType();


}
