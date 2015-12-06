package com.alliancefoundry.model;

/**
 * Created by Paul Bernard on 11/23/15.
 */
public class Pair {

    private String name;
    private String value;

    public Pair(String name, String value){
        this.name = name;
        this.value = value;
    }

    public Pair(){
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
