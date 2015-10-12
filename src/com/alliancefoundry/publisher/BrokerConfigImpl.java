package com.alliancefoundry.publisher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliancefoundry.exceptions.PeregrineErrorCodes;
import com.alliancefoundry.exceptions.PeregrineException;
import com.alliancefoundry.model.Event;

/**
 * Created by: Paul Fahey, Curtis Robinson
 * 
 *
 */

public class BrokerConfigImpl implements IBrokerConfig {
	
	static final Logger log = LoggerFactory.getLogger(BrokerConfigImpl.class);

	@Override
	public Map<String, String> getConfigForEvent(Event ev, String configFile) throws PeregrineException {
		
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
	
	public List<String>  getEventTypes(String configFile) throws PeregrineException{

		Properties properties = new Properties();
		List<String> types = new ArrayList<>();
		
		try {
			properties.load(this.getClass().getClassLoader().getResourceAsStream(configFile));
		} catch (IOException e) {
			log.error("Error reading configuration file  with an error message of: " + e.getMessage());
			PeregrineException exception = new PeregrineException(PeregrineErrorCodes.INPUT_SOURCE_ERROR, "Error reading configuration file.", e);
			throw exception;
		}catch (NullPointerException e) {
			log.error("Error reading configuration file with an error message of: " + e.getMessage());
			PeregrineException exception = new PeregrineException(PeregrineErrorCodes.INPUT_SOURCE_ERROR, "Error reading configuration file.", e);
			throw exception;
		}
				
		for (Map.Entry<Object, Object> entry : properties.entrySet())
		{
			types.add((String) entry.getValue());
		}

		return types;
	}
}
