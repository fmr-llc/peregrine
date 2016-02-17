package com.alliancefoundry.publisher;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Used to capture message type configuration via property binding.
 * 
 * @author Peter
 */
@ConfigurationProperties(prefix="message")
@Component
public class MessageTypeConfiguration {

	// used only for property binding
	public Map<String,String> type = new HashMap<>();
	public Map<String,MessageDestination> typedMessageDestinations = new HashMap<>();
	
	public Map<String, MessageDestination> getTypedMessageDestinations() {
		return typedMessageDestinations;
	}

	public Map<String, String> getType() {
		return type;
	}

	/**
	 * Used after construction to split the message destinations by pipe delimiter
	 */
	@PostConstruct
	public void resolveTypedMessageDestinations() {
		// split apart by delimiter and create a MessageDestination
		for(Map.Entry<String,String> entry : type.entrySet()) {
			String delim[] = entry.getValue().split("\\|");
			typedMessageDestinations.put(entry.getKey(), new MessageDestination(delim[0], delim[1]));
		}
	}

	public static class MessageDestination {
		String publisher;
		String destination;
		
		public MessageDestination(String publisher, String destination) {
			super();
			this.publisher = publisher;
			this.destination = destination;
		}
		public String getPublisher() {
			return publisher;
		}
		public String getDestination() {
			return destination;
		}
	}
}
