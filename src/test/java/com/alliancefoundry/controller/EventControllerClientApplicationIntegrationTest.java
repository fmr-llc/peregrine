package com.alliancefoundry.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.alliancefoundry.Boot;
import com.alliancefoundry.model.Event;
import com.alliancefoundry.model.EventRequest;
import com.alliancefoundry.model.EventResponse;
import com.alliancefoundry.model.EventsResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes={Boot.class})
@WebIntegrationTest(randomPort = true)
public class EventControllerClientApplicationIntegrationTest {
	@Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

	@Test
	public void testPostNewEvent() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		
		Event e = new Event();
		e.setEventId(UUID.randomUUID().toString());
		e.setObjectId("AAPL");
		e.setDataType("StockQuote");
		e.setEventId(UUID.randomUUID().toString());
		e.setMessageType("test-message");
		e.setSource("Yahoo");
		e.setReplayIndicator(Boolean.FALSE);
		EventRequest req = new EventRequest(e);
		
		MvcResult postResult = mockMvc.perform(post("/eventservice/event")
			.accept(MediaType.APPLICATION_JSON).content(req.toString()).contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk())
			.andReturn()
			;
		EventResponse eventResponse = mapper.readValue(
				postResult.getResponse().getContentAsString(),
				EventResponse.class
				);
		
		assertEquals("EventReponse: "+eventResponse, "OK", eventResponse.getPersistStatus());
				
		MvcResult result = mockMvc.perform(get("/eventservice/event-sources").accept(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))			
			.andReturn();

		EventsResponse eventsResponse = mapper.readValue(result.getResponse().getContentAsString(), EventsResponse.class);
		assertTrue(eventsResponse.getEventSources().contains("Yahoo"));
	}
}
