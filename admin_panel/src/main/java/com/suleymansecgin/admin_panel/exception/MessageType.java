package com.suleymansecgin.admin_panel.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MessageType {
	
    GENERAL_EXCEPTION("9999","genel bir hata oluştu"),
    
    // Authentication hataları
    AUTHENTICATION_FAILED("1001", "Kimlik doğrulama başarısız"),
    INVALID_CREDENTIALS("1002", "Geçersiz kullanıcı adı veya şifre"),
    USER_NOT_FOUND("1003", "Kullanıcı bulunamadı"),
    USER_DISABLED("1004", "Kullanıcı hesabı devre dışı"),
    USER_LOCKED("1005", "Kullanıcı hesabı kilitli"),
    USER_EXPIRED("1006", "Kullanıcı hesabı süresi dolmuş"),
    CREDENTIALS_EXPIRED("1007", "Kullanıcı kimlik bilgileri süresi dolmuş"),
    INVALID_TOKEN("1008", "Geçersiz token"),
    TOKEN_EXPIRED("1009", "Token süresi dolmuş"),
    REFRESH_TOKEN_NOT_FOUND("1010", "Refresh token bulunamadı"),
    REFRESH_TOKEN_EXPIRED("1011", "Refresh token süresi dolmuş"),
    REFRESH_TOKEN_REVOKED("1012", "Refresh token iptal edilmiş"),
    
    // Authorization hataları
    ACCESS_DENIED("2001", "Erişim reddedildi"),
    INSUFFICIENT_PERMISSIONS("2002", "Yetersiz yetki"),
    ROLE_NOT_FOUND("2003", "Rol bulunamadı"),
    PERMISSION_NOT_FOUND("2004", "Yetki bulunamadı"),
    
    // MFA hataları
    MFA_REQUIRED("3001", "İki faktörlü kimlik doğrulama gerekli"),
    INVALID_MFA_CODE("3002", "Geçersiz MFA kodu"),
    MFA_ALREADY_ENABLED("3003", "MFA zaten etkin"),
    MFA_NOT_ENABLED("3004", "MFA etkin değil"),
    
    // Validation hataları
    VALIDATION_ERROR("4001", "Doğrulama hatası"),
    USERNAME_ALREADY_EXISTS("4002", "Kullanıcı adı zaten kullanılıyor"),
    EMAIL_ALREADY_EXISTS("4003", "E-posta adresi zaten kullanılıyor");

	private String code;
	
	private String message;
	
	MessageType(String code, String message) {
		this.code = code;
		this.message = message;
	}
}

