package com.tcnw.ELH_task_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableDiscoveryClient
@EnableJpaRepositories
@SpringBootApplication
public class ElhTaskServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ElhTaskServiceApplication.class, args);
	}

}
