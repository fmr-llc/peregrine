package com.alliancefoundry.serializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliancefoundry.exceptions.PeregrineErrorCodes;
import com.alliancefoundry.exceptions.PeregrineException;
import com.alliancefoundry.model.Event;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonEventSerializer {
	
	static final Logger log = LoggerFactory.getLogger(JsonEventSerializer.class);
	
	public String convertToJSON(Event event) throws PeregrineException{
		
		ObjectMapper mapper = new ObjectMapper();
		String jsonEvent = null;
		
		try {
			jsonEvent = mapper.writeValueAsString(event);
		} 
		catch (JsonProcessingException e) {
			log.error("Error converting object to JSON String: " + e.getMessage());
			PeregrineException exception = new PeregrineException(PeregrineErrorCodes.JSON_PARSE_ERROR, "Error converting object to JSON String.", e);
			throw exception;
		}
		
		return jsonEvent;
	}

}
