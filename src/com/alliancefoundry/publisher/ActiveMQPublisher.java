package com.alliancefoundry.publisher;

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

import com.alliancefoundry.model.Event;
import com.alliancefoundry.serializer.JsonEventSerializer;

public class ActiveMQPublisher implements PublisherInterface {
	
	private String brokerUrl;
	private String username;
	private String password;
	private ConnectionFactory connectionFactory;
	private boolean usingLoginCredentials = false;
	
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
	
	@Override
	public void publishEvent(Event event, String Topic) {
		
		// create connection
				Connection connection = null;
				Session session = null;
				MessageProducer producer = null;
				
				String topicName = Topic;
				
				JsonEventSerializer serializer = new JsonEventSerializer();
				String jsonMessage = serializer.convertToJSON(event);
				
				// create connection
				try {
					connection = connectionFactory.createConnection();
				} catch (JMSException e) {
					System.out.println("An internal error occurred, preventing the "
							+ "publisher from connecting to the ActiveMQ server.");
				}
				
				// create session
				try {
					session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
				} catch (JMSException e) {
					System.out.println("An internal error occurred, preventing the "
							+ "Connection object from creating a session.");
				}
				
				Destination destination = null;
				try {
					destination = session.createTopic(topicName);
				} catch (JMSException e) {
					System.out.println("An internal error occurred, preventing the "
							+ "session from creating a topic.");
				}
				
				// create producer
				try {
					producer = session.createProducer(destination);
				} catch (JMSException e) {
					System.out.println("An internal error occurred, preventing the "
							+ "session from creating a MessageProducer.");
				}
				try {
					producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
				} catch (JMSException e) {
					System.out.println("An internal error occurred, preventing the "
							+ "JMS provider from setting the delivery mode.");
				}
				
				TextMessage txtMessage = null;
				try {
					txtMessage = session.createTextMessage(jsonMessage);
				} catch (JMSException e) {
					System.out.println("An internal error occurred, preventing the " 
							+ "JMS provider from creating the text message.");
				}
				
				// publish topic to subscribers
				try {
					producer.send(txtMessage);
				} catch (MessageFormatException e) {
					System.out.println("The producer tried to send an invalid message.");
				} catch (InvalidDestinationException e) {
					System.out.println("The producer tried to send a message with an "
							+ "invalid destination.");
				} catch (UnsupportedOperationException e) {
					System.out.println("The destination for the message was not specified "
							+ "at creation time.");
				} catch (JMSException e) {
					System.out.println("An internal error occurred, preventing the " 
							+ "producer from sending the message.");
				}
		}
}
