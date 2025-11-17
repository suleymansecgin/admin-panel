package com.suleymansecgin.admin_panel.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "urun_pazaryeri_eslestirme", indexes = {
	@Index(name = "idx_urun_pazaryeri", columnList = "urun_id,pazaryeri_id"),
	@Index(name = "idx_pazaryeri_urun_kodu", columnList = "pazaryeri_urun_kodu")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UrunPazaryeriEslesme {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "urun_id", nullable = false)
	private Urun urun;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pazaryeri_id", nullable = false)
	private PazaryeriAyarlari pazaryeri;
	
	@Column(nullable = false, length = 200)
	private String pazaryeriUrunKodu; // ASIN, MLID, vb.
	
	@Column(precision = 10, scale = 2)
	private BigDecimal fiyat; // Pazaryeri özel fiyat (null ise temel fiyat kullanılır)
	
	@Column(nullable = false)
	private Boolean aktif = true;
	
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

