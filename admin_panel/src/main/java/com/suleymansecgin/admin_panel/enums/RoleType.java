package com.suleymansecgin.admin_panel.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RoleType {
	
	ADMIN("ADMIN", "Yönetici"),
	EDITOR("EDITOR", "Editör"),
	ACCOUNTANT("ACCOUNTANT", "Muhasebe"),
	WAREHOUSE("WAREHOUSE", "Depo"),
	VIEWER("VIEWER", "Görüntüleyici");

	private final String code;
	private final String description;
}

