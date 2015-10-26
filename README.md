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