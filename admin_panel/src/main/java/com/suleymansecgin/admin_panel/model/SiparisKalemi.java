package com.suleymansecgin.admin_panel.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "siparis_kalemleri", indexes = {
	@Index(name = "idx_siparis_id", columnList = "siparis_id"),
	@Index(name = "idx_urun_id", columnList = "urun_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SiparisKalemi {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "siparis_id", nullable = false)
	private Siparis siparis;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "urun_id", nullable = false)
	private Urun urun;
	
	@Column(nullable = false)
	private Integer miktar;
	
	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal birimFiyat;
	
	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal toplamFiyat;
	
	@Column(length = 200)
	private String pazaryeriUrunKodu; // Sipariş geldiğinde pazaryerindeki ürün kodu
}

