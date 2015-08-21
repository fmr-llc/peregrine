package com.alliancefoundry.controller;


import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


import com.alliancefoundry.model.Event;


/**
 * Created by: Paul Bernard
 * 
 *
 */
@RestController
public class EventServiceController  {

	/*
	 * Gets information about an event
	 */
    @RequestMapping(value="/event/{id}", method = RequestMethod.GET)
    public String getEventByID(@PathVariable String id){
        Event evt = new Event();
        evt.setEventId(id);
        return "response : " + evt.getEventId();
    }


}
