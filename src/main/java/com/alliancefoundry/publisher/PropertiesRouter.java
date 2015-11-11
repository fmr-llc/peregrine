package com.alliancefoundry.publisher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private String propertiesFile;
    private boolean init = false;
    private Map<String, String> configParams = new HashMap<String, String>();

    private void init(){

        try {

            File file = new File(propertiesFile);

            FileInputStream fileInput = new FileInputStream(file);
            Properties properties = new Properties();
            properties.load(fileInput);
            fileInput.close();

            Enumeration enuKeys = properties.keys();
            while (enuKeys.hasMoreElements()) {
                String key = (String) enuKeys.nextElement();
                String value = properties.getProperty(key);
                configParams.put(key, value);
            }
        } catch (FileNotFoundException e) {
            log.error("router configuration file not found.", e);
        } catch (IOException e) {
            log.error("attempt to read router configuration file failed.", e);
        }
    }


    public String getLocation(){
        return propertiesFile;
    }

    public void setLocation(String file){
        propertiesFile = file;
    }

    @Override
    public String getPublisher(String messageType) {

        if (init==false){
            init();
            init = true;
        }


        String configStr = configParams.get("message.type." + messageType);

        int delimiterPos = configStr.indexOf("|");
        String publisher = configStr.substring(0, delimiterPos);

        return publisher;
    }

    @Override
    public String getDestination(String messageType) {

        if (init==false){
            init();
            init = true;
        }

        String configStr = configParams.get("message.type." + messageType);

        int delimiterPos = configStr.indexOf("|");

        String destination = configStr.substring(delimiterPos+1);

        return destination;
    }

    @Override
    public Map<String, PublisherInterface> getPublishers() {
        // iterate through appropriate subset of properties
        // instantiate publishers based upon class name
        // set publisher properties and add to collection.

        if (init==false){
            init();
            init = true;
        }

        Map<String, PublisherInterface> ret = new HashMap<String, PublisherInterface>();

        Iterator<String> iter = configParams.keySet().iterator();

        // create all the object first to avoid sequencing issues
        while(iter.hasNext()){
            String key = iter.next();
            if ((key.startsWith("broker.")) && (key.endsWith(".name"))){
                int endPos = key.lastIndexOf(".name");
                String name = key.substring(7, endPos);


                String keyVal = configParams.get("broker." + name + ".name");
                log.debug("extracted publisher instance named: " + keyVal);
                String className = configParams.get("broker." + name + ".class");
                log.debug("extracted publisher class name: " + className);
                String url = configParams.get("broker." + name + ".url");
                log.debug("extracted publisher url: " + url);

                try {

                    PublisherInterface obj = (PublisherInterface) Class.forName(className).newInstance();
                    obj.setBrokerUrl(url);
                    ret.put(keyVal, obj);

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

        }

        return ret;

    }
}
