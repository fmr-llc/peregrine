package com.alliancefoundry.serializer;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.joda.time.DateTime;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

/**
 * Deserializer used to handle a particular Joda {@link DateTime} format. This class is intended to be
 * used by the event service internal code. 
 * 
 * @author Paul Bernard
 * @author Peter Mularien
 */
public class CustomJsonDateTimeDeserializer extends JsonDeserializer<DateTime>
{
    @Override
    public DateTime deserialize(JsonParser jsonParser,
            DeserializationContext deserializationContext) throws IOException, JsonProcessingException {

        SimpleDateFormat format = new SimpleDateFormat(SerializerConstants.EVENT_SERVICE_DATE_TIME_FORMAT);
        String date = jsonParser.getText();

        if (date==null || date.length()<1){
            return null;
        }

        try {
            return new DateTime(format.parse(date));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

    }

}
