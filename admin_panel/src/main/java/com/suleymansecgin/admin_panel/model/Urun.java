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
@Table(name = "urunler", indexes = {
	@Index(name = "idx_urun_kodu", columnList = "urun_kodu"),
	@Index(name = "idx_aktif", columnList = "aktif")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Urun {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false, unique = true, length = 100)
	private String urunKodu; // Panel içi benzersiz ürün kodu
	
	@Column(nullable = false, length = 500)
	private String urunAdi;
	
	@Column(length = 2000)
	private String aciklama;
	
	@Column(nullable = false)
	private Integer stokMiktari = 0;
	
	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal temelFiyat; // Varsayılan fiyat
	
	@Column(precision = 10, scale = 2)
	private BigDecimal maliyetFiyati; // Ürün maliyet fiyatı (kar-zarar analizi için)
	
	@Column(length = 500)
	private String kategori;
	
	@Column(length = 1000)
	private String resimUrl;
	
	@Column(nullable = false)
	private Boolean aktif = true;
	
	@OneToMany(mappedBy = "urun", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<UrunPazaryeriEslesme> pazaryeriEslesmeleri = new HashSet<>();
	
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

