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

Peregrine is written in Java and targets JDK 8.  The POM is written to create an executable war (a war file that can
be deployed to a web container such as Tomcat, et 'al or run "container-less" using an embedded Jetty instance.)
The implementation language (java) and packaging structure (executable war) was chosen to facilitate the widest
possible accessibility in terms of target platforms and potential project contributor community.

The executable war, in addition to containing an embedded http server (Jetty) also embeds a DerbyDB instance, intended
for demonstration purposes only.  This packaging supports the ability to "get up and running" with very little effort.
Copy Peregrine to a local directory, install a messaging broker such as (ActiveMQ) and you're in business.

After you get a feel for how Peregrine can be configured, you can change the persistence store to any number of
supported databases: Derby (Embedded or Server), MySql/Maria DB, and PostgreSQL.  In addition to offering a variety of
supported databases, Peregrine also supports any JMS compliance message broker as well as Kafka.


#### Executable war ###

A primary goal is to provide a package that is easy to get up and running quickly.  It is for this reason that
Peregrine is packaged as an Executable war file.  Embedded within the war file when is a Jetty instance as
well as Embedded Derby Database.  This configuration is good for getting this up and running quick as well as
for development purposes but it is not a recommended configuration for production use cases.

Production use cases should use one of the supported external message brokers and external databases.

Embedded mode can be started as follows:

java -cp -Dlog4j.configuration=/path to log config>/logback.xml
        -Dpropsroot="<path to configuration files>/" -Dport=8082 -Dcontext="/eventservice"
        -jar eventservice-<version>.war

        where
        * -Dlog4j.configuration points to your logback.xml configuration file.
        * -Dpropsroot points to the directory where you locate the application property files.
        * -Dport is set to the port that you want the embedded version of Jetty to listen on.
        * -Dcontext is set the context root of that you would like the application to run under.









