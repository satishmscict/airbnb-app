package com.project.airbnb_app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AirbnbAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(AirbnbAppApplication.class, args);
	}

}
