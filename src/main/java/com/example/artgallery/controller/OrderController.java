package com.example.artgallery.controller;

import com.example.artgallery.dto.OrderDTO;
import com.example.artgallery.model.User;
import com.example.artgallery.repository.UserRepository;
import com.example.artgallery.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;
    private final UserRepository userRepository;

    public OrderController(OrderService orderService, UserRepository userRepository) {
        this.orderService = orderService;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody OrderDTO.CartItem[] cartItems) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        orderService.createOrder(cartItems, userEmail);
        return ResponseEntity.ok("Order placed successfully.");
    }

    @GetMapping("/my-history")
    public ResponseEntity<List<OrderDTO>> getMyOrderHistory() {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User buyer = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        List<OrderDTO> orders = orderService.getOrdersForBuyer(buyer.getId());
        return ResponseEntity.ok(orders);
    }
}