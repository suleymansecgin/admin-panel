package com.suleymansecgin.admin_panel.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoleRequest {
	
	@NotBlank(message = "Rol kodu boş olamaz")
	@Size(min = 2, max = 50, message = "Rol kodu 2-50 karakter arasında olmalıdır")
	private String code;
	
	@NotBlank(message = "Rol adı boş olamaz")
	@Size(min = 2, max = 100, message = "Rol adı 2-100 karakter arasında olmalıdır")
	private String name;
	
	@Size(max = 500, message = "Açıklama en fazla 500 karakter olabilir")
	private String description;
	
	private Set<Long> permissionIds;
}

