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
public class AuthRequest {
    
    @NotBlank(message = "Kullanıcı adı boş olamaz")
    private String username;
    
    @NotBlank(message = "Şifre boş olamaz")
    private String password;
}

