package com.alliancefoundry.exceptions;

/**
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
	 * @param arg0	message to be attached to the exception
	 */
	public EventNotFoundException(String arg0) {
		super(arg0);
	}

}
