package com.suleymansecgin.admin_panel.exception;

import lombok.Getter;

@Getter
public enum MessageType {
    

    GENERAL_EXCEPTION("9999", "Genel bir hata oluştu"),
    
    USER_NOT_FOUND("1001", "Kullanıcı bulunamadı"),
    USER_ALREADY_EXISTS("1002", "Kullanıcı zaten mevcut"),
    INVALID_CREDENTIALS("1003", "Geçersiz kullanıcı adı veya şifre"),
    UNAUTHORIZED("1004", "Yetkisiz erişim"),
    INVALID_TOKEN("1005", "Geçersiz token"),
    TOKEN_EXPIRED("1006", "Token süresi dolmuş"),
    EMAIL_ALREADY_EXISTS("1007", "E-posta adresi zaten kullanılıyor");

    private String code;

    private String message;

    MessageType(String code, String message){
        this.code = code;
        this.message = message;
    }

    public String getMessage(){
        return this.message;
    }

}
