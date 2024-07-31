package com.foodsquad.FoodSquad.repository;

import com.foodsquad.FoodSquad.model.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, String> {
    @Query("SELECT COUNT(o) FROM Order o JOIN o.menuItems mi WHERE mi.id = :menuItemId")
    int countByMenuItemId(@Param("menuItemId") Long menuItemId);

    @Query("SELECT o FROM Order o JOIN o.user u")
    Page<Order> findAllOrdersWithUsers(Pageable pageable);

    @Query("SELECT o FROM Order o JOIN o.user u WHERE o.id = :orderId")
    Optional<Order> findOrderWithUserById(@Param("orderId") String orderId);

    @Query("SELECT o FROM Order o JOIN o.user u WHERE u.id = :userId")
    Page<Order> findOrdersByUserId(@Param("userId") String userId, Pageable pageable);
}