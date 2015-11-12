package com.alliancefoundry.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModel;

/**
 * Created by Paul Bernard on 11/11/15.
 */


@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class EventRequest {

    private Event event;

    public EventRequest(){}

    public EventRequest(Event evt){
        this.event = evt;
    }

    public Event getEvent(){
        return event;
    }

    public void setEvent(Event evt){
        this.event = evt;
    }

}
