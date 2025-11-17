package com.suleymansecgin.admin_panel.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "siparisler", indexes = {
	@Index(name = "idx_pazaryeri_siparis_id", columnList = "pazaryeri_siparis_id"),
	@Index(name = "idx_pazaryeri_id", columnList = "pazaryeri_id"),
	@Index(name = "idx_durum", columnList = "durum"),
	@Index(name = "idx_tarih", columnList = "siparis_tarihi")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Siparis {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pazaryeri_id", nullable = false)
	private PazaryeriAyarlari pazaryeri;
	
	@Column(nullable = false, length = 200)
	private String pazaryeriSiparisId; // Pazaryerinin verdiği sipariş numarası
	
	@Column(nullable = false, length = 50)
	private String durum; // Yeni, Hazırlanıyor, Kargoya Verildi, Teslim Edildi, İptal
	
	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal toplamTutar;
	
	@Column(nullable = false)
	private LocalDateTime siparisTarihi;
	
	@Column(length = 500)
	private String musteriAdi;
	
	@Column(length = 500)
	private String musteriAdresi;
	
	@Column(length = 50)
	private String musteriTelefon;
	
	@Column(length = 200)
	private String musteriEmail;
	
	@Column(length = 200)
	private String kargoTakipNo;
	
	@Column(length = 100)
	private String kargoFirmasi;
	
	@Column(length = 2000)
	private String notlar;
	
	@OneToMany(mappedBy = "siparis", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<SiparisKalemi> siparisKalemleri = new HashSet<>();
	
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;
	
	@Column(nullable = false)
	private LocalDateTime updatedAt;
	
	@PrePersist
	protected void onCreate() {
		createdAt = LocalDateTime.now();
		updatedAt = LocalDateTime.now();
		if (siparisTarihi == null) {
			siparisTarihi = LocalDateTime.now();
		}
	}
	
	@PreUpdate
	protected void onUpdate() {
		updatedAt = LocalDateTime.now();
	}
}

