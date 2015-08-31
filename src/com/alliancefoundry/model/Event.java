package com.alliancefoundry.model;


import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateTime;

import com.alliancefoundry.serializer.CustomJsonDateDeserializer;
import com.alliancefoundry.serializer.MyDateTimeSerializer;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;



/**
 * Created by Paul Bernard
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Event {

    // Headers
    private long eventId;
    private String parentId;
    private String eventName;
    private String objectId;
    private String correlationId;
    private String sequenceNumber;
    private String messageType;
    private String dataType;
    private String source;
    private String destination;
    private String subdestination;
    private boolean replayIndicator;
    @JsonSerialize(using = MyDateTimeSerializer.class)
    @JsonDeserialize(using = CustomJsonDateDeserializer.class)
    private DateTime publishedTimeStamp;
    @JsonSerialize(using = MyDateTimeSerializer.class)
    @JsonDeserialize(using = CustomJsonDateDeserializer.class)
    private DateTime receivedTimeStamp;
    @JsonSerialize(using = MyDateTimeSerializer.class)
    @JsonDeserialize(using = CustomJsonDateDeserializer.class)
    private DateTime expirationTimeStamp;

    // other
    private Map<String, String> customHeaders = new HashMap<String, String>();
    private Map<String, String> payload = new HashMap<String, String>();
    private String preEventState;
    private String postEventState;
    private boolean isPublishable;
    @JsonSerialize(using = MyDateTimeSerializer.class)
    @JsonDeserialize(using = CustomJsonDateDeserializer.class)
    private DateTime insertTimeStamp;

    public Event(){
    	
    }
    
    /**
	 * @param parentId
	 * @param eventName
	 * @param objectId
	 * @param correlationId
	 * @param sequenceNumber
	 * @param messageType
	 * @param dataType
	 * @param source
	 * @param destination
	 * @param subdestination
	 * @param replayIndicator
	 * @param publishedTimeStamp
	 * @param receivedTimeStamp
	 * @param expirationTimeStamp
	 * @param preEventState
	 * @param postEventState
	 * @param isPublishable
	 * @param insertTimeStamp
	 * 
	 * Should probably remove after testing.  Event won't need to be created by
	 * the system.
	 * 
	 */
	public Event(String parentId, String eventName, String objectId, String correlationId, String sequenceNumber,
			String messageType, String dataType, String source, String destination, String subdestination,
			boolean replayIndicator, DateTime publishedTimeStamp, DateTime receivedTimeStamp,
			DateTime expirationTimeStamp, String preEventState, String postEventState, boolean isPublishable,
			DateTime insertTimeStamp) {
		this.parentId = parentId;
		this.eventName = eventName;
		this.objectId = objectId;
		this.correlationId = correlationId;
		this.sequenceNumber = sequenceNumber;
		this.messageType = messageType;
		this.dataType = dataType;
		this.source = source;
		this.destination = destination;
		this.subdestination = subdestination;
		this.replayIndicator = replayIndicator;
		this.publishedTimeStamp = publishedTimeStamp;
		this.receivedTimeStamp = receivedTimeStamp;
		this.expirationTimeStamp = expirationTimeStamp;
		this.preEventState = preEventState;
		this.postEventState = postEventState;
		this.isPublishable = isPublishable;
		this.insertTimeStamp = insertTimeStamp;
	}
    
	
	
    public Map<String, String> getCustomHeaders() {
		return customHeaders;
	}

	public void setCustomHeaders(Map<String, String> customHeaders) {
		this.customHeaders = customHeaders;
	}

	public Map<String, String> getPayload() {
		return payload;
	}

	public void setPayload(Map<String, String> payload) {
		this.payload = payload;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public void setCorrelationId(String correlationId) {
		this.correlationId = correlationId;
	}

	public void setSequenceNumber(String sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public void setSubdestination(String subdestination) {
		this.subdestination = subdestination;
	}

	public void setReplayIndicator(boolean replayIndicator) {
		this.replayIndicator = replayIndicator;
	}

	public void setPublishedTimeStamp(DateTime publishedTimeStamp) {
		this.publishedTimeStamp = publishedTimeStamp;
	}

	public void setReceivedTimeStamp(DateTime receivedTimeStamp) {
		this.receivedTimeStamp = receivedTimeStamp;
	}

	public void setExpirationTimeStamp(DateTime expirationTimeStamp) {
		this.expirationTimeStamp = expirationTimeStamp;
	}

	public void setPreEventState(String preEventState) {
		this.preEventState = preEventState;
	}

	public void setPostEventState(String postEventState) {
		this.postEventState = postEventState;
	}

	public void setPublishable(boolean isPublishable) {
		this.isPublishable = isPublishable;
	}

	public void setInsertTimeStamp(DateTime insertTimeStamp) {
		this.insertTimeStamp = insertTimeStamp;
	}

	public void setEventId(long id){
        eventId = id;
    }

    public long getEventId(){
        return eventId;
    }

	public String getParentId() {
		return parentId;
	}

	public String getEventName() {
		return eventName;
	}

	public String getObjectId() {
		return objectId;
	}

	public String getCorrelationId() {
		return correlationId;
	}

	public String getSequenceNumber() {
		return sequenceNumber;
	}

	public String getMessageType() {
		return messageType;
	}

	public String getDataType() {
		return dataType;
	}

	public String getSource() {
		return source;
	}

	public String getDestination() {
		return destination;
	}

	public String getSubdestination() {
		return subdestination;
	}

	public boolean isReplayIndicator() {
		return replayIndicator;
	}

	public DateTime getPublishedTimeStamp() {
		return publishedTimeStamp;
	}

	public DateTime getReceivedTimeStamp() {
		return receivedTimeStamp;
	}

	public DateTime getExpirationTimeStamp() {
		return expirationTimeStamp;
	}

	public String getPreEventState() {
		return preEventState;
	}

	public String getPostEventState() {
		return postEventState;
	}

	public boolean isPublishable() {
		return isPublishable;
	}

	public DateTime getInsertTimeStamp() {
		return insertTimeStamp;
	}

}
