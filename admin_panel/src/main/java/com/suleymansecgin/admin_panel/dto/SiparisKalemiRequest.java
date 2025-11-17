package com.suleymansecgin.admin_panel.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SiparisKalemiRequest {
	
	@NotNull(message = "Ürün ID belirtilmelidir")
	private Long urunId;
	
	@NotNull(message = "Miktar belirtilmelidir")
	@Positive(message = "Miktar pozitif olmalıdır")
	private Integer miktar;
	
	@NotNull(message = "Birim fiyat belirtilmelidir")
	private BigDecimal birimFiyat;
	
	private String pazaryeriUrunKodu;
}

