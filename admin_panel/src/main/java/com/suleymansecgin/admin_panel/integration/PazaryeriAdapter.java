package com.suleymansecgin.admin_panel.integration;

import com.suleymansecgin.admin_panel.model.PazaryeriAyarlari;
import com.suleymansecgin.admin_panel.model.Siparis;

import java.util.List;
import java.util.Map;

/**
 * Pazaryeri API'leri ile iletişim için temel adaptör interface'i
 * Her pazaryeri için bu interface'i implement eden özel adaptörler oluşturulmalıdır
 */
public interface PazaryeriAdapter {
	
	/**
	 * Pazaryeri ayarlarını doğrular
	 */
	boolean validateCredentials(PazaryeriAyarlari ayarlar);
	
	/**
	 * Pazaryerinden yeni siparişleri çeker
	 */
	List<Siparis> fetchNewOrders(PazaryeriAyarlari ayarlar);
	
	/**
	 * Pazaryerine stok güncellemesi gönderir
	 */
	boolean updateStock(PazaryeriAyarlari ayarlar, Long urunId, Integer stokMiktari, String pazaryeriUrunKodu);
	
	/**
	 * Pazaryerine sipariş durumu güncellemesi gönderir
	 */
	boolean updateOrderStatus(PazaryeriAyarlari ayarlar, String pazaryeriSiparisId, String durum, String kargoTakipNo, String kargoFirmasi);
	
	/**
	 * Pazaryerinden ürün bilgilerini çeker
	 */
	Map<String, Object> fetchProductInfo(PazaryeriAyarlari ayarlar, String pazaryeriUrunKodu);
	
	/**
	 * Pazaryeri adını döndürür
	 */
	String getPazaryeriAdi();
}

