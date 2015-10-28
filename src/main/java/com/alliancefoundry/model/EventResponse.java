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
    private String publishStatus;
    private String publishStatusMessage;

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

    public String getPersistStatus(){
        return status;
    }

    public void setPersistStatus(String status){
        this.status = status;
    }

    public String getPersistStatusMessage(){
        return statusMessage;
    }

    public void setPersistStatusMessage(String msg){
        statusMessage = msg;
    }

    public String getPublishStatus(){
        return publishStatus;
    }

    public void setPublishStatus(String status){
        this.publishStatus = status;
    }

    public String getPublishStatusMessage(){
        return publishStatusMessage;
    }

    public void setPublishStatusMessage(String msg){
        publishStatusMessage = msg;
    }
}
