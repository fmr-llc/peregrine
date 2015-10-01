package com.alliancefoundry.publisher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.alliancefoundry.model.Event;

/**
 * Created by: Paul Fahey, Curtis Robinson
 * 
 *
 */

public class BrokerConfigImpl implements IBrokerConfig {

	@Override
	public Map<String, String> getConfigForEvent(Event ev, String configFile) {
		
		Map<String, String> config = null;

	
		if(ev.getMessageType() == null){
			return null;
		}
		
		List<String>  eventTypeDatas = getEventTypes(configFile);

		for(String typeData : eventTypeDatas){
			String[] bundle = typeData.split(",");
			String type = bundle[0].trim();
			String dest = bundle[1].trim();
			String topic = bundle[2].trim();
			
			if (ev.getMessageType().equals(type)){
				config = new HashMap<String, String>();
				config.put(DESTINATION_KEY, dest);
				config.put(TOPIC_KEY, topic);
				break;
			}
		}	
		
		return config;
	}
	
	public List<String>  getEventTypes(String configFile){

		Properties properties = new Properties();
		List<String> types = new ArrayList<>();
		
		try {
			properties.load(this.getClass().getClassLoader().getResourceAsStream(configFile));
		} catch (IOException e) {
			System.out.println("Could Not Read Properties File");
		}
				
		for (Map.Entry<Object, Object> entry : properties.entrySet())
		{
			types.add((String) entry.getValue());
		}

		return types;
	}
}
