/**
 * 
 */
package com.alliancefoundry.model;

/**
 * Created by: Bobby Writtenberry
 *
 */
public class EventPublicationAuditResponse {

	private EventPublicationAudit audit;
	private String msg;
	
	/**
	 * 
	 */
	public EventPublicationAuditResponse() {}

	/**
	 * @return the audit
	 */
	public EventPublicationAudit getAudit() {
		return audit;
	}

	/**
	 * @param audit the audit to set
	 */
	public void setAudit(EventPublicationAudit audit) {
		this.audit = audit;
	}

	/**
	 * @return the msg
	 */
	public String getMsg() {
		return msg;
	}

	/**
	 * @param msg the msg to set
	 */
	public void setMsg(String msg) {
		this.msg = msg;
	}

}
