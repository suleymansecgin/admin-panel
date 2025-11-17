package com.suleymansecgin.admin_panel.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * RBAC kullanım örneği için örnek controller
 * Bu controller, farklı roller ve yetkiler için örnek endpoint'ler içerir
 */
@RestController
@RequestMapping("/api/example")
public class ExampleController {
	
	/**
	 * Sadece ADMIN rolüne sahip kullanıcılar erişebilir
	 */
	@GetMapping("/admin-only")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Map<String, String>> adminOnly() {
		Map<String, String> response = new HashMap<>();
		response.put("message", "Bu endpoint'e sadece ADMIN rolü erişebilir");
		return ResponseEntity.ok(response);
	}
	
	/**
	 * USER_CREATE yetkisine sahip kullanıcılar erişebilir
	 */
	@GetMapping("/user-create")
	@PreAuthorize("hasAuthority('USER_CREATE')")
	public ResponseEntity<Map<String, String>> userCreate() {
		Map<String, String> response = new HashMap<>();
		response.put("message", "Bu endpoint'e USER_CREATE yetkisi olan kullanıcılar erişebilir");
		return ResponseEntity.ok(response);
	}
	
	/**
	 * ACCOUNTING_READ yetkisine sahip kullanıcılar erişebilir
	 */
	@GetMapping("/accounting")
	@PreAuthorize("hasAuthority('ACCOUNTING_READ')")
	public ResponseEntity<Map<String, String>> accounting() {
		Map<String, String> response = new HashMap<>();
		response.put("message", "Bu endpoint'e ACCOUNTING_READ yetkisi olan kullanıcılar erişebilir");
		return ResponseEntity.ok(response);
	}
	
	/**
	 * WAREHOUSE_READ yetkisine sahip kullanıcılar erişebilir
	 */
	@GetMapping("/warehouse")
	@PreAuthorize("hasAuthority('WAREHOUSE_READ')")
	public ResponseEntity<Map<String, String>> warehouse() {
		Map<String, String> response = new HashMap<>();
		response.put("message", "Bu endpoint'e WAREHOUSE_READ yetkisi olan kullanıcılar erişebilir");
		return ResponseEntity.ok(response);
	}
	
	/**
	 * Herhangi bir authenticated kullanıcı erişebilir
	 */
	@GetMapping("/public")
	public ResponseEntity<Map<String, String>> publicEndpoint() {
		Map<String, String> response = new HashMap<>();
		response.put("message", "Bu endpoint'e authenticated tüm kullanıcılar erişebilir");
		return ResponseEntity.ok(response);
	}
}

