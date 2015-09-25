package com.alliancefoundry.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.alliancefoundry.dao.DAO;
import com.alliancefoundry.exceptions.EventNotFoundException;
import com.alliancefoundry.model.DataItem;
import com.alliancefoundry.model.Event;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JDBC_broker_DAO_tests {
	
	DAO dao;
	Event event;
	String eventId;
	Event eventFromDb;
	
	Event getEvent1, getEvent2, getEvent3;

	@Before
	public void setUp() throws Exception {
		
		AbstractApplicationContext ctx;
		ctx = new ClassPathXmlApplicationContext("db-mock-events.xml");

		getEvent1 = ctx.getBean("parentEvent", Event.class);
		getEvent2 = ctx.getBean("childEvent1", Event.class);
		getEvent3 = ctx.getBean("childEvent2", Event.class);
		
		ctx.close(); 
		
		ctx = new ClassPathXmlApplicationContext("eventservice-beans.xml");
		
		dao = ctx.getBean("dao", DAO.class);

		ctx.close();
	}
	
	/***********************
	 *Testing insertEvent()
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 * @throws EventNotFoundException *
	 ***********************/
	
	@Test
	public void insertToDbAndBrokerTest() throws JsonParseException, JsonMappingException, IOException, EventNotFoundException {
		event = getEvent2;

		Map<String,String> headers = new HashMap<String,String>();
		Map<String,DataItem> payload = new HashMap<String,DataItem>();
		
		headers.put("some key", "some value");
		payload.put("some key", new DataItem("some data type","some value"));
		headers.put("some other key", "some other value");
		payload.put("some other key", new DataItem("some other data type","some other value"));
		
		event.setCustomHeaders(headers);
		event.setCustomPayload(payload);
		
		eventId = dao.insertEvent(event);
	
		eventFromDb = dao.getEvent(eventId);
	
		String expected = eventId;
		String actual = eventFromDb.getEventId();
		
		KafkaSubscriber kafkaSubscriber = new KafkaSubscriber("testJaneDoe345");
		String event_json = kafkaSubscriber.consumeEvent();
		ObjectMapper mapper = new ObjectMapper(); 
		Event actual2 = mapper.readValue(event_json, Event.class);
		
		//db insert check
		assertEquals(expected,actual);	
		
		//broker check
		assertEquals(eventFromDb.getEventId(), actual2.getEventId());	
	}
	
	@Test
	public void insertMultipleEventsToDbTest() throws EventNotFoundException, JsonParseException, JsonMappingException, IOException {
		
		event = getEvent1;
		Event event2 = getEvent3;
		List<Event> events = new ArrayList<Event>();
		events.add(event);
		events.add(event2);
		
		List<String> expected = new ArrayList<String>();
		expected = dao.insertEvents(events);
		
		List<String> actual = new ArrayList<String>();
		
		//for broker testing
		List<String> actual2 = new ArrayList<String>();
		
		for(String id : expected){
			
			actual.add((dao.getEvent(id)).getEventId());
			
			KafkaSubscriber kafkaSubscriber = new KafkaSubscriber("testJaneDoe346");
			
			String eventasString = kafkaSubscriber.consumeEvent();
			ObjectMapper mapper = new ObjectMapper(); 
			Event event = mapper.readValue(eventasString, Event.class);

			actual2.add(event.getEventId());
		}
		
		//db insert check
		assertEquals(expected,actual);	
				
		//broker check
		for(String evid : expected){
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