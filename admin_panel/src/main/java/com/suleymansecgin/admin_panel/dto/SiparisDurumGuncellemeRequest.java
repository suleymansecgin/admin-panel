package com.suleymansecgin.admin_panel.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SiparisDurumGuncellemeRequest {
	
	@NotBlank(message = "Durum belirtilmelidir")
	private String durum;
	
	private String kargoTakipNo;
	
	private String kargoFirmasi;
}

