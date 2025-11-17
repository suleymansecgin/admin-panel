package com.suleymansecgin.admin_panel.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "senkronizasyon_loglari", indexes = {
	@Index(name = "idx_pazaryeri_id", columnList = "pazaryeri_id"),
	@Index(name = "idx_islem_tipi", columnList = "islem_tipi"),
	@Index(name = "idx_durum", columnList = "durum"),
	@Index(name = "idx_tarih", columnList = "tarih")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SenkronizasyonLoglari {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false, length = 50)
	private String islemTipi; // Stok, Sipariş, Ürün, Fiyat
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pazaryeri_id")
	private PazaryeriAyarlari pazaryeri;
	
	@Column(nullable = false, length = 50)
	private String durum; // Başarılı, Hata, Uyarı
	
	@Column(length = 500)
	private String mesaj;
	
	@Column(columnDefinition = "TEXT")
	private String hataDetay; // Detaylı hata mesajı
	
	@Column(length = 200)
	private String islemDetay; // Hangi ürün/sipariş için işlem yapıldığı
	
	@Column(nullable = false)
	private LocalDateTime tarih;
	
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;
	
	@PrePersist
	protected void onCreate() {
		createdAt = LocalDateTime.now();
		if (tarih == null) {
			tarih = LocalDateTime.now();
		}
	}
}

