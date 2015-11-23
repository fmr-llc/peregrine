package com.alliancefoundry.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import io.swagger.annotations.ApiModel;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class EventResponse {

    private Event evt;
    private String status;
    private String statusMessage;
    private String persistStatus;
    private String persistStatusMessage;
    private String publishStatus;
    private String publishStatusMessage;
    private long processingDuration;


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

    public void setStatusMessage(String status){
        this.statusMessage = status;
    }


    public String getPersistStatus(){
        return persistStatus;
    }

    public void setPersistStatus(String status){
        this.persistStatus = status;
    }


    public String getPersistStatusMessage(){
        return persistStatusMessage;
    }

    public void setPersistStatusMessage(String msg){
        persistStatusMessage = msg;
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

    public long getProcessingDuration(){
        return processingDuration;
    }

    public void setProcessingDuration(long duration){
        this.processingDuration = duration;
    }


}
