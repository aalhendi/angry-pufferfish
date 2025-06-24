package com.aalhendi.account_ms.infrastructure.grpc;

import com.aalhendi.customer.grpc.CustomerServiceGrpc;
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

    @Value("${grpc.client.customer-service.host:localhost}")
    private String customerServiceHost;

    @Value("${grpc.client.customer-service.port:9091}")
    private int customerServicePort;

    /**
     * Creates a managed channel for customer service communication.
     */
    @Bean
    public ManagedChannel customerServiceChannel() {
        return ManagedChannelBuilder.forAddress(customerServiceHost, customerServicePort)
                .usePlaintext() // TODO(aalhendi): use TLS in prod. this is for dev.
                .build();
    }

    /**
     * Creates a blocking stub for customer service gRPC calls.
     */
    @Bean
    public CustomerServiceGrpc.CustomerServiceBlockingStub customerServiceStub(ManagedChannel customerServiceChannel) {
        return CustomerServiceGrpc.newBlockingStub(customerServiceChannel);
    }
} 