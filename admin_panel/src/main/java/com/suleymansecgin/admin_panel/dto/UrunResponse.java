package com.suleymansecgin.admin_panel.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UrunResponse {
	
	private Long id;
	private String urunKodu;
	private String urunAdi;
	private String aciklama;
	private Integer stokMiktari;
	private BigDecimal temelFiyat;
	private BigDecimal maliyetFiyati;
	private String kategori;
	private String resimUrl;
	private Boolean aktif;
	private List<UrunPazaryeriEslesmeResponse> pazaryeriEslesmeleri;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}

