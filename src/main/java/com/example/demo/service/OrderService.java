package com.example.demo.service;

import com.example.demo.dto.OrderDto;
import com.example.demo.dto.OrderItemDto;
import com.example.demo.dto.OrderResponse;
import com.example.demo.model.Order;
import com.example.demo.model.OrderItem;
import com.example.demo.repository.CarPartRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CarPartRepository carPartRepository;
    private final CarPartService carPartService;

    public OrderResponse placeOrder(String username, OrderDto orderRequest) {
        var matchingUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        Order order = Order.builder()
                .date(LocalDate.now())
                .user(matchingUser)
                .build();


        List<OrderItem> items = orderRequest.getItems().stream()
                .map(orderItemDto -> {
                    var carPart = carPartService.getById(orderItemDto.getCarPartId());
                    if (carPart.getStock() < orderItemDto.getQuantity()) {
                        throw new IllegalArgumentException("Insufficient stock for part: " + carPart.getName());
                    }
                    carPart.setStock(carPart.getStock() - orderItemDto.getQuantity());
                    carPartRepository.save(carPart);

                    return OrderItem.builder()
                            .carPart(carPart)
                            .quantity(orderItemDto.getQuantity())
                            .order(order)
                            .build();
                })
                .toList();

        order.setItems(items);
        order.calculateTotal();


        var savedOrder = orderRepository.save(order);

        return OrderResponse.builder()
                .id(savedOrder.getId())
                .items(savedOrder.getItems().stream()
                        .map(orderItem -> OrderItemDto.builder()
                                .carPartId(orderItem.getCarPart().getId())
                                .quantity(orderItem.getQuantity())
                                .carPartName(orderItem.getCarPart().getName())
                                .price(orderItem.getCarPart().getPrice())
                                .build()).toList())
                .date(savedOrder.getDate())
                .total(savedOrder.getTotal())
                .build();
    }

    public List<OrderResponse> getOrdersByUsername(String username) {
        var matchingUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        return orderRepository.findByUser(matchingUser).stream()
                .map(order ->
                        OrderResponse.builder()
                                .id(order.getId())
                                .total(order.getTotal())
                                .date(order.getDate())
                                .items(order.getItems().stream().map(
                                        orderItem ->
                                                OrderItemDto.builder()
                                                        .carPartId(orderItem.getCarPart().getId())
                                                        .carPartName(orderItem.getCarPart().getName())
                                                        .price(orderItem.getCarPart().getPrice())
                                                        .quantity(orderItem.getQuantity())
                                                        .build()).toList()
                                )
                                .build()
                ).toList();
    }
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(order ->
                        OrderResponse.builder()
                                .id(order.getId())
                                .total(order.getTotal())
                                .date(order.getDate())
                                .userFullName(order.getUser().getFirstName() + " " + order.getUser().getLastName())
                                .items(order.getItems().stream().map(
                                        orderItem ->
                                                OrderItemDto.builder()
                                                        .carPartId(orderItem.getCarPart().getId())
                                                        .carPartName(orderItem.getCarPart().getName())
                                                        .quantity(orderItem.getQuantity())
                                                        .build()
                                ).toList())
                                .build()
                ).toList();
    }
}
