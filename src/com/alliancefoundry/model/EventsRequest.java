package com.alliancefoundry.model;

import org.joda.time.DateTime;

import com.alliancefoundry.exceptions.PeregrineErrorCodes;
import com.alliancefoundry.exceptions.PeregrineException;

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
	
	/**
	 * 
	 * @param req					request parameters
	 * @param latest				determines whether using only params related to getLatestEvent
	 * @return						generations count if not looking for latest, unimportant constant otherwise
	 * @throws PeregrineException 	if some problem related to an event occurs
	 */
	public static Integer verifyRequestParameters(EventsRequest req, boolean latest) throws PeregrineException {
		if(!latest){
			if( req.getCreatedAfter() == null){
    			throw new PeregrineException(PeregrineErrorCodes.EVENT_REQUEST_ARGUMENT_ERROR,"A createdAfter date must be specified");
    		}
			if(req.getSource() == null && req.getObjectId() == null && req.getCorrelationId() == null){
				throw new PeregrineException(PeregrineErrorCodes.EVENT_REQUEST_ARGUMENT_ERROR,"A source, object id, or correlation id must be specified");
			}
			Integer genNum = req.getGenerations();
			if(genNum != null && genNum < 1) {
				throw new PeregrineException(PeregrineErrorCodes.EVENT_REQUEST_ARGUMENT_ERROR,"Invalid value for generations.  Must be greater than 0");
			}
			return genNum;
		} else {
			if (req.getSource() == null) {
				throw new PeregrineException(PeregrineErrorCodes.EVENT_REQUEST_ARGUMENT_ERROR,"A source must be specified");
			}
			return -1;
		}
	}

}
