package com.alliancefoundry.serializer;

import static org.junit.Assert.assertEquals;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Unit test for custom JSON serializers.
 * 
 * @author Peter Mularien
 */
public class JsonSerializersUnitTest {

	public static class SimpleTestObject {
	    @JsonSerialize(using = CustomJsonDateTimeSerializer.class)
	    @JsonDeserialize(using = CustomJsonDateTimeDeserializer.class)
	    private DateTime dateTime;
	    
	    public SimpleTestObject() {};
	}
	
	@Test
	public void testSerializer() throws Exception {
		SimpleTestObject o = new SimpleTestObject();
		o.dateTime = new DateTime(2012, 1, 1, 23, 59, 59, 500, DateTimeZone.forID("GMT"));
		ObjectMapper mapper = new ObjectMapper();
		String objJsonString = mapper.writeValueAsString(o);
		assertEquals("{\"dateTime\":\"2012-01-01T23:59:59.500+0000\"}", objJsonString);
	}

	@Test
	public void testDeserializer() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		SimpleTestObject o = mapper.readValue("{\"dateTime\":\"2012-01-01T23:59:59.500+0000\"}", SimpleTestObject.class);
		assertEquals(new DateTime(2012, 1, 1, 23, 59, 59, 500, DateTimeZone.forID("GMT")).getMillis(), o.dateTime.getMillis());
	}

}
