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









