package com.alliancefoundry.publisher;

import java.util.List;
import java.util.Map;

import com.alliancefoundry.model.Event;

public interface IMapEvents {
	
	String DESTINATION_KEY = "destination";
	String TOPIC_KEY = "topic";
	
	public Map<String, String> getConfigFromEvent(Event ev);
	
}
