CREATE TABLE `event_store` (
  `eventId` varchar(45) NOT NULL,
  `parentId` varchar(45) DEFAULT NULL,
  `eventName` varchar(45) DEFAULT NULL,
  `objectId` varchar(45) NOT NULL,
  `correlationId` varchar(45) DEFAULT NULL,
  `sequenceNumber` int(11) DEFAULT NULL,
  `messageType` varchar(45) NOT NULL,
  `dataType` varchar(45) DEFAULT NULL,
  `source` varchar(45) DEFAULT NULL,
  `destination` varchar(45) DEFAULT NULL,
  `subdestination` varchar(45) DEFAULT NULL,
  `replayIndicator` bit(1) NOT NULL,
  `preEventState` text DEFAULT NULL,
  `postEventState` text DEFAULT NULL,
  `isPublishable` bit(1) NOT NULL,
  `timestamp` bigint(20) NOT NULL,
  PRIMARY KEY (`eventId`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `event_headers` (
  `eventId` varchar(45) NOT NULL,
  `name` varchar(45) NOT NULL,
  `value` varchar(45) NOT NULL,
  PRIMARY KEY (`eventId`,`name`),
  CONSTRAINT `headersEventId` FOREIGN KEY (`eventId`) REFERENCES `event_store` (`eventId`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `event_payload` (
  `eventId` varchar(45) NOT NULL,
  `name` varchar(45) NOT NULL,
  `value` varchar(45) NOT NULL,
  `dataType` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`eventId`,`name`),
  CONSTRAINT `payloadEventId` FOREIGN KEY (`eventId`) REFERENCES `event_store` (`eventId`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `publication_audit` (
  `eventId` varchar(45) NOT NULL,
  `captureTimestamp` bigint(20) NOT NULL,
  `persistTimestamp` bigint(20) NOT NULL,
  `publishCount` int(11) DEFAULT 0,
  PRIMARY KEY (`eventId`),
  CONSTRAINT `auditEventId` FOREIGN KEY (`eventId`) REFERENCES `event_store` (`eventId`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `publish_timestamp` (
  `eventId` varchar(45) NOT NULL,
  `publishTimestamp` bigint(20) NOT NULL,
  `publishId` int(11) DEFAULT 1,
  PRIMARY KEY (`eventId`,`publishId`),
  CONSTRAINT `publishEventId` FOREIGN KEY (`eventId`) REFERENCES `event_store` (`eventId`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;