package com.alliancefoundry.exceptions;

/**
 * Created by: Curtis Robinson
 * 
 *
 */

public enum PeregrineErrorCodes {
	
	JSON_PARSE_ERROR,
	JSON_MAPPING_ERROR,
	INPUT_SOURCE_ERROR,
	JSM_SEND_ERROR,
	JMS_INTERNAL_ERROR,
	JMS_RECEIVE_ERROR,
	MSG_FORMAT_ERROR,
	
	INVALID_DESTINATION,
	DESTINATION_NOT_SUPPLIED,
	
	INVALID_DESTINATION_OR_TOPIC,
	
	NOT_AN_ERROR
	
}
