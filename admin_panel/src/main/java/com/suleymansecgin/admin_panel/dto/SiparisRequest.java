package com.suleymansecgin.admin_panel.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
public class SiparisRequest {
	
	@NotNull(message = "Pazaryeri ID belirtilmelidir")
	private Long pazaryeriId;
	
	@NotBlank(message = "Pazaryeri sipariş ID boş olamaz")
	private String pazaryeriSiparisId;
	
	@NotBlank(message = "Durum belirtilmelidir")
	private String durum;
	
	@NotNull(message = "Toplam tutar belirtilmelidir")
	@Positive(message = "Toplam tutar pozitif olmalıdır")
	private BigDecimal toplamTutar;
	
	@NotNull(message = "Sipariş tarihi belirtilmelidir")
	private LocalDateTime siparisTarihi;
	
	private String musteriAdi;
	private String musteriAdresi;
	private String musteriTelefon;
	private String musteriEmail;
	private String kargoTakipNo;
	private String kargoFirmasi;
	private String notlar;
	
	@NotNull(message = "Sipariş kalemleri belirtilmelidir")
	private List<SiparisKalemiRequest> siparisKalemleri;
}

