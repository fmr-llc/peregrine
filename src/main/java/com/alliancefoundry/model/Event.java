package com.alliancefoundry.model;

import java.util.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.joda.time.DateTime;

import com.alliancefoundry.serializer.CustomJsonDateTimeDeserializer;
import com.alliancefoundry.serializer.CustomJsonDateTimeSerializer;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class Event {

	private static final Logger log = LoggerFactory.getLogger(Event.class);

    private String eventId;
    private String parentEventId;
	private String objectId;
	private String correlationId;
	private String correlationType;
	private String messageType;
	private String dataType;
	private String source;
	private String destination;
	private String subDestination;
    private String eventName;
	private Map<String, String> customHeaders;
	private List<Triplet> customPayload;
	private String preEventState;
	private String postEventState;
	private Boolean isPublishable;
	private Integer sequenceNumber;
	private Boolean replayIndicator;
	private Integer generationLevel;


    @JsonSerialize(using = CustomJsonDateTimeSerializer.class)
    @JsonDeserialize(using = CustomJsonDateTimeDeserializer.class)
    private DateTime publishTimeStamp;

    @JsonSerialize(using = CustomJsonDateTimeSerializer.class)
    @JsonDeserialize(using = CustomJsonDateTimeDeserializer.class)
    private DateTime receivedTimeStamp;

    @JsonSerialize(using = CustomJsonDateTimeSerializer.class)
    @JsonDeserialize(using = CustomJsonDateTimeDeserializer.class)
    private DateTime expirationTimeStamp;

    @JsonSerialize(using = CustomJsonDateTimeSerializer.class)
    @JsonDeserialize(using = CustomJsonDateTimeDeserializer.class)
    private DateTime insertTimeStamp;


    public Event(){
    	UUID uuid = UUID.randomUUID();
		eventId = uuid.toString();
    	
    	receivedTimeStamp = DateTime.now();
    	customHeaders = new HashMap<String, String>();
    	customPayload = new ArrayList<Triplet>();
    	insertTimeStamp = DateTime.now();
    }

	public Event(String eventId){
		this.eventId = eventId;
	}
	
    public Event(Map<String, Object> map){
		this();
    	
    	this.eventId = (String) map.get("eventId");
    	this.parentEventId = (String) map.get("parentId");
		this.eventName = (String) map.get("eventName");
		this.objectId = (String) map.get("objectId");
		this.correlationId = (String) map.get("correlationId");
		try{
			this.sequenceNumber = (Integer) map.get("sequenceNumber");		
		}catch(Exception ex){
			System.out.println("Error converting SequenceNumber to an Integer.");
		}
		this.messageType = (String) map.get("messageType");
		this.dataType = (String) map.get("dataType");
		this.source = (String) map.get("source");
		this.destination = (String) map.get("destination");
		this.subDestination = (String) map.get("subdestination");
		try{
			this.replayIndicator = (Boolean) map.get("replayIndicator");
		}catch(Exception ex){
			System.out.println("Error converting ReplayIndicator to a Boolean.");
		}
		try{
			this.publishTimeStamp = DateTime.parse((String) map.get("publishTimeStamp"));
		}catch(Exception ex){
			System.out.println("Error converting PublishTimeStamp to a DateTime object.");
		}
		try{
			this.receivedTimeStamp = DateTime.parse((String) map.get("receivedTimeStamp"));
		}catch(Exception ex){
			System.out.println("Error converting ReceivedTimeStamp to a DateTime object.");
		}
		try{
			this.expirationTimeStamp = DateTime.parse((String) map.get("expirationTimeStamp"));
		}catch(Exception ex){
			System.out.println("Error converting ExpirationTimeStamp to a DateTime object.");
		}
		this.preEventState = (String) map.get("preEventState");
		this.postEventState = (String) map.get("postEventState");
		try{
			this.isPublishable = (Boolean) map.get("publishable");
		}catch(Exception ex){
			System.out.println("Error converting IsPublishable to a Boolean.");
		}
		try{
			this.insertTimeStamp = DateTime.parse((String) map.get("insertTimeStamp"));
		}catch(Exception ex){
			System.out.println("Error converting InsertTimeStamp to a DateTime object.");
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
		
		this.parentEventId = parentId;
		this.eventName = eventName;
		this.objectId = objectId;
		this.correlationId = correlationId;
		this.sequenceNumber = sequenceNumber;
		this.messageType = messageType;
		this.dataType = dataType;
		this.source = source;
		this.destination = destination;
		this.subDestination = subdestination;
		this.replayIndicator = replayIndicator;
		this.publishTimeStamp = publishTimeStamp;
		this.receivedTimeStamp = receivedTimeStamp;
		this.expirationTimeStamp = expirationTimeStamp;
		this.preEventState = preEventState;
		this.postEventState = postEventState;
		this.isPublishable = isPublishable;
		this.insertTimeStamp = insertTimeStamp;
	}
	
	public Map<String, String> getCustomHeaders() { return customHeaders; }

	public void setCustomHeaders(Map<String, String> customHeaders) { this.customHeaders = customHeaders; }

	public List<Triplet> getCustomPayload() { return customPayload; }

	public void setCustomPayload(List<Triplet> customPayload) { this.customPayload = customPayload; }

	public void setParentEventId(String parentId) { this.parentEventId = parentId; }

	public void setEventName(String eventName) { this.eventName = eventName; }

	public void setObjectId(String objectId) { this.objectId = objectId; }

	public void setCorrelationId(String correlationId) { this.correlationId = correlationId; }

	public void setCorrelationType(String correlationType){ this.correlationType = correlationType; }

	public void setSequenceNumber(Integer sequenceNumber) { this.sequenceNumber = sequenceNumber; }

	public void setMessageType(String messageType) { this.messageType = messageType; }

	public void setDataType(String dataType) { this.dataType = dataType; }

	public void setSource(String source) { this.source = source; }

	public void setDestination(String destination) { this.destination = destination; }

	public void setSubdestination(String subdestination) { this.subDestination = subdestination; }

	public void setReplayIndicator(Boolean replayIndicator) { this.replayIndicator = replayIndicator; }

	public void setPublishTimeStamp(DateTime publishTimeStamp) { this.publishTimeStamp = publishTimeStamp; }

	public void setReceivedTimeStamp(DateTime receivedTimeStamp) { this.receivedTimeStamp = receivedTimeStamp; }

	public void setExpirationTimeStamp(DateTime expirationTimeStamp) { this.expirationTimeStamp = expirationTimeStamp; }

	public void setPreEventState(String preEventState) { this.preEventState = preEventState; }

	public void setPostEventState(String postEventState) { this.postEventState = postEventState; }

	public void setPublishable(Boolean isPublishable) { this.isPublishable = isPublishable; }

	public void setInsertTimeStamp(DateTime insertTimeStamp) { this.insertTimeStamp = insertTimeStamp; }

	public void setEventId(String id){ eventId = id; }

	public void setGenerationLevel(Integer level){ generationLevel = level; }

    public String getEventId(){ return eventId; }

	public String getParentEventId() { return parentEventId; }

	public String getEventName() { return eventName; }

	public String getObjectId() { return objectId; }

	public String getCorrelationId() { return correlationId; }

	public String getCorrelationType(){ return correlationType; }

	public Integer getSequenceNumber() { return sequenceNumber; }

	public String getMessageType() { return messageType; }

	public String getDataType() { return dataType; }

	public String getSource() { return source; }

	public String getDestination() { return destination; }

	public String getSubdestination() { return subDestination; }

	public Boolean isReplayIndicator() { return replayIndicator; }

	public DateTime getPublishTimeStamp() { return publishTimeStamp; }

	public DateTime getReceivedTimeStamp() { return receivedTimeStamp; }

	public DateTime getExpirationTimeStamp() { return expirationTimeStamp; }

	public String getPreEventState() { return preEventState; }

	public String getPostEventState() { return postEventState; }

	public Boolean isPublishable() { return isPublishable; }

	public DateTime getInsertTimeStamp() { return insertTimeStamp; }

	public Integer getGenerationLevel(){ return generationLevel; }

	
	@Override
	public boolean equals(Object obj) {

		if(!(obj instanceof Event)){ return false; }

		Event e2 = (Event)obj;

		if (!this.toString().equals(e2.toString())) return false;
		
		return true;
	}

	@Override
	public String toString() {

		ObjectMapper mapper = new ObjectMapper();
		try{
			String objectAsString = mapper.writeValueAsString(this);
			log.debug("object converted toString()=" + objectAsString);
			return objectAsString;

		} catch (JsonProcessingException e){
			log.error("can't convert Event object to String");
			return "{}";
		}

	}
    
}