# TODO

- Public APIs have to be REST by spec, but services are co-dependent... Maybe gRPC internally?
  - gRPC for internal messaging
  - OpenFeign for public. turns out its just a declarative thing that integrates p well with spring. will have to explore a little more
- since co-dependent, will probably need circuit breaker for catastrophic & cascading failures. keep it simple
- microservices will need distributed tracing... so span-ids and trace-ids.
- probably OTLP as exporter. others are either paid/super enterprise or just sound like a headache
- Discovery: turns out Eureka isnt as much of a thing anymore. So we are left with Hashicorp, K8s DNS or just skip discovery all together. No zookeeper... 
- Decide on LB... is it too much?
  - K8s can do infra lb.. `kube-proxy` should be more than enough
  - Spring probably has some app-level lb... imo thats overkill for a demo
  - if we pick Consul then it can also do lb on mesh... but K8s would do it for free. going full Hashicorp means we get config as well as part of their suite.
  - grpc can round-robin as policy
- write docs that pass https://en.wikipedia.org/wiki/Bus_factor
- TDD? best way to show this would probably be a commit with tests (failing), then another commit with tests passing
- No CI/CD... im pretty sure anyone can write GH Actions or Jenkins/Argo pipeline.


## Immediate

- Write migrations to init db tables for both microservices
- Basic JPA entity
- Failing tests
- Flesh out entities
- Begin building public endpoints
- Failing tests for endpoints
- build logic, test logic, wire up endpoints
- make containers that spin up the DB instance (how many DBs do we want to support?)
- Make 2 profiles. One that runs the migrations automatically, and one that just runs.
