package com.alliancefoundry.publisher;

import com.alliancefoundry.exceptions.PeregrineException;
import com.alliancefoundry.model.Event;

public interface IPublisher {

	/**
	 * Connection implementation for a given broker
	 */
	public void connect();
	
	/** 
	 * Publishes to a event topic
	 * @param event Event object of interest
	 * @param destination Destination for an event to be published to
	 * @throws PeregrineException 
	 */

	public void publishEvent(Event event, String destination) throws PeregrineException;
}
