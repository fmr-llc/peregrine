package com.alliancefoundry.dao;

/**
 * Created by: Bobby Writtenberry
 *
 */
public class SqlQuery {

	private String singleEventById;
	private String multipleEventsById;
	private String header;
	private String payload;
	private String insertSingleEvent;
	private String insertHeader;
	private String insertPayload;
	private String latestEvent;
	private String reqCreatedAfter;
	private String reqCreatedBefore;
	private String reqSource;
	private String reqObjectId;
	private String reqCorrelationId;
	private String reqEventName;
	
	/**
	 * @return the singleEventById
	 */
	public String getSingleEventById() {
		return singleEventById;
	}
	/**
	 * @param singleEventById the singleEventById to set
	 */
	public void setSingleEventById(String singleEventById) {
		this.singleEventById = singleEventById;
	}
	/**
	 * @return the multipleEventsById
	 */
	public String getMultipleEventsById() {
		return multipleEventsById;
	}
	/**
	 * @param multipleEventsById the multipleEventsById to set
	 */
	public void setMultipleEventsById(String multipleEventsById) {
		this.multipleEventsById = multipleEventsById;
	}
	/**
	 * @return the header
	 */
	public String getHeader() {
		return header;
	}
	/**
	 * @param header the header to set
	 */
	public void setHeader(String header) {
		this.header = header;
	}
	/**
	 * @return the payload
	 */
	public String getPayload() {
		return payload;
	}
	/**
	 * @param payload the payload to set
	 */
	public void setPayload(String payload) {
		this.payload = payload;
	}
	/**
	 * @return the insertSingleEvent
	 */
	public String getInsertSingleEvent() {
		return insertSingleEvent;
	}
	/**
	 * @param insertSingleEvent the insertSingleEvent to set
	 */
	public void setInsertSingleEvent(String insertSingleEvent) {
		this.insertSingleEvent = insertSingleEvent;
	}
	/**
	 * @return the insertHeader
	 */
	public String getInsertHeader() {
		return insertHeader;
	}
	/**
	 * @param insertHeader the insertHeader to set
	 */
	public void setInsertHeader(String insertHeader) {
		this.insertHeader = insertHeader;
	}
	/**
	 * @return the insertPayload
	 */
	public String getInsertPayload() {
		return insertPayload;
	}
	/**
	 * @param insertPayload the insertPayload to set
	 */
	public void setInsertPayload(String insertPayload) {
		this.insertPayload = insertPayload;
	}
	/**
	 * @return the latestEvent
	 */
	public String getLatestEvent() {
		return latestEvent;
	}
	/**
	 * @param latestEvent the latestEvent to set
	 */
	public void setLatestEvent(String latestEvent) {
		this.latestEvent = latestEvent;
	}
	/**
	 * @return the reqCreatedAfter
	 */
	public String getReqCreatedAfter() {
		return reqCreatedAfter;
	}
	/**
	 * @param reqCreatedAfter the reqCreatedAfter to set
	 */
	public void setReqCreatedAfter(String reqCreatedAfter) {
		this.reqCreatedAfter = reqCreatedAfter;
	}
	/**
	 * @return the reqCreatedBefore
	 */
	public String getReqCreatedBefore() {
		return reqCreatedBefore;
	}
	/**
	 * @param reqCreatedBefore the reqCreatedBefore to set
	 */
	public void setReqCreatedBefore(String reqCreatedBefore) {
		this.reqCreatedBefore = reqCreatedBefore;
	}
	/**
	 * @return the reqSource
	 */
	public String getReqSource() {
		return reqSource;
	}
	/**
	 * @param reqSource the reqSource to set
	 */
	public void setReqSource(String reqSource) {
		this.reqSource = reqSource;
	}
	/**
	 * @return the reqObjectId
	 */
	public String getReqObjectId() {
		return reqObjectId;
	}
	/**
	 * @param reqObjectId the reqObjectId to set
	 */
	public void setReqObjectId(String reqObjectId) {
		this.reqObjectId = reqObjectId;
	}
	/**
	 * @return the reqCorrelationId
	 */
	public String getReqCorrelationId() {
		return reqCorrelationId;
	}
	/**
	 * @param reqCorrelationId the reqCorrelationId to set
	 */
	public void setReqCorrelationId(String reqCorrelationId) {
		this.reqCorrelationId = reqCorrelationId;
	}
	/**
	 * @return the reqEventName
	 */
	public String getReqEventName() {
		return reqEventName;
	}
	/**
	 * @param reqEventName the reqEventName to set
	 */
	public void setReqEventName(String reqEventName) {
		this.reqEventName = reqEventName;
	}
}
