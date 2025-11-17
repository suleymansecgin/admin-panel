package com.suleymansecgin.admin_panel.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
	
	@NotBlank(message = "Kullanıcı adı boş olamaz")
	@Size(min = 3, max = 50, message = "Kullanıcı adı 3-50 karakter arasında olmalıdır")
	private String username;
	
	@NotBlank(message = "E-posta boş olamaz")
	@Email(message = "Geçerli bir e-posta adresi giriniz")
	private String email;
	
	@NotBlank(message = "Şifre boş olamaz")
	@Size(min = 6, message = "Şifre en az 6 karakter olmalıdır")
	private String password;
	
	@NotBlank(message = "Ad boş olamaz")
	private String firstName;
	
	@NotBlank(message = "Soyad boş olamaz")
	private String lastName;
}

