package com.alliancefoundry.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by: Bobby Writtenberry
 *
 */
public class EventResponse {

	private List<Event> events = new ArrayList<Event>();
	private EventsRequest request;
	private String msg;
	
	/**
	 * 
	 */
	public EventResponse() {}

	/**
	 * @return the events
	 */
	public List<Event> getEvents() {
		return events;
	}

	/**
	 * @param events the events to set
	 */
	public void setEvents(List<Event> events) {
		this.events = events;
	}

	/**
	 * @return the request
	 */
	public EventsRequest getRequest() {
		return request;
	}

	/**
	 * @param request the request to set
	 */
	public void setRequest(EventsRequest request) {
		this.request = request;
	}

	/**
	 * @return the msg
	 */
	public String getMsg() {
		return msg;
	}

	/**
	 * @param msg the msg to set
	 */
	public void setMsg(String msg) {
		this.msg = msg;
	}

}
