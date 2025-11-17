package com.suleymansecgin.admin_panel.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRoleRequest {
	
	@NotEmpty(message = "En az bir rol seçilmelidir")
	private Set<Long> roleIds;
}

