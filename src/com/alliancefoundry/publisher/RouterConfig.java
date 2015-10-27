package com.alliancefoundry.publisher;

/**
 * Created by Paul Bernard on 10/26/15.
 */
public interface RouterConfig {

    String getPublisher(String messageType);
    String getDestination(String messageType);


}
