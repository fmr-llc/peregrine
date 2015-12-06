package com.alliancefoundry.dao.impl.dbms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by Paul Bernard on 12/6/15.
 */
public class DaoProvider {

    private static final Logger log = LoggerFactory.getLogger(DaoProvider.class);

    protected JdbcTemplate jdbcTemplate;

    protected boolean schemaInit = false;

    public void setDataSource(DataSource dataSource) {

        this.jdbcTemplate = new JdbcTemplate(dataSource);
        org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator
                tr = new org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator();
        this.jdbcTemplate.setExceptionTranslator(tr);

        try {
            if (validateDB()){
                schemaInit = true;
            } else {
                log.error("problem with schema, table or connection.");
            }
        } catch (Exception e){
            log.error("connection not available");
        }
    }

    protected boolean validateDB(){

        try {
            if (schemaInit == false) {
                if (schemaExists() == false) {
                    boolean schemaOK = createSchema();
                    boolean tablesOK = createTables();
                    if (schemaOK && tablesOK) {
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return true;
                }
            }

            return true;

        } catch (Exception e){
            log.error(e.getMessage());
            return false;
        }


    }

    private boolean schemaExists() {

        String sql = "SELECT * FROM SYS.SYSSCHEMAS WHERE SCHEMANAME = 'EVENT'";

        try {
            String schema = (String)jdbcTemplate
                    .queryForObject(
                            sql, new Object[] { },
                            new StringRowMapperPos(2));

            if (schema!=null){
                log.debug("schema found with name: " + schema);
                return true;
            } else {

                log.debug("could not determine if schema exists.");
                return false;
            }
        } catch (Exception e){
            return false;
        }


    }

    private boolean createSchema(){

        String sql = "CREATE SCHEMA event";

        try {
            // create table
            PreparedStatement ps = jdbcTemplate
                    .getDataSource()
                    .getConnection()
                    .prepareStatement(sql);
            ps.execute();

            schemaInit = true;
            return true;

        } catch (SQLException e){
            log.debug("schema already exists. ");
            return true;
        }

    }

    private boolean createTables(){

        String tableSql1 = "CREATE TABLE event.event_store (\n" +
                "  eventId varchar(45) NOT NULL,\n" +
                "  parentId varchar(45) DEFAULT NULL,\n" +
                "  eventName varchar(45) DEFAULT NULL,\n" +
                "  objectId varchar(45) DEFAULT NULL,\n" +
                "  correlationId varchar(45) DEFAULT NULL,\n" +
                "  sequenceNumber int DEFAULT NULL,\n" +
                "  messageType varchar(45) NOT NULL,\n" +
                "  dataType varchar(45) DEFAULT NULL,\n" +
                "  source varchar(45) DEFAULT NULL,\n" +
                "  destination varchar(45) DEFAULT NULL,\n" +
                "  subdestination varchar(45) DEFAULT NULL,\n" +
                "  replayIndicator boolean DEFAULT FALSE,\n" +
                "  publishTimestamp bigint DEFAULT 0,\n" +
                "  receivedTimestamp bigint DEFAULT 0,\n" +
                "  expirationTimestamp bigint DEFAULT 0,\n" +
                "  preEventState clob DEFAULT NULL,\n" +
                "  postEventState clob DEFAULT NULL,\n" +
                "  isPublishable boolean DEFAULT FALSE,\n" +
                "  insertTimestamp bigint NOT NULL,\n" +
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
            PreparedStatement ps2 = jdbcTemplate
                    .getDataSource()
                    .getConnection().prepareStatement(tableSql1);
            ps2.execute();
        } catch (SQLException e){
            log.warn("table already exists");
        }

        try {
            PreparedStatement ps2 = jdbcTemplate
                    .getDataSource()
                    .getConnection().prepareStatement(tableSql2);
            ps2.execute();
        } catch (SQLException e){
            log.warn("table already exists");
        }

        try {
            PreparedStatement ps2 = jdbcTemplate
                    .getDataSource()
                    .getConnection().prepareStatement(tableSql3);
            ps2.execute();
        } catch (SQLException e){
            log.warn("table already exists");
        }

        try {
            PreparedStatement ps2 = jdbcTemplate
                    .getDataSource()
                    .getConnection().prepareStatement(tableSql4);
            ps2.execute();
        } catch (SQLException e){
            log.warn("table already exists");
        }

        try {
            PreparedStatement ps2 = jdbcTemplate
                    .getDataSource()
                    .getConnection().prepareStatement(tableSql5);
            ps2.execute();
        } catch (SQLException e){
            log.warn("table already exists");
        }



        return true;

    }
}
