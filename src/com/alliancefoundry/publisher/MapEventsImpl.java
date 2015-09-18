package com.alliancefoundry.publisher;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alliancefoundry.model.Event;

public class MapEventsImpl implements IMapEvents {

	@Override
	public Map<String, String> getConfigFromEvent(Event ev) {
		Map<String, String> config = new HashMap<String, String>();
		
		if(ev.getMessageType() == null){
			return null;
		}
		
		// Example of messagetype would be
		// "kafaka - topic1"
		String[] bundle = ev.getMessageType().split(" - ");
		String dest = bundle[0].trim();
		String topic  = null;
		if(bundle.length > 1){
			topic = bundle[1].trim();			
		}
		
		config.put(DESTINATION_KEY, dest);
		config.put(TOPIC_KEY, topic);
		
		return config;
	}

}
