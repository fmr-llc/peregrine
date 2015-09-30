package com.alliancefoundry.publisher;

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

import org.apache.activemq.ActiveMQConnectionFactory;

import com.alliancefoundry.exceptions.PeregrineErrorCodes;
import com.alliancefoundry.exceptions.PeregrineException;
import com.alliancefoundry.model.Event;
import com.alliancefoundry.serializer.JsonEventSerializer;
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

	@Override
	public void publishEvent(Event event, String topicName)  {
		PeregrineException exception = null;
		
		
		
		JsonEventSerializer serializer = new JsonEventSerializer();
		String jsonMessage = serializer.convertToJSON(event);
		
		try {
			// create connection
			Connection connection = connectionFactory.createConnection();
			
			// create session
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			
			Destination destination = session.createTopic(topicName);
			
			
			// create producer
			MessageProducer producer = session.createProducer(destination);
			producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
			
			
			TextMessage txtMessage = session.createTextMessage(jsonMessage);
			
			// publish topic to subscribers
			producer.send(txtMessage);
		} 
		catch (MessageFormatException e) {
			exception = new PeregrineException(PeregrineErrorCodes.MSG_FORMAT_ERROR, "The producer tried to send an invalid message.", e);
		} catch (InvalidDestinationException e) {
			exception = new PeregrineException(PeregrineErrorCodes.INVALID_DESTINATION, "The producer tried to send a message with an invalid destination.", e);
		} catch (UnsupportedOperationException e) {
			exception = new PeregrineException(PeregrineErrorCodes.DESTINATION_NOT_SUPPLIED, "The destination for the message was not specified at creation time.", e);
		} catch (JMSException e) {
			exception = new PeregrineException(PeregrineErrorCodes.JMS_INTERNAL_ERROR, "An internal error occurred, preventing the operation from occuring", e);
		}
		
		if(exception != null){
			// if anything bad occured, throw the exception
			throw exception;
		}
	}
}
