# Design Decisions

## Event Driven Architecture

There are a couple of popular patterns for EDA. 

- Pub/Sub
  - publish events to a topic
  - decoupled & simple but no guaranteed delivery is a no-go for anything financial
- Event notification
  - emit (scream) events when state changes. other services react if they care
  - also simple and easy debugging but no history for changes, also no guaranteed delivery
- Event Sourcing
  - all changes are immutable events, can rebuild state via replays 
  - this would probably be ideal. nice audit trails.
  - failures will be on same MS, so we just roll back DB and log the error. send it to DLQ
  - idempotent events. or use sets rather than path
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

Going to do event sourcing. We can mix and match later.

## DB Table Stuff

- Primary Key selection. Customer ID as PK vs a standard PK (UUIDv7 / TypeID) + a `customer_id` field. pagination
- PK is always called `tablename_id`. I like easy debugging
- no enums in db for now (account status). discuss normalization.
- no `deleted_at` column.
- we are not doing federated foreign keys :) (tight coupling and breaks autonomy of microservice. which is the whole point of having a microservice)
- VARCHAR(10) vs numeric (int) type for account number and customer id.
- ON UPDATE RESTRICT and ON DELETE CASCADE