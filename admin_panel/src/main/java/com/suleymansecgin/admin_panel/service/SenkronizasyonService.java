package com.suleymansecgin.admin_panel.service;

import com.suleymansecgin.admin_panel.dto.SenkronizasyonLoglariResponse;
import com.suleymansecgin.admin_panel.model.*;
import com.suleymansecgin.admin_panel.repository.*;
import com.suleymansecgin.admin_panel.integration.PazaryeriAdapter;
import com.suleymansecgin.admin_panel.integration.PazaryeriAdapterFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SenkronizasyonService {
	
	@Autowired
	private SenkronizasyonLoglariRepository logRepository;
	
	@Autowired
	private PazaryeriAyarlariRepository pazaryeriAyarlariRepository;
	
	@Autowired
	private SiparisRepository siparisRepository;
	
	@Autowired
	private UrunRepository urunRepository;
	
	@Autowired
	private UrunPazaryeriEslesmeRepository eslesmeRepository;
	
	@Autowired
	private PazaryeriAdapterFactory adapterFactory;
	
	/**
	 * Tüm aktif pazaryerlerinden yeni siparişleri çeker
	 */
	@Transactional
	public void syncOrdersFromAllPazaryerleri() {
		List<PazaryeriAyarlari> aktifPazaryerleri = pazaryeriAyarlariRepository.findByAktifTrue();
		
		for (PazaryeriAyarlari pazaryeri : aktifPazaryerleri) {
			try {
				syncOrdersFromPazaryeri(pazaryeri.getId());
			} catch (Exception e) {
				logError(pazaryeri.getId(), "Sipariş", "Tüm pazaryerlerinden sipariş çekme hatası: " + e.getMessage(), e.getMessage());
			}
		}
	}
	
	/**
	 * Belirli bir pazaryerinden yeni siparişleri çeker
	 */
	@Transactional
	public void syncOrdersFromPazaryeri(Long pazaryeriId) {
		PazaryeriAyarlari pazaryeri = pazaryeriAyarlariRepository.findById(pazaryeriId)
			.orElseThrow(() -> new RuntimeException("Pazaryeri bulunamadı: " + pazaryeriId));
		
		if (!pazaryeri.getAktif()) {
			logWarning(pazaryeriId, "Sipariş", "Pazaryeri pasif durumda");
			return;
		}
		
		try {
			PazaryeriAdapter adapter = adapterFactory.getAdapter(pazaryeri.getPazaryeriAdi());
			List<Siparis> yeniSiparisler = adapter.fetchNewOrders(pazaryeri);
			
			int basariliSayisi = 0;
			for (Siparis siparis : yeniSiparisler) {
				try {
					// Sipariş zaten var mı kontrol et
					if (siparisRepository.findByPazaryeriIdAndPazaryeriSiparisId(
						pazaryeriId, siparis.getPazaryeriSiparisId()).isEmpty()) {
						
						siparis.setPazaryeri(pazaryeri);
						siparis = siparisRepository.save(siparis);
						
						// Stok güncelle
						for (SiparisKalemi kalem : siparis.getSiparisKalemleri()) {
							Urun urun = kalem.getUrun();
							urun.setStokMiktari(urun.getStokMiktari() - kalem.getMiktar());
							urunRepository.save(urun);
						}
						
						basariliSayisi++;
					}
				} catch (Exception e) {
					logError(pazaryeriId, "Sipariş", "Sipariş kaydetme hatası: " + e.getMessage(), e.getMessage());
				}
			}
			
			// Son senkronizasyon tarihini güncelle
			pazaryeri.setSonSenkronizasyonTarihi(LocalDateTime.now());
			pazaryeriAyarlariRepository.save(pazaryeri);
			
			logSuccess(pazaryeriId, "Sipariş", basariliSayisi + " yeni sipariş çekildi");
			
		} catch (Exception e) {
			logError(pazaryeriId, "Sipariş", "Sipariş senkronizasyonu hatası: " + e.getMessage(), e.getMessage());
		}
	}
	
	/**
	 * Tüm aktif pazaryerlerine stok güncellemelerini gönderir
	 */
	@Transactional
	public void syncStockToAllPazaryerleri(Long urunId) {
		Urun urun = urunRepository.findById(urunId)
			.orElseThrow(() -> new RuntimeException("Ürün bulunamadı: " + urunId));
		
		List<UrunPazaryeriEslesme> aktifEslesmeler = eslesmeRepository.findByUrunIdAndAktifTrue(urunId);
		
		for (UrunPazaryeriEslesme eslesme : aktifEslesmeler) {
			PazaryeriAyarlari pazaryeri = eslesme.getPazaryeri();
			
			if (!pazaryeri.getAktif()) {
				continue;
			}
			
			try {
				PazaryeriAdapter adapter = adapterFactory.getAdapter(pazaryeri.getPazaryeriAdi());
				boolean success = adapter.updateStock(
					pazaryeri,
					urunId,
					urun.getStokMiktari(),
					eslesme.getPazaryeriUrunKodu()
				);
				
				if (success) {
					logSuccess(pazaryeri.getId(), "Stok", "Ürün stoku güncellendi: " + urun.getUrunKodu());
				} else {
					logError(pazaryeri.getId(), "Stok", "Stok güncelleme başarısız", "Ürün: " + urun.getUrunKodu());
				}
			} catch (Exception e) {
				logError(pazaryeri.getId(), "Stok", "Stok güncelleme hatası: " + e.getMessage(), "Ürün: " + urun.getUrunKodu());
			}
		}
	}
	
	@Transactional(readOnly = true)
	public List<SenkronizasyonLoglariResponse> getAllLogs() {
		return logRepository.findAllOrderByTarihDesc().stream()
			.map(this::convertToResponse)
			.collect(Collectors.toList());
	}
	
	@Transactional(readOnly = true)
	public List<SenkronizasyonLoglariResponse> getLogsByPazaryeri(Long pazaryeriId) {
		return logRepository.findByPazaryeriId(pazaryeriId).stream()
			.map(this::convertToResponse)
			.collect(Collectors.toList());
	}
	
	@Transactional(readOnly = true)
	public List<SenkronizasyonLoglariResponse> getSonHatalar() {
		LocalDateTime son24Saat = LocalDateTime.now().minusHours(24);
		return logRepository.findSonHatalar(son24Saat).stream()
			.map(this::convertToResponse)
			.collect(Collectors.toList());
	}
	
	private void logSuccess(Long pazaryeriId, String islemTipi, String mesaj) {
		SenkronizasyonLoglari log = new SenkronizasyonLoglari();
		log.setIslemTipi(islemTipi);
		if (pazaryeriId != null) {
			log.setPazaryeri(pazaryeriAyarlariRepository.findById(pazaryeriId).orElse(null));
		}
		log.setDurum("Başarılı");
		log.setMesaj(mesaj);
		log.setTarih(LocalDateTime.now());
		logRepository.save(log);
	}
	
	private void logWarning(Long pazaryeriId, String islemTipi, String mesaj) {
		SenkronizasyonLoglari log = new SenkronizasyonLoglari();
		log.setIslemTipi(islemTipi);
		if (pazaryeriId != null) {
			log.setPazaryeri(pazaryeriAyarlariRepository.findById(pazaryeriId).orElse(null));
		}
		log.setDurum("Uyarı");
		log.setMesaj(mesaj);
		log.setTarih(LocalDateTime.now());
		logRepository.save(log);
	}
	
	private void logError(Long pazaryeriId, String islemTipi, String mesaj, String hataDetay) {
		SenkronizasyonLoglari log = new SenkronizasyonLoglari();
		log.setIslemTipi(islemTipi);
		if (pazaryeriId != null) {
			log.setPazaryeri(pazaryeriAyarlariRepository.findById(pazaryeriId).orElse(null));
		}
		log.setDurum("Hata");
		log.setMesaj(mesaj);
		log.setHataDetay(hataDetay);
		log.setTarih(LocalDateTime.now());
		logRepository.save(log);
	}
	
	private SenkronizasyonLoglariResponse convertToResponse(SenkronizasyonLoglari log) {
		SenkronizasyonLoglariResponse response = new SenkronizasyonLoglariResponse();
		response.setId(log.getId());
		response.setIslemTipi(log.getIslemTipi());
		if (log.getPazaryeri() != null) {
			response.setPazaryeriId(log.getPazaryeri().getId());
			response.setPazaryeriAdi(log.getPazaryeri().getPazaryeriAdi());
		}
		response.setDurum(log.getDurum());
		response.setMesaj(log.getMesaj());
		response.setHataDetay(log.getHataDetay());
		response.setIslemDetay(log.getIslemDetay());
		response.setTarih(log.getTarih());
		response.setCreatedAt(log.getCreatedAt());
		return response;
	}
}

