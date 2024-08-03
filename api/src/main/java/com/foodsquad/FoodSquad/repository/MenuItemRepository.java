package com.foodsquad.FoodSquad.repository;

import com.foodsquad.FoodSquad.model.entity.MenuItem;
import com.foodsquad.FoodSquad.model.entity.MenuItemCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
    Page<MenuItem> findByCategory(MenuItemCategory category, Pageable pageable);
}