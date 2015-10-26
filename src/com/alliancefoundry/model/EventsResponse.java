package com.alliancefoundry.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Paul Bernard on 10/25/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
@JsonTypeInfo(include= JsonTypeInfo.As.WRAPPER_OBJECT, use= JsonTypeInfo.Id.NAME)
public class EventsResponse {

    private List<EventResponse> evts;
    private String status;
    private String statusMessage;

    public EventsResponse(List<EventResponse> evts){
        this.evts = evts;
    }

    public EventsResponse(){}

    public List<EventResponse> getEvents(){

        if (evts==null){
            // no events in container
            return new ArrayList<EventResponse>();
        }

        return evts;
    }

    public void setEvents(List<EventResponse> evt){
        this.evts = evt;
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
