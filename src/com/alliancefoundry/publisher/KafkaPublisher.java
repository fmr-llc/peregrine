package com.alliancefoundry.publisher;

import java.util.Properties;

import com.alliancefoundry.model.Event;
import com.alliancefoundry.serializer.JsonEventSerializer;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

public class KafkaPublisher implements IPublisher {

	private Producer<String, String> producer;		
	private Properties properties;

	public KafkaPublisher() {
		
	}
	
	@Override
	public void connect() {
		
		ProducerConfig config = new ProducerConfig(this.getProperties());
		producer = new Producer<String, String>(config);
	}
	
	@Override
	public void publishEvent(Event event, String destination) {
		
		JsonEventSerializer serializer = new JsonEventSerializer();
		String jsonEvent = serializer.convertToJSON(event);

		String topic = destination;
		KeyedMessage<String, String> jsonData = new KeyedMessage<String, String>(topic, jsonEvent);
		producer.send(jsonData);		
	}

	public Properties getProperties() {
		return properties;
	}
	
	public void setProperties(Properties properties) {
		this.properties = properties;
	}
}
