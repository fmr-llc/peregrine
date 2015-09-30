package com.alliancefoundry.publisher;

import java.util.Properties;

import com.alliancefoundry.exceptions.PeregrineException;
import com.alliancefoundry.model.Event;
import com.alliancefoundry.serializer.JsonEventSerializer;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

public class KafkaPublisher implements PublisherInterface {

	private Producer<String, String> producer;
	private String brokerUrl;

	public KafkaPublisher() {
		
	}
	
	@Override
	public void connect() {
		Properties props = new Properties();

		props.put("metadata.broker.list", brokerUrl);
		props.put("serializer.class", "kafka.serializer.StringEncoder");
		props.put("request.required.acks", "1");

		ProducerConfig config = new ProducerConfig(props);
		producer = new Producer<String, String>(config);
	}
	
	public String getBrokerUrl() {
		return brokerUrl;
	}

	public void setBrokerUrl(String brokerUrl) {
		this.brokerUrl = brokerUrl;
	}
	
	@Override
	public void publishEvent(Event event, String Topic) throws PeregrineException {
		
		JsonEventSerializer serializer = new JsonEventSerializer();
		String jsonEvent = serializer.convertToJSON(event);

		String topic = Topic;
		KeyedMessage<String, String> jsonData = new KeyedMessage<String, String>(topic, jsonEvent);
		producer.send(jsonData);		
	}
}
