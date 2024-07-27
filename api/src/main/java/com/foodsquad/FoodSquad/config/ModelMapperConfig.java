package com.foodsquad.FoodSquad.config;

import com.foodsquad.FoodSquad.model.dto.OrderDTO;
import com.foodsquad.FoodSquad.model.entity.Order;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.foodsquad.FoodSquad.model.entity.MenuItem;


import java.util.List;
import java.util.stream.Collectors;


@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.addMappings(new PropertyMap<Order, OrderDTO>() {
            @Override
            protected void configure() {
                using(ctx -> ((List<MenuItem>) ctx.getSource()).stream()
                        .map(MenuItem::getId)
                        .collect(Collectors.toList()))
                        .map(source.getMenuItems(), destination.getMenuItemIds());
            }
        });
        return modelMapper;
    }


}