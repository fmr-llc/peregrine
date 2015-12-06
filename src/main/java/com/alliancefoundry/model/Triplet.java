package com.alliancefoundry.model;

/**
 * Created by Paul Bernard on 11/23/15.
 */
public class Triplet extends Pair {

    private String type;

    public Triplet(String name, String value, String type){
        super(name, value);
        this.type = type;
    }

    public Triplet(){
        // Default Constructor
    }

    public String getType(){
        return this.type;
    }

    public void setType(String value){
        this.type = value;
    }

}
