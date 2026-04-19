package com.example.ElDnevniko;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
@EnableScheduling
@SpringBootApplication
public class ElDnevnikoApplication {

	public static void main(String[] args) {
		SpringApplication.run(ElDnevnikoApplication.class, args);
	}

}
