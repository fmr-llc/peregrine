CREATE TABLE event_store (
  eventId varchar(45) NOT NULL,
  parentId varchar(45) DEFAULT NULL,
  eventName varchar(45) DEFAULT NULL,
  objectId varchar(45) NOT NULL,
  correlationId varchar(45) DEFAULT NULL,
  sequenceNumber int DEFAULT NULL,
  messageType varchar(45) NOT NULL,
  dataType varchar(45) DEFAULT NULL,
  source varchar(45) DEFAULT NULL,
  destination varchar(45) DEFAULT NULL,
  subdestination varchar(45) DEFAULT NULL,
  replayIndicator char(1) FOR BIT DATA NOT NULL,
  preEventState clob DEFAULT NULL,
  postEventState clob DEFAULT NULL,
  isPublishable char(1) FOR BIT DATA NOT NULL,
  timestamp bigint NOT NULL,
  PRIMARY KEY (eventId)
);

CREATE TABLE event.event_headers (
  eventId varchar(45) NOT NULL,
  name varchar(45) NOT NULL,
  value varchar(45) NOT NULL,
  PRIMARY KEY (eventId,name),
  CONSTRAINT headersEventId FOREIGN KEY (eventId) REFERENCES event_store (eventId) ON DELETE CASCADE
);

CREATE TABLE event.event_payload (
  eventId varchar(45) NOT NULL,
  name varchar(45) NOT NULL,
  value varchar(45) NOT NULL,
  dataType varchar(45) DEFAULT NULL,
  PRIMARY KEY (eventId,name),
  CONSTRAINT payloadEventId FOREIGN KEY (eventId) REFERENCES event_store (eventId) ON DELETE CASCADE
);

CREATE TABLE event.publication_audit (
  eventId varchar(45) NOT NULL,
  captureTimestamp bigint NOT NULL,
  persistTimestamp bigint NOT NULL,
  publishCount int DEFAULT 0,
  PRIMARY KEY (eventId),
  CONSTRAINT auditEventId FOREIGN KEY (eventId) REFERENCES event_store (eventId) ON DELETE CASCADE
);

CREATE TABLE event.publish_timestamp (
  eventId varchar(45) NOT NULL,
  publishTimestamp bigint NOT NULL,
  publishId int DEFAULT 1,
  PRIMARY KEY (eventId,publishId),
  CONSTRAINT publishEventId FOREIGN KEY (eventId) REFERENCES event_store (eventId) ON DELETE CASCADE
);