package com.example.demo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.OrderRequest;
import com.example.demo.dto.OrderResponse;
import com.example.demo.service.OrderService;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/place")
    public ResponseEntity<OrderResponse> placeOrder(@RequestBody OrderRequest orderRequest) {
        log.info("Received order request: {}", orderRequest);
        
        try {
            // Get the authenticated user's username from Security Context
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication == null || !authentication.isAuthenticated()) {
                log.error("No authenticated user found");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            String username = authentication.getName();
            log.info("Authenticated username: {}", username);
            
            if (username == null || username.equals("anonymousUser")) {
                log.error("Invalid or anonymous user");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            OrderResponse response = orderService.placeOrder(username, orderRequest);
            log.info("Order placed successfully for user: {}", username);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            log.error("Error placing order", e);
            throw e; // Let global exception handler deal with it
        }
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/my-orders")
    public ResponseEntity<List<OrderResponse>> getMyOrders() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            String username = authentication.getName();
            
            if (username == null || username.equals("anonymousUser")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            List<OrderResponse> orders = orderService.getOrdersByUser(username);
            return ResponseEntity.ok(orders);
            
        } catch (Exception e) {
            log.error("Error fetching user orders", e);
            throw e;
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        try {
            List<OrderResponse> orders = orderService.getAllOrders();
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            log.error("Error fetching all orders", e);
            throw e;
        }
    }
}

// Alternative approach if you prefer to inject User directly (requires custom resolver)
// @PostMapping("/place")
// public ResponseEntity<OrderResponse> placeOrder(@RequestBody OrderRequest orderRequest, 
//                                               @AuthenticationPrincipal UserDetails userDetails) {
//     String username = userDetails.getUsername();
//     OrderResponse response = orderService.placeOrder(username, orderRequest);
//     return ResponseEntity.status(HttpStatus.CREATED).body(response);
// }

// import lombok.RequiredArgsConstructor;
// import org.springframework.http.*;
// import org.springframework.security.access.prepost.PreAuthorize;
// import org.springframework.security.core.annotation.AuthenticationPrincipal;
// import org.springframework.web.bind.annotation.*;

// import com.example.demo.dto.OrderRequest;
// import com.example.demo.dto.OrderResponse;
// import com.example.demo.entity.User;
// import com.example.demo.service.OrderService;

// import java.util.List;

// @RestController
// @RequestMapping("/api/orders")
// @RequiredArgsConstructor
// public class OrderController {

//     private final OrderService orderService;

//     @PreAuthorize("hasRole('CUSTOMER')")
//     @PostMapping("/place")
//     public ResponseEntity<OrderResponse> placeOrder(
//             @RequestBody OrderRequest request,
//             @AuthenticationPrincipal User user) {
//         return new ResponseEntity<>(
//                 orderService.placeOrder(user.getUsername(), request),
//                 HttpStatus.CREATED);
//     }

//     @PreAuthorize("hasRole('CUSTOMER')")
//     @GetMapping("/my")
//     public ResponseEntity<List<OrderResponse>> getMyOrders(@AuthenticationPrincipal User user) {
//         return ResponseEntity.ok(orderService.getOrdersByUser(user.getUsername()));
//     }

//     @PreAuthorize("hasRole('ADMIN')")
//     @GetMapping
//     public ResponseEntity<List<OrderResponse>> getAllOrders() {
//         return ResponseEntity.ok(orderService.getAllOrders());
//     }
// }
