package com.suleymansecgin.admin_panel.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public enum PermissionType {
	
	// Kullanıcı Yönetimi
	USER_CREATE("USER_CREATE", "Kullanıcı Ekleme"),
	USER_APPROVE("USER_APPROVE", "Kullanıcı Kaydını Onaylama"),
	
	// Rol Yönetimi
	ROLE_MANAGE("ROLE_MANAGE", "Rol Yönetimi"),
	
	// Pazaryeri Yönetimi
	MARKETPLACE_MANAGE("MARKETPLACE_MANAGE", "Pazaryeri Yönetimi"),
	
	// Ürün Yönetimi
	PRODUCT_MANAGE("PRODUCT_MANAGE", "Ürün Yönetimi"),
	
	// Sipariş Yönetimi
	ORDER_MANAGE("ORDER_MANAGE", "Sipariş Yönetimi"),
	
	// Senkronizasyon Logları
	SYNC_LOGS_VIEW("SYNC_LOGS_VIEW", "Senkronizasyon Logları");

	private final String code;
	private final String description;
	
	PermissionType(String code, String description) {
		this.code = code;
		this.description = description;
	}
}

