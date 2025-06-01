package com.example.demo.service;

import java.util.List;

import com.example.demo.dto.OrderRequest;
import com.example.demo.dto.OrderResponse;

public interface OrderService {
    OrderResponse placeOrder(String username, OrderRequest orderRequest);

    List<OrderResponse> getOrdersByUser(String username);

    List<OrderResponse> getAllOrders();
}
