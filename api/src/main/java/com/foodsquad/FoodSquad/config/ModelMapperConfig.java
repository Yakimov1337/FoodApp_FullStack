package com.foodsquad.FoodSquad.config;

import com.foodsquad.FoodSquad.model.dto.OrderDTO;
import com.foodsquad.FoodSquad.model.entity.MenuItem;
import com.foodsquad.FoodSquad.model.entity.Order;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.addMappings(new PropertyMap<Order, OrderDTO>() {
            @Override
            protected void configure() {
                using(ctx -> ((Map<MenuItem, Integer>) ctx.getSource()).entrySet().stream()
                        .collect(Collectors.toMap(e -> e.getKey().getId(), Map.Entry::getValue)))
                        .map(source.getMenuItemsWithQuantity(), destination.getMenuItemQuantities());
            }
        });
        return modelMapper;
    }
}
