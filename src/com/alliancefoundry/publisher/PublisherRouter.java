package com.alliancefoundry.publisher;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.alliancefoundry.exceptions.PeregrineErrorCodes;
import com.alliancefoundry.exceptions.PeregrineException;
import com.alliancefoundry.model.Event;

/**
 * Created by: Paul Fahey, Curtis Robinson
 * 
 *
 */

public class PublisherRouter {

	@Autowired
	private Map<String, IPublisher> publishers;
	private String configFile;
	private IBrokerConfig mapper = new BrokerConfigImpl();
	
	public void setEventMapper(IBrokerConfig mapper){
		this.mapper = mapper;
	}
	
	public void publishEventByMapper(Event event) throws PeregrineException{
		
		Map<String, String> eventConfig = mapper.getConfigForEvent(event, this.getConfigFile());
		if(eventConfig == null){
			System.out.println("Event's topic and destination could not be determined");
			throw new PeregrineException(PeregrineErrorCodes.INVALID_DESTINATION_OR_TOPIC, "Event's topic and destination could not be determined");
		}
		
		
		String destination = eventConfig.get(IBrokerConfig.DESTINATION_KEY);
		String topic = eventConfig.get(IBrokerConfig.TOPIC_KEY);
		
		IPublisher publisher = publishers.get(destination);
		if(publisher != null){
			publisher.publishEvent(event, topic);
		}

	}
		
	public void connectPublishers(){
		Set<String> keys = publishers.keySet();
		for( String key : keys){
			publishers.get(key).connect();
		}
			
	}
	
	/**
	 * 
	 * @param events Events to be published
	 * @throws PeregrineException 
	 */
	public void attemptPublishEvent(List<Event> events) throws PeregrineException{
		
		for(Event event : events){
			
			if(event.getIsPublishable() == true){
					
				connectPublishers();
				publishEventByMapper(event);

				System.out.println("Event: " + event.getEventId()+ " was published");
							
			}
			else{
				System.out.println("Event: " + event.getEventId()+ " was not published");
			}
		}
	}
	
	public Map<String, IPublisher> getPublishers() {
		return publishers;
	}

	public void setPublishers(Map<String, IPublisher> publishers) {
		this.publishers = publishers;
	}

	public String getConfigFile() {
		return configFile;
	}

	public void setConfigFile(String configFile) {
		this.configFile = configFile;
	}
	
}