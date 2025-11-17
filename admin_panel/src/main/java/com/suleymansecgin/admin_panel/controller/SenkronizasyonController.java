package com.suleymansecgin.admin_panel.controller;

import com.suleymansecgin.admin_panel.dto.SenkronizasyonLoglariResponse;
import com.suleymansecgin.admin_panel.service.SenkronizasyonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/senkronizasyon")
public class SenkronizasyonController {
	
	@Autowired
	private SenkronizasyonService senkronizasyonService;
	
	@PostMapping("/siparisler/tum")
	@PreAuthorize("hasAuthority('ORDER_MANAGE')")
	public ResponseEntity<Void> syncOrdersFromAllPazaryerleri() {
		senkronizasyonService.syncOrdersFromAllPazaryerleri();
		return ResponseEntity.ok().build();
	}
	
	@PostMapping("/siparisler/{pazaryeriId}")
	@PreAuthorize("hasAuthority('ORDER_MANAGE')")
	public ResponseEntity<Void> syncOrdersFromPazaryeri(@PathVariable Long pazaryeriId) {
		senkronizasyonService.syncOrdersFromPazaryeri(pazaryeriId);
		return ResponseEntity.ok().build();
	}
	
	@PostMapping("/stok/{urunId}")
	@PreAuthorize("hasAuthority('PRODUCT_MANAGE')")
	public ResponseEntity<Void> syncStockToAllPazaryerleri(@PathVariable Long urunId) {
		senkronizasyonService.syncStockToAllPazaryerleri(urunId);
		return ResponseEntity.ok().build();
	}
	
	@GetMapping("/loglar")
	@PreAuthorize("hasAuthority('SYNC_LOGS_VIEW')")
	public ResponseEntity<List<SenkronizasyonLoglariResponse>> getAllLogs() {
		List<SenkronizasyonLoglariResponse> loglar = senkronizasyonService.getAllLogs();
		return ResponseEntity.ok(loglar);
	}
	
	@GetMapping("/loglar/pazaryeri/{pazaryeriId}")
	@PreAuthorize("hasAuthority('SYNC_LOGS_VIEW')")
	public ResponseEntity<List<SenkronizasyonLoglariResponse>> getLogsByPazaryeri(@PathVariable Long pazaryeriId) {
		List<SenkronizasyonLoglariResponse> loglar = senkronizasyonService.getLogsByPazaryeri(pazaryeriId);
		return ResponseEntity.ok(loglar);
	}
	
	@GetMapping("/loglar/hatalar")
	@PreAuthorize("hasAuthority('SYNC_LOGS_VIEW')")
	public ResponseEntity<List<SenkronizasyonLoglariResponse>> getSonHatalar() {
		List<SenkronizasyonLoglariResponse> loglar = senkronizasyonService.getSonHatalar();
		return ResponseEntity.ok(loglar);
	}
}

