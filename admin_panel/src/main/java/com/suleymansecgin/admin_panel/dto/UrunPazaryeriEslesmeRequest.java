package com.suleymansecgin.admin_panel.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UrunPazaryeriEslesmeRequest {
	
	@NotNull(message = "Ürün ID belirtilmelidir")
	private Long urunId;
	
	@NotNull(message = "Pazaryeri ID belirtilmelidir")
	private Long pazaryeriId;
	
	@NotBlank(message = "Pazaryeri ürün kodu boş olamaz")
	private String pazaryeriUrunKodu;
	
	private BigDecimal fiyat;
	
	@NotNull(message = "Aktif durumu belirtilmelidir")
	private Boolean aktif;
}

