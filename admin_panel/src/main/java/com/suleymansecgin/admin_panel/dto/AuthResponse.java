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
public class AuthResponse {
	
	private String accessToken;
	private String refreshToken;
	private String tokenType = "Bearer";
	private Long expiresIn;
	private UserInfo user;
	private Boolean mfaRequired = false;
	
	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class UserInfo {
		private Long id;
		private String username;
		private String email;
		private String firstName;
		private String lastName;
		private Boolean enabled;
		private Set<String> roles;
		private Set<String> permissions;
		private Boolean mfaEnabled;
	}
}

