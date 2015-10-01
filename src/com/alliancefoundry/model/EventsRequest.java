package com.alliancefoundry.model;

import org.joda.time.DateTime;

/**
 * Created by: Bobby Writtenberry
 *
 */
public class EventsRequest {
	
	private DateTime createdAfter;
	private DateTime createdBefore;
	private String source;
	private String objectId;
	private String correlationId;
	private String eventName;
	private Integer generations;
	
	/**
	 * @param createdAfter
	 * @param createdBefore
	 * @param source
	 * @param objectId
	 * @param correlationId
	 * @param eventName
	 * @param generations
	 */
	public EventsRequest(DateTime createdAfter, DateTime createdBefore, String source, String objectId,
			String correlationId, String eventName, Integer generations) {
		this.createdAfter = createdAfter;
		this.createdBefore = createdBefore;
		this.source = source;
		this.objectId = objectId;
		this.correlationId = correlationId;
		this.eventName = eventName;
		this.generations = generations;
	}

	/**
	 * @return the createdAfter
	 */
	public DateTime getCreatedAfter() {
		return createdAfter;
	}

	/**
	 * @param createdAfter the createdAfter to set
	 */
	public void setCreatedAfter(DateTime createdAfter) {
		this.createdAfter = createdAfter;
	}

	/**
	 * @return the createdBefore
	 */
	public DateTime getCreatedBefore() {
		return createdBefore;
	}

	/**
	 * @param createdBefore the createdBefore to set
	 */
	public void setCreatedBefore(DateTime createdBefore) {
		this.createdBefore = createdBefore;
	}

	/**
	 * @return the source
	 */
	public String getSource() {
		return source;
	}

	/**
	 * @param source the source to set
	 */
	public void setSource(String source) {
		this.source = source;
	}

	/**
	 * @return the objectId
	 */
	public String getObjectId() {
		return objectId;
	}

	/**
	 * @param objectId the objectId to set
	 */
	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	/**
	 * @return the correlationId
	 */
	public String getCorrelationId() {
		return correlationId;
	}

	/**
	 * @param correlationId the correlationId to set
	 */
	public void setCorrelationId(String correlationId) {
		this.correlationId = correlationId;
	}

	/**
	 * @return the eventName
	 */
	public String getEventName() {
		return eventName;
	}

	/**
	 * @param eventName the eventName to set
	 */
	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	/**
	 * @return the generations
	 */
	public Integer getGenerations() {
		return generations;
	}

	/**
	 * @param generations the generations to set
	 */
	public void setGenerations(Integer generations) {
		this.generations = generations;
	}

}
