package com.foodsquad.FoodSquad;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FoodSquadApplication {

	public static void main(String[] args) {
		SpringApplication.run(FoodSquadApplication.class, args);
	}

}
