package com.alliancefoundry.model;

import org.joda.time.DateTime;

import com.alliancefoundry.serializer.CustomJsonDateDeserializer;
import com.alliancefoundry.serializer.MyDateTimeSerializer;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

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
	
	public DateTime getCreatedAfter(){
		return createdAfter;
	}
	
	public void setCreatedAfter(DateTime param){
		createdAfter = param;
	}
	
	public DateTime getCreatedBefore(){
		return createdBefore;
	}
	
	public void setCreatedBefore(DateTime param){
		createdBefore = param;
	}
	
	public String getSource(){
		return source;
	}
	
	public void setSource(String param){
		source = param;
	}
	
	public String getObjectId(){
		return objectId;
	}
	
	public void setObjectId(String param){
		objectId = param;
	}
	
	public String getCorrelationId(){
		return correlationId;
	}
	
	public void setCorrelationId(String param){
		correlationId = param;
	}
	
	public String getEventName(){
		return eventName;
	}
	
	public void setEventName(String param){
		eventName = param;
	}
	
	public Integer getGenerations(){
		return generations;
	}
	
	public void setGenerations(Integer param){
		generations = param;
	}

}
