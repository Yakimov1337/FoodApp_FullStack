package com.foodsquad.FoodSquad.repository;

import com.foodsquad.FoodSquad.model.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
}