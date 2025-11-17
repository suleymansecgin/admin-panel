package com.suleymansecgin.admin_panel.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UrunRequest {
	
	@NotBlank(message = "Ürün kodu boş olamaz")
	private String urunKodu;
	
	@NotBlank(message = "Ürün adı boş olamaz")
	private String urunAdi;
	
	private String aciklama;
	
	@NotNull(message = "Stok miktarı belirtilmelidir")
	@PositiveOrZero(message = "Stok miktarı negatif olamaz")
	private Integer stokMiktari;
	
	@NotNull(message = "Temel fiyat belirtilmelidir")
	private BigDecimal temelFiyat;
	
	private BigDecimal maliyetFiyati; // Ürün maliyet fiyatı (opsiyonel)
	
	private String kategori;
	
	private String resimUrl;
	
	@NotNull(message = "Aktif durumu belirtilmelidir")
	private Boolean aktif;
}

