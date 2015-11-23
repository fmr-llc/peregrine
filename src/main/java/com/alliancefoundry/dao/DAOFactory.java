package com.alliancefoundry.dao;


/**
 * Created by Paul Bernard on 10/24/15.
 */
public class DAOFactory {

    private EventDAO dao;


    public EventDAO getDAO(){
       return dao;
    }

    public void setDAO(EventDAO dao){
        this.dao = dao;
    }
}
