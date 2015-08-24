package com.alliancefoundry.model;


import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateTime;



/**
 * Created by Paul Bernard
 */
public class Event {

    // Headers
    private long eventId;
    private String parentId;
    private String eventName;
    private String objectId;
    private String correlationId;
    private String sequenceNumber;
    private String messageType;
    private String dataType;
    private String source;
    private String destination;
    private String subdestination;
    private boolean replayIndicator;
    private DateTime publishedTimeStamp;
    private DateTime receivedTimeStamp;
    private DateTime expirationTimeStamp;

    // other
    private Map<String, String> customHeaders = new HashMap<String, String>();
    private Map<String, String> payload = new HashMap<String, String>();
    private String preEventState;
    private String postEventState;
    private boolean isPublishable;
    private DateTime insertTimeStamp;

    public void setEventId(long id){
        eventId = id;
    }

    public long getEventId(){
        return eventId;
    }



}
