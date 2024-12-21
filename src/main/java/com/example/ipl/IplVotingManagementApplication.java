package com.example.ipl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.example.ipl.repositories")
@EntityScan(basePackages = "com.example.ipl.model")
@ComponentScan(basePackages = "com.example.ipl")
public class IplVotingManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(IplVotingManagementApplication.class, args);
	}

}
