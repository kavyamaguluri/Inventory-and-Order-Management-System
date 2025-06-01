package com.example.demo.service;

import org.springframework.stereotype.Service;

import com.example.demo.dto.AuthRequest;
import com.example.demo.dto.AuthResponse;
import com.example.demo.dto.RegisterRequest;

@Service
public interface AuthService {
    AuthResponse authenticate(AuthRequest request);

    void register(RegisterRequest request);
}
