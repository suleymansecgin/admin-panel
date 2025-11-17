package com.suleymansecgin.admin_panel.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SiparisKalemiResponse {
	
	private Long id;
	private Long urunId;
	private String urunKodu;
	private String urunAdi;
	private Integer miktar;
	private BigDecimal birimFiyat;
	private BigDecimal toplamFiyat;
	private String pazaryeriUrunKodu;
}

