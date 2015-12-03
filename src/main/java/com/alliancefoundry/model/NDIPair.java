package com.alliancefoundry.model;

/**
 * Created by Paul Bernard on 11/23/15.
 */
public class NDIPair {

    private DataItem value;
    private String name;

    public NDIPair(String name, DataItem value){
        this.name = name;
        this.value = value;
    }

    public NDIPair(){
        // Default Constructor
    }

    public String getName(){
        return this.name;
    }

    public void setName(String name){
        this.name = name;
    }

    public DataItem getValue(){
        return this.value;
    }

    public void setValue(DataItem value){
        this.value = value;
    }

}
