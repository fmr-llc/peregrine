package com.alliancefoundry.tests.DAOTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.alliancefoundry.dao.IDAO;
import com.alliancefoundry.exceptions.EventNotFoundException;
import com.alliancefoundry.exceptions.PeregrineErrorCodes;
import com.alliancefoundry.exceptions.PeregrineException;
import com.alliancefoundry.model.DataItem;
import com.alliancefoundry.model.Event;
import com.alliancefoundry.publisher.PublisherRouter;
import com.alliancefoundry.tests.PublisherTests.ActiveMQTests.ActiveMQSubscriber;
import com.alliancefoundry.tests.PublisherTests.KafkaTests.KafkaSubscriber;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by: Paul Fahey, Robert Coords
 * 
 *
 */

public class JDBC_broker_DAO_tests {
	
	IDAO dao;
	Event event;
	String eventId;
	Event eventFromDb;
	
	Event getEvent1, getEvent2, getEvent3, getEvent4, getEvent5, getEvent6;
	Event eventFromListener;
	
	MessageListener listener;
	ActiveMQSubscriber subscriber1, subscriber2;
	PublisherRouter publisher;

	@Before
	public void setUp() throws Exception {
		
		AbstractApplicationContext ctx;
		ctx = new ClassPathXmlApplicationContext("kafka-mock-events.xml");

		getEvent1 = ctx.getBean("event8", Event.class);
		getEvent2 = ctx.getBean("event9", Event.class);
		getEvent3 = ctx.getBean("event10", Event.class);
		
		ctx.close(); 
		
		ctx = new ClassPathXmlApplicationContext("activemq-mock-events.xml");
		
		getEvent4 = ctx.getBean("mockEvent4", Event.class);
		getEvent5 = ctx.getBean("mockEvent5", Event.class);
		getEvent6 = ctx.getBean("mockEvent6", Event.class);
		
		ctx.close();
		
		ctx = new ClassPathXmlApplicationContext("eventservice-beans.xml");
		
		dao = ctx.getBean("dao", IDAO.class);

		ctx.close();
		
		AbstractApplicationContext pubctx;
		pubctx = new ClassPathXmlApplicationContext("eventservice-beans.xml");
		pubctx.registerShutdownHook();
		
		// setup subscriber for ActiveMQ
		subscriber1 = pubctx.getBean("activemqSubscriber1", ActiveMQSubscriber.class);
		subscriber1.subscribeTopic("topic3b");
		subscriber2 = pubctx.getBean("activemqSubscriber2", ActiveMQSubscriber.class);
		subscriber2.subscribeTopic("topic3a");
		
		// setup publisher
		publisher = pubctx.getBean("eventPublisherservice", PublisherRouter.class);
		pubctx.close();
	}
	
	/***********************
	 *Testing insertEvent()
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 * @throws EventNotFoundException 
	 * @throws PeregrineException *
	 ***********************/
	
	@Test
	public void insertToDbAndKafkaBrokerTest() throws EventNotFoundException, PeregrineException {
		event = getEvent2;

		Map<String,String> headers = new HashMap<String,String>();
		Map<String,DataItem> payload = new HashMap<String,DataItem>();
		
		headers.put("some key", "some value");
		payload.put("some key", new DataItem("some data type","some value"));
		headers.put("some other key", "some other value");
		payload.put("some other key", new DataItem("some other data type","some other value"));
		
		event.setCustomHeaders(headers);
		event.setCustomPayload(payload);
		
		String eventId = dao.insertEvent(event);
		
		publisher.attemptPublishEvent(event);
	
		eventFromDb = dao.getEvent(eventId);
	
		String expected = event.toString();
		String actual = eventFromDb.toString();
		
		KafkaSubscriber kafkaSubscriber = new KafkaSubscriber("testJaneDoe347");
		String event_json = kafkaSubscriber.consumeEvent();
		ObjectMapper mapper = new ObjectMapper(); 
		Event actual2;
		try {
			actual2 = mapper.readValue(event_json, Event.class);
		} catch (JsonParseException e) {
			throw new PeregrineException(PeregrineErrorCodes.JSON_PARSE_ERROR, "Could not parse into JSON object.", e);
		} catch (JsonMappingException e) {
			throw new PeregrineException(PeregrineErrorCodes.JSON_MAPPING_ERROR, "Could not map JSON object into Event object.", e);
		} catch (IOException e) {
			throw new PeregrineException(PeregrineErrorCodes.INPUT_SOURCE_ERROR, "No import source found.", e);
		}
		
		//db insert check
		assertEquals(expected,actual);	
		
		//broker check
		assertEquals(eventFromDb.toString(), actual2.toString());	
	}
	
	@Test
	public void insertMultipleEventsToDbAndKafkaTest() throws PeregrineException, EventNotFoundException {
		
		event = getEvent1;
		Event event2 = getEvent3;
		List<Event> events = new ArrayList<Event>();
		events.add(event);
		events.add(event2);
		
		List<String> expected = new ArrayList<String>();
		expected = dao.insertEvents(events);
		
		publisher.attemptPublishEvents(events);
	
		List<String> actual = new ArrayList<String>();
		
		//for broker testing
		List<String> actual2 = new ArrayList<String>();
		
		for(String id : expected){
			
			actual.add((dao.getEvent(id)).getEventId());
			
			KafkaSubscriber kafkaSubscriber = new KafkaSubscriber("testJaneDoe346");
			
			String eventasString = kafkaSubscriber.consumeEvent();
			ObjectMapper mapper = new ObjectMapper(); 
			Event event;
			try {
				event = mapper.readValue(eventasString, Event.class);
			} catch (JsonParseException e) {
				throw new PeregrineException(PeregrineErrorCodes.JSON_PARSE_ERROR, "Could not parse into JSON object.", e);
			} catch (JsonMappingException e) {
				throw new PeregrineException(PeregrineErrorCodes.JSON_MAPPING_ERROR, "Could not map JSON object into Event object.", e);
			} catch (IOException e) {
				throw new PeregrineException(PeregrineErrorCodes.INPUT_SOURCE_ERROR, "No import source found.", e);
			}

			actual2.add(event.getEventId());
		}
		
		//db insert check
		assertEquals(expected,actual);	
		
		//broker check
		for(String evid : actual){
			assertTrue(actual2.contains(evid));
		}

	}
	
	/***********************
	 *Testing insertEventActiveMQ()
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 * @throws EventNotFoundException 
	 * @throws PeregrineException *
	 ***********************/
	
	@Test
	public void insertToDbAndActiveMQBrokerTest() throws EventNotFoundException, PeregrineException {
		event = getEvent5;
		
		// setup listener for activemq
		listener = new MessageListener() {
			public void onMessage(Message message) {
				PeregrineException exception = null;
				if(message instanceof TextMessage){
					TextMessage txt = (TextMessage)message;
					try {
						String eventAsJson = txt.getText();
						// turn json string back into event object
						ObjectMapper mapper = new ObjectMapper(); 
						eventFromListener = mapper.readValue(eventAsJson, Event.class);
					}  catch (JsonParseException e) {
						exception = new PeregrineException(PeregrineErrorCodes.MSG_FORMAT_ERROR, "Error converting JSON to an Object.", e);
					} catch (JsonMappingException e) {
						exception = new PeregrineException(PeregrineErrorCodes.MSG_FORMAT_ERROR, "Error mapping JSON to Object.", e);
					} catch (IOException e) {
						exception = new PeregrineException(PeregrineErrorCodes.INPUT_SOURCE_ERROR, "Error parsing input source", e);
					} catch (JMSException e) {
						exception = new PeregrineException(PeregrineErrorCodes.JMS_INTERNAL_ERROR, "An internal error occurred", e);
					}
					
					if(exception != null){
						// Cannot throw exception here,
						// Just log it
						System.out.println("An error has occured, replace with a log");
					}

				}				
			}
		};
		
		Map<String,String> headers = new HashMap<String,String>();
		Map<String,DataItem> payload = new HashMap<String,DataItem>();
		
		headers.put("some key", "some value");
		payload.put("some key", new DataItem("some data type","some value"));
		headers.put("some other key", "some other value");
		payload.put("some other key", new DataItem("some other data type","some other value"));
		
		event.setCustomHeaders(headers);
		event.setCustomPayload(payload);
		
		subscriber1.setConsumerListener(listener);
		
		String eventId = dao.insertEvent(event);
		
		publisher.attemptPublishEvent(event);
	
		eventFromDb = dao.getEvent(eventId);
	
		Event expected = event;
		Event actual = eventFromDb;
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			System.out.println("Error sleep interrupted.");
		}
		
		Event actual2 = eventFromListener;
		
		//db insert check
		assertEquals(expected,actual);
				
		//broker check
		assertEquals(actual.toString(), actual2.toString());	
	}
	
	@Test
	public void insertMultipleEventsToDbAndActiveMQTest() throws PeregrineException, EventNotFoundException {
		event = getEvent4;
		Event event2 = getEvent6;
		List<Event> events = new ArrayList<Event>();
		events.add(event);
		events.add(event2);
		
		//for broker testing
		List<String> actual2 = new ArrayList<String>();
		
		// setup listener for activemq
		listener = new MessageListener() {
			public void onMessage(Message message) {
				PeregrineException exception = null;
				if(message instanceof TextMessage){
					TextMessage txt = (TextMessage)message;
					try {
						String eventAsJson = txt.getText();
						// turn json string back into event object
						ObjectMapper mapper = new ObjectMapper(); 
						eventFromListener = mapper.readValue(eventAsJson, Event.class);
						actual2.add(eventFromListener.toString());
					}  catch (JsonParseException e) {
						exception = new PeregrineException(PeregrineErrorCodes.MSG_FORMAT_ERROR, "Error converting JSON to an Object.", e);
					} catch (JsonMappingException e) {
						exception = new PeregrineException(PeregrineErrorCodes.MSG_FORMAT_ERROR, "Error mapping JSON to Object.", e);
					} catch (IOException e) {
						exception = new PeregrineException(PeregrineErrorCodes.INPUT_SOURCE_ERROR, "Error parsing input source", e);
					} catch (JMSException e) {
						exception = new PeregrineException(PeregrineErrorCodes.JMS_INTERNAL_ERROR, "An internal error occurred", e);
					}
					
					if(exception != null){
						// Cannot throw exception here,
						// Just log it
						System.out.println("An error has occured, replace with a log");
					}

				}				
			}
		};
		
		subscriber2.setConsumerListener(listener);
		
		List<String> ids = new ArrayList<String>();
		List<String> expected = new ArrayList<String>();
		List<String> actual = new ArrayList<String>();
		ids = dao.insertEvents(events);
		for (Event e : events) {
			expected.add(e.toString());
		}
		for (String id : ids) {
			actual.add(dao.getEvent(id).toString());
		}
		
		publisher.attemptPublishEvents(events);
		
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			System.out.println("Error sleep interrupted.");
		}
		
		//db insert check
		assertEquals(expected,actual);	
				
		//broker check
		for(String evid : actual){
			assertTrue(actual2.contains(evid));
		}

	}
	
	public boolean compareLists(List<String> expected, List<String> actual2) {
		if (expected.size() == actual2.size()) {
			for (int a = 0; a < expected.size(); a++) {
				String ev1 = expected.get(a);
				String ev2 = actual2.get(a);
				if (!ev1.equals(ev2)) {
					return false;
				}
			}
			return true;
		} else {
			return false;
		}
	}
}