package com.alliancefoundry.publisher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by Paul Bernard on 10/26/15.
 */
public class PropertiesRouter implements RouterConfig {

    private static final Logger log = LoggerFactory.getLogger(PropertiesRouter.class);
    private String propertiesFile;
    private boolean init = false;
    private Map<String, String> configParams = new HashMap<>();

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

        String destination = configStr.substring(delimiterPos+2);

        return destination;
    }
}
