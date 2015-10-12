/**
 * 
 */
package com.alliancefoundry.model;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.alliancefoundry.serializer.JsonDateTimeDeserializer;
import com.alliancefoundry.serializer.JsonDateTimeSerializer;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Created by: Bobby Writtenberry
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class EventPublicationAudit{

	private String eventId;
	@JsonSerialize(using = JsonDateTimeSerializer.class)
    @JsonDeserialize(using = JsonDateTimeDeserializer.class)
	private DateTime captureTimestamp;
	@JsonSerialize(using = JsonDateTimeSerializer.class)
    @JsonDeserialize(using = JsonDateTimeDeserializer.class)
	private DateTime persistTimestamp;
	@JsonSerialize(contentUsing = JsonDateTimeSerializer.class)
    @JsonDeserialize(contentUsing = JsonDateTimeDeserializer.class)
	private List<DateTime> publishTimestamps = new ArrayList<DateTime>();
	private int publishCount = 0;
	
	/**
	 * 
	 */
	public EventPublicationAudit() {}
	
	/**
	 * @param eventId
	 * @param persistTimestamp
	 */
	public EventPublicationAudit(String eventId, DateTime persistTimestamp) {
		super();
		this.eventId = eventId;
		this.persistTimestamp = persistTimestamp;
	}

	/**
	 * @param eventId
	 * @param captureTimestamp
	 * @param persistTimestamp
	 * @param publishTimestamp
	 */
	public EventPublicationAudit(String eventId, DateTime captureTimestamp, DateTime persistTimestamp,
			DateTime publishTimestamp) {
		super();
		this.eventId = eventId;
		this.captureTimestamp = captureTimestamp.toDateTime(DateTimeZone.UTC);
		this.persistTimestamp = persistTimestamp.toDateTime(DateTimeZone.UTC);
		publishTimestamps.add(publishTimestamp.toDateTime(DateTimeZone.UTC));
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
	 * @return the captureTimestamp
	 */
	public DateTime getCaptureTimestamp() {
		return captureTimestamp;
	}

	/**
	 * @param captureTimestamp the captureTimestamp to set
	 */
	public void setCaptureTimestamp(DateTime captureTimestamp) {
		this.captureTimestamp = captureTimestamp.toDateTime(DateTimeZone.UTC);
	}

	/**
	 * @return the persistTimestamp
	 */
	public DateTime getPersistTimestamp() {
		return persistTimestamp;
	}

	/**
	 * @param persistTimestamp the persistTimestamp to set
	 */
	public void setPersistTimestamp(DateTime persistTimestamp) {
		this.persistTimestamp = persistTimestamp.toDateTime(DateTimeZone.UTC);
	}

	/**
	 * @return the publishTimestamps
	 */
	public List<DateTime> getPublishTimestamps() {
		return publishTimestamps;
	}

	/**
	 * @return the number of times an event has been published
	 */
	public int getPublishCount() {
		return publishTimestamps.size();
	}
	
	/**
	 * @param publishTimestamps the publishTimestamp to set
	 */
	public void addPublishTimestamp(DateTime publishTimestamp) {
		this.publishTimestamps.add(publishTimestamp.toDateTime(DateTimeZone.UTC));
		publishCount++;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		EventPublicationAudit other = (EventPublicationAudit) obj;
		if (eventId == null) {
			if (other.eventId != null)
				return false;
		} else if (!eventId.equals(other.eventId))
			return false;
		if (captureTimestamp == null) {
			if (other.captureTimestamp != null)
				return false;
		} else if (!captureTimestamp.equals(other.captureTimestamp))
			return false;
		if (persistTimestamp == null) {
			if (other.persistTimestamp != null)
				return false;
		} else if (!persistTimestamp.equals(other.persistTimestamp))
			return false;
		if (publishTimestamps == null) {
			if (other.publishTimestamps != null)
				return false;
		} else if(publishTimestamps.size() != other.publishTimestamps.size()) 
			return false;
		for(int count = 0; count < publishTimestamps.size(); count++){
				if(!publishTimestamps.get(count).equals(other.publishTimestamps.get(count)))
						return false;
		} 
		if (!(publishCount == other.publishCount))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "Event [getEventId()=" + getEventId() + ", getCaptureTimestamp()=" + getCaptureTimestamp() + ", getPersistTimestamp()="
				+ getPersistTimestamp() + ", getPublishTimestamps()=" + getPublishTimestamps() + ", getPublishCount()=" + getPublishCount() + "]";
	}
}
