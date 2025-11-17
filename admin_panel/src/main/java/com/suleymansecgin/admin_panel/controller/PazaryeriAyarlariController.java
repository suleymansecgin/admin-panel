package com.suleymansecgin.admin_panel.controller;

import com.suleymansecgin.admin_panel.dto.PazaryeriAyarlariRequest;
import com.suleymansecgin.admin_panel.dto.PazaryeriAyarlariResponse;
import com.suleymansecgin.admin_panel.service.PazaryeriAyarlariService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pazaryerleri")
public class PazaryeriAyarlariController {
	
	@Autowired
	private PazaryeriAyarlariService pazaryeriAyarlariService;
	
	@GetMapping
	@PreAuthorize("hasAuthority('MARKETPLACE_MANAGE')")
	public ResponseEntity<List<PazaryeriAyarlariResponse>> getAllPazaryerleri() {
		List<PazaryeriAyarlariResponse> pazaryerleri = pazaryeriAyarlariService.getAllPazaryerleri();
		return ResponseEntity.ok(pazaryerleri);
	}
	
	@GetMapping("/aktif")
	@PreAuthorize("hasAuthority('MARKETPLACE_MANAGE')")
	public ResponseEntity<List<PazaryeriAyarlariResponse>> getAktifPazaryerleri() {
		List<PazaryeriAyarlariResponse> pazaryerleri = pazaryeriAyarlariService.getAktifPazaryerleri();
		return ResponseEntity.ok(pazaryerleri);
	}
	
	@GetMapping("/{id}")
	@PreAuthorize("hasAuthority('MARKETPLACE_MANAGE')")
	public ResponseEntity<PazaryeriAyarlariResponse> getPazaryeriById(@PathVariable Long id) {
		PazaryeriAyarlariResponse pazaryeri = pazaryeriAyarlariService.getPazaryeriById(id);
		return ResponseEntity.ok(pazaryeri);
	}
	
	@PostMapping
	@PreAuthorize("hasAuthority('MARKETPLACE_MANAGE')")
	public ResponseEntity<PazaryeriAyarlariResponse> createPazaryeri(@Valid @RequestBody PazaryeriAyarlariRequest request) {
		PazaryeriAyarlariResponse pazaryeri = pazaryeriAyarlariService.createPazaryeri(request);
		return ResponseEntity.ok(pazaryeri);
	}
	
	@PutMapping("/{id}")
	@PreAuthorize("hasAuthority('MARKETPLACE_MANAGE')")
	public ResponseEntity<PazaryeriAyarlariResponse> updatePazaryeri(
			@PathVariable Long id,
			@Valid @RequestBody PazaryeriAyarlariRequest request) {
		PazaryeriAyarlariResponse pazaryeri = pazaryeriAyarlariService.updatePazaryeri(id, request);
		return ResponseEntity.ok(pazaryeri);
	}
	
	@DeleteMapping("/{id}")
	@PreAuthorize("hasAuthority('MARKETPLACE_MANAGE')")
	public ResponseEntity<Void> deletePazaryeri(@PathVariable Long id) {
		pazaryeriAyarlariService.deletePazaryeri(id);
		return ResponseEntity.noContent().build();
	}
}

