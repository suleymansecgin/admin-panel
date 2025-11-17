package com.suleymansecgin.admin_panel.integration;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Pazaryeri adaptörlerini yöneten factory sınıfı
 */
@Component
public class PazaryeriAdapterFactory {
	
	private final Map<String, PazaryeriAdapter> adapters;
	
	public PazaryeriAdapterFactory(List<PazaryeriAdapter> adapterList) {
		this.adapters = new HashMap<>();
		for (PazaryeriAdapter adapter : adapterList) {
			adapters.put(adapter.getPazaryeriAdi(), adapter);
		}
	}
	
	/**
	 * Pazaryeri adına göre uygun adaptörü döndürür
	 */
	public PazaryeriAdapter getAdapter(String pazaryeriAdi) {
		PazaryeriAdapter adapter = adapters.get(pazaryeriAdi);
		if (adapter == null) {
			throw new IllegalArgumentException("Pazaryeri adaptörü bulunamadı: " + pazaryeriAdi);
		}
		return adapter;
	}
	
	/**
	 * Tüm adaptörleri döndürür
	 */
	public Map<String, PazaryeriAdapter> getAllAdapters() {
		return new HashMap<>(adapters);
	}
}

