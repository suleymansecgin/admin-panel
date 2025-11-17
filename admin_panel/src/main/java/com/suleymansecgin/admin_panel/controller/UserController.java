package com.suleymansecgin.admin_panel.controller;

import com.suleymansecgin.admin_panel.dto.*;
import com.suleymansecgin.admin_panel.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@GetMapping
	@PreAuthorize("hasAuthority('USER_CREATE')")
	public ResponseEntity<List<UserResponse>> getAllUsers() {
		List<UserResponse> users = userService.getAllUsers();
		return ResponseEntity.ok(users);
	}
	
	@GetMapping("/{id}")
	@PreAuthorize("hasAuthority('USER_CREATE')")
	public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
		UserResponse user = userService.getUserById(id);
		return ResponseEntity.ok(user);
	}
	
	@PutMapping("/{id}/roles")
	@PreAuthorize("hasAuthority('USER_CREATE')")
	public ResponseEntity<UserResponse> assignRolesToUser(
			@PathVariable Long id,
			@Valid @RequestBody UserRoleRequest request) {
		UserResponse user = userService.assignRolesToUser(id, request);
		return ResponseEntity.ok(user);
	}
	
	@PutMapping("/{id}/status")
	@PreAuthorize("hasAuthority('USER_APPROVE')")
	public ResponseEntity<UserResponse> updateUserStatus(
			@PathVariable Long id,
			@RequestParam Boolean enabled) {
		UserResponse user = userService.updateUserStatus(id, enabled);
		return ResponseEntity.ok(user);
	}
}

