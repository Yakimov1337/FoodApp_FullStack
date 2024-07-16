package com.foodsquad.FoodSquad.config;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class StartupListener implements ApplicationListener<ApplicationReadyEvent> {

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        System.out.println("*************************************************************");
        System.out.println("Application has started successfully!");
        System.out.println("Access Swagger UI at: http://localhost:8080/swagger-ui.html");
        System.out.println("*************************************************************");
    }
}