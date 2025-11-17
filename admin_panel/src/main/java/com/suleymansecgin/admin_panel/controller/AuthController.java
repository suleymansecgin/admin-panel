package com.suleymansecgin.admin_panel.controller;

import com.suleymansecgin.admin_panel.dto.*;
import com.suleymansecgin.admin_panel.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
	
	@Autowired
	private AuthService authService;
	
	@PostMapping("/login")
	public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
		AuthResponse response = authService.login(request);
		return ResponseEntity.ok(response);
	}
	
	@PostMapping("/register")
	public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
		AuthResponse response = authService.register(request);
		return ResponseEntity.ok(response);
	}
	
	@PostMapping("/refresh")
	public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
		AuthResponse response = authService.refreshToken(request);
		return ResponseEntity.ok(response);
	}
	
	@PostMapping("/logout")
	public ResponseEntity<Void> logout(@RequestBody RefreshTokenRequest request, Authentication authentication) {
		authService.logout(request.getRefreshToken());
		return ResponseEntity.ok().build();
	}
	
	@PostMapping("/mfa/setup")
	public ResponseEntity<MfaSetupResponse> setupMfa(Authentication authentication) {
		String username = authentication.getName();
		MfaSetupResponse response = authService.setupMfa(username);
		return ResponseEntity.ok(response);
	}
	
	@PostMapping("/mfa/enable")
	public ResponseEntity<Void> enableMfa(
			@Valid @RequestBody MfaVerifyRequest request,
			@RequestParam String secret,
			Authentication authentication) {
		String username = authentication.getName();
		authService.enableMfa(username, secret, request.getCode());
		return ResponseEntity.ok().build();
	}
	
	@PostMapping("/mfa/disable")
	public ResponseEntity<Void> disableMfa(
			@Valid @RequestBody MfaVerifyRequest request,
			Authentication authentication) {
		String username = authentication.getName();
		authService.disableMfa(username, request.getCode());
		return ResponseEntity.ok().build();
	}
}

