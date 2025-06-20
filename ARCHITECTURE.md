# Architecture

```mermaid
flowchart LR  
Client -->|REST| APIGateway  
APIGateway -->|gRPC| AccountService  
APIGateway -->|gRPC| CustomerService  
AccountService -->|MQ| CustomerService  
CustomerService -->|MQ| AccountService  
```
