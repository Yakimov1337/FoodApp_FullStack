package com.foodsquad.FoodSquad.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodsquad.FoodSquad.model.entity.*;
import com.foodsquad.FoodSquad.repository.MenuItemRepository;
import com.foodsquad.FoodSquad.repository.OrderRepository;
import com.foodsquad.FoodSquad.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class DatabaseSeeder {
    private final ResourceLoader resourceLoader;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    public DatabaseSeeder(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @PostConstruct
    public void seedDatabase() {
        // Check if the database is empty before seeding
        if (userRepository.count() == 0) {
            seedUsers();
        } else {
            System.out.println("Users already exist in the database, skipping user seeding.");
        }

        if (menuItemRepository.count() == 0) {
            seedMenuItems();
        } else {
            System.out.println("MenuItems already exist in the database, skipping menu item seeding.");
        }

        if (orderRepository.count() == 0) {
            seedOrders();
        } else {
            System.out.println("Orders already exist in the database, skipping order seeding.");
        }
    }

    private void seedUsers() {
        List<User> users = List.of(
                createUser("John Doe", "john.doe@example.com", "123123", UserRole.NORMAL),
                createUser("Jane Smith", "jane.smith@example.com", "123123", UserRole.NORMAL),
                createUser("Admin User", "admin@example.com", "123123", UserRole.ADMIN),
                createUser("Moderator User", "moderator@example.com", "123123", UserRole.MODERATOR)
        );

        userRepository.saveAll(users);
        System.out.println("Users seeded successfully.");
    }

    private void seedMenuItems() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ClassPathResource resource = new ClassPathResource("menu_batch.json");
            InputStream inputStream = resource.getInputStream();


            // Check if the file exists
            if (!resource.exists()) {
                System.out.println("File not found: menu_batch.json");
                return;
            }

            // Read the JSON array from the file
            JsonNode rootNode = objectMapper.readTree(inputStream);
            List<MenuItem> menuItems = new ArrayList<>();

            // Retrieve users
            User adminUser = userRepository.findByEmail("admin@example.com").orElse(null);
            User moderator = userRepository.findByEmail("moderator@example.com").orElse(null);

            if (adminUser == null || moderator == null) {
                System.out.println("Admin or Moderator user not found. Cannot seed menu items.");
                return;
            }

            // Iterate over each element in the JSON array
            for (JsonNode node : rootNode) {
                String title = node.get("title").asText();
                String description = node.get("description").asText();
                String imageUrl = node.get("imageUrl").asText();
                double price = node.get("price").asDouble();
                boolean defaultItem = node.get("defaultItem").asBoolean();
                String categoryText = node.get("category").asText();
                String creatorField = node.get("creator").asText();

                MenuItemCategory category;
                try {
                    category = MenuItemCategory.valueOf(categoryText.toUpperCase());
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid category: " + categoryText);
                    continue; // Skip this item if category is invalid
                }

                User creator = creatorField.equalsIgnoreCase("adminUser") ? adminUser : moderator;

                MenuItem menuItem = createMenuItem(title, description, imageUrl, price,defaultItem, category, creator);
                menuItems.add(menuItem);
            }

            // Save all menu items to the repository
            menuItemRepository.saveAll(menuItems);
            System.out.println("Menu items seeded successfully.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void seedOrders() {
        List<MenuItem> allMenuItems = menuItemRepository.findAll();
        List<Order> orders = List.of(
                createOrder(5000, OrderStatus.COMPLETED, LocalDateTime.now().minusDays(5), userRepository.findByEmail("moderator@example.com").get(), true, allMenuItems.subList(0, 3)),
                createOrder(3000, OrderStatus.PENDING, LocalDateTime.now().minusDays(3), userRepository.findByEmail("jane.smith@example.com").get(), false, allMenuItems.subList(3, 6)),
                createOrder(1200, OrderStatus.CANCELLED, LocalDateTime.now().minusDays(10), userRepository.findByEmail("admin@example.com").get(), false, allMenuItems.subList(6, 9))
        );

        orderRepository.saveAll(orders);
        System.out.println("Orders seeded successfully.");
    }

    private User createUser(String name, String email, String password, UserRole role) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        return user;
    }

    private MenuItem createMenuItem(String title, String description, String imageUrl, double price,boolean defaultItem, MenuItemCategory category, User user) {
        MenuItem menuItem = new MenuItem();
        menuItem.setTitle(title);
        menuItem.setDescription(description);
        menuItem.setImageUrl(imageUrl);
        menuItem.setPrice(price);
        menuItem.setCategory(category);
        menuItem.setUser(user);
        menuItem.setDefaultItem(defaultItem);
        return menuItem;
    }

    private Order createOrder(int totalCost, OrderStatus status, LocalDateTime createdOn, User user, boolean paid, List<MenuItem> menuItems) {
        Order order = new Order();
        order.setTotalCost(totalCost);
        order.setStatus(status);
        order.setCreatedOn(createdOn);
        order.setUser(user);
        order.setPaid(paid);
        order.setMenuItems(menuItems);
        return order;
    }
}
