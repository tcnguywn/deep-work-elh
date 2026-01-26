package com.tcnw.ELH_time_tracking_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories
@EnableDiscoveryClient
@SpringBootApplication
public class ElhTimeTrackingServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ElhTimeTrackingServiceApplication.class, args);
	}

}
