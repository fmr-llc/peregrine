package com.alliancefoundry.publisher;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliancefoundry.exceptions.PeregrineErrorCodes;
import com.alliancefoundry.exceptions.PeregrineException;
import com.alliancefoundry.model.Event;

/**
 * Created by: Paul Fahey, Curtis Robinson, Bobby Writtenberry
 * 
 *
 */

public class PublisherRouter {
	
	static final Logger log = LoggerFactory.getLogger(PublisherRouter.class);

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
			log.error("Event's topic and destination could not be determined");
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
	public void attemptPublishEvent(Event event) throws PeregrineException{
		List<Event> events = new ArrayList<>();
		events.add(event);
		attemptPublishEvents(events);
	}
	
	/**
	 * 
	 * @param events Events to be published
	 * @throws PeregrineException 
	 */
	public void attemptPublishEvents(List<Event> events) throws PeregrineException{
		
		for(Event event : events){
			
			if(event.getIsPublishable() == true){
					
				connectPublishers();
				publishEventByMapper(event);
				log.info("Event with ID: " + event.getEventId()+ " was published");
							
			}
			else{
				log.info("Event with ID: " + event.getEventId()+ " was not published");
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