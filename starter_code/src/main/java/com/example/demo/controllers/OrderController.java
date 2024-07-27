package com.example.demo.controllers;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    private static final Logger log = LogManager.getLogger(OrderController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @PostMapping("/submit/{username}")
    public ResponseEntity<UserOrder> submit(@PathVariable String username) {
        try {
            User user = userRepository.findByUsername(username);
            if (user == null) {
                log.warn("User with username '{}' not found", username);
                return ResponseEntity.notFound().build();
            }
            UserOrder order = UserOrder.createFromCart(user.getCart());
            order = orderRepository.save(order);
            log.info("Order successfully created for user '{}', order ID: {}", username, order.getId());
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            log.error("Error occurred while creating order for user '{}': {}", username, e.getMessage(), e);
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/history/{username}")
    public ResponseEntity<List<UserOrder>> getOrdersForUser(@PathVariable String username) {
        try {
            User user = userRepository.findByUsername(username);
            if (user == null) {
                log.warn("User with username '{}' not found", username);
                return ResponseEntity.notFound().build();
            }
            List<UserOrder> orders = orderRepository.findByUser(user);
            if (orders.isEmpty()) {
                log.info("No orders found for user '{}'", username);
            } else {
                log.info("Retrieved {} orders for user '{}'", orders.size(), username);
            }
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            log.error("Error occurred while retrieving orders for user '{}': {}", username, e.getMessage(), e);
            return ResponseEntity.status(500).build();
        }
    }
}
