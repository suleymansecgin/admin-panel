package com.suleymansecgin.admin_panel.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    
    private String token;
    private String type = "Bearer";
    private String username;
    private String email;
    private String role;
}

