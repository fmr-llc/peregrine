package com.alliancefoundry.dao;

import java.sql.SQLException;
import java.util.List;

import com.alliancefoundry.model.Event;
import com.alliancefoundry.model.EventsRequest;

public interface DAO {
	public String insertEvent(Event event) throws SQLException;
	public List<String> insertEvents(List<Event> events) throws SQLException;
	public Event getEvent(String eventId) throws SQLException;
	public List<Event> getEvents(EventsRequest req) throws IllegalArgumentException, SQLException;
	public Event getLatestEvent(EventsRequest req) throws SQLException;
}
