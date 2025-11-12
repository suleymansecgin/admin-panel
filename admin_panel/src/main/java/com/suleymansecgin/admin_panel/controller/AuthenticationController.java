package com.suleymansecgin.admin_panel.controller;

import com.suleymansecgin.admin_panel.dto.AuthRequest;
import com.suleymansecgin.admin_panel.dto.AuthResponse;
import com.suleymansecgin.admin_panel.dto.RegisterRequest;
import com.suleymansecgin.admin_panel.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    
    private final AuthenticationService authenticationService;
    
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authenticationService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        AuthResponse response = authenticationService.login(request);
        return ResponseEntity.ok(response);
    }
}

