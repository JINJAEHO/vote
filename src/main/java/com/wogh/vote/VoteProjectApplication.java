package com.wogh.vote;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class VoteProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(VoteProjectApplication.class, args);
	}

}
