package com.foodsquad.FoodSquad.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Food Squad API")
                        .version("1.0")
                        .description("API documentation for Food Squad application"))
                .addTagsItem(new Tag().name("1. Authentication").description("Authentication API"))
                .addTagsItem(new Tag().name("2. User Management").description("User Management API"))
                .addTagsItem(new Tag().name("3. Order Management").description("Order Management API"))
                .addTagsItem(new Tag().name("4. Menu Item Management").description("Menu Item Management API"));
    }
}
