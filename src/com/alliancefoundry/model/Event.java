package com.alliancefoundry.model;

import java.util.HashMap;
import java.util.Map;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.alliancefoundry.serializer.JsonDateTimeSerializer;
import com.alliancefoundry.exceptions.PeregrineErrorCodes;
import com.alliancefoundry.exceptions.PeregrineException;
import com.alliancefoundry.serializer.JsonDateTimeDeserializer;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;



/**
 * Created by: Paul Bernard, Bobby Writtenberry, Paul Fahey
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
    private Integer sequenceNumber = null;
    private String messageType;
    private String dataType;
    private String source;
    private String destination;
    private String subdestination;
    private boolean replayIndicator;

    // other
    private Map<String, String> customHeaders;
    private Map<String, DataItem> customPayload;
    private String preEventState;
    private String postEventState;
    private boolean isPublishable;
    @JsonSerialize(using = JsonDateTimeSerializer.class)
    @JsonDeserialize(using = JsonDateTimeDeserializer.class)
    private DateTime timestamp;


    public Event(){
    	timestamp = new DateTime(DateTime.now().toDateTime(DateTimeZone.UTC));
    	customHeaders = new HashMap<String, String>();
    	customPayload = new HashMap<String, DataItem>();
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
	 * @param preEventState
	 * @param postEventState
	 * @param isPublishable
	 * @param timestamp
	 */
    
	public Event(String parentId, String eventName, String objectId, String correlationId,
			Integer sequenceNumber, String messageType, String dataType, String source, String destination,
			String subdestination, boolean replayIndicator, DateTime publishTimeStamp, DateTime receivedTimeStamp,
			DateTime expirationTimeStamp, String preEventState, String postEventState, boolean isPublishable,
			DateTime timestamp) {
		
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
		this.preEventState = preEventState;
		this.postEventState = postEventState;
		this.isPublishable = isPublishable;
		this.timestamp = timestamp;
	}

	/**
	 * @return the eventId
	 */
	public String getEventId() {
		return eventId;
	}

	/**
	 * @param eventId the eventId to set
	 */
	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	/**
	 * @return the parentId
	 */
	public String getParentId() {
		return parentId;
	}

	/**
	 * @param parentId the parentId to set
	 */
	public void setParentId(String parentId) {
		this.parentId = parentId;
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
	 * @return the sequenceNumber
	 */
	public Integer getSequenceNumber() {
		return sequenceNumber;
	}

	/**
	 * @param sequenceNumber the sequenceNumber to set
	 */
	public void setSequenceNumber(Integer sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	/**
	 * @return the messageType
	 */
	public String getMessageType() {
		return messageType;
	}

	/**
	 * @param messageType the messageType to set
	 */
	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

	/**
	 * @return the dataType
	 */
	public String getDataType() {
		return dataType;
	}

	/**
	 * @param dataType the dataType to set
	 */
	public void setDataType(String dataType) {
		this.dataType = dataType;
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
	 * @return the destination
	 */
	public String getDestination() {
		return destination;
	}

	/**
	 * @param destination the destination to set
	 */
	public void setDestination(String destination) {
		this.destination = destination;
	}

	/**
	 * @return the subdestination
	 */
	public String getSubdestination() {
		return subdestination;
	}

	/**
	 * @param subdestination the subdestination to set
	 */
	public void setSubdestination(String subdestination) {
		this.subdestination = subdestination;
	}

	/**
	 * @return the replayIndicator
	 */
	public boolean isReplayIndicator() {
		return replayIndicator;
	}

	/**
	 * @param replayIndicator the replayIndicator to set
	 */
	public void setReplayIndicator(boolean replayIndicator) {
		this.replayIndicator = replayIndicator;
	}

	/**
	 * @return the customHeaders
	 */
	public Map<String, String> getCustomHeaders() {
		return customHeaders;
	}

	/**
	 * @param customHeaders the customHeaders to set
	 */
	public void setCustomHeaders(Map<String, String> customHeaders) {
		this.customHeaders = customHeaders;
	}

	/**
	 * @return the customPayload
	 */
	public Map<String, DataItem> getCustomPayload() {
		return customPayload;
	}

	/**
	 * @param customPayload the customPayload to set
	 */
	public void setCustomPayload(Map<String, DataItem> customPayload) {
		this.customPayload = customPayload;
	}

	/**
	 * @return the preEventState
	 */
	public String getPreEventState() {
		return preEventState;
	}

	/**
	 * @param preEventState the preEventState to set
	 */
	public void setPreEventState(String preEventState) {
		this.preEventState = preEventState;
	}

	/**
	 * @return the postEventState
	 */
	public String getPostEventState() {
		return postEventState;
	}

	/**
	 * @param postEventState the postEventState to set
	 */
	public void setPostEventState(String postEventState) {
		this.postEventState = postEventState;
	}
	
	/**
	 * @return the isPublishable
	 */
	public boolean getIsPublishable() {
		return isPublishable;
	}
	
	/**
	 * @param isPublishable the isPublishable to set
	 */
	public void setIsPublishable(boolean isPublishable) {
		this.isPublishable = isPublishable;
	}

	/**
	 * @return the timestamp
	 */
	public DateTime getTimestamp() {
		return timestamp;
	}

	/**
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(DateTime timestamp) {
		if(timestamp == null) this.timestamp = timestamp;
		else this.timestamp = timestamp.toDateTime(DateTimeZone.UTC);
	}

	public static void verifyNonNullables(Event event) throws PeregrineException{
		if(event.getObjectId() == null){
			throw new PeregrineException(PeregrineErrorCodes.EVENT_INSERTION_ERROR,"An Object Id must be specified for event - " + event);
		}
		if(event.getMessageType() == null){
			throw new PeregrineException(PeregrineErrorCodes.EVENT_INSERTION_ERROR,"A Message Type must be specified for event - " + event);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((correlationId == null) ? 0 : correlationId.hashCode());
		result = prime * result + ((customHeaders == null) ? 0 : customHeaders.hashCode());
		result = prime * result + ((customPayload == null) ? 0 : customPayload.hashCode());
		result = prime * result + ((dataType == null) ? 0 : dataType.hashCode());
		result = prime * result + ((destination == null) ? 0 : destination.hashCode());
		result = prime * result + ((eventId == null) ? 0 : eventId.hashCode());
		result = prime * result + ((eventName == null) ? 0 : eventName.hashCode());
		result = prime * result + (isPublishable ? 1231 : 1237);
		result = prime * result + ((messageType == null) ? 0 : messageType.hashCode());
		result = prime * result + ((objectId == null) ? 0 : objectId.hashCode());
		result = prime * result + ((parentId == null) ? 0 : parentId.hashCode());
		result = prime * result + ((postEventState == null) ? 0 : postEventState.hashCode());
		result = prime * result + ((preEventState == null) ? 0 : preEventState.hashCode());
		result = prime * result + ((timestamp == null) ? 0 : timestamp.hashCode());
		result = prime * result + (replayIndicator ? 1231 : 1237);
		result = prime * result + ((sequenceNumber == null) ? 0 : sequenceNumber.hashCode());
		result = prime * result + ((source == null) ? 0 : source.hashCode());
		result = prime * result + ((subdestination == null) ? 0 : subdestination.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		Event other = (Event) obj;
		if (correlationId == null) {
			if (other.correlationId != null)
				return false;
		} else if (!correlationId.equals(other.correlationId))
			return false;
		if (customHeaders == null) {
			if (other.customHeaders != null)
				return false;
		} else if (!customHeaders.equals(other.customHeaders))
			return false;
		if (customPayload == null) {
			if (other.customPayload != null)
				return false;
		} else if (!customPayload.equals(other.customPayload))
			return false;
		if (dataType == null) {
			if (other.dataType != null)
				return false;
		} else if (!dataType.equals(other.dataType))
			return false;
		if (destination == null) {
			if (other.destination != null)
				return false;
		} else if (!destination.equals(other.destination))
			return false;
		if (eventId == null) {
			if (other.eventId != null)
				return false;
		} else if (!eventId.equals(other.eventId))
			return false;
		if (eventName == null) {
			if (other.eventName != null)
				return false;
		} else if (!eventName.equals(other.eventName))
			return false;
		if (isPublishable != other.isPublishable)
			return false;
		if (messageType == null) {
			if (other.messageType != null)
				return false;
		} else if (!messageType.equals(other.messageType))
			return false;
		if (objectId == null) {
			if (other.objectId != null)
				return false;
		} else if (!objectId.equals(other.objectId))
			return false;
		if (parentId == null) {
			if (other.parentId != null)
				return false;
		} else if (!parentId.equals(other.parentId))
			return false;
		if (postEventState == null) {
			if (other.postEventState != null)
				return false;
		} else if (!postEventState.equals(other.postEventState))
			return false;
		if (preEventState == null) {
			if (other.preEventState != null)
				return false;
		} else if (!preEventState.equals(other.preEventState))
			return false;
		if (timestamp == null) {
			if (other.timestamp != null)
				return false;
		} else if (!timestamp.equals(other.timestamp))
			return false;
		if (replayIndicator != other.replayIndicator)
			return false;
		if (sequenceNumber == null) {
			if (other.sequenceNumber != null)
				return false;
		} else if (!sequenceNumber.equals(other.sequenceNumber))
			return false;
		if (source == null) {
			if (other.source != null)
				return false;
		} else if (!source.equals(other.source))
			return false;
		if (subdestination == null) {
			if (other.subdestination != null)
				return false;
		} else if (!subdestination.equals(other.subdestination))
			return false;
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
				+ isReplayIndicator() + ", getPreEventState()=" + getPreEventState() + ", getPostEventState()=" 
				+ getPostEventState() + ", isPublishable()=" + getIsPublishable() + ", getTimestamp()=" + getTimestamp() + "]";
	}
}