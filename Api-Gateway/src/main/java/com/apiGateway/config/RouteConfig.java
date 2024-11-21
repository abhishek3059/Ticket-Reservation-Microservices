package com.apiGateway.config;

import com.apiGateway.filter.JwtFilter;
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
    public RouteLocator routeLocator(RouteLocatorBuilder builder, WebClient.Builder client){
        JwtFilter jwtFilter = new JwtFilter(client);
        return builder.routes()
                .route("auth-service-public", r -> r
                        .path("/api/auth/login", "/api/auth/register","/api/auth/validate")
                        .uri("lb://USER-AUTHENTICATION"))
                .route("auth-service-protected", r -> r
                        .path("/api/auth/users/**")
                        .filters(f->f.filter(jwtFilter.apply(new JwtFilter.config())))
                        .uri("lb://USER-AUTHENTICATION"))
                .route("location-service-public",r-> r
                        .path("/api/bookings/public/**")
                        .uri("lb://LOCATION-SERVICE"))
                .route("location-service-protected",r-> r
                        .path("/api/location/private/**")
                        .filters(f->f.filter(jwtFilter.apply(new JwtFilter.config())))
                        .uri("lb://LOCATION-SERVICE"))
                .route("train-service-public",r-> r
                        .path("/api/trains/public/**")
                        .uri("lb://TRAIN-SERVICE"))
                .route("train-service-protected",r-> r
                        .path("/api/trains/private/**")
                        .filters(f->f.filter(jwtFilter.apply(new JwtFilter.config())))
                        .uri("lb://TRAIN-SERVICE"))
                .route("passenger-service",r-> r
                        .path("/api/passenger/**")
                        .filters(f->f.filter(jwtFilter.apply(new JwtFilter.config())))
                        .uri("lb://PASSENGER-SERVICE"))
                .route("booking-service",r-> r
                        .path("/api/bookings/**")
                        .filters(f->f.filter(jwtFilter.apply(new JwtFilter.config())))
                        .uri("lb://BOOKING-SERVICE"))


                .build();
    }

    @Bean
    @Primary
    @LoadBalanced
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }


}
