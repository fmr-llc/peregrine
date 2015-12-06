package com.alliancefoundry.dao.impl.dbms;

/**
 * Created by Paul Bernard on 12/5/15.
 */
public class DBConstants {

    public final static String SCHEMA = "event";

    public final static String EVENT_STORE_TBL = SCHEMA + ".event_store";
    public final static String EVENT_HEADERS_TBL = SCHEMA + ".event_headers";
    public final static String EVENT_PAYLOAD_TBL = SCHEMA + ".event_payload";

    public final static String EVENT_ID = "eventId";
    public final static String NAME = "name";
    public final static String VALUE = "value";
    public final static String DATA_TYPE = "dataType";
    public final static String PARENT_EVENT_ID = "parentId";
    public final static String EVENT_NAME = "eventName";
    public final static String OBJECT_ID = "objectId";
    public final static String CORRELATION_ID = "correlationId";
    public final static String SEQUENCE_NUMBER = "sequenceNumber";
    public final static String MESSAGE_TYPE = "messageType";
    public final static String SOURCE = "source";
    public final static String DESTINATION = "destination";
    public final static String SUBDESTINATION = "subdestination";
    public final static String REPLAY_INDICATOR = "replayIndicator";
    public final static String PUBLISH_TIMESTAMP = "publishTimestamp";
    public final static String RECEIVED_TIMESTAMP = "receivedTimestamp";
    public final static String EXPIRATION_TIMESTAMP = "expirationTimestamp";
    public final static String PREEVENT_STATE = "preEventState";
    public final static String POSTEVENT_STATE = "postEventState";
    public final static String IS_PUBLISHABLE = "isPublishable";
    public final static String INSERT_TIMESTAMP = "insertTimestamp";

}
