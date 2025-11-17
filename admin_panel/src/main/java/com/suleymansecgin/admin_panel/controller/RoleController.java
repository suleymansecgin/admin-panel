package com.suleymansecgin.admin_panel.controller;

import com.suleymansecgin.admin_panel.dto.*;
import com.suleymansecgin.admin_panel.service.RoleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class RoleController {
	
	@Autowired
	private RoleService roleService;
	
	@GetMapping
	@PreAuthorize("hasAuthority('ROLE_MANAGE')")
	public ResponseEntity<List<RoleResponse>> getAllRoles() {
		List<RoleResponse> roles = roleService.getAllRoles();
		return ResponseEntity.ok(roles);
	}
	
	@GetMapping("/{id}")
	@PreAuthorize("hasAuthority('ROLE_MANAGE')")
	public ResponseEntity<RoleResponse> getRoleById(@PathVariable Long id) {
		RoleResponse role = roleService.getRoleById(id);
		return ResponseEntity.ok(role);
	}
	
	@GetMapping("/code/{code}")
	@PreAuthorize("hasAuthority('ROLE_MANAGE')")
	public ResponseEntity<RoleResponse> getRoleByCode(@PathVariable String code) {
		RoleResponse role = roleService.getRoleByCode(code);
		return ResponseEntity.ok(role);
	}
	
	@PostMapping
	@PreAuthorize("hasAuthority('ROLE_MANAGE')")
	public ResponseEntity<RoleResponse> createRole(@Valid @RequestBody RoleRequest request) {
		RoleResponse role = roleService.createRole(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(role);
	}
	
	@PutMapping("/{id}")
	@PreAuthorize("hasAuthority('ROLE_MANAGE')")
	public ResponseEntity<RoleResponse> updateRole(@PathVariable Long id, @Valid @RequestBody RoleRequest request) {
		RoleResponse role = roleService.updateRole(id, request);
		return ResponseEntity.ok(role);
	}
	
	@DeleteMapping("/{id}")
	@PreAuthorize("hasAuthority('ROLE_MANAGE')")
	public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
		roleService.deleteRole(id);
		return ResponseEntity.noContent().build();
	}
	
	@GetMapping("/permissions")
	@PreAuthorize("hasAuthority('ROLE_MANAGE')")
	public ResponseEntity<List<PermissionResponse>> getAllPermissions() {
		List<PermissionResponse> permissions = roleService.getAllPermissions();
		return ResponseEntity.ok(permissions);
	}
}

