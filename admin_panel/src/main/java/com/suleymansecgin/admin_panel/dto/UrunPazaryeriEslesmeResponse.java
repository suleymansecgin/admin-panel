package com.suleymansecgin.admin_panel.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UrunPazaryeriEslesmeResponse {
	
	private Long id;
	private Long urunId;
	private String urunKodu;
	private String urunAdi;
	private Long pazaryeriId;
	private String pazaryeriAdi;
	private String pazaryeriUrunKodu;
	private BigDecimal fiyat;
	private Boolean aktif;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}

