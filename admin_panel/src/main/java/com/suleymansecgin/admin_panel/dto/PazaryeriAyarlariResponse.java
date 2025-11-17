package com.suleymansecgin.admin_panel.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PazaryeriAyarlariResponse {
	
	private Long id;
	private String pazaryeriAdi;
	private LocalDateTime sonSenkronizasyonTarihi;
	private Boolean aktif;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}

