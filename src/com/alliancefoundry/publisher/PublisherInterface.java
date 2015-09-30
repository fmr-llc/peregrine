package com.alliancefoundry.publisher;

import com.alliancefoundry.exceptions.PeregrineException;
import com.alliancefoundry.model.Event;

public interface PublisherInterface {

	public void connect();
	
	/** 
	 * fires event to topic
	 * @param event Event object of interest
	 * @param Topic Destination for an event to be published to
	 */
	public void publishEvent(Event event, String Topic)  throws PeregrineException;
	
}
