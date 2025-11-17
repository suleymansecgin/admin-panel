package com.suleymansecgin.admin_panel.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PazaryeriAyarlariRequest {
	
	@NotBlank(message = "Pazaryeri adı boş olamaz")
	private String pazaryeriAdi;
	
	private String apiKey;
	
	private String secretKey;
	
	private String yetkiToken;
	
	@NotNull(message = "Aktif durumu belirtilmelidir")
	private Boolean aktif;
	
	private String ekstraAyarlar;
}

