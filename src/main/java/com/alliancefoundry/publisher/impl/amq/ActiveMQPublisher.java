package com.alliancefoundry.publisher.impl.amq;

import java.util.List;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import com.alliancefoundry.publisher.PublisherException;
import com.alliancefoundry.publisher.PublisherInterface;
import com.alliancefoundry.publisher.RouterConfig;
import org.apache.activemq.ActiveMQConnectionFactory;

import com.alliancefoundry.model.Event;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActiveMQPublisher implements PublisherInterface {

	private static final Logger log = LoggerFactory.getLogger(ActiveMQPublisher.class);
	private String brokerUrl;
	private String username;
	private String password;

	private ActiveMQConnectionFactory connectionFactory;
	PooledConnectionFactory pcf;
	private boolean isConnected;
	private boolean usingLoginCredentials = false;
	private String destType;


	// required for bean
	public ActiveMQPublisher() {

	}
	
	@Override
	public void init() {
	
		// if username and password are being used, then use that form of connection factory
		if(usingLoginCredentials){
			// create connection factory
			connectionFactory = new ActiveMQConnectionFactory(this.username, this.password, this.brokerUrl);
			connectionFactory.setUseAsyncSend(true);


		} else{// connect with no login credintials
			// create connection factory
			connectionFactory = new ActiveMQConnectionFactory(this.brokerUrl);
		}

		pcf = new PooledConnectionFactory(connectionFactory);


	}

	@Override
	public boolean publishEvents(List<Event> events, RouterConfig config) throws PublisherException {
		throw new PublisherException("simultaneous publishing of events is not yet implemented");
	}

	@Override
	public boolean publishEvent(Event event, RouterConfig config) throws PublisherException {

		if (pcf==null) {
			init();
		}

		if (pcf==null){

			throw new PublisherException("No connection to message broker is available.");

		}

		Connection connection = null;
		Session session = null;

		String topicName = config.getDestination(event.getMessageType());

		log.debug("publishing event to topic/queue with address: " + topicName);

		try {

			connection = pcf.createConnection();

			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);


			MessageProducer producer = null;

			if (producer==null){

				Destination destination = null;
				if (topicName.startsWith("queue://")){
					destination = session.createQueue(topicName.substring(8));
				} else if (topicName.startsWith("topic://")){
					destination = session.createTopic(topicName.substring(8));
				} else {
					log.error("invalid configuration on topic or queue name");
					throw new PublisherException("invalid configuration on topic or queue name");
				}

				producer = session.createProducer(destination);
				if (producer==null) throw new PublisherException("configured producer could not be found");

				producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

				log.debug("stashed producer with name: " + topicName);

			}

			ObjectMapper mapper = new ObjectMapper();
			String jsonMessage = null;
			jsonMessage = mapper.writeValueAsString(event);

			TextMessage txtMessage = null;

			txtMessage = session.createTextMessage(jsonMessage);
			log.debug("message being sent = " + txtMessage);
			producer.send(txtMessage);





		} catch (JMSException e) {

			log.error("Publishing Error", e);
			throw new PublisherException("generic JMS Exception", e);

		} catch (JsonProcessingException e) {

			log.error("Error converting object to JSON String.");
			throw new PublisherException("message could not be serialized to json string.", e);

		} finally {
			try {
				if (session!=null) { session.close(); }
				if (connection!=null){ connection.close();  }

			} catch (JMSException e){
				log.warn("connection already closed");
			}

		}

		return true;

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


}
