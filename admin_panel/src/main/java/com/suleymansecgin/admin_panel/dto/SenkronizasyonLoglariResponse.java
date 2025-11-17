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
public class SenkronizasyonLoglariResponse {
	
	private Long id;
	private String islemTipi;
	private Long pazaryeriId;
	private String pazaryeriAdi;
	private String durum;
	private String mesaj;
	private String hataDetay;
	private String islemDetay;
	private LocalDateTime tarih;
	private LocalDateTime createdAt;
}

