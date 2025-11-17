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
public class MfaVerifyRequest {
	
	@NotBlank(message = "MFA kodu boş olamaz")
	private String code;
}

