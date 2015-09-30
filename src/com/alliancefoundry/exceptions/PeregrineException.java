package com.alliancefoundry.exceptions;

public class PeregrineException extends Exception {
	
	PeregrineErrorCodes errorCode = PeregrineErrorCodes.NOT_AN_ERROR;
	
	public PeregrineException(PeregrineErrorCodes errorCode) {
		this.errorCode = errorCode;
	}

	public PeregrineException(PeregrineErrorCodes errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
	}

	public PeregrineException(PeregrineErrorCodes errorCode, Throwable cause) {
		super(cause);
		this.errorCode = errorCode;
	}

	public PeregrineException(PeregrineErrorCodes errorCode, String message, Throwable cause) {
		super(message, cause);
		this.errorCode = errorCode;
	}

	public PeregrineException(PeregrineErrorCodes errorCode, String message, Throwable cause, boolean enabledSupression, boolean writableStackTrace) {
		super(message, cause, enabledSupression, writableStackTrace);
		this.errorCode = errorCode;
	}
	
	
	
}
