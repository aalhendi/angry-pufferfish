# Design Decisions

## Event Driven Architecture

There are a couple of popular patterns for EDA. 

- Pub/Sub
  - publish events to a topic
  - decoupled & simple but no guaranteed delivery is a no-go for anything financial
- Event notification
  - emit (scream) events when state changes. other services react if they care
  - also simple and easy debugging but no history for changes
- Event Sourcing
  - all changes are immutable events, can rebuild state via replays 
  - this would probably be ideal. nice audit trails.
- CQRS
  - split read/write models
  - probably overkill for demo. perf here is unmatched.
- Saga
  - can manage multi step tx across services
  - this would also be nice. we can do compensating actions for refunds/failed tx etc. error handling is a pain tho
- Choreography
  - our services react to events without a central coordinator
  - decentralized & resilient but debugging isnt fun. 
- Event Mesh
  - cross-cloud event bus. 
  - this is also a good option for multi-region and cross-site deployments. but not for the demo lol
- Event APIs
  - webhooks essentially. (or other events)
  - nice to integrate with rest but not so "pure"

I think if i can get event-sourcing done in time ill go with it. if not, saga for transfers at least