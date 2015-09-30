package com.alliancefoundry.publisher;

import java.util.Map;

import com.alliancefoundry.model.Event;

public interface BrokerConfig {
	
	/**
	 * Constant to map the broker destination for a given event
	 */
	String DESTINATION_KEY = "destination";
	
	/**
	 * Constant to map the topic within a broker for which an event should be published to
	 */
	String TOPIC_KEY = "topic";
	
	/**
	 * Maps proper configuration to give to the router based on an events messagetype
	 * @param ev Event that needs to determine its routing mechanism
	 * @return Proper configuration mapping is returned for the router to use
	 */
	public Map<String, String> getConfigForEvent(Event ev);
	
}
