package com.alliancefoundry.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonSerialize(include=JsonSerialize.Inclusion.NON_EMPTY)
public class EventsResponse {

    private List<EventResponse> evts;
    private String status;
    private String statusMessage;
    private List<String> eventNames;
    private List<String> eventSources;
    private long processingDuration;

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

    public List<String> getEventNames(){ return this.eventNames; }

    public void setEventNames(List<String> names) { this.eventNames = names; }

    public List<String> getEventSources(){ return this.eventSources; }

    public void setEventSources(List<String> sources) { this.eventSources = sources; }

    public long getProcessingDuration(){
        return processingDuration;
    }

    public void setProcessingDuration(long duration){
        this.processingDuration = duration;
    }


}
