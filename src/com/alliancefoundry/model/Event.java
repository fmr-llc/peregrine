package com.alliancefoundry.model;


import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
    private String eventId;
    private String parentId;
    private String eventName;
    private String objectId;
    private String correlationId;
    private Integer sequenceNumber = null;
    private String messageType;
    private String dataType;
    private String source;
    private String destination;
    private String subdestination;
    private Boolean replayIndicator;
    @JsonSerialize(using = MyDateTimeSerializer.class)
    @JsonDeserialize(using = CustomJsonDateDeserializer.class)
    private DateTime publishTimeStamp;
    @JsonSerialize(using = MyDateTimeSerializer.class)
    @JsonDeserialize(using = CustomJsonDateDeserializer.class)
    private DateTime receivedTimeStamp;
    @JsonSerialize(using = MyDateTimeSerializer.class)
    @JsonDeserialize(using = CustomJsonDateDeserializer.class)
    private DateTime expirationTimeStamp;

    // other
    private Map<String, String> customHeaders;
    private Map<String, DataItem> customPayload;
    private String preEventState;
    private String postEventState;
    private Boolean isPublishable;
    @JsonSerialize(using = MyDateTimeSerializer.class)
    @JsonDeserialize(using = CustomJsonDateDeserializer.class)
    private DateTime insertTimeStamp;

    public Event(){
    	UUID uuid = UUID.randomUUID();
		eventId = uuid.toString();
    	
    	receivedTimeStamp = DateTime.now();
    	customHeaders = new HashMap<String, String>();
    	customPayload = new HashMap<String, DataItem>();
    	insertTimeStamp = DateTime.now();
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
	 * @param publishTimeStamp
	 * @param receivedTimeStamp
	 * @param expirationTimeStamp
	 * @param preEventState
	 * @param postEventState
	 * @param isPublishable
	 * @param insertTimeStamp
	 */
	public Event(String parentId, String eventName, String objectId, String correlationId,
			Integer sequenceNumber, String messageType, String dataType, String source, String destination,
			String subdestination, boolean replayIndicator, DateTime publishTimeStamp, DateTime receivedTimeStamp,
			DateTime expirationTimeStamp, String preEventState, String postEventState, boolean isPublishable,
			DateTime insertTimeStamp) {
		
		//call empty constructor to initialize hash maps
		this();
		
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
		this.publishTimeStamp = publishTimeStamp;
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

	public Map<String, DataItem> getCustomPayload() {
		return customPayload;
	}

	public void setCustomPayload(Map<String, DataItem> customPayload) {
		this.customPayload = customPayload;
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

	public void setSequenceNumber(int sequenceNumber) {
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

	public void setPublishTimeStamp(DateTime publishTimeStamp) {
		this.publishTimeStamp = publishTimeStamp;
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

	public void setEventId(String id){
        eventId = id;
    }

    public String getEventId(){
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

	public int getSequenceNumber() {
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

	public DateTime getPublishTimeStamp() {
		return publishTimeStamp;
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
