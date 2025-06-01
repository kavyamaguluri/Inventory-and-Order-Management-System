package com.example.demo.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import com.example.demo.dto.OrderItemResponse;
import com.example.demo.dto.OrderRequest;
import com.example.demo.dto.OrderResponse;
import com.example.demo.entity.Item;
import com.example.demo.entity.Order;
import com.example.demo.entity.OrderItem;
import com.example.demo.entity.User;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.ItemRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public OrderResponse placeOrder(String username, OrderRequest orderRequest) {
        log.info("Starting placeOrder for username: {}", username);
        
        try {
            // Fetch user or throw error
            log.debug("Fetching user by username: {}", username);
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            log.debug("User found: {}", user.getUsername());

            // Create new Order instance
            log.debug("Creating new order");
            Order order = new Order();
            order.setUser(user);
            order.setTimestamp(LocalDateTime.now());
            order.setItems(new ArrayList<>());

            // Create OrderItems and establish bidirectional relationship
            log.debug("Processing {} order items", orderRequest.getItems().size());
            List<OrderItem> orderItems = new ArrayList<>();
            
            for (int i = 0; i < orderRequest.getItems().size(); i++) {
                var orderItemRequest = orderRequest.getItems().get(i);
                log.debug("Processing item {}: itemId={}, quantity={}", i, orderItemRequest.getItemId(), orderItemRequest.getQuantity());
                
                Item item = itemRepository.findById(orderItemRequest.getItemId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Item not found with id " + orderItemRequest.getItemId()));
                log.debug("Found item: {} with quantity: {}", item.getName(), item.getQuantity());

                if (item.getQuantity() < orderItemRequest.getQuantity()) {
                    log.error("Insufficient quantity for item: {}. Required: {}, Available: {}", 
                             item.getName(), orderItemRequest.getQuantity(), item.getQuantity());
                    throw new IllegalArgumentException(
                            "Insufficient quantity for item: " + item.getName());
                }

                // Deduct stock and save updated item
                log.debug("Updating item quantity from {} to {}", item.getQuantity(), 
                         item.getQuantity() - orderItemRequest.getQuantity());
                item.setQuantity(item.getQuantity() - orderItemRequest.getQuantity());
                itemRepository.save(item);
                log.debug("Item updated successfully");

                // Build OrderItem
                OrderItem orderItem = OrderItem.builder()
                        .item(item)
                        .quantity(orderItemRequest.getQuantity())
                        .order(order)
                        .build();
                
                orderItems.add(orderItem);
                log.debug("OrderItem created and added to list");
            }

            // Calculate total price
            log.debug("Calculating total price");
            double totalPrice = orderItems.stream()
                    .mapToDouble(oi -> oi.getQuantity() * oi.getItem().getPrice())
                    .sum();
            log.debug("Total price calculated: {}", totalPrice);

            // Set order items and total price
            order.setItems(orderItems);
            order.setTotalPrice(totalPrice);

            // Save the order (will cascade save OrderItems due to CascadeType.ALL)
            log.debug("Saving order to database");
            Order savedOrder = orderRepository.save(order);
            log.debug("Order saved successfully with ID: {}", savedOrder.getId());

            // Map saved order to response DTO and return
            log.debug("Mapping order to response DTO");
            OrderResponse response = mapToResponse(savedOrder);
            log.info("Order placed successfully for user: {} with order ID: {}", username, savedOrder.getId());
            
            return response;
            
        } catch (Exception e) {
            log.error("Error placing order for user: {}", username, e);
            throw e;
        }
    }

    @Override
    public List<OrderResponse> getOrdersByUser(String username) {
        log.info("Fetching orders for user: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        List<Order> orders = orderRepository.findByUser(user);
        
        return orders.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public List<OrderResponse> getAllOrders() {
        log.info("Fetching all orders");
        return orderRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private OrderResponse mapToResponse(Order order) {
        log.debug("Mapping order {} to response", order.getId());
        List<OrderItemResponse> items = order.getItems().stream()
                .map(oi -> new OrderItemResponse(
                        oi.getItem().getId(),
                        oi.getItem().getName(),
                        oi.getQuantity(),
                        oi.getItem().getPrice()))
                .collect(Collectors.toList());

        return new OrderResponse(
                order.getId(),
                items,
                order.getTotalPrice(),
                order.getTimestamp());
    }
}

// @Service
// @RequiredArgsConstructor
// @Transactional
// public class OrderServiceImpl implements OrderService {

//         private final OrderRepository orderRepository;
//         private final UserRepository userRepository;
//         private final ItemRepository itemRepository;

//         @Override
//         public OrderResponse placeOrder(String username, OrderRequest orderRequest) {
//                 // Fetch user or throw error
//                 User user = userRepository.findByUsername(username)
//                                 .orElseThrow(() -> new ResourceNotFoundException("User not found"));

//                 // Create new Order instance first (no builder here for clarity)
//                 Order order = new Order();
//                 order.setUser(user);
//                 order.setTimestamp(LocalDateTime.now());

//                 // Map OrderItemRequests to OrderItems, set order for each OrderItem
//                 List<OrderItem> orderItems = orderRequest.getItems().stream()
//                                 .map(orderItemRequest -> {
//                                         Item item = itemRepository.findById(orderItemRequest.getItemId())
//                                                         .orElseThrow(() -> new ResourceNotFoundException(
//                                                                         "Item not found with id " + orderItemRequest
//                                                                                         .getItemId()));

//                                         if (item.getQuantity() < orderItemRequest.getQuantity()) {
//                                                 throw new IllegalArgumentException(
//                                                                 "Insufficient quantity for item: " + item.getName());
//                                         }

//                                         // Deduct stock and save updated item
//                                         item.setQuantity(item.getQuantity() - orderItemRequest.getQuantity());
//                                         itemRepository.save(item);

//                                         // Build OrderItem and set reference to order
//                                         OrderItem orderItem = OrderItem.builder()
//                                                         .item(item)
//                                                         .quantity(orderItemRequest.getQuantity())
//                                                         .order(order) // Set order here, important!
//                                                         .build();

//                                         return orderItem;
//                                 }).collect(Collectors.toList());

//                 // Set order items and total price in order
//                 order.setItems(orderItems);

//                 double totalPrice = orderItems.stream()
//                                 .mapToDouble(oi -> oi.getQuantity() * oi.getItem().getPrice())
//                                 .sum();
//                 order.setTotalPrice(totalPrice);

//                 // Save the order (will cascade save OrderItems)
//                 Order savedOrder = orderRepository.save(order);

//                 // Map saved order to response DTO and return
//                 return mapToResponse(savedOrder);
//         }

//         @Override
//         public List<OrderResponse> getOrdersByUser(String username) {
//                 User user = userRepository.findByUsername(username)
//                                 .orElseThrow(() -> new ResourceNotFoundException("User not found"));
//                 List<Order> orders = orderRepository.findByUser(user);

//                 return orders.stream().map(this::mapToResponse).collect(Collectors.toList());
//         }

//         @Override
//         public List<OrderResponse> getAllOrders() {
//                 return orderRepository.findAll().stream()
//                                 .map(this::mapToResponse)
//                                 .collect(Collectors.toList());
//         }

//         private OrderResponse mapToResponse(Order order) {
//                 List<OrderItemResponse> items = order.getItems().stream()
//                                 .map(oi -> new OrderItemResponse(
//                                                 oi.getItem().getId(),
//                                                 oi.getItem().getName(),
//                                                 oi.getQuantity(),
//                                                 oi.getItem().getPrice()))
//                                 .collect(Collectors.toList());

//                 return new OrderResponse(
//                                 order.getId(),
//                                 items,
//                                 order.getTotalPrice(),
//                                 order.getTimestamp());
//         }
// }

// @Override
// public OrderResponse placeOrder(String username, OrderRequest orderRequest) {
// User user = userRepository.findByUsername(username)
// .orElseThrow(() -> new ResourceNotFoundException("User not found"));

// List<OrderItem> orderItems = orderRequest.getItems().stream()
// .map(orderItemRequest -> {
// Item item = itemRepository.findById(orderItemRequest.getItemId())
// .orElseThrow(() -> new ResourceNotFoundException(
// "Item not found with id " + orderItemRequest
// .getItemId()));

// if (item.getQuantity() < orderItemRequest.getQuantity()) {
// throw new IllegalArgumentException(
// "Insufficient quantity for item: " + item.getName());
// }

// item.setQuantity(item.getQuantity() - orderItemRequest.getQuantity());
// itemRepository.save(item);

// return OrderItem.builder()
// .item(item)
// .quantity(orderItemRequest.getQuantity())
// .build();
// }).collect(Collectors.toList());

// double totalPrice = orderItems.stream()
// .mapToDouble(oi -> oi.getQuantity() * oi.getItem().getPrice())
// .sum();

// Order order = Order.builder()
// .user(user)
// .items(orderItems)
// .totalPrice(totalPrice)
// .timestamp(LocalDateTime.now())
// .build();

// orderItems.forEach(oi -> oi.setOrder(order));

// return mapToResponse(order);
// }