package com.suleymansecgin.admin_panel.controller;

import com.suleymansecgin.admin_panel.dto.*;
import com.suleymansecgin.admin_panel.service.UrunService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/urunler")
public class UrunController {
	
	@Autowired
	private UrunService urunService;
	
	@GetMapping
	@PreAuthorize("hasAuthority('PRODUCT_MANAGE')")
	public ResponseEntity<List<UrunResponse>> getAllUrunler() {
		List<UrunResponse> urunler = urunService.getAllUrunler();
		return ResponseEntity.ok(urunler);
	}
	
	@GetMapping("/{id}")
	@PreAuthorize("hasAuthority('PRODUCT_MANAGE')")
	public ResponseEntity<UrunResponse> getUrunById(@PathVariable Long id) {
		UrunResponse urun = urunService.getUrunById(id);
		return ResponseEntity.ok(urun);
	}
	
	@PostMapping
	@PreAuthorize("hasAuthority('PRODUCT_MANAGE')")
	public ResponseEntity<UrunResponse> createUrun(@Valid @RequestBody UrunRequest request) {
		UrunResponse urun = urunService.createUrun(request);
		return ResponseEntity.ok(urun);
	}
	
	@PutMapping("/{id}")
	@PreAuthorize("hasAuthority('PRODUCT_MANAGE')")
	public ResponseEntity<UrunResponse> updateUrun(
			@PathVariable Long id,
			@Valid @RequestBody UrunRequest request) {
		UrunResponse urun = urunService.updateUrun(id, request);
		return ResponseEntity.ok(urun);
	}
	
	@DeleteMapping("/{id}")
	@PreAuthorize("hasAuthority('PRODUCT_MANAGE')")
	public ResponseEntity<Void> deleteUrun(@PathVariable Long id) {
		urunService.deleteUrun(id);
		return ResponseEntity.noContent().build();
	}
	
	@PostMapping("/{urunId}/pazaryeri-eslesme")
	@PreAuthorize("hasAuthority('PRODUCT_MANAGE')")
	public ResponseEntity<UrunPazaryeriEslesmeResponse> createEslesme(
			@PathVariable Long urunId,
			@Valid @RequestBody UrunPazaryeriEslesmeRequest request) {
		request.setUrunId(urunId);
		UrunPazaryeriEslesmeResponse eslesme = urunService.createEslesme(request);
		return ResponseEntity.ok(eslesme);
	}
	
	@PutMapping("/eslesme/{id}")
	@PreAuthorize("hasAuthority('PRODUCT_MANAGE')")
	public ResponseEntity<UrunPazaryeriEslesmeResponse> updateEslesme(
			@PathVariable Long id,
			@Valid @RequestBody UrunPazaryeriEslesmeRequest request) {
		UrunPazaryeriEslesmeResponse eslesme = urunService.updateEslesme(id, request);
		return ResponseEntity.ok(eslesme);
	}
	
	@DeleteMapping("/eslesme/{id}")
	@PreAuthorize("hasAuthority('PRODUCT_MANAGE')")
	public ResponseEntity<Void> deleteEslesme(@PathVariable Long id) {
		urunService.deleteEslesme(id);
		return ResponseEntity.noContent().build();
	}
}

