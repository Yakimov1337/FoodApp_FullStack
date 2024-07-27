package com.foodsquad.FoodSquad.config;

import com.foodsquad.FoodSquad.model.entity.*;
import com.foodsquad.FoodSquad.repository.MenuItemRepository;
import com.foodsquad.FoodSquad.repository.OrderRepository;
import com.foodsquad.FoodSquad.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class DatabaseSeeder {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

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
        User adminUser = userRepository.findByEmail("admin@example.com").get();
        User moderator = userRepository.findByEmail("moderator@example.com").get();
        List<MenuItem> menuItems = List.of(
                createMenuItem("Gelato", "An Italian version of ice cream that's denser and has a more intense flavor, made with more milk and less cream than its American counterpart, available in a variety of flavors.", "https://emmaduckworthbakes.com/wp-content/uploads/2023/06/Chocolate-Gelato-Recipe.jpg", 10, MenuItemCategory.DESSERT, adminUser),
                createMenuItem("Chocolate Souffle", "A small chocolate cake with a gooey, molten center, served warm to contrast the soft cake exterior and the liquid chocolate inside.", "https://rud.ua/uploads/under_recipe/shokoladnyi-fondan-600x300_5f2d431d9f2d3.jpg", 9.99, MenuItemCategory.DESSERT, adminUser),
                createMenuItem("Cheesecake", "A smooth and creamy dessert with a dense, rich filling made of cream cheese, eggs, and sugar, on a crumbly graham cracker crust, often topped with fruit or chocolate.", "https://popmenucloud.com/cdn-cgi/image/width%3D1200%2Cheight%3D1200%2Cfit%3Dscale-down%2Cformat%3Dauto%2Cquality%3D60/hfdbjynv/350f06d0-db15-4a80-87e9-d6305569c047.jpg", 7.99, MenuItemCategory.DESSERT, moderator),
                createMenuItem("Crème Brûlée", "A French dessert featuring a rich custard base topped with a contrasting layer of hard caramel, cracked with a spoon for that satisfying first bite.", "https://www.onceuponachef.com/images/2023/02/Creme-brulee-1-1200x814.jpg", 8.99, MenuItemCategory.DESSERT, adminUser),
                createMenuItem("Chorizo Tacos", "Scrambled eggs with chorizo, bacon, or potatoes, topped with cheese and salsa, wrapped in a soft flour tortilla.", "https://www.badmanners.com/sites/default/files/styles/recipe_big/public/recipes/Poblano%20Breakfast%20Tacos_Edited.jpg", 9.99, MenuItemCategory.TACOS, moderator),
                createMenuItem("Birria Tacos", "Tender, stewed meat marinated in a rich and spicy sauce, served in a corn tortilla that's been dipped in the stew's fat and grilled until crispy. Often accompanied by a side of consommé for dipping.", "https://hips.hearstapps.com/hmg-prod/images/delish-202104-birriatacos-033-1619806490.jpg?crop=0.670xw:1.00xh;0,0&resize=1200:*", 16.99, MenuItemCategory.TACOS, moderator),
                createMenuItem("Vegetarian Tacos", "A mixture of sautéed vegetables (like bell peppers, onions, zucchini) and beans, topped with avocado and queso fresco, served in a corn tortilla.", "https://img.taste.com.au/J58ry7EH/w720-h480-cfill-q80/taste/2022/04/cuban-vegetarian-tacos-177724-1.jpg", 12, MenuItemCategory.TACOS, moderator),
                createMenuItem("Chicken Tinga Tacos", "Shredded chicken cooked in a spicy, smoky tomato sauce with onions and chipotle peppers, garnished with avocado slices and queso fresco.", "https://hips.hearstapps.com/hmg-prod/images/chicken-tinga-5-1579643696.jpg?crop=0.888888888888889xw:1xh;center,top&resize=1200:*", 13.99, MenuItemCategory.TACOS, adminUser),
                createMenuItem("Al Pastor Tacos", "Marinated pork shoulder that's been thinly sliced, cooked on a vertical rotisserie, and served with pineapple, onions, and cilantro.", "https://thestayathomechef.com/wp-content/uploads/2019/07/Tacos-al-Pastor-2-1.jpg", 13, MenuItemCategory.TACOS, moderator),
                createMenuItem("Carnitas Tacos", "Slow-cooked pork shoulder, pulled apart and then crisped up in the oven or pan, served with diced onions, cilantro, and a squeeze of lime.", "https://www.inspiredtaste.net/wp-content/uploads/2022/11/Carnitas-1200-1200x800.jpg", 11.99, MenuItemCategory.TACOS, adminUser)
        );

        menuItemRepository.saveAll(menuItems);
        System.out.println("MenuItems seeded successfully.");
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

    private MenuItem createMenuItem(String title, String description, String imageUrl, double price, MenuItemCategory category, User user) {
        MenuItem menuItem = new MenuItem();
        menuItem.setTitle(title);
        menuItem.setDescription(description);
        menuItem.setImageUrl(imageUrl);
        menuItem.setPrice(price);
        menuItem.setCategory(category);
        menuItem.setUser(user);
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
