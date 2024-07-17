package com.foodsquad.FoodSquad.repository;

import com.foodsquad.FoodSquad.model.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}