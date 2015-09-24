package com.alliancefoundry.tests;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.InvalidDestinationException;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.spring.ActiveMQConnectionFactory;

public class ActiveMQSubscriber {
	
	String brokerUrl;
	String name;
	
	Connection connection = null;
	Session session = null;
	MessageConsumer consumer = null;

	public ActiveMQSubscriber(){
	}
	
	public ActiveMQSubscriber(String brokerUrl, String name){
		this.brokerUrl = brokerUrl;
		this.name = name;
	}
	
	public void subscribeTopic(String topicName) {
		// create connectionFactory
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory();

		// create connection
		try {
			connection = connectionFactory.createConnection();
		} catch (JMSException e) {
			System.out.println("An internal error occurred, preventing the "
					+ "subscriber from connecting to the ActiveMQ server.");
		}
		
		// start
		try {
			connection.start();
		} catch (JMSException e1) {
			System.out.println("An internal error occurred, preventing the "
					+ "JMS provider from starting the message delivery.");
		}
		
		// create session
		try {
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		} catch (JMSException e1) {
			System.out.println("An internal error occurred, preventing the "
					+ "Connection object from creating a session.");
		}
		
		Destination destination = null;
		try {
			destination = session.createTopic(topicName);
		} catch (JMSException e1) {
			System.out.println("An internal error occurred, preventing the "
					+ "session from creating a topic.");
		}
		
		try {
			consumer = session.createConsumer(destination);
		} catch (InvalidDestinationException e1) {
			System.out.println("The specified destination was invalid.");
		} catch (JMSException e1) {
			System.out.println("An internal error occurred, preventing the "
					+ "session from creating a consumer.");
		}
	}
	
	public void setConsumerListener(MessageListener listener){
		try {
			consumer.setMessageListener(listener);
		} catch (JMSException e) {
			System.out.println("An internal error has occurred, preventing the "
					+ "MessageListener from being set.");
		}
	}
	
	public void Shutdown() throws JMSException{
		consumer.close();
		session.close();
		connection.close();

	}

	public String getBrokerUrl() {
		return brokerUrl;
	}

	public void setBrokerUrl(String brokerUrl) {
		this.brokerUrl = brokerUrl;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
