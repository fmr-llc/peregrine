package com.alliancefoundry.serializer;

import com.alliancefoundry.model.Event;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonEventSerializer {
	
	public String convertToJSON(Event event){
		
		ObjectMapper mapper = new ObjectMapper();
		String jsonEvent = null;
		
		try {
			jsonEvent = mapper.writeValueAsString(event);
		} 
		catch (JsonProcessingException e) {
			System.out.println("Error converting object to JSON String.");
		}
		
		return jsonEvent;
	}

}
