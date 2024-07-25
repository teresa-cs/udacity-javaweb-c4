package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OrderControllerTest {

    @InjectMocks
    private OrderController orderController;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CartRepository cartRepository;

    private User user;
    private Cart cart;
    private UserOrder userOrder;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // Setup test data
        user = new User();
        user.setUsername("testUser");
        cart = new Cart();
        user.setCart(cart);

        userOrder = new UserOrder();
    }

    @Test
    public void testSubmit_Success() {
        when(userRepository.findByUsername("testUser")).thenReturn(user);
        when(orderRepository.save(any(UserOrder.class))).thenReturn(userOrder);
        ResponseEntity<UserOrder> response = orderController.submit("testUser");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(userOrder, response.getBody());
        verify(orderRepository, times(1)).save(any(UserOrder.class));
    }

    @Test
    public void testSubmit_UserNotFound() {
        when(userRepository.findByUsername("testUser")).thenReturn(null);

        ResponseEntity<UserOrder> response = orderController.submit("testUser");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(orderRepository, never()).save(any(UserOrder.class));
    }

    @Test
    public void testGetOrdersForUser_Success() {
        List<UserOrder> orders = Arrays.asList(userOrder);
        when(userRepository.findByUsername("testUser")).thenReturn(user);
        when(orderRepository.findByUser(user)).thenReturn(orders);

        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("testUser");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(orders, response.getBody());
    }

    @Test
    public void testGetOrdersForUser_UserNotFound() {
        when(userRepository.findByUsername("testUser")).thenReturn(null);

        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("testUser");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }
}
