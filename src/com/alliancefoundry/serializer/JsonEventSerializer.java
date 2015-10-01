package com.alliancefoundry.serializer;

import com.alliancefoundry.exceptions.PeregrineErrorCodes;
import com.alliancefoundry.exceptions.PeregrineException;
import com.alliancefoundry.model.Event;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonEventSerializer {
	
	public String convertToJSON(Event event) throws PeregrineException{
		
		ObjectMapper mapper = new ObjectMapper();
		String jsonEvent = null;
		
		try {
			jsonEvent = mapper.writeValueAsString(event);
		} 
		catch (JsonProcessingException e) {
			PeregrineException exception = new PeregrineException(PeregrineErrorCodes.JSON_PARSE_ERROR, "Error converting object to JSON String.", e);
			throw exception;
		}
		
		return jsonEvent;
	}

}
