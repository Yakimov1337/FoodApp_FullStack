package com.foodsquad.FoodSquad.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.foodsquad.FoodSquad.model.entity.Order;
import com.foodsquad.FoodSquad.model.entity.OrderStatus;
import com.foodsquad.FoodSquad.repository.OrderRepository;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class OrderStatusScheduler {

    @Autowired
    private OrderRepository orderRepository;

    @Scheduled(cron = "0 0 0 * * *") // Run at midnight every day
    // If there is an order older than a day and status is PENDING, set its status to CANCELLED
    public void cancelOldOrders() {
        LocalDateTime oneDayAgo = LocalDateTime.now().minusDays(1);
        List<Order> oldPendingOrders = orderRepository.findByCreatedOnBeforeAndStatus(oneDayAgo, OrderStatus.PENDING);

        for (Order order : oldPendingOrders) {
            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);
        }
        System.out.println("Checked and cancelled old pending orders");
    }
}
