package com.example.demo.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

        private final OrderRepository orderRepository;
        private final UserRepository userRepository;
        private final ItemRepository itemRepository;

        @Override
        public OrderResponse placeOrder(String username, OrderRequest orderRequest) {
                User user = userRepository.findByUsername(username)
                                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

                List<OrderItem> orderItems = orderRequest.getItems().stream()
                                .map(orderItemRequest -> {
                                        Item item = itemRepository.findById(orderItemRequest.getItemId())
                                                        .orElseThrow(() -> new ResourceNotFoundException(
                                                                        "Item not found with id " + orderItemRequest
                                                                                        .getItemId()));

                                        if (item.getQuantity() < orderItemRequest.getQuantity()) {
                                                throw new IllegalArgumentException(
                                                                "Insufficient quantity for item: " + item.getName());
                                        }

                                        item.setQuantity(item.getQuantity() - orderItemRequest.getQuantity());
                                        itemRepository.save(item);

                                        return OrderItem.builder()
                                                        .item(item)
                                                        .quantity(orderItemRequest.getQuantity())
                                                        .build();
                                }).collect(Collectors.toList());

                double totalPrice = orderItems.stream()
                                .mapToDouble(oi -> oi.getQuantity() * oi.getItem().getPrice())
                                .sum();

                Order order = Order.builder()
                                .user(user)
                                .items(orderItems)
                                .totalPrice(totalPrice)
                                .timestamp(LocalDateTime.now())
                                .build();

                orderItems.forEach(oi -> oi.setOrder(order));

                return mapToResponse(order);
        }

        @Override
        public List<OrderResponse> getOrdersByUser(String username) {
                User user = userRepository.findByUsername(username)
                                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
                List<Order> orders = orderRepository.findByUser(user);

                return orders.stream().map(this::mapToResponse).collect(Collectors.toList());
        }

        @Override
        public List<OrderResponse> getAllOrders() {
                return orderRepository.findAll().stream()
                                .map(this::mapToResponse)
                                .collect(Collectors.toList());
        }

        private OrderResponse mapToResponse(Order order) {
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
