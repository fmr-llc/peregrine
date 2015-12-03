package com.alliancefoundry.model;

/**
 * Created by Paul Bernard on 11/23/15.
 */
public class NVPair {

    private String name;
    private String value;

    public NVPair(String name, String value){
        this.name = name;
        this.value = value;
    }

    public NVPair(){
        // Default Constructor
    }

    public String getName(){
        return this.name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getValue(){
        return this.value;
    }

    public void setValue(String value){
        this.value = value;
    }

}
