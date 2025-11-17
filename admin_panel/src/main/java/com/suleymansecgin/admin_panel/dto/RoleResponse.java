package com.suleymansecgin.admin_panel.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoleResponse {
	
	private Long id;
	private String code;
	private String name;
	private String description;
	private Set<PermissionResponse> permissions;
	
	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class PermissionResponse {
		private Long id;
		private String code;
		private String name;
		private String description;
	}
}

