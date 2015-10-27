package com.alliancefoundry.publisher.impl;

import java.util.List;
import java.util.Map;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.InvalidDestinationException;
import javax.jms.JMSException;
import javax.jms.MessageFormatException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import com.alliancefoundry.publisher.EventServicePublisher;
import com.alliancefoundry.publisher.PublisherInterface;
import com.alliancefoundry.publisher.RouterConfig;
import org.apache.activemq.ActiveMQConnectionFactory;

import com.alliancefoundry.model.Event;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActiveMQPublisher implements PublisherInterface {

	private static final Logger log = LoggerFactory.getLogger(ActiveMQPublisher.class);
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
	public void publishEvent(Event event, RouterConfig config){

		if (connectionFactory==null) {
			connect();
		}
		
		// create connection
		Connection connection = null;
		Session session = null;
		MessageProducer producer = null;
		
		String topicName = config.getDestination(event.getMessageType());
		
		// turn java onject to json string
		ObjectMapper mapper = new ObjectMapper(); 
		//POJO to JSON
		String jsonMessage = null;
		try {
			jsonMessage = mapper.writeValueAsString(event);
		} catch (JsonProcessingException e1) {
			log.error("Error converting object to JSON String.");
		}

		
		// create connection
		try {
			connection = connectionFactory.createConnection();
		} catch (JMSException e) {
			log.error("An internal error occurred, preventing the "
					+ "publisher from connecting to the ActiveMQ server.");
		}
		
		// create session
		try {
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		} catch (JMSException e) {
			log.error("An internal error occurred, preventing the "
					+ "Connection object from creating a session.");
		}
		
		Destination destination = null;
		try {
			destination = session.createTopic(topicName);
		} catch (JMSException e) {
			log.error("An internal error occurred, preventing the "
					+ "session from creating a topic.");
		}
		
		// create producer
		try {
			producer = session.createProducer(destination);
		} catch (JMSException e) {
			log.error("An internal error occurred, preventing the "
					+ "session from creating a MessageProducer.");
		}
		try {
			producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
		} catch (JMSException e) {
			log.error("An internal error occurred, preventing the "
					+ "JMS provider from setting the delivery mode.");
		}
		
		TextMessage txtMessage = null;
		try {
			txtMessage = session.createTextMessage(jsonMessage);
		} catch (JMSException e) {
			log.error("An internal error occurred, preventing the "
					+ "JMS provider from creating the text message.");
		}
		
		// publish topic to subscribers
		try {
			producer.send(txtMessage);
		} catch (MessageFormatException e) {
			log.error("The producer tried to send an invalid message.");
		} catch (InvalidDestinationException e) {
			log.error("The producer tried to send a message with an "
					+ "invalid destination.");
		} catch (UnsupportedOperationException e) {
			log.error("The destination for the message was not specified "
					+ "at creation time.");
		} catch (JMSException e) {
			log.error("An internal error occurred, preventing the "
					+ "producer from sending the message.");
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


	@Override
	public void publishEvent(List<Event> events, RouterConfig config) {

	}


}
