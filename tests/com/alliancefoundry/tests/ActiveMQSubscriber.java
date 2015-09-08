package com.alliancefoundry.tests;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
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

	
	public ActiveMQSubscriber(String brokerUrl, String name){
		this.brokerUrl = brokerUrl;
		this.name = name;
	}
	
	public void subscribeTopic(String topicName) throws Exception{
		// create connectionfactory
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
		
		
		try {
			// create connection
			connection = connectionFactory.createConnection();
			
			// start
			connection.start();
			
			// create session
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			
			Destination destination = session.createTopic(topicName);
			
			consumer = session.createConsumer(destination);
			
			// create listener
			MessageListener listener = new MessageListener() {
				
				public void onMessage(Message message) {
					if(message instanceof TextMessage){
						TextMessage txt = (TextMessage)message;
						try {
							System.out.println(name + ": " + txt.getText());
						} catch (JMSException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			};
			
			consumer.setMessageListener(listener);			

			
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setConsumerListener(MessageListener listener){
		try {
			consumer.setMessageListener(listener);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void Shutdown() throws JMSException{
		consumer.close();
		session.close();
		connection.close();

	}

}
