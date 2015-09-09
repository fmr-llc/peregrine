package com.alliancefoundry.publisher;

import java.util.Map;
import java.util.Properties;

import com.alliancefoundry.model.Event;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

public class KafkaPublisher implements PublisherInterface {

	private Producer<String, String> producer;
	private String brokerUrl;
	private String destType;

	
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
	public void publishEvent(Event event, Map<String, String> eventConfig) {
		
		ObjectMapper mapper = new ObjectMapper();
		String jsonEvent = null;
		
		try {
			jsonEvent = mapper.writeValueAsString(event);
		} 
		catch (JsonProcessingException e) {
		
			e.printStackTrace();
		}

		String topic = (String)eventConfig.get(EventServicePublisher.TOPIC_KEY);
		KeyedMessage<String, String> jsonData = new KeyedMessage<String, String>(topic, jsonEvent);
		producer.send(jsonData);
		
		System.out.println(jsonData);
		
		producer.close();

	}
	
	public String getDestType() {
		return destType;
	}

	public void setDestType(String destType) {
		this.destType = destType;
	}

	
	
}

