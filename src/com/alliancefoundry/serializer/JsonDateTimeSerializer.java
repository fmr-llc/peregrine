package com.alliancefoundry.serializer;

import java.io.IOException;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class JsonDateTimeSerializer extends JsonSerializer<DateTime> {
	
	public static final String ISODateTime = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    private static DateTimeFormatter formatter = DateTimeFormat.forPattern(ISODateTime);

	public void serialize(DateTime value, JsonGenerator gen, SerializerProvider arg2) throws IOException, JsonProcessingException {
	    gen.writeString(formatter.print(value));
	}

}
