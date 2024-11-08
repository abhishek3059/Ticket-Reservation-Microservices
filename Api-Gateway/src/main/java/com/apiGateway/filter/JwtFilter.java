package com.apiGateway.filter;

import com.apiGateway.customException.BadCredentialsException;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class JwtFilter extends AbstractGatewayFilterFactory<JwtFilter.config> {
    private final WebClient webClient;

    public JwtFilter(WebClient.Builder webClientBuilder) {
        super(config.class);
        this.webClient = webClientBuilder
            .baseUrl("http://USER-AUTHENTICATION")
            .build();
    }

    @Override
    public GatewayFilter apply(config config) {
        return ((exchange, chain) -> {
            String path = exchange.getRequest().getPath().toString();

            // Skip JWT validation for public endpoints
            if (path.startsWith("/api/auth/login") || 
                path.startsWith("/api/auth/register") || 
                path.startsWith("/api/auth/validate")) {
                return chain.filter(exchange);
            }

            String token = exchange.getRequest().getHeaders().getFirst("Authorization");

            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                String finalToken = token;

                return webClient.get()
                        .uri("/api/auth/validate/" + finalToken)
                        .retrieve()
                        .onStatus(HttpStatusCode::is4xxClientError, response -> {
                            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                            return Mono.error(new BadCredentialsException("Not Authorized"));
                        })
                        .onStatus(HttpStatusCode::is5xxServerError, response -> {
                            exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
                            return Mono.error(new RuntimeException("Internal Server Error"));
                        })
                        .toBodilessEntity()
                        .flatMap(response -> chain.filter(exchange))
                        .onErrorResume(error -> {
                            System.out.println("Error occurred: " + error.getMessage());
                            exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
                            return exchange.getResponse().setComplete();
                        });
            } else {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
        });
    }

    public static class config {
    }
}
