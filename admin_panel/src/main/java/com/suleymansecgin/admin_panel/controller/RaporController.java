package com.suleymansecgin.admin_panel.controller;

import com.suleymansecgin.admin_panel.dto.KarZararRaporuResponse;
import com.suleymansecgin.admin_panel.service.RaporService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/raporlar")
public class RaporController {
	
	@Autowired
	private RaporService raporService;
	
	@GetMapping("/kar-zarar")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<KarZararRaporuResponse> getKarZararRaporu(
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime baslangicTarihi,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime bitisTarihi) {
		
		// Eğer tarih belirtilmemişse, son 30 günü getir
		if (baslangicTarihi == null) {
			baslangicTarihi = LocalDateTime.now().minusDays(30);
		}
		if (bitisTarihi == null) {
			bitisTarihi = LocalDateTime.now();
		}
		
		KarZararRaporuResponse rapor = raporService.getKarZararRaporu(baslangicTarihi, bitisTarihi);
		return ResponseEntity.ok(rapor);
	}
}

