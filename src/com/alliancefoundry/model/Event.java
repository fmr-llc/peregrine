package com.alliancefoundry.model;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.joda.time.DateTime;

import com.alliancefoundry.serializer.CustomJsonDateDeserializer;
import com.alliancefoundry.serializer.MyDateTimeSerializer;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;



/**
 * Created by Paul Bernard
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class Event {

    // Headers
    private String eventId;
    private String parentId;
    private String eventName;
    private String objectId;
    private String correlationId;
    private Integer sequenceNumber;
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
	
    public Event(Map<String, Object> map){
		this();
    	
    	this.eventId = (String) map.get("eventId");
    	this.parentId = (String) map.get("parentId");
		this.eventName = (String) map.get("eventName");
		this.objectId = (String) map.get("objectId");
		this.correlationId = (String) map.get("correlationId");
		try{
			System.out.println(map.get("sequenceNumber")+"");
			System.out.println(map.get("sequenceNumber")+"");
			this.sequenceNumber = (Integer) map.get("sequenceNumber");		
		}catch(Exception ex){
			ex.printStackTrace();
		}
		this.messageType = (String) map.get("messageType");
		this.dataType = (String) map.get("dataType");
		this.source = (String) map.get("source");
		this.destination = (String) map.get("destination");
		this.subdestination = (String) map.get("subdestination");
		try{
			this.replayIndicator = (Boolean) map.get("replayIndicator");
		}catch(Exception ex){
			ex.printStackTrace();
		}
		try{
			this.publishTimeStamp = DateTime.parse((String) map.get("publishTimeStamp"));
		}catch(Exception ex){
			ex.printStackTrace();
		}
		try{
			this.receivedTimeStamp = DateTime.parse((String) map.get("receivedTimeStamp"));
		}catch(Exception ex){
			ex.printStackTrace();
		}
		try{
			this.expirationTimeStamp = DateTime.parse((String) map.get("expirationTimeStamp"));
		}catch(Exception ex){
			ex.printStackTrace();
		}
		this.preEventState = (String) map.get("preEventState");
		this.postEventState = (String) map.get("postEventState");
		try{
			this.isPublishable = (Boolean) map.get("publishable");
		}catch(Exception ex){
			ex.printStackTrace();
		}
		try{
			this.insertTimeStamp = DateTime.parse((String) map.get("insertTimeStamp"));
		}catch(Exception ex){
			ex.printStackTrace();
		}
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

	
	@Override
	public boolean equals(Object obj) {
		// if object isnt an event object use normal equals comparison
		if(!(obj instanceof Event)){
			return super.equals(obj);
		}
		Event e2 = (Event)obj;

		// test eventid
		if(!eventId.equals(e2.eventId)){
			return false;
		}

		// test parentid
		if(parentId != null && e2.parentId != null){
			// perform test
			if(!parentId.equals(e2.parentId)){
				return false;
			}
		}else if(parentId == null && e2.parentId == null){
		}else{
			// not the same, either one or the other is null but not both
			return false;
		}

		// test eventName
		if(eventName != null && e2.eventName != null){
			// perform test
			if(!eventName.equals(e2.eventName)){
				return false;
			}
		}else if(eventName == null && e2.eventName == null){
		}else{
			// not the same, either one or the other is null but not both
			return false;
		}
		
		// test objectId
		if(objectId != null && e2.objectId != null){
			// perform test
			if(!objectId.equals(e2.objectId)){
				return false;
			}
		}else if(objectId == null && e2.objectId == null){
		}else{
			// not the same, either one or the other is null but not both
			return false;
		}
		
		// test correlationId
		if(correlationId != null && e2.correlationId != null){
			// perform test
			if(!correlationId.equals(e2.correlationId)){
				return false;
			}
		}else if(correlationId == null && e2.correlationId == null){
		}else{
			// not the same, either one or the other is null but not both
			return false;
		}
		
		// test sequenceNumber
		if(sequenceNumber != null && e2.sequenceNumber != null){
			// perform test
			if(!sequenceNumber.equals(e2.sequenceNumber)){
				return false;
			}
		}else if(sequenceNumber == null && e2.sequenceNumber == null){
		}else{
			// not the same, either one or the other is null but not both
			return false;
		}
		
		// test messageType
		if(messageType != null && e2.messageType != null){
			// perform test
			if(!messageType.equals(e2.messageType)){
				return false;
			}
		}else if(messageType == null && e2.messageType == null){
		}else{
			// not the same, either one or the other is null but not both
			return false;
		}
		
		// test source
		if(source != null && e2.source != null){
			// perform test
			if(!source.equals(e2.source)){
				return false;
			}
		}else if(source == null && e2.source == null){
		}else{
			// not the same, either one or the other is null but not both
			return false;
		}

		// test destination
		if(destination != null && e2.destination != null){
			// perform test
			if(!destination.equals(e2.destination)){
				return false;
			}
		}else if(destination == null && e2.destination == null){
		}else{
			// not the same, either one or the other is null but not both
			return false;
		}
    	System.out.println("kuyj");

		// test subdestination
		if(subdestination != null && e2.subdestination != null){
			// perform test
			if(!subdestination.equals(e2.subdestination)){
				return false;
			}
		}else if(subdestination == null && e2.subdestination == null){
		}else{
			// not the same, either one or the other is null but not both
			return false;
		}

		// test replayIndicator
		if (replayIndicator != e2.replayIndicator){
			return false;
		}
		
		// test publishedTimeStamp
		if(publishTimeStamp != null && e2.publishTimeStamp != null){
			// perform test
			if(publishTimeStamp.getMillis() != e2.publishTimeStamp.getMillis()){
				return false;
			}
		}else if(publishTimeStamp == null && e2.publishTimeStamp == null){
		}else{
			// not the same, either one or the other is null but not both
			return false;
		}
		
		// test receivedTimeStamp
		if(receivedTimeStamp != null && e2.receivedTimeStamp != null){
			// perform test
			if(receivedTimeStamp.getMillis() != e2.receivedTimeStamp.getMillis()){
				return false;
			}
		}else if(receivedTimeStamp == null && e2.receivedTimeStamp == null){
		}else{
			// not the same, either one or the other is null but not both
			return false;
		}

		// test expirationTimeStamp
		if(expirationTimeStamp != null && e2.expirationTimeStamp != null){
			// perform test
			if(expirationTimeStamp.getMillis() != e2.expirationTimeStamp.getMillis()){
				return false;
			}
		}else if(expirationTimeStamp == null && e2.expirationTimeStamp == null){
		}else{
			// not the same, either one or the other is null but not both
			return false;
		}

		// test customHeaders
		if(customHeaders != null && e2.customHeaders != null){
			// perform test
			if(customHeaders.size() != e2.customHeaders.size()){
				return false;
			}
		}else if(customHeaders == null && e2.customHeaders == null){
		}else{
			// not the same, either one or the other is null but not both
			return false;
		}

		// test payload
		if(customPayload != null && e2.customPayload != null){
			// perform test
			if(customPayload.size() != e2.customPayload.size()){
				return false;
			}
		}else if(customPayload == null && e2.customPayload == null){
		}else{
			// not the same, either one or the other is null but not both
			return false;
		}

		// test preEventState
		if(preEventState != null && e2.preEventState != null){
			// perform test
			if(!preEventState.equals(e2.preEventState)){
				return false;
			}
		}else if(preEventState == null && e2.preEventState == null){
		}else{
			// not the same, either one or the other is null but not both
			return false;
		}

		// test postEventState
		if(postEventState != null && e2.postEventState != null){
			// perform test
			if(!postEventState.equals(e2.postEventState)){
				return false;
			}
		}else if(postEventState == null && e2.postEventState == null){
		}else{
			// not the same, either one or the other is null but not both
			return false;
		}
		
		// test isPublishable
		if (isPublishable != e2.isPublishable){
			return false;
		}

		
		// test insertTimeStamp
		if(insertTimeStamp != null && e2.insertTimeStamp != null){
			// perform test
			if(insertTimeStamp.getMillis() != e2.insertTimeStamp.getMillis()){
				return false;
			}
		}else if(insertTimeStamp == null && e2.insertTimeStamp == null){
		}else{
			// not the same, either one or the other is null but not both
			return false;
		}	    // other
		
		return true;
	}

	@Override
	public String toString() {
		return "Event [getCustomHeaders()=" + getCustomHeaders() + ", getCustomPayload()=" + getCustomPayload()
				+ ", getEventId()=" + getEventId() + ", getParentId()=" + getParentId() + ", getEventName()="
				+ getEventName() + ", getObjectId()=" + getObjectId() + ", getCorrelationId()=" + getCorrelationId()
				+ ", getSequenceNumber()=" + getSequenceNumber() + ", getMessageType()=" + getMessageType()
				+ ", getDataType()=" + getDataType() + ", getSource()=" + getSource() + ", getDestination()="
				+ getDestination() + ", getSubdestination()=" + getSubdestination() + ", isReplayIndicator()="
				+ isReplayIndicator() + ", getPublishTimeStamp()=" + getPublishTimeStamp() + ", getReceivedTimeStamp()="
				+ getReceivedTimeStamp() + ", getExpirationTimeStamp()=" + getExpirationTimeStamp()
				+ ", getPreEventState()=" + getPreEventState() + ", getPostEventState()=" + getPostEventState()
				+ ", isPublishable()=" + isPublishable() + ", getInsertTimeStamp()=" + getInsertTimeStamp() + "]";
	}
    
}