package com.apiGateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class RouteConfig {
    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder){
        return builder.routes()
                .route("auth-service-public", r -> r
                        .path("/api/auth/login", "/api/auth/register","/api/auth/validate")
                        .uri("lb://USER-AUTHENTICATION"))
                .route("auth-service-protected", r -> r
                        .path("/api/auth/users/**")
                        .uri("lb://USER-AUTHENTICATION"))
                .build();
    }

    @Bean
    @Primary
    @LoadBalanced
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }
}
