package com.alliancefoundry.publisher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliancefoundry.publisher.BrokersConfiguration.BrokerConfiguration;
import com.alliancefoundry.publisher.MessageTypeConfiguration.MessageDestination;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

/**
 * Created by Paul Bernard on 10/26/15.
 */
public class PropertiesRouter implements RouterConfig {

    private static final Logger log = LoggerFactory.getLogger(PropertiesRouter.class);
    
    private BrokersConfiguration brokers;
    private MessageTypeConfiguration messageTypes;

    public BrokersConfiguration getBrokers() {
		return brokers;
	}

	public void setBrokers(BrokersConfiguration brokers) {
		this.brokers = brokers;
	}
	
	public MessageTypeConfiguration getMessageTypes() {
		return messageTypes;
	}

	public void setMessageTypes(MessageTypeConfiguration messageTypes) {
		this.messageTypes = messageTypes;
	}

    @Override
    public String getPublisher(String messageType) {
    	// TODO: push this down into the MessageTypeConfiguration class
    	MessageDestination messageDestination = getMessageTypes().getTypedMessageDestinations().get(messageType);
    	if(messageDestination == null) {
    		return null;
    	} else {
    		return messageDestination.getPublisher();
    	}
    }

    @Override
    public String getDestination(String messageType) {
    	MessageDestination messageDestination = getMessageTypes().getTypedMessageDestinations().get(messageType);
    	if(messageDestination == null) {
    		return null;
    	} else {
    		return messageDestination.getDestination();
    	}
    }

    @Override
    public Map<String, PublisherInterface> getPublishers() {
        Map<String, PublisherInterface> ret = new HashMap<String, PublisherInterface>();
        for (BrokerConfiguration brokerConfiguration : brokers.getBrokers().values()) {
        	try {
        		// TODO: push this down into the BrokerConfiguration implementation class?
                PublisherInterface obj = (PublisherInterface) Class.forName(brokerConfiguration.getClassName()).newInstance();
                obj.setBrokerUrl(brokerConfiguration.getUrl());
                ret.put(brokerConfiguration.getName(), obj);
            } catch (ClassNotFoundException e){
                log.error("configuration error publisher class not found.", e);
            } catch (InstantiationException e){
                log.error("configuration error publisher cannot be instantiated.", e);
            } catch (IllegalAccessException e){
                log.error("Illegal Access exception publisher cannot be instantiated.", e);
            } catch (PublisherException e){
                log.error("publisher exception initializing.");
            }
		}
        return ret;

    }
}
