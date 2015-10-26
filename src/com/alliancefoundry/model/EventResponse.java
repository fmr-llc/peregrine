package com.alliancefoundry.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

/**
 * Created by Paul Bernard on 10/24/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
@JsonTypeInfo(include=As.WRAPPER_OBJECT, use=Id.NAME)
public class EventResponse {

    private Event evt;
    private String status;
    private String statusMessage;

    public EventResponse(){

    }

    public EventResponse(Event evt){
        this.evt = evt;
    }

    public Event getEvent(){
        return evt;
    }

    public void setEvent(Event evt){
        this.evt = evt;
    }

    public String getStatus(){
        return status;
    }

    public void setStatus(String status){
        this.status = status;
    }

    public String getStatusMessage(){
        return statusMessage;
    }

    public void setStatusMessage(String msg){
        statusMessage = msg;
    }
}
