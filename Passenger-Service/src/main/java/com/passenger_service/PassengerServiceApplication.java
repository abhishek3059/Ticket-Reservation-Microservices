package com.passenger_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
public class PassengerServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PassengerServiceApplication.class, args);
	}
	@Bean
	@LoadBalanced
	public WebClient.Builder webClient(){
		return WebClient.builder();
	}
}
