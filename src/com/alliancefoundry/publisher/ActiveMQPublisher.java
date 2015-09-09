package com.alliancefoundry.publisher;

import java.util.Map;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

import com.alliancefoundry.model.Event;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ActiveMQPublisher implements PublisherInterface {
	
	private String brokerUrl;
	private String username;
	private String password;
	private ConnectionFactory connectionFactory;
	private boolean isConnected;
	private boolean usingLoginCredentials = false;
	private String destType;

	
	// required for bean
	public ActiveMQPublisher() {

	}
	
	@Override
	public void connect() {
	
		// if username and password are being used, then use that form of connection factory
		if(usingLoginCredentials){
			// create connection factory
			connectionFactory = new ActiveMQConnectionFactory(this.username, this.password, this.brokerUrl);
		}else{// connect with no login credintials
			// create connection factory
			connectionFactory = new ActiveMQConnectionFactory(this.brokerUrl);
		}
		
	}

	@Override
	public void publishEvent(Event event, Map<String, String> config){
		
		// create connection
		Connection connection = null;
		Session session = null;
		MessageProducer producer = null;
		
		String topicName = config.get(EventServicePublisher.TOPIC_KEY);
		
		// turn java onject to json string
		ObjectMapper mapper = new ObjectMapper(); 
		//POJO to JSON
		String jsonMessage = null;
		try {
			jsonMessage = mapper.writeValueAsString(event);
		} catch (JsonProcessingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		
		try {
			// create connection
			connection = connectionFactory.createConnection();
			
			// create session
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			
			Destination destination = session.createTopic(topicName);
			
			// create producer
			producer = session.createProducer(destination);
			producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
			
			TextMessage txtMessage = session.createTextMessage(jsonMessage);
			
			// publish topic to subscribers
			producer.send(txtMessage);

			System.out.println("Message sent to subscribers");
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public String getBrokerUrl() {
		return brokerUrl;
	}

	public void setBrokerUrl(String brokerUrl) {
		this.brokerUrl = brokerUrl;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isUsingLoginCredentials() {
		return usingLoginCredentials;
	}

	public void setUsingLoginCredentials(boolean usingLoginCredentials) {
		this.usingLoginCredentials = usingLoginCredentials;
	}
	
	public String getDestType() {
		return destType;
	}

	public void setDestType(String destType) {
		this.destType = destType;
	}


}