package com.foodsquad.FoodSquad.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodsquad.FoodSquad.model.entity.*;
import com.foodsquad.FoodSquad.repository.MenuItemRepository;
import com.foodsquad.FoodSquad.repository.OrderRepository;
import com.foodsquad.FoodSquad.repository.ReviewRepository;
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
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

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
    private ReviewRepository reviewRepository;

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
        if (reviewRepository.count() == 0) {
            seedReviews();
        } else {
            System.out.println("Reviews already exist in the database, skipping review seeding.");
        }
    }

    private void seedUsers() {
        List<User> users = List.of(
                createUser("John Doe", "john.doe@example.com", "123123", "+359 899 78 7878", UserRole.NORMAL),
                createUser("Jane Smith", "jane.smith@example.com", "123123", "+359 899 78 7878", UserRole.NORMAL),
                createUser("Admin User", "admin@example.com", "123123", "+359 899 78 7878", UserRole.ADMIN),
                createUser("Moderator User", "moderator@example.com", "123123", "+359 899 78 7878", UserRole.MODERATOR)
        );

        userRepository.saveAll(users);
        System.out.println("Users seeded successfully.");
    }

//    private void seedMenuItems() {
//        try {
//            ObjectMapper objectMapper = new ObjectMapper();
//            ClassPathResource resource = new ClassPathResource("menu_batch.json");
//            InputStream inputStream = resource.getInputStream();
//
//
//            // Check if the file exists
//            if (!resource.exists()) {
//                System.out.println("File not found: menu_batch.json");
//                return;
//            }
//
//            // Read the JSON array from the file
//            JsonNode rootNode = objectMapper.readTree(inputStream);
//            List<MenuItem> menuItems = new ArrayList<>();
//
//            // Retrieve users
//            User adminUser = userRepository.findByEmail("admin@example.com").orElse(null);
//            User moderator = userRepository.findByEmail("moderator@example.com").orElse(null);
//
//            if (adminUser == null || moderator == null) {
//                System.out.println("Admin or Moderator user not found. Cannot seed menu items.");
//                return;
//            }
//
//            // Iterate over each element in the JSON array
//            for (JsonNode node : rootNode) {
//                String title = node.get("title").asText();
//                String description = node.get("description").asText();
//                String imageUrl = node.get("imageUrl").asText();
//                double price = node.get("price").asDouble();
//                boolean defaultItem = node.get("defaultItem").asBoolean();
//                String categoryText = node.get("category").asText();
//                String creatorField = node.get("creator").asText();
//
//                MenuItemCategory category;
//                try {
//                    category = MenuItemCategory.valueOf(categoryText.toUpperCase());
//                } catch (IllegalArgumentException e) {
//                    System.out.println("Invalid category: " + categoryText);
//                    continue; // Skip this item if category is invalid
//                }
//
//                User creator = creatorField.equalsIgnoreCase("adminUser") ? adminUser : moderator;
//
//                MenuItem menuItem = createMenuItem(title, description, imageUrl, price,defaultItem, category, creator);
//                menuItems.add(menuItem);
//            }
//
//            // Save all menu items to the repository
//            menuItemRepository.saveAll(menuItems);
//            System.out.println("Menu items seeded successfully.");
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

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

                MenuItem menuItem = createMenuItem(title, description, imageUrl, price, defaultItem, category, creator);
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
        List<Order> orders = new ArrayList<>();

        // Example data: Creating orders with quantities for seeding
        Map<Long, Integer> order1Items = new HashMap<>();
        order1Items.put(7L, 5); // 5 of MenuItem with ID 7
        order1Items.put(1L, 4); // 2 of MenuItem with ID 1

        Map<Long, Integer> order2Items = new HashMap<>();
        order2Items.put(9L, 3); // 3 of MenuItem with ID 9
        order2Items.put(18L, 21); // 21 of MenuItem with ID 1

        Map<Long, Integer> order3Items = new HashMap<>();
        order3Items.put(4L, 1); // 1 of MenuItem with ID 4
        order3Items.put(18L, 3); // 3 of MenuItem with ID 18

        orders.add(createOrder(OrderStatus.COMPLETED, LocalDateTime.now().minusDays(5), userRepository.findByEmail("moderator@example.com").get(), true, order1Items));
        orders.add(createOrder(OrderStatus.PENDING, LocalDateTime.now().minusDays(3), userRepository.findByEmail("jane.smith@example.com").get(), false, order2Items));
        orders.add(createOrder(OrderStatus.CANCELLED, LocalDateTime.now().minusDays(3), userRepository.findByEmail("admin@example.com").get(), false, order3Items));
        orders.add(createOrder(OrderStatus.CANCELLED, LocalDateTime.now().minusDays(3), userRepository.findByEmail("john.doe@example.com").get(), false, order3Items));

        orderRepository.saveAll(orders);
        System.out.println("Orders seeded successfully.");
    }

    private User createUser(String name, String email, String password, String phoneNumber, UserRole role) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setPhoneNumber(phoneNumber);
        user.setRole(role);
        return user;
    }

    private MenuItem createMenuItem(String title, String description, String imageUrl, double price, boolean defaultItem, MenuItemCategory category, User user) {
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

    private Order createOrder(OrderStatus status, LocalDateTime createdOn, User user, boolean paid, Map<Long, Integer> menuItemQuantities) {
        Order order = new Order();
        order.setStatus(status);
        order.setCreatedOn(createdOn);
        order.setUser(user);
        order.setPaid(paid);

        Map<MenuItem, Integer> menuItemsWithQuantity = new HashMap<>();
        for (Map.Entry<Long, Integer> entry : menuItemQuantities.entrySet()) {
            MenuItem menuItem = menuItemRepository.findById(entry.getKey()).orElseThrow(() -> new IllegalArgumentException("Invalid menu item ID"));
            menuItemsWithQuantity.put(menuItem, entry.getValue());
        }

        order.setMenuItemsWithQuantity(menuItemsWithQuantity);
        double totalCost = menuItemsWithQuantity.entrySet().stream()
                .mapToDouble(entry -> entry.getKey().getPrice().doubleValue() * entry.getValue())
                .sum();
        // Round the totalCost to 2 decimal places
        BigDecimal roundedTotalCost = BigDecimal.valueOf(totalCost).setScale(2, RoundingMode.HALF_UP);
        order.setTotalCost(roundedTotalCost.doubleValue());
        return order;
    }

    private void seedReviews() {
        List<MenuItem> menuItems = menuItemRepository.findAll();
        List<User> users = userRepository.findAll();
        Random random = new Random();

        List<Review> reviews = new ArrayList<>();
        String[] comments = {"Great taste!", "Could be better.", "I loved it!", "Not my favorite.", "Amazing quality!"};

        for (MenuItem menuItem : menuItems) {
            for (int i = 0; i < 3; i++) {  // Each item gets 3 reviews
                User user = users.get(random.nextInt(users.size())); // Random user
                String comment = comments[random.nextInt(comments.length)]; // Random comment
                int rating = random.nextInt(5) + 1; // Rating between 1 and 5

                Review review = new Review();
                review.setMenuItem(menuItem);
                review.setUser(user);
                review.setComment(comment);
                review.setRating(rating);
                review.setCreatedOn(LocalDateTime.now().minusDays(random.nextInt(30)));

                reviews.add(review);
            }
        }

        reviewRepository.saveAll(reviews);
        System.out.println("Reviews seeded successfully.");
    }

}
