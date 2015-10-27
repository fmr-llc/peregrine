package com.alliancefoundry.publisher;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.alliancefoundry.model.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventServicePublisher {

	private static final Logger log = LoggerFactory.getLogger(EventServicePublisher.class);
	private Map<String, PublisherInterface> publishers = new HashMap<>();
	private Map<String, RouterConfig> router = new HashMap<>();
	private String activeConfig;

	public boolean publishEvent(Event event) throws PublisherException {

		if (event==null) throw new PublisherException("Request to publish a null event was received.");

		log.debug("router count: " + router.size());
		log.debug("publisher count: " + publishers.size());
		log.debug("active configuration bean name being used: " + activeConfig);


		log.debug("Event Service Publisher has received an request to publish event: " + event.toString());

		RouterConfig config = router.get(activeConfig);

		if (config==null){

			log.error("Router Config not found.  Configuration error.");

			return false;

		} else {

			log.debug("router configuration being used: " + config.getClass().getCanonicalName());

			String pubStr = config.getPublisher(event.getMessageType());

			log.debug("publisher being used: " + pubStr);

			PublisherInterface publisher = publishers.get(pubStr);

			try {
				publisher.publishEvent(event, config);
				return true;

			} catch (PublisherException e) {
				log.error("publish of events failed. ", e);
				return false;
			}
		}




	}
	
	public boolean publishEvent(List<Event> events) throws PublisherException {
			return false;

	}
	
	public void connectPublishers(){

	}


	public Map<String, PublisherInterface> getPublishers() {
		return publishers;
	}

	public void setPublishers(Map<String, PublisherInterface> publishers) {
		this.publishers = publishers;
	}

	public Map<String, RouterConfig> getRouterConfig(){ return router; }

	public void setRouterConfig(Map<String, RouterConfig> config){ this.router = config; }

	public String getActiveConfig(){ return activeConfig; }

	public void setActiveConfig(String config) { this.activeConfig = config; }
	
}