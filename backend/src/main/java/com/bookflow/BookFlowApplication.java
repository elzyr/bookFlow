package com.bookflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class BookFlowApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookFlowApplication.class, args);
	}

}
