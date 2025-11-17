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
public class SiparisResponse {
	
	private Long id;
	private Long pazaryeriId;
	private String pazaryeriAdi;
	private String pazaryeriSiparisId;
	private String durum;
	private BigDecimal toplamTutar;
	private LocalDateTime siparisTarihi;
	private String musteriAdi;
	private String musteriAdresi;
	private String musteriTelefon;
	private String musteriEmail;
	private String kargoTakipNo;
	private String kargoFirmasi;
	private String notlar;
	private List<SiparisKalemiResponse> siparisKalemleri;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}

