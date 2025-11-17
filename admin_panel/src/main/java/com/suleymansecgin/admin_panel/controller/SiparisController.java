package com.suleymansecgin.admin_panel.controller;

import com.suleymansecgin.admin_panel.dto.*;
import com.suleymansecgin.admin_panel.service.SiparisService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/siparisler")
public class SiparisController {
	
	@Autowired
	private SiparisService siparisService;
	
	@GetMapping
	@PreAuthorize("hasAuthority('ORDER_MANAGE')")
	public ResponseEntity<List<SiparisResponse>> getAllSiparisler() {
		List<SiparisResponse> siparisler = siparisService.getAllSiparisler();
		return ResponseEntity.ok(siparisler);
	}
	
	@GetMapping("/{id}")
	@PreAuthorize("hasAuthority('ORDER_MANAGE')")
	public ResponseEntity<SiparisResponse> getSiparisById(@PathVariable Long id) {
		SiparisResponse siparis = siparisService.getSiparisById(id);
		return ResponseEntity.ok(siparis);
	}
	
	@GetMapping("/durum/{durum}")
	@PreAuthorize("hasAuthority('ORDER_MANAGE')")
	public ResponseEntity<List<SiparisResponse>> getSiparislerByDurum(@PathVariable String durum) {
		List<SiparisResponse> siparisler = siparisService.getSiparislerByDurum(durum);
		return ResponseEntity.ok(siparisler);
	}
	
	@GetMapping("/pazaryeri/{pazaryeriId}")
	@PreAuthorize("hasAuthority('ORDER_MANAGE')")
	public ResponseEntity<List<SiparisResponse>> getSiparislerByPazaryeri(@PathVariable Long pazaryeriId) {
		List<SiparisResponse> siparisler = siparisService.getSiparislerByPazaryeri(pazaryeriId);
		return ResponseEntity.ok(siparisler);
	}
	
	@PostMapping
	@PreAuthorize("hasAuthority('ORDER_MANAGE')")
	public ResponseEntity<SiparisResponse> createSiparis(@Valid @RequestBody SiparisRequest request) {
		SiparisResponse siparis = siparisService.createSiparis(request);
		return ResponseEntity.ok(siparis);
	}
	
	@PutMapping("/{id}/durum")
	@PreAuthorize("hasAuthority('ORDER_MANAGE')")
	public ResponseEntity<SiparisResponse> updateSiparisDurum(
			@PathVariable Long id,
			@Valid @RequestBody SiparisDurumGuncellemeRequest request) {
		SiparisResponse siparis = siparisService.updateSiparisDurum(id, request);
		return ResponseEntity.ok(siparis);
	}
}

