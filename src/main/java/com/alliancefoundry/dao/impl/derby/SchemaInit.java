package com.alliancefoundry.dao.impl.derby;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Paul Bernard on 11/22/15.
 */
public class SchemaInit {

    private static final Logger log = LoggerFactory.getLogger(SchemaInit.class);

    protected boolean schemaInit = false;

    private boolean schemaExists(Connection conn){

        String sql = "SELECT * FROM SYS.SYSSCHEMAS";
        try{
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                log.debug("schema found with name: " + rs.getString(2));
                if (rs.getString(1).equalsIgnoreCase("event")) return true;
            }

            log.debug("no schemas found with correct name.");
            return false;

        } catch (SQLException e){
            log.debug("could not determine if schema exists.");
            return false;
        }

    }

    private boolean createSchema(Connection conn){

        String sql = "CREATE SCHEMA event";

        try {
            // create table
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.execute();

            schemaInit = true;
            return true;

        } catch (SQLException e){
            log.debug("schema already exists. ");
            return true;
        }

    }

    private boolean createTables(Connection conn){

        String tableSql1 = "CREATE TABLE event.event_store (\n" +
                "  eventId varchar(45) NOT NULL,\n" +
                "  parentId varchar(45) DEFAULT NULL,\n" +
                "  eventName varchar(45) DEFAULT NULL,\n" +
                "  objectId varchar(45) NOT NULL,\n" +
                "  correlationId varchar(45) DEFAULT NULL,\n" +
                "  sequenceNumber int DEFAULT NULL,\n" +
                "  messageType varchar(45) NOT NULL,\n" +
                "  dataType varchar(45) DEFAULT NULL,\n" +
                "  source varchar(45) DEFAULT NULL,\n" +
                "  destination varchar(45) DEFAULT NULL,\n" +
                "  subdestination varchar(45) DEFAULT NULL,\n" +
                "  replayIndicator boolean NOT NULL,\n" +
                "  publishTimestamp bigint,\n" +
                "  receivedTimestamp bigint,\n" +
                "  expirationTimestamp bigint,\n" +
                "  preEventState clob DEFAULT NULL,\n" +
                "  postEventState clob DEFAULT NULL,\n" +
                "  isPublishable boolean NOT NULL,\n" +
                "  insertTimeStamp bigint NOT NULL,\n" +
                "  PRIMARY KEY (eventId)\n" +
                ")";


        String tableSql2 = "CREATE TABLE event.event_headers (\n" +
                "  eventId varchar(45) NOT NULL,\n" +
                "  name varchar(45) NOT NULL,\n" +
                "  value varchar(45) NOT NULL,\n" +
                "  PRIMARY KEY (eventId,name)\n" +
                //"  CONSTRAINT headersEventId FOREIGN KEY (eventId) REFERENCES event_store (eventId) ON DELETE CASCADE\n" +
                ")";


        String tableSql3 = "CREATE TABLE event.event_payload (\n" +
                "  eventId varchar(45) NOT NULL,\n" +
                "  name varchar(45) NOT NULL,\n" +
                "  value varchar(45) NOT NULL,\n" +
                "  dataType varchar(45) DEFAULT NULL,\n" +
                "  PRIMARY KEY (eventId,name)\n" +
                //"  CONSTRAINT payloadEventId FOREIGN KEY (eventId) REFERENCES event_store (eventId) ON DELETE CASCADE\n" +
                ")";

        String tableSql4 = "CREATE TABLE event.publication_audit (\n" +
                "  eventId varchar(45) NOT NULL,\n" +
                "  captureTimestamp bigint NOT NULL,\n" +
                "  persistTimestamp bigint NOT NULL,\n" +
                "  publishCount int DEFAULT 0,\n" +
                "  PRIMARY KEY (eventId)\n" +
                //"  CONSTRAINT auditEventId FOREIGN KEY (eventId) REFERENCES event_store (eventId) ON DELETE CASCADE\n" +
                ")";

        String tableSql5 = "CREATE TABLE event.publish_timestamp (\n" +
                "  eventId varchar(45) NOT NULL,\n" +
                "  publishTimestamp bigint NOT NULL,\n" +
                "  publishId int DEFAULT 1,\n" +
                "  PRIMARY KEY (eventId,publishId)\n" +
                //"  CONSTRAINT publishEventId FOREIGN KEY (eventId) REFERENCES event_store (eventId) ON DELETE CASCADE\n" +
                ")";

        try {
            PreparedStatement ps2 = conn.prepareStatement(tableSql1);
            ps2.execute();
        } catch (SQLException e){
            log.warn("table already exists");
        }

        try {
            PreparedStatement ps2 = conn.prepareStatement(tableSql2);
            ps2.execute();
        } catch (SQLException e){
            log.warn("table already exists");
        }

        try {
            PreparedStatement ps2 = conn.prepareStatement(tableSql3);
            ps2.execute();
        } catch (SQLException e){
            log.warn("table already exists");
        }

        try {
            PreparedStatement ps2 = conn.prepareStatement(tableSql4);
            ps2.execute();
        } catch (SQLException e){
            log.warn("table already exists");
        }

        try {
            PreparedStatement ps2 = conn.prepareStatement(tableSql5);
            ps2.execute();
        } catch (SQLException e){
            log.warn("table already exists");
        }



        return true;

    }

    protected void validateDB(Connection conn){
        if (schemaInit==false){
            if (schemaExists(conn)==false){
                createSchema(conn);
                createTables(conn);
            }
        }

    }
}
