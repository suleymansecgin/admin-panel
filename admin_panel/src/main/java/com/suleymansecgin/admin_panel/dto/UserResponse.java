package com.suleymansecgin.admin_panel.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
	
	private Long id;
	private String username;
	private String email;
	private String firstName;
	private String lastName;
	private Boolean enabled;
	private Boolean mfaEnabled;
	private Set<RoleInfo> roles;
	private LocalDateTime createdAt;
	private LocalDateTime lastLoginAt;
	
	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class RoleInfo {
		private Long id;
		private String code;
		private String name;
	}
}

