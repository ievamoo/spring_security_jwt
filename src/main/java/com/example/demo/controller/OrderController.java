package com.example.demo.controller;

import com.example.demo.dto.OrderDto;
import com.example.demo.dto.OrderResponse;
import com.example.demo.model.Order;
import com.example.demo.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> placeOrder(Authentication authentication,
                                                    @RequestBody OrderDto orderRequest) {
        String username = authentication.getName();
         var savedOrder = orderService.placeOrder(username, orderRequest);
        return ResponseEntity.ok(savedOrder);
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getOrdersByUser(Authentication authentication) {
        var username = authentication.getName();
        var orders = orderService.getOrdersByUsername(username);
        return ResponseEntity.ok(orders);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }
}
