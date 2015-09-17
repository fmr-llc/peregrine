package com.alliancefoundry.publisher;

import com.alliancefoundry.model.Event;

public interface PublisherInterface {
	
	public void connect();
	public void publishEvent(Event event, String Topic);
	public String getDestType();
}
