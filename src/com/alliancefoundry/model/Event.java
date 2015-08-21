package com.alliancefoundry.model;

import com.wordnik.swagger.annotations.Api;

import java.util.HashMap;
import java.util.Map;



/**
 * Created by Paul Bernard
 */
public class Event {

    // Headers
    private String m_eventId;
    private String m_parentId;
    private String m_eventName;
    private String m_objectId;
    private String m_correlationId;
    private String m_SequenceNumber;
    private String m_messageType;
    private String m_dataType;
    private String m_source;
    private String m_destination;
    private String m_subdestination;
    private boolean m_replayIndicator;
    private String m_publishedTimeStamp;
    private String m_receivedTimeStamp;
    private String m_expirationTimeStamp;

    // other
    private Map<String, String> m_customHeaders = new HashMap<String, String>();
    private Map<String, String> m_payload = new HashMap<String, String>();
    private String m_preEventState;
    private String m_postEventState;
    private boolean m_isPublishable;
    private String m_insertTimeStamp;

    public void setEventId(String id){
        m_eventId = id;
    }

    public String getEventId(){
        return m_eventId;
    }



}
