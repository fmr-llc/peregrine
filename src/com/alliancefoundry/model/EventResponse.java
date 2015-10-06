package com.alliancefoundry.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by: Bobby Writtenberry
 *
 */
public class EventResponse {

	private List<String> eventIds = new ArrayList<String>();
	private List<Event> events = new ArrayList<Event>();
	private String msg;
	
	/**
	 * 
	 */
	public EventResponse() {}

	/**
	 * @return the eventIds
	 */
	public List<String> getEventIds() {
		return eventIds;
	}

	/**
	 * @param eventIds the eventIds to set
	 */
	public void setEventIds(List<String> eventIds) {
		this.eventIds = eventIds;
	}

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
