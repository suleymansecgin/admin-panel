package com.suleymansecgin.admin_panel.integration;

import com.suleymansecgin.admin_panel.model.PazaryeriAyarlari;
import com.suleymansecgin.admin_panel.model.Siparis;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

/**
 * Trendyol pazaryeri için adaptör
 * Gerçek API entegrasyonu için Trendyol API dokümantasyonuna göre implement edilmelidir
 */
@Component
public class TrendyolAdapter extends BasePazaryeriAdapter {
	
	@Override
	public boolean validateCredentials(PazaryeriAyarlari ayarlar) {
		// Trendyol API kimlik doğrulama
		// Gerçek implementasyon için Trendyol API dokümantasyonuna bakılmalıdır
		try {
			String response = getRequest("https://api.trendyol.com/sapigw/suppliers/test", ayarlar);
			return response != null;
		} catch (Exception e) {
			return false;
		}
	}
	
	@Override
	public List<Siparis> fetchNewOrders(PazaryeriAyarlari ayarlar) {
		// Trendyol'dan yeni siparişleri çek
		// Gerçek implementasyon için Trendyol API dokümantasyonuna bakılmalıdır
		List<Siparis> siparisler = new ArrayList<>();
		// TODO: Gerçek API çağrısı yapılacak
		return siparisler;
	}
	
	@Override
	public boolean updateStock(PazaryeriAyarlari ayarlar, Long urunId, Integer stokMiktari, String pazaryeriUrunKodu) {
		// Trendyol'a stok güncellemesi gönder
		// Gerçek implementasyon için Trendyol API dokümantasyonuna bakılmalıdır
		try {
			Map<String, Object> body = Map.of(
				"items", List.of(Map.of(
					"barcode", pazaryeriUrunKodu,
					"quantity", stokMiktari
				))
			);
			String response = putRequest("https://api.trendyol.com/sapigw/suppliers/stock", body, ayarlar);
			return response != null;
		} catch (Exception e) {
			return false;
		}
	}
	
	@Override
	public boolean updateOrderStatus(PazaryeriAyarlari ayarlar, String pazaryeriSiparisId, String durum, String kargoTakipNo, String kargoFirmasi) {
		// Trendyol'a sipariş durumu güncellemesi gönder
		// Gerçek implementasyon için Trendyol API dokümantasyonuna bakılmalıdır
		try {
			Map<String, Object> body = Map.of(
				"packageId", pazaryeriSiparisId,
				"status", durum,
				"trackingNumber", kargoTakipNo != null ? kargoTakipNo : "",
				"carrier", kargoFirmasi != null ? kargoFirmasi : ""
			);
			String response = putRequest("https://api.trendyol.com/sapigw/suppliers/orders/" + pazaryeriSiparisId + "/status", body, ayarlar);
			return response != null;
		} catch (Exception e) {
			return false;
		}
	}
	
	@Override
	public Map<String, Object> fetchProductInfo(PazaryeriAyarlari ayarlar, String pazaryeriUrunKodu) {
		// Trendyol'dan ürün bilgilerini çek
		// Gerçek implementasyon için Trendyol API dokümantasyonuna bakılmalıdır
		try {
			String response = getRequest("https://api.trendyol.com/sapigw/suppliers/products/" + pazaryeriUrunKodu, ayarlar);
			// TODO: JSON parse edilip Map'e dönüştürülecek
			return Map.of();
		} catch (Exception e) {
			return Map.of();
		}
	}
	
	@Override
	public String getPazaryeriAdi() {
		return "Trendyol";
	}
	
	@Override
	protected void addAuthHeaders(HttpHeaders headers, PazaryeriAyarlari ayarlar) {
		// Trendyol özel authentication header'ları (Basic Auth)
		if (ayarlar.getApiKey() != null && ayarlar.getSecretKey() != null) {
			String credentials = ayarlar.getApiKey() + ":" + ayarlar.getSecretKey();
			String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());
			headers.set("Authorization", "Basic " + encodedCredentials);
		}
		headers.set("Content-Type", "application/json");
	}
}

