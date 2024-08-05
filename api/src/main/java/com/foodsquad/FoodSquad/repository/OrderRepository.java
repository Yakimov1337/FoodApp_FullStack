package com.foodsquad.FoodSquad.repository;

import com.foodsquad.FoodSquad.model.entity.Order;
import com.foodsquad.FoodSquad.model.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, String> {

    /**
     * Sums the quantities of a specific menu item across all orders.
     *
     * @param menuItemId The ID of the menu item.
     * @return The total quantity of the menu item.
     */
    @Query(value = "SELECT SUM(quantity) FROM order_menu_item WHERE menu_item_id = :menuItemId", nativeQuery = true)
    Integer sumQuantityByMenuItemId(@Param("menuItemId") Long menuItemId);

    /**
     * Finds all orders with their associated users and returns them in a pageable format.
     *
     * @param pageable The pageable object containing pagination information.
     * @return A page of orders with their associated users.
     */
    @Query("SELECT o FROM Order o JOIN o.user u")
    Page<Order> findAllOrdersWithUsers(Pageable pageable);

    /**
     * Finds an order by its ID, including the associated user.
     *
     * @param orderId The ID of the order.
     * @return An optional order with the associated user.
     */
    @Query("SELECT o FROM Order o JOIN o.user u WHERE o.id = :orderId")
    Optional<Order> findOrderWithUserById(@Param("orderId") String orderId);

    /**
     * Finds orders by the user ID and returns them in a pageable format.
     *
     * @param userId The ID of the user.
     * @param pageable The pageable object containing pagination information.
     * @return A page of orders for the specified user.
     */
    @Query("SELECT o FROM Order o JOIN o.user u WHERE u.id = :userId")
    Page<Order> findOrdersByUserId(@Param("userId") String userId, Pageable pageable);

    /**
     * Removes references to a menu item from the order_menu_item table.
     *
     * @param menuItemId The ID of the menu item to remove references to.
     */
    @Modifying
    @Query(value = "DELETE FROM order_menu_item WHERE menu_item_id = :menuItemId", nativeQuery = true)
    void removeMenuItemReferences(@Param("menuItemId") Long menuItemId);


    /**
     * Counts the number of orders for a specific user.
     *
     * @param userId The ID of the user.
     * @return The number of orders for the user.
     */
    long countByUserId(String userId);

    /**
     * Finds orders created before a specific date and with a specific status.
     *
     * @param createdOn The date before which the orders were created.
     * @param status The status of the orders.
     * @return A list of orders matching the criteria.
     */
    List<Order> findByCreatedOnBeforeAndStatus(LocalDateTime createdOn, OrderStatus status);
}
