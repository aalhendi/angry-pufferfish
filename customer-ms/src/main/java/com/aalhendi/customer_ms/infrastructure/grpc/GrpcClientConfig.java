package com.aalhendi.customer_ms.infrastructure.grpc;

import com.aalhendi.account.grpc.AccountServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for gRPC clients.
 * Sets up the gRPC channel and stubs for inter-service communication.
 */
@Configuration
public class GrpcClientConfig {

    @Value("${grpc.client.account-service.host:localhost}")
    private String accountServiceHost;

    @Value("${grpc.client.account-service.port:9092}")
    private int accountServicePort;

    /**
     * Creates a managed channel for account service communication.
     */
    @Bean
    public ManagedChannel accountServiceChannel() {
        return ManagedChannelBuilder.forAddress(accountServiceHost, accountServicePort)
                .usePlaintext() // TODO(aalhendi): use TLS in prod. this is for dev.
                .build();
    }

    /**
     * Creates a blocking stub for account service gRPC calls.
     */
    @Bean
    public AccountServiceGrpc.AccountServiceBlockingStub accountServiceStub(ManagedChannel accountServiceChannel) {
        return AccountServiceGrpc.newBlockingStub(accountServiceChannel);
    }
} 