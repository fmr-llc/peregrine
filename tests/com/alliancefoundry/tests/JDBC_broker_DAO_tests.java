package com.alliancefoundry.tests;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.alliancefoundry.dao.JDBCDAOimpl;
import com.alliancefoundry.model.DataItem;
import com.alliancefoundry.model.Event;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JDBC_broker_DAO_tests {
	
	JDBCDAOimpl dao;
	Event event;
	String eventId;
	Event eventFromDb;
	
	Event getEvent2;

	@Before
	public void setUp() throws Exception {
		
		dao = new JDBCDAOimpl();

		AbstractApplicationContext ctx;
		ctx = new ClassPathXmlApplicationContext("db-mock-events.xml");

		getEvent2 = ctx.getBean("childEvent1", Event.class);

		ctx.close();
	}
	
	/***********************
	 *Testing insertEvent()
	 * @throws SQLException 
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException *
	 ***********************/
	
	@Test
	public void insertToDbAndBrokerTest() throws SQLException, JsonParseException, JsonMappingException, IOException {
		event = getEvent2;

		Map<String,String> headers = new HashMap<String,String>();
		Map<String,DataItem> payload = new HashMap<String,DataItem>();
		
		headers.put("some key", "some value");
		payload.put("some key", new DataItem("some data type","some value"));
		headers.put("some other key", "some other value");
		payload.put("some other key", new DataItem("some other data type","some other value"));
		
		event.setCustomHeaders(headers);
		event.setCustomPayload(payload);
		
		try {
			eventId = dao.insertEvent(event);
		} catch (SQLException e) {
			System.out.println("There was a SQL issue when inserting the event into the database");
		}
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
}