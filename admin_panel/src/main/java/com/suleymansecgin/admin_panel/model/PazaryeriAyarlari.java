package com.suleymansecgin.admin_panel.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "pazaryeri_ayarlari", indexes = {
	@Index(name = "idx_pazaryeri_adi", columnList = "pazaryeri_adi")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PazaryeriAyarlari {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false, length = 100)
	private String pazaryeriAdi; // Hepsiburada, Trendyol, Amazon vb.
	
	@Column(length = 500)
	private String apiKey;
	
	@Column(length = 500)
	private String secretKey;
	
	@Column(length = 1000)
	private String yetkiToken;
	
	@Column
	private LocalDateTime sonSenkronizasyonTarihi;
	
	@Column(nullable = false)
	private Boolean aktif = true;
	
	@Column(length = 2000)
	private String ekstraAyarlar; // JSON formatında pazaryeri özel ayarları
	
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;
	
	@Column(nullable = false)
	private LocalDateTime updatedAt;
	
	@PrePersist
	protected void onCreate() {
		createdAt = LocalDateTime.now();
		updatedAt = LocalDateTime.now();
	}
	
	@PreUpdate
	protected void onUpdate() {
		updatedAt = LocalDateTime.now();
	}
}

