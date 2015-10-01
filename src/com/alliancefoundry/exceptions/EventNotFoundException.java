package com.alliancefoundry.exceptions;

/**
 * Created by: Bobby Writtenberry
 *
 * 
 * Exception to be thrown when an event can not be found
 * 
 */
public class EventNotFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public EventNotFoundException() {
	}

	/**
	 * @param arg0	String message to be attached to the exception
	 */
	public EventNotFoundException(String arg0) {
		super(arg0);
	}
	
	/**
	 * @param arg0	Exception to be added to the custom exception
	 */
	public EventNotFoundException(Throwable arg0) {
		super(arg0);
	}

	/**
	 * @param arg0	String message to be attached to the exception
	 * @param arg1	Exception to be added to the custom exception
	 */
	public EventNotFoundException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}
	
	/**
	 * @param arg0	String message to be attached to the exception
	 * @param arg1	Exception to be added to the custom exception
	 * @param arg2	boolean to indicate suppression
	 * @param arg3	boolean to indicate stack trace
	 */
	public EventNotFoundException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}
}
