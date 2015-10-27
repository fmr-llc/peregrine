# Peregrine #

The "Peregrine" Event Service is a component which is used as part of a comprehensive "Event Sourcing" based
architecture.  Event Sourcing is a very old idea that has become popular in recent years.

In general you'll find that Event Sourcing is based upon the idea of storing incremental changes in state, over time,
in for form of "events" in contrast to persisting a transactional "current state".  Persisted events are immutable.
Changes in state are reflected as a series of new events.


#### References: ####

* Event Driven Architecture: http://www.slideshare.net/stnor/event-driven-architecture-3395407?related=1
* Aka use case "Akka Persistence": http://www.slideshare.net/ktoso/akka-persistence-event-sourcing-in-30-minutes  (here is an older implementation associated with aka: https://github.com/eligosource/eventsourced)
* Martin Fowler's description: http://martinfowler.com/eaaDev/EventSourcing.html
* Microsoft's description: https://msdn.microsoft.com/en-us/library/Dn589792.aspx
* github: http://ookami86.github.io/event-sourcing-in-practice/
* Similar Projects: http://docs.geteventstore.com/introduction/event-sourcing-basics/ (http://docs.geteventstore.com/introduction/event-sourcing-basics/)
* Rationale: http://blog.arkency.com/2015/03/why-use-event-sourcing/
* More Rationale: https://lostechies.com/gabrielschenker/2015/05/26/event-sourcing-revisited/
* InfoQ: http://www.infoq.com/news/2014/09/greg-young-event-sourcing
* Use case: https://victorops.com/blog/fun-event-sourcing/
* Use case: https://lostechies.com/gabrielschenker/2015/05/26/event-sourcing-revisited/
* Blog post: http://jeremydmiller.com/2014/10/22/building-an-eventstore-with-user-defined-projections-on-top-of-postgresql-and-node-js/
* Building an event store: https://cqrs.files.wordpress.com/2010/11/cqrs_documents.pdf

#### Implementation ####

Peregrine is written in Java (java/JDK 8) to facilitate the widest possible accessibility in terms of target platforms
and potential project contributor community.


#### Executable war ###

A primary goal is to provide a package that is easy to quickly get up and running.  It is for this reason that
Peregrine is packaged as an Executable war file. The POM is designed to create an executable war (a war file that can
be deployed to a web container such as Tomcat, et 'al or run "container-less" using an embedded Jetty instance.)

Structurally this means that embedded within the war file is a Jetty instance as well as Embedded Derby Database.
This configuration is  sufficient for getting this up and running quick as well as for development purposes but it is
not a recommended configuration for production use cases.

Production use cases should use one of the supported external message brokers and external databases.

Embedded mode can be started as follows:

java -cp -Dlog4j.configuration=/path to log config>/logback.xml
        -Dpropsroot="<path to configuration file>/" -Dport=8082 -Dcontext="/eventservice"
        -jar eventservice-<version>.war

        where
        * -Dlog4j.configuration points to your logback.xml configuration file.
        * -Dpropsroot points to the directory where you locate the application property files.
        * -Dport is set to the port that you want the embedded version of Jetty to listen on.
        * -Dcontext is set the context root of that you would like the application to run under.

The application's configuration file: event-config.properties should be placed in the location specified in the
jvm parameter propsroot.  A sample file can be found in the source repository under /resources.


#### Event Model, Persistence and Payload ####

Event data persistence is intended to capture any and all information for consumers to be able to consume
and process an event.  Event payload will be lightweight and be carried using a MapMessage format wherein both the header
and payload will contain a collection of Name/Value pairs.  Publishers will be able to contribute custom data on both
message headers and message payload.  The core message data will be as below:


Attribute | Message Location | Description |
--------- | ---------------- | ----------- |
EventId | Header | An Id to uniquely identify the event being published |
ParentId | Header | (Optional)An Id referencing the parent event of the current event |
EventName | Header | The name of the event that occurred (Should be a past tense verb, ex:  TradeCreated) |
ObjectId | Header | The Id that uniquely identifies the object for the event being published
CorrelationId | Header | (Optional) An Id that can be used to tie events togeter |
SequenceNumber | Header | (Optional) An Id provided by the event producer that denotes the sequence number for the event |
MessageType | Header | A value that identifies the type of message being published.  This field will be used to look up configuration information about how to publish the event |
DataType | Header | A value describing the type of data that will be stored in the event store (PreEventState and PostEventState) |
Source | Header | A value indicating the source of the event (this may be pulled from the HTTP header of the caller to the event service) |
Destination | Header | (Optional) A value indicating a "hint" to the destination for the message.  This field can be used by consumers for filtering. |
Subdestination | Header | (Optional) A value indicating a "hint" to the sub destination for the message.  This field can be used by consumers for filtering. |
CustomHeaders | Header | (Optional) A name/value pair collection of headers provided by the publisher to include on the message. |
CustomPayload | Payload | (Optional) A name/value pair collection of values provided by the publisher to include on the message. |
PreEventState | N/A | (Optional) A serialized representation of the object before the event was published.  This data will only be persisted in the database and will not be put on the message. |
PostEventState | N/A | (Optional) A serialized representation of the object after the event was published.  This data will only be persisted in the database and will not be put on the message. |
IsPublishable | N/A | A flag indicating whether the event can be published |
ReplayIndicator | Header | A flag indicating whether an event is being replayed |
PublishTimeStamp | Header | Timestamp provided by the event publisher.  This field will be used for ordering events. |
ReceivedTimeStamp | Header | Timestamp indicating when the event was received by the event service |
InsertTimeStamp | N/A | Timestamp indicating when the event was written to the database |
ExpirationTimeStamp | Header | (Optional) Timestamp indicating when the event should expire |



#### Service API ####
The service implementation will be RESTful and below is the API:

Method | Response | URL | HTTP Method | Description
------ | -------- | --- | ----------- | -----------
SetEvent(event) | Result containing the event id | http://event-service/event/
SetEvents(events) | Result containing the event ids of the events that were created | http://event-service/events/
GetEvent(eventId) | Event object or null if no event exists | http://event-service/event/{id}
GetEvents(eventParameters)*	 | List of events  |  http://event-service/events?[parameters]
GetLatestEvent(eventParameters)** | Event object or null if no event exists  | http://event-service/latest-event?[parameters]
ReplayEvent(eventId) | {NONE} | http://event-service/replay/{id}

GetEvents Parameters

Searching the event store can be performed with various parameters provided in order to locate the events for a specific
use case.  In addition, because the event store can contain event trees (via the eventId - parentId relationships),
queries can be performed expanding the results by generations.  The following describe the parameters can can be used
to search:


CreatedAfter (Required) - Specifies the point in time after which the event in the query should be returned.
This value should be formatted as an ISO 8061 date/time value.

CreatedBefore (Optional) - Specifies the point in time before which the event in the query should be returned.
This value should be formatted as an ISO 8061 date/time value. If no value is supplied, the search will query from
the most recent event back to the time stamp specified in the CreatedAfter parameter.

Source (Optional) - Specifies the source of the event

Object Id (Optional) - Specifies the id of the object for which the event was produced.

Correlation Id (Optional) - Specifies the correlation id of the object for which the event was produced.

Event Name (Optional) - Specifies the name of the event that occurred

Generations (Optional) - Specifies the number of generations below the initial results (in the event tree) to return as
part of the query result.



Note that although Source, Object Id and Correlation Id are optional, at least one of these values must be specified.