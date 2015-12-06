package com.alliancefoundry.serializer;

import java.io.IOException;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * Serializer used to handle a particular Joda {@link DateTime} format. This class is intended to be
 * used by the event service internal code. 
 * 
 * @author Paul Bernard
 * @author Peter Mularien
 */
public class CustomJsonDateTimeSerializer extends JsonSerializer<DateTime> {

	private final static DateTimeFormatter DATE_FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd");
	private final static DateTimeFormatter TIME_FORMAT = DateTimeFormat.forPattern("HH:mm:ss");


	private static DateTimeFormatter formatter =
            DateTimeFormat.forPattern(SerializerConstants.EVENT_SERVICE_DATE_TIME_FORMAT);
	
	public void serialize(DateTime value, JsonGenerator gen, SerializerProvider arg2) throws IOException, JsonProcessingException {
	    gen.writeString(formatter.print(value));
	}

}
