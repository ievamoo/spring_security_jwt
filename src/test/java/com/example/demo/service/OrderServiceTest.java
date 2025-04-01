package com.example.demo.service;

import com.example.demo.dto.OrderDto;
import com.example.demo.dto.OrderItemDto;
import com.example.demo.model.*;
import com.example.demo.repository.CarPartRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    @Mock private OrderRepository orderRepository;
    @Mock private UserRepository userRepository;
    @Mock private CarPartRepository carPartRepository;
    @Mock private CarPartService carPartService;

    @InjectMocks
    private OrderService orderService;

    private User testUser;
    private CarPart testPart;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        MockitoAnnotations.openMocks(this);

        testUser = User.builder()
                .id(1L)
                .username("john")
                .firstName("John")
                .lastName("Doe")
                .build();

        testPart = CarPart.builder()
                .name("Brake Pad")
                .stock(10)
                .price(50.0)
                .build();

        Field idField = CarPart.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(testPart, 10L);
    }

    @Test
    void placeOrder_ShouldReturnOrderResponse_WhenValidRequest() {
        OrderDto orderDto = new OrderDto(List.of(
                OrderItemDto.builder().carPartId(10L).quantity(2).build()
        ));

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(testUser));
        when(carPartService.getById(10L)).thenReturn(testPart);
        when(carPartRepository.save(any())).thenReturn(testPart);

        Order savedOrder = Order.builder()
                .id(100L)
                .user(testUser)
                .date(LocalDate.now())
                .items(new ArrayList<>())
                .build();
        savedOrder.setItems(List.of(OrderItem.builder()
                .carPart(testPart)
                .order(savedOrder)
                .quantity(2)
                .build()));
        savedOrder.calculateTotal();

        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        var response = orderService.placeOrder("john", orderDto);

        assertNotNull(response);
        assertEquals(1, response.getItems().size());
        assertEquals(100.0, response.getTotal(), 0.001);
        assertEquals(100L, response.getId());
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void placeOrder_ShouldThrowException_WhenInsufficientStock() {
        OrderDto orderDto = new OrderDto(List.of(
                OrderItemDto.builder().carPartId(10L).quantity(99).build()
        ));
        testPart.setStock(5);

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(testUser));
        when(carPartService.getById(10L)).thenReturn(testPart);

        assertThrows(IllegalArgumentException.class, () ->
                orderService.placeOrder("john", orderDto));
    }

    @Test
    void placeOrder_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () ->
                orderService.placeOrder("ghost", new OrderDto(List.of())));
    }

    @Test
    void getOrdersByUsername_ShouldReturnOrderResponses() {
        Order order = Order.builder()
                .id(1L)
                .user(testUser)
                .date(LocalDate.now())
                .items(List.of(OrderItem.builder()
                        .carPart(testPart)
                        .quantity(1)
                        .build()))
                .build();
        order.calculateTotal();

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(testUser));
        when(orderRepository.findByUser(testUser)).thenReturn(List.of(order));

        var responses = orderService.getOrdersByUsername("john");

        assertEquals(1, responses.size());
        assertEquals(1L, responses.get(0).getId());
        assertEquals(50.0, responses.get(0).getTotal(), 0.001);
    }

    @Test
    void getOrdersByUsername_ShouldThrow_WhenUserNotFound() {
        when(userRepository.findByUsername("nobody")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () ->
                orderService.getOrdersByUsername("nobody"));
    }

    @Test
    void getAllOrders_ShouldReturnAllOrderResponses() {
        Order order = Order.builder()
                .id(5L)
                .user(testUser)
                .date(LocalDate.now())
                .items(List.of(OrderItem.builder()
                        .carPart(testPart)
                        .quantity(3)
                        .build()))
                .build();
        order.calculateTotal();

        when(orderRepository.findAll()).thenReturn(List.of(order));

        var result = orderService.getAllOrders();

        assertEquals(1, result.size());
        assertEquals("John Doe", result.get(0).getUserFullName());
        assertEquals(5L, result.get(0).getId());
        assertEquals(150.0, result.get(0).getTotal(), 0.001);
    }
}
